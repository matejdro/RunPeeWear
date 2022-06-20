package com.matejdro.runpeewear.wear.util.ambient

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import java.time.Instant
import kotlin.random.Random

// Adapted from https://github.com/android/wear-os-samples/blob/main/AlwaysOnKotlin/compose/src/main/java/com/example/android/wearable/wear/alwayson/AlwaysOnScreen.kt


/**
 * Number of pixels to offset the content rendered in the display to prevent screen burn-in.
 */
const val BURN_IN_OFFSET_PX = 10

fun Modifier.ambientBurnInProtection(ambientState: AmbientState, ambientUpdateTimestamp: Instant) = composed {
   padding(with(LocalDensity.current) { BURN_IN_OFFSET_PX.toDp() })
      .burnInTranslation(ambientState, ambientUpdateTimestamp)
}


/**
 * If the screen requires burn-in protection, items must be shifted around periodically
 * in ambient mode. To ensure that content isn't shifted off the screen, avoid placing
 * content within 10 pixels of the edge of the screen.
 *
 * Activities should also avoid solid white areas to prevent pixel burn-in. Both of
 * these requirements only apply in ambient mode, and only when
 * [AmbientState.Ambient.doBurnInProtection] is set to true.
 */
fun Modifier.burnInTranslation(
   ambientState: AmbientState,
   ambientUpdateTimestamp: Instant
): Modifier = composed {
   val translationX = rememberBurnInTranslation(ambientState, ambientUpdateTimestamp)
   val translationY = rememberBurnInTranslation(ambientState, ambientUpdateTimestamp)

   this.graphicsLayer {
      this.translationX = translationX
      this.translationY = translationY
   }
}

@Composable
fun rememberBurnInTranslation(
   ambientState: AmbientState,
   ambientUpdateTimestamp: Instant
): Float = remember(ambientState, ambientUpdateTimestamp) {
   when (ambientState) {
      AmbientState.Interactive -> 0f
      is AmbientState.Ambient -> if (ambientState.doBurnInProtection) {
         Random.nextInt(-BURN_IN_OFFSET_PX, BURN_IN_OFFSET_PX + 1).toFloat()
      } else {
         0f
      }
   }
}
