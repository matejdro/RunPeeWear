package com.matejdro.runpeewear.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import com.matejdro.runpeewear.common.AppColors


@Composable
fun MobileAppTheme(
   content: @Composable () -> Unit
) {
   MaterialTheme(
      colors = colorPalette,
      content = content
   )
}

private val colorPalette = darkColors(
   primary = AppColors.primary,
   primaryVariant = AppColors.primaryDark,
   secondary = AppColors.primary,
   secondaryVariant = AppColors.primaryDark,
   error = AppColors.error,
   onPrimary = AppColors.onPrimary,
   onSecondary = AppColors.onPrimary,
   onError = AppColors.onError
)
