package com.matejdro.runpeewear.wear

import android.net.Uri
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
import logcat.logcat
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class WearableViewModel @Inject constructor(
   private val dataClient: DataClient
) : ViewModel() {
   private val _status = MutableStateFlow<PeeTimerStatus>(PeeTimerStatus.Loading)
   val status: StateFlow<PeeTimerStatus>
      get() = _status

   private var currentPeeTimes: PeeTimes? = null

   init {
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
      val peeTimes = currentPeeTimes ?: return

      logcat { "Starting timer for ${peeTimes.movieName}" }
   }

   fun tick() {

   }

   private fun resetTimerState(peeTimes: PeeTimes) {
      currentPeeTimes = peeTimes

//      _status.value = PeeTimerStatus.WaitingForStart(peeTimes.movieName, peeTimes.timerCue)

      val time = peeTimes.times.first()
      val next = peeTimes.times.elementAt(1)
      _status.value = PeeTimerStatus.WaitingForNextPeetime(
         Instant.now().plusSeconds(100),
         time.isRecommended,
         Instant.now().plusSeconds(200),
         next.isRecommended,
         time.synopsis
      )
   }
}

sealed interface PeeTimerStatus {
   object Loading : PeeTimerStatus
   object NoData : PeeTimerStatus
   data class WaitingForStart(val movieName: String, val timerCue: String) : PeeTimerStatus
   data class WaitingForNextPeetime(
      val nextPeetime: Instant,
      val isRecommended: Boolean,
      val nextNextPeetime: Instant?,
      val isNextNextRecommended: Boolean?,
      val lastSynopsis: String
   ) : PeeTimerStatus

   data class InPeetime(
      val isRecommended: Boolean,
      val nextNextPeetime: Instant?,
      val isNextNextRecommended: Boolean?,
      val peetimeCue: String,
      val peetimeSynopsis: String
   ) : PeeTimerStatus
}
