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

   private fun resetTimerState(peeTimes: PeeTimes) {
      currentPeeTimes = peeTimes

      _status.value = PeeTimerStatus.WaitingForStart(peeTimes.movieName, peeTimes.timerCue)
   }
}

sealed interface PeeTimerStatus {
   object Loading : PeeTimerStatus
   object NoData : PeeTimerStatus
   data class WaitingForStart(val movieName: String, val timerCue: String) : PeeTimerStatus
}
