package com.matejdro.runpeewear.wear.util

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import logcat.logcat
import kotlin.math.sqrt

/**
 * Add extra vertical padding to allow content to scroll fully to the screen center
 */
@Stable
public fun Modifier.roundVerticalPadding(): Modifier = composed() {
   val isRound = LocalConfiguration.current.isScreenRound
   var inset: Dp = 0.dp
   if (isRound) {
      val screenHeightDp = LocalConfiguration.current.screenHeightDp
      val screenWidthDp = LocalConfiguration.current.smallestScreenWidthDp
      val maxSquareEdge = (sqrt(((screenHeightDp * screenWidthDp) / 2).toDouble()))
      inset = Dp(((screenHeightDp - maxSquareEdge) / 2).toFloat())
   }
   logcat { "Inset $inset" }
   padding(vertical = inset)
}
