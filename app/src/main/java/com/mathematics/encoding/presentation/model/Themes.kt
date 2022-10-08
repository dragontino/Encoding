package com.mathematics.encoding.presentation.model

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

enum class Themes {
    Light,
    Dark,
    System;

    fun switch() = when(this) {
        Light -> Dark
        Dark -> Light
        System -> Light
    }
}


@Composable
internal fun Themes.isDark() = when (this) {
    Themes.Light -> false
    Themes.Dark -> true
    Themes.System -> isSystemInDarkTheme()
}
