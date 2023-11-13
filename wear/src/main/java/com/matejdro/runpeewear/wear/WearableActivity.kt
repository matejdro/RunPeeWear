@file:OptIn(ExperimentalComposeUiApi::class)

package com.matejdro.runpeewear.wear

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.LocalTextStyle
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.layout.fillMaxRectangle
import com.matejdro.runpeewear.wear.theme.WearAppTheme
import com.matejdro.runpeewear.wear.util.ambient.AmbientCallbackController
import com.matejdro.runpeewear.wear.util.ambient.AmbientScreen
import com.matejdro.runpeewear.wear.util.ambient.AmbientState
import com.matejdro.runpeewear.wear.util.ambient.LocalAmbientCallbackController
import com.matejdro.runpeewear.wear.util.roundVerticalPadding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.Instant

@AndroidEntryPoint
class WearableActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {
   private val viewModel: WearableViewModel by viewModels()
   private val ambientCallbackController = AmbientCallbackController()

   override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback {
      return ambientCallbackController
   }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      AmbientModeSupport.attach(this)

      setContent {
         WearAppTheme {
            CompositionLocalProvider(LocalAmbientCallbackController provides ambientCallbackController) {
               RootContent()
            }
         }
      }
   }

   override fun onStart() {
      super.onStart()

      viewModel.tick()
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
         is PeeTimerStatus.WaitingForNextPeetime -> {
            WaitingForNextPeetime(state)
         }
         is PeeTimerStatus.InPeetime -> {
            InPeetime(state)
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
      FullScreenScrollable {
         Text(state.movieName, textAlign = TextAlign.Center)
         Text(state.timerCue, textAlign = TextAlign.Center)
         Button(onClick = { viewModel.startTimer() }) {
            Text("Start timer", Modifier.padding(16.dp))
         }
      }
   }

   @Composable
   private fun WaitingForNextPeetime(state: PeeTimerStatus.WaitingForNextPeetime) {
      AmbientScreen(
         Modifier.fillMaxRectangle(),
         updateCallback = { viewModel.tick() }) { modifier: Modifier, ambientState: AmbientState, _: Instant ->
         AmbientOrInteractive(
            ambientState,
            ambient = {
               Column(
                  modifier,
                  verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
                  horizontalAlignment = Alignment.CenterHorizontally
               ) {
                  Text(
                     "U: ${formatMinutes(state.minutesToUpcomingPeetime)}, R: ${state.isRecommended.yesOrNoString()}",
                     style = darkTextStyle()
                  )
                  state.minutesToNextPeetime?.let {
                     Text("N: ${formatMinutes(it)}", style = darkTextStyle())
                  }

                  state.minutesToNextRecommendedPeetime?.let {
                     Text("NR: ${formatMinutes(it)}", style = darkTextStyle())
                  }
               }
            },
            interactive = {
               FullScreenScrollable {
                  val recommendedText = if (state.isRecommended) "and it's recommended" else "but it's not recommended"
                  Text(
                     "Upcoming pee time in ${formatMinutes(state.minutesToUpcomingPeetime)}, $recommendedText."
                  )

                  state.minutesToNextPeetime?.let {
                     Text("Next pee time in ${formatMinutes(it)}")
                  }

                  state.minutesToNextRecommendedPeetime?.let {
                     Text("Next recommended pee time in ${formatMinutes(it)}")
                  }

                  Spacer(Modifier.height(LocalConfiguration.current.screenHeightDp.dp))

                  state.lastSynopsis?.let {
                     Text("Last peetime synopsis:")
                     Text(it)
                  }

                  TimerCancelFooter(state.movieName)
               }
            }
         )
      }
   }

   @Composable
   private fun InPeetime(state: PeeTimerStatus.InPeetime) {
      AmbientScreen(
         Modifier
            .padding(top = 8.dp, start = 8.dp, end = 8.dp),
         updateCallback = { viewModel.tick() }) { modifier: Modifier, ambientState: AmbientState, _: Instant ->
         AmbientOrInteractive(
            ambientState,
            ambient = {
               Column(
                  modifier,
                  verticalArrangement = Arrangement.Center,
                  horizontalAlignment = Alignment.CenterHorizontally
               ) {
                  Text(
                     "NOW, R: ${state.isRecommended.yesOrNoString()}",
                     style = darkTextStyle(),
                     fontSize = 18.sp
                  )
                  Text(
                     state.peetimeCue,
                     style = darkTextStyle(),
                     fontSize = 18.sp
                  )

                  state.minutesToNextRecommendedPeetime?.let {
                     Text(
                        "NR: ${formatMinutes(it)}", style = darkTextStyle(),
                        fontSize = 18.sp
                     )
                  }

                  state.minutesToNextPeetime?.let {
                     Text(
                        "N: ${formatMinutes(it)}", style = darkTextStyle(),
                        fontSize = 18.sp
                     )
                  }
               }
            },
            interactive = {
               FullScreenScrollable {
                  val recommendedText = when {
                      state.peetimeMeta.isNotBlank() -> state.peetimeMeta.trim()
                      state.isRecommended -> "and it's recommended"
                      else -> "but it's not recommended"
                  }
                  Text(
                     "Pee time NOW, $recommendedText"
                  )

                  Text(state.peetimeCue)

                  state.minutesToNextPeetime?.let {
                     Text("Next pee time in ${formatMinutes(it)}")
                  }

                  state.minutesToNextRecommendedPeetime?.let {
                     Text("Next recommended pee time in ${formatMinutes(it)}")
                  }

                  Spacer(Modifier.height(LocalConfiguration.current.screenHeightDp.dp))

                  Text("Peetime synopsis:")
                  Text(state.peetimeSynopsis)

                  TimerCancelFooter(state.movieName)
               }
            }
         )
      }
   }

   @Composable
   private fun TimerCancelFooter(movieName: String) {
      Text("Timer active for $movieName", Modifier.padding(top = 32.dp))
      Button(onClick = { viewModel.abortTimer() }) {
         Text("Abort timer", Modifier.padding(16.dp))
      }
   }

   @Composable
   private fun FullScreenScrollable(content: @Composable ColumnScope.() -> Unit) {
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
            .focusable(),
         verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
         horizontalAlignment = Alignment.CenterHorizontally
      ) {
         Spacer(
            Modifier
               .focusRequester(focusRequester)
               .focusable()
         )

         content()
      }

      LaunchedEffect(Unit) {
         focusRequester.requestFocus()
      }
   }
}

@Composable
private fun AmbientOrInteractive(
   ambientState: AmbientState,
   ambient: @Composable () -> Unit,
   interactive: @Composable () -> Unit
) {
   val saveableState = rememberSaveableStateHolder()

   saveableState.SaveableStateProvider(ambientState) {
      when (ambientState) {
         is AmbientState.Ambient -> ambient()
         AmbientState.Interactive -> interactive()
      }
   }
}

@Composable
private fun darkTextStyle() = LocalTextStyle.current.copy(
   fontSize = 24.sp,
   fontWeight = FontWeight(1),
   color = Color(0xFF666666)
)

private fun Boolean.yesOrNoString(): String {
   return if (this) "Y" else "N"
}

private fun formatMinutes(minutes: Int): String {
   return "${(minutes / 60).toString().padStart(2, '0')}:${(minutes % 60).toString().padStart(2, '0')}"
}
