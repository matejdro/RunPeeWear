package com.matejdro.runpeewear.wear

import android.content.SharedPreferences
import android.net.Uri
import android.os.Vibrator
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.DataClient
import com.matejdro.runpeewear.common.CommPaths
import com.matejdro.runpeewear.model.PeeTimes
import com.matejdro.runpeewear.wear.util.getDataItemFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class WearableViewModel @Inject constructor(
   private val dataClient: DataClient,
   private val vibrator: Vibrator,
   private val preferences: SharedPreferences
) : ViewModel() {
   private val _status = MutableStateFlow<PeeTimerStatus>(PeeTimerStatus.Loading)
   val status: StateFlow<PeeTimerStatus>
      get() = _status

   private var currentPeeTimes: PeeTimes? = null
   private var movieStartTime: Instant? = null
      set(value) {
         field = value
         preferences.edit {
            putLong(PREFERENCE_KEY_LAST_MOVIE_START, value?.epochSecond ?: -1)
         }
      }

   init {
      val lastMovieStartEpochSecond = preferences.getLong(PREFERENCE_KEY_LAST_MOVIE_START, -1)
      movieStartTime = if (lastMovieStartEpochSecond >= 0) {
         Instant.ofEpochSecond(lastMovieStartEpochSecond)
      } else {
         null
      }

      viewModelScope.launch {
         dataClient.getDataItemFlow(Uri.parse("wear://*${CommPaths.DATA_PEE_TIMES}")).collect {
            if (it == null) {
               _status.value = PeeTimerStatus.NoData
               return@collect
            }
            val peeTimes = PeeTimes.ADAPTER.decode(it.data)
            resetTimerState(peeTimes)
         }
      }
   }

   fun startTimer() {
      movieStartTime = Instant.now()
      tick()
   }

   fun abortTimer() {
      movieStartTime = null
      tick()
   }

   fun tick() {
      val peeTimes = currentPeeTimes ?: return
      val movieStartTime = movieStartTime
      if (movieStartTime == null) {
         _status.value = PeeTimerStatus.WaitingForStart(peeTimes.movieName, peeTimes.timerCue)
         return
      }

      val now = Instant.now()
      val secondsSinceStart = (now.epochSecond - movieStartTime.epochSecond).toInt()
      val startThreshold = secondsSinceStart - PEETIME_DURATION_SECONDS
      val endThreshold = secondsSinceStart + THRESHOLD_PEETIME_START

      val remainingPeetimes = peeTimes.times.dropWhile { it.cueTimeSecondsAfterMovieStart < endThreshold }

      val activePeetime = peeTimes.times.find { it.cueTimeSecondsAfterMovieStart in startThreshold..endThreshold }
      if (activePeetime != null) {
         if (_status.value !is PeeTimerStatus.InPeetime) {
            vibrateAlert()
         }

         val nextPeetime = remainingPeetimes.firstOrNull()
         val nextRecommendedPeetime = remainingPeetimes.firstOrNull { it.isRecommended }
         _status.value = PeeTimerStatus.InPeetime(
            activePeetime.isRecommended,
            nextPeetime?.cueTimeSecondsAfterMovieStart?.minus(secondsSinceStart)?.div(60),
            nextRecommendedPeetime?.cueTimeSecondsAfterMovieStart?.minus(secondsSinceStart)?.div(60),
            activePeetime.cue,
            activePeetime.synopsis,
            peeTimes.movieName
         )
         return
      }

      if (remainingPeetimes.isEmpty()) {
         abortTimer()
         return
      }

      val upcomingPeetime = remainingPeetimes.first()
      val remainingPeetimesAfterUpcoming = remainingPeetimes.drop(1)
      val nextPeetime = remainingPeetimesAfterUpcoming.firstOrNull()
      val nextRecommendedPeetime = remainingPeetimesAfterUpcoming.firstOrNull { it.isRecommended }
      val lastPeetime = peeTimes.times.lastOrNull { it.cueTimeSecondsAfterMovieStart < endThreshold }

      _status.value = PeeTimerStatus.WaitingForNextPeetime(
         (upcomingPeetime.cueTimeSecondsAfterMovieStart - secondsSinceStart) / 60,
         upcomingPeetime.isRecommended,
         nextPeetime?.cueTimeSecondsAfterMovieStart?.minus(secondsSinceStart)?.div(60),
         nextRecommendedPeetime?.cueTimeSecondsAfterMovieStart?.minus(secondsSinceStart)?.div(60),
         lastPeetime?.synopsis,
         peeTimes.movieName
      )
   }

   private fun resetTimerState(peeTimes: PeeTimes) {
      val previousPeeTimes = currentPeeTimes
      currentPeeTimes = peeTimes

      if (previousPeeTimes != null && peeTimes.movieName != previousPeeTimes.movieName) {
         abortTimer()
      } else {
         tick()
      }
   }

   private fun vibrateAlert() {
      vibrator.vibrate(longArrayOf(100, 100, 100, 100, 100, 100, 100, 100, 100, 100), -1)
   }
}

sealed interface PeeTimerStatus {
   object Loading : PeeTimerStatus
   object NoData : PeeTimerStatus
   data class WaitingForStart(val movieName: String, val timerCue: String) : PeeTimerStatus
   data class WaitingForNextPeetime(
      val minutesToUpcomingPeetime: Int,
      val isRecommended: Boolean,
      val minutesToNextPeetime: Int?,
      val minutesToNextRecommendedPeetime: Int?,
      val lastSynopsis: String?,
      val movieName: String
   ) : PeeTimerStatus

   data class InPeetime(
      val isRecommended: Boolean,
      val minutesToNextPeetime: Int?,
      val minutesToNextRecommendedPeetime: Int?,
      val peetimeCue: String,
      val peetimeSynopsis: String,
      val movieName: String
   ) : PeeTimerStatus
}

// Ambient mode ticks every 60 seconds, so we are forced to warn more in advance
private const val THRESHOLD_PEETIME_START = 70

private const val PEETIME_DURATION_SECONDS = 5 * 60

private const val PREFERENCE_KEY_LAST_MOVIE_START = "LAST_MOVIE_START"
