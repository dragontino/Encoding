package com.mathematics.encoding.data.model

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.TextFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

enum class Themes(val themeName: String, val icon: ImageVector) {
    Light("Светлая", Icons.Rounded.LightMode),
    Dark("Тёмная", Icons.Rounded.DarkMode),
    System("Автоматически", Icons.Rounded.TextFormat);
}


@Composable
internal fun Themes.isDark() = when (this) {
    Themes.Light -> false
    Themes.Dark -> true
    Themes.System -> isSystemInDarkTheme()
}
