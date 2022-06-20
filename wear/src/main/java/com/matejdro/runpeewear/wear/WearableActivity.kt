@file:OptIn(ExperimentalComposeUiApi::class)

package com.matejdro.runpeewear.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.layout.fillMaxRectangle
import com.matejdro.runpeewear.wear.theme.WearAppTheme
import com.matejdro.runpeewear.wear.util.roundVerticalPadding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

   @Composable
   private fun Loading() {
      Box(Modifier.fillMaxRectangle(), contentAlignment = Alignment.Center) {
         Text("Loading")
      }
   }

   @Composable
   private fun NoData() {
      Box(Modifier.fillMaxRectangle(), contentAlignment = Alignment.Center) {
         Text("No Data. Select movie in the phone app.")
      }
   }

   @Composable
   private fun WaitingForStart(state: PeeTimerStatus.WaitingForStart) {
      val scrollState = rememberScrollState()
      val scope = rememberCoroutineScope()
      val focusRequester = remember { FocusRequester() }

      Column(
         Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .verticalScroll(scrollState)
            .roundVerticalPadding()
            .onRotaryScrollEvent {
               scope.launch {
                  scrollState.scrollBy(it.verticalScrollPixels)
               }
               true
            }
            .focusRequester(focusRequester)
            .focusable(),
         verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
         horizontalAlignment = Alignment.CenterHorizontally
      ) {
         Spacer(Modifier.focusable())

         Text(state.movieName, textAlign = TextAlign.Center)
         Text(state.timerCue, textAlign = TextAlign.Center)
         Button(onClick = { viewModel.startTimer() }) {
            Text("Start timer", Modifier.padding(16.dp))
         }
      }

      LaunchedEffect(Unit) {
         focusRequester.requestFocus()
      }
   }
}
