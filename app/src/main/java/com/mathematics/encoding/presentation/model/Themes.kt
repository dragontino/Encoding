package com.mathematics.encoding.presentation.model

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

enum class Themes {
    Light,
    Dark;

    val isDark get(): Boolean = when (this) {
        Light -> false
        Dark -> true
    }

    fun switch() = when (this) {
        Light -> Dark
        Dark -> Light
    }
}


@Composable
internal fun Themes?.toNotNullableThemes() = when {
    this != null -> this
    isSystemInDarkTheme() -> Themes.Dark
    else -> Themes.Light
}



@Composable
internal fun Themes?.isDark() = when (this) {
    null -> isSystemInDarkTheme()
    else -> this.isDark
}
