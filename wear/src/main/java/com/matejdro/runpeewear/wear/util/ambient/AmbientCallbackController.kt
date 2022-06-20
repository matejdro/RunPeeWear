package com.matejdro.runpeewear.wear.util.ambient

import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.wear.ambient.AmbientModeSupport
import java.time.Clock
import java.time.Instant


/**
 * The [Clock] driving the time information. Overridable only for testing.
 */
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
internal var clock: Clock = Clock.systemDefaultZone()

val LocalAmbientCallbackController =
    compositionLocalOf<AmbientCallbackController> { throw IllegalStateException("Missing AmbientCallbackController") }

class AmbientCallbackController : AmbientModeSupport.AmbientCallback() {
    var updateCallback: (() -> Unit)? = null

    /**
     * A ticker state that increase whenever we get a call to `onUpdateAmbient`
     */
    var ambientUpdateTimestamp by mutableStateOf(Instant.now(clock))

    /**
     * The current [AmbientState].
     */
    var ambientState by mutableStateOf<AmbientState>(AmbientState.Interactive)

    /**
     * Prepares the UI for ambient mode.
     */
    override fun onEnterAmbient(ambientDetails: Bundle) {
        super.onEnterAmbient(ambientDetails)
        val isLowBitAmbient = ambientDetails.getBoolean(
            AmbientModeSupport.EXTRA_LOWBIT_AMBIENT,
            false
        )

        // Official method returns false even for Galaxy Watch 4, which has OLED screen??? Force at true just to be safe.
        val doBurnInProtection = true


        ambientState = AmbientState.Ambient(
            isLowBitAmbient = isLowBitAmbient,
            doBurnInProtection = doBurnInProtection
        )
    }

    override fun onUpdateAmbient() {
        super.onUpdateAmbient()
        ambientUpdateTimestamp = Instant.now(clock)
        updateCallback?.invoke()
    }

    /**
     * Restores the UI to active (non-ambient) mode.
     */
    override fun onExitAmbient() {
        super.onExitAmbient()
        ambientState = AmbientState.Interactive
    }
}
