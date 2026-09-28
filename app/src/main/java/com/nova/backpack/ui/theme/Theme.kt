package com.nova.backpack.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.nova.backpack.data.AppSettings

/**
 * Applies theme colors, RTL/LTR direction and the central animation config for the
 * whole app in one place, driven entirely by AppSettings (Settings screen state).
 */
@Composable
fun NovaTheme(settings: AppSettings, content: @Composable () -> Unit) {
    val spec = if (settings.themeId == "custom" && settings.customAccentHex != null && settings.customBgHex != null) {
        val base = NovaThemes.byId("nova")
        base.copy(
            id = "custom",
            background = Color(android.graphics.Color.parseColor(settings.customBgHex)),
            accent = Color(android.graphics.Color.parseColor(settings.customAccentHex))
        )
    } else NovaThemes.byId(settings.themeId)

    val colorScheme = darkColorScheme(
        background = spec.background,
        surface = spec.surface,
        surfaceVariant = spec.surfaceAlt,
        primary = spec.accent,
        onBackground = spec.text,
        onSurface = spec.text,
        secondary = spec.accent,
    )

    val direction = if (settings.language == "fa") LayoutDirection.Rtl else LayoutDirection.Ltr
    val animConfig = NovaAnimationConfig(
        enabled = settings.animationsEnabled,
        reduceMotion = settings.reduceMotion,
        speed = settings.animationSpeed,
        pageOpenAnimation = settings.pageOpenAnimation
    )

    CompositionLocalProvider(
        LocalLayoutDirection provides direction,
        LocalNovaAnimation provides animConfig,
        LocalNovaThemeSpec provides spec
    ) {
        MaterialTheme(colorScheme = colorScheme, content = content)
    }
}

val LocalNovaThemeSpec = androidx.compose.runtime.compositionLocalOf { NovaThemes.byId("nova") }
