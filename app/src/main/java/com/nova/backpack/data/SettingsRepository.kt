package com.nova.backpack.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "nova_settings")

/** Everything from the Settings screen — one central place, read by the whole app. */
data class AppSettings(
    val language: String = "fa",              // "fa" | "en"
    val themeId: String = "nova",
    val customAccentHex: String? = null,
    val customBgHex: String? = null,
    val liquidGlassEnabled: Boolean = false,
    val animationsEnabled: Boolean = true,
    val reduceMotion: Boolean = false,
    val hapticEnabled: Boolean = true,
    val animationSpeed: Float = 1f,           // 0.5x .. 2x
    val pageOpenAnimation: String = "Spring",
    val lockEnabled: Boolean = false,
    val pinHash: String? = null
)

class SettingsRepository(private val context: Context) {

    private object Keys {
        val LANGUAGE = stringPreferencesKey("language")
        val THEME_ID = stringPreferencesKey("theme_id")
        val CUSTOM_ACCENT = stringPreferencesKey("custom_accent")
        val CUSTOM_BG = stringPreferencesKey("custom_bg")
        val LIQUID_GLASS = booleanPreferencesKey("liquid_glass")
        val ANIM_ON = booleanPreferencesKey("anim_on")
        val REDUCE_MOTION = booleanPreferencesKey("reduce_motion")
        val HAPTIC = booleanPreferencesKey("haptic")
        val SPEED = floatPreferencesKey("anim_speed")
        val PAGE_OPEN = stringPreferencesKey("page_open_anim")
        val LOCK_ENABLED = booleanPreferencesKey("lock_enabled")
        val PIN_HASH = stringPreferencesKey("pin_hash")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { p ->
        AppSettings(
            language = p[Keys.LANGUAGE] ?: "fa",
            themeId = p[Keys.THEME_ID] ?: "nova",
            customAccentHex = p[Keys.CUSTOM_ACCENT],
            customBgHex = p[Keys.CUSTOM_BG],
            liquidGlassEnabled = p[Keys.LIQUID_GLASS] ?: false,
            animationsEnabled = p[Keys.ANIM_ON] ?: true,
            reduceMotion = p[Keys.REDUCE_MOTION] ?: false,
            hapticEnabled = p[Keys.HAPTIC] ?: true,
            animationSpeed = p[Keys.SPEED] ?: 1f,
            pageOpenAnimation = p[Keys.PAGE_OPEN] ?: "Spring",
            lockEnabled = p[Keys.LOCK_ENABLED] ?: false,
            pinHash = p[Keys.PIN_HASH]
        )
    }

    suspend fun setLanguage(lang: String) = context.dataStore.edit { it[Keys.LANGUAGE] = lang }
    suspend fun setTheme(id: String) = context.dataStore.edit { it[Keys.THEME_ID] = id; it.remove(Keys.CUSTOM_ACCENT); it.remove(Keys.CUSTOM_BG) }
    suspend fun setCustomTheme(accentHex: String, bgHex: String) = context.dataStore.edit {
        it[Keys.CUSTOM_ACCENT] = accentHex; it[Keys.CUSTOM_BG] = bgHex; it[Keys.THEME_ID] = "custom"
    }
    suspend fun setLiquidGlass(on: Boolean) = context.dataStore.edit { it[Keys.LIQUID_GLASS] = on }
    suspend fun setAnimationsEnabled(on: Boolean) = context.dataStore.edit { it[Keys.ANIM_ON] = on }
    suspend fun setReduceMotion(on: Boolean) = context.dataStore.edit { it[Keys.REDUCE_MOTION] = on }
    suspend fun setHaptic(on: Boolean) = context.dataStore.edit { it[Keys.HAPTIC] = on }
    suspend fun setSpeed(speed: Float) = context.dataStore.edit { it[Keys.SPEED] = speed }
    suspend fun setPageOpenAnimation(name: String) = context.dataStore.edit { it[Keys.PAGE_OPEN] = name }
    suspend fun setLock(enabled: Boolean, pinHash: String?) = context.dataStore.edit {
        it[Keys.LOCK_ENABLED] = enabled
        if (pinHash != null) it[Keys.PIN_HASH] = pinHash
    }
}
