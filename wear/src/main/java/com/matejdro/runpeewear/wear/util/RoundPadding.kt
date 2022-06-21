package com.matejdro.runpeewear.wear.util

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt

/**
 * Add extra vertical padding to allow content to scroll fully to the screen center
 */
@Stable
fun Modifier.roundVerticalPadding(): Modifier = composed() {
   val isRound = LocalConfiguration.current.isScreenRound
   var inset: Dp = 0.dp
   if (isRound) {
      val screenHeightDp = LocalConfiguration.current.screenHeightDp
      val screenWidthDp = LocalConfiguration.current.smallestScreenWidthDp
      val maxSquareEdge = (sqrt(((screenHeightDp * screenWidthDp) / 2).toDouble()))
      inset = Dp(((screenHeightDp - maxSquareEdge) / 2).toFloat())
   }
   padding(top = inset, bottom = inset * 2)
}

/**
 * Add extra horizontal padding to account for the watch's roundness
 */
@Stable
fun Modifier.roundHorizontalPadding(): Modifier = composed() {
   val isRound = LocalConfiguration.current.isScreenRound
   var inset: Dp = 0.dp
   if (isRound) {
      val screenHeightDp = LocalConfiguration.current.screenHeightDp
      val screenWidthDp = LocalConfiguration.current.smallestScreenWidthDp
      val maxSquareEdge = (sqrt(((screenHeightDp * screenWidthDp) / 2).toDouble()))
      inset = Dp(((screenHeightDp - maxSquareEdge) / 2).toFloat())
   }
   padding(horizontal = inset)
}

/**
 * Add extra vertical padding to the top to account for the watch's roundness
 */
@Stable
fun Modifier.roundTopPadding(): Modifier = composed() {
   val isRound = LocalConfiguration.current.isScreenRound
   var inset: Dp = 0.dp
   if (isRound) {
      val screenHeightDp = LocalConfiguration.current.screenHeightDp
      val screenWidthDp = LocalConfiguration.current.smallestScreenWidthDp
      val maxSquareEdge = (sqrt(((screenHeightDp * screenWidthDp) / 2).toDouble()))
      inset = Dp(((screenHeightDp - maxSquareEdge) / 2).toFloat())
   }
   padding(top = inset)
}
