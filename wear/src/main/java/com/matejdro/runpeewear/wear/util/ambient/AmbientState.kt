package com.matejdro.runpeewear.wear.util.ambient

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * A description of the current ambient state of the app.
 *
 * @author https://github.com/android/wear-os-samples/blob/main/AlwaysOnKotlin/compose/src/main/java/com/example/android/wearable/wear/alwayson/AmbientState.kt
 */
sealed interface AmbientState : Parcelable {

    /**
     * The app is interactive.
     */
    @Parcelize
    object Interactive : AmbientState

    /**
     * The app is in ambient mode, with the given parameters.
     */
    @Parcelize
    data class Ambient(
        /**
         * If the display is low-bit in ambient mode. i.e. it requires anti-aliased fonts.
         */
        val isLowBitAmbient: Boolean,

        /**
         * If the display requires burn-in protection in ambient mode, rendered pixels need to be
         * intermittently offset to avoid screen burn-in.
         */
        val doBurnInProtection: Boolean
    ) : AmbientState
}
