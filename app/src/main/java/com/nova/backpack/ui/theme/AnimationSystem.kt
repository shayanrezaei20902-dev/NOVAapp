package com.nova.backpack.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.runtime.compositionLocalOf

/**
 * Central animation system (per the spec: "one system, not hard-coded per screen").
 * Every screen reads durations/springs from here instead of hard-coding tween(280).
 * Changing Settings changes this once, and every screen picks it up automatically.
 */
data class NovaAnimationConfig(
    val enabled: Boolean = true,
    val reduceMotion: Boolean = false,
    val speed: Float = 1f,           // 0.5x .. 2x, from the Settings slider
    val pageOpenAnimation: String = "Spring"
) {
    /** Base duration for a standard transition, already adjusted for speed + reduce-motion + on/off. */
    val baseDurationMs: Int
        get() = when {
            !enabled -> 0
            reduceMotion -> (120 / speed).toInt().coerceAtLeast(1)
            else -> (280 / speed).toInt().coerceAtLeast(1)
        }

    fun <T> tween(): TweenSpec<T> = tween(durationMillis = baseDurationMs, easing = easingFor(pageOpenAnimation))

    fun <T> spring(): SpringSpec<T> = when (pageOpenAnimation) {
        "Spring", "Zoom", "Elastic" -> spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
        else -> spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
    }

    private fun easingFor(name: String): Easing = when (name) {
        "Smooth Fade" -> LinearOutSlowInEasing
        "Slide Up", "Slide Right", "Liquid" -> FastOutSlowInEasing
        "Zoom", "Spring" -> FastOutSlowInEasing
        "Blur Reveal" -> LinearOutSlowInEasing
        "Elastic" -> LinearOutSlowInEasing
        else -> FastOutSlowInEasing
    }
}

val LocalNovaAnimation = compositionLocalOf { NovaAnimationConfig() }
