package com.matejdro.runpeewear.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.Text
import com.matejdro.runpeewear.wear.theme.WearAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WearableActivity : ComponentActivity() {
   val viewModel: WearableViewModel by viewModels()
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      setContent {
         WearAppTheme {
            RootContent()
         }
      }
   }

   @Composable
   private fun RootContent() {
      when (val state = viewModel.status.collectAsState().value) {
         PeeTimerStatus.Loading -> {
            Loading()
         }
         PeeTimerStatus.NoData -> {
            NoData()
         }
         is PeeTimerStatus.WaitingForStart -> {
            WaitingForStart(state)
         }
      }
   }
}

@Composable
private fun Loading() {
   Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("Loading")
   }
}

@Composable
private fun NoData() {
   Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("No Data. Select movie in the phone app.")
   }
}

@Composable
private fun WaitingForStart(state: PeeTimerStatus.WaitingForStart) {
   Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("Movie ${state.movieName}")
   }
}
