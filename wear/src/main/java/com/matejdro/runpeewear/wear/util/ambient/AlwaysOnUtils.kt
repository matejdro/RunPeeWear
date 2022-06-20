package com.matejdro.runpeewear.wear.util.ambient

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import java.time.Instant

// Adapted from https://github.com/android/wear-os-samples/blob/main/AlwaysOnKotlin/compose/src/main/java/com/example/android/wearable/wear/alwayson/AlwaysOnApp.kt

@Composable
fun AmbientScreen(
   modifier: Modifier,
   updateCallback: (ambientUpdateTimestamp: Instant) -> Unit,
   content: @Composable (
      modifier: Modifier,
      ambientState: AmbientState,
      ambientUpdateTimestamp: Instant
   ) -> Unit
) {
   val ambientCallbackController = LocalAmbientCallbackController.current

   DisposableEffect(ambientCallbackController) {
      ambientCallbackController.updateCallback = {
         updateCallback(ambientCallbackController.ambientUpdateTimestamp)
      }
      onDispose {
         ambientCallbackController.updateCallback = null
      }
   }

   content(
      modifier.ambientBurnInProtection(ambientCallbackController.ambientState, ambientCallbackController.ambientUpdateTimestamp),
      ambientCallbackController.ambientState, ambientCallbackController.ambientUpdateTimestamp
   )
}
