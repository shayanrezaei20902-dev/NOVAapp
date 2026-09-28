package com.nova.backpack.ui.theme

import androidx.compose.ui.graphics.Color

/** One of the 10 ready-made themes. Each swaps five real colors, not just one accent. */
data class NovaThemeSpec(
    val id: String,
    val label: String,
    val background: Color,
    val surface: Color,
    val surfaceAlt: Color,
    val text: Color,
    val subText: Color,
    val accent: Color
)

object NovaThemes {
    val builtIn = listOf(
        NovaThemeSpec("nova",   "NOVA",   Color(0xFF0E0F13), Color(0xFF171922), Color(0xFF1F2230), Color(0xFFEEF0F6), Color(0xFF9AA0B4), Color(0xFF7C8CFF)),
        NovaThemeSpec("sunset", "Sunset", Color(0xFF1C1210), Color(0xFF231815), Color(0xFF2B1E19), Color(0xFFF3E9E4), Color(0xFFBBA79E), Color(0xFFFF8C6B)),
        NovaThemeSpec("forest", "Forest", Color(0xFF0E1712), Color(0xFF16211B), Color(0xFF1C2A22), Color(0xFFE9F3ED), Color(0xFF9BB5A8), Color(0xFF5FD39A)),
        NovaThemeSpec("rose",   "Rose",   Color(0xFF1A1015), Color(0xFF23161C), Color(0xFF2C1B22), Color(0xFFF5E9EE), Color(0xFFB79AA5), Color(0xFFFF6FA5)),
        NovaThemeSpec("ocean",  "Ocean",  Color(0xFF0B141C), Color(0xFF122232), Color(0xFF162B3E), Color(0xFFE7F1F8), Color(0xFF93AFC2), Color(0xFF4BC8FF)),
        NovaThemeSpec("gold",   "Gold",   Color(0xFF161207), Color(0xFF20190C), Color(0xFF2A2010), Color(0xFFF5EFDD), Color(0xFFBBAE87), Color(0xFFF2C14E)),
        NovaThemeSpec("grape",  "Grape",  Color(0xFF150F1C), Color(0xFF201628), Color(0xFF291D33), Color(0xFFEEE7F5), Color(0xFFA997B8), Color(0xFFB57CFF)),
        NovaThemeSpec("mono",   "Mono",   Color(0xFF101010), Color(0xFF1A1A1A), Color(0xFF232323), Color(0xFFE6E6E6), Color(0xFF9A9A9A), Color(0xFFE6E6E6)),
        NovaThemeSpec("mint",   "Mint",   Color(0xFF0D1614), Color(0xFF14201D), Color(0xFF192824), Color(0xFFE6F5F1), Color(0xFF8FB8AE), Color(0xFF3FE0C5)),
        NovaThemeSpec("cream",  "Cream",  Color(0xFFF4F1EA), Color(0xFFFFFFFF), Color(0xFFEFEADF), Color(0xFF1C1A16), Color(0xFF7A7367), Color(0xFFD97757)),
    )

    fun byId(id: String) = builtIn.find { it.id == id } ?: builtIn.first()
}
