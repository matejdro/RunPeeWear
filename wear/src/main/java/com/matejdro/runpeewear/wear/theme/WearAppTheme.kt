package com.matejdro.runpeewear.wear.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Typography
import com.matejdro.runpeewear.common.AppColors


@Composable
fun WearAppTheme(
   content: @Composable () -> Unit
) {
   val defaultTextStyle = Typography().body1.copy(
      textAlign = TextAlign.Center
   )

   MaterialTheme(
      colors = colorPalette,
      content = content,
      typography = Typography().copy(body1 = defaultTextStyle)
   )
}

private val colorPalette = Colors(
   primary = AppColors.primary,
   primaryVariant = AppColors.primaryDark,
   secondary = AppColors.primary,
   secondaryVariant = AppColors.primaryDark,
   error = AppColors.error,
   onPrimary = AppColors.onPrimary,
   onSecondary = AppColors.onPrimary,
   onError = AppColors.onError
)
