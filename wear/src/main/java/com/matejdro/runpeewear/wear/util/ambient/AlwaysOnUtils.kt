package com.matejdro.runpeewear.wear.util.ambient

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.wear.ambient.AmbientLifecycleObserver
import si.inova.kotlinova.core.activity.requireActivity
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
    val activity = LocalContext.current.requireActivity() as ComponentActivity
    val ambientCallbackController = remember { AmbientCallbackController() }


    DisposableEffect(ambientCallbackController) {
        val ambientLifecycleObserver =
            AmbientLifecycleObserver(activity, ambientCallbackController)

        ambientCallbackController.updateCallback = {
            updateCallback(ambientCallbackController.ambientUpdateTimestamp)
        }

        activity.lifecycle.addObserver(ambientLifecycleObserver)

        onDispose {
            ambientCallbackController.updateCallback = null
            activity.lifecycle.removeObserver(ambientLifecycleObserver)
        }
    }

    content(
        modifier.ambientBurnInProtection(
            ambientCallbackController.ambientState,
            ambientCallbackController.ambientUpdateTimestamp
        ),
        ambientCallbackController.ambientState, ambientCallbackController.ambientUpdateTimestamp
    )
}
