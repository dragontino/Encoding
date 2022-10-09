package com.mathematics.encoding.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.LiveData
import com.mathematics.encoding.presentation.model.Settings
import com.mathematics.encoding.presentation.model.Themes
import com.mathematics.encoding.presentation.model.isDark

private val DarkColorScheme = darkColorScheme(
    primary = OrangeDark,
    secondary = PurpleGrey80,
    tertiary = CheckedThumbDark,
    tertiaryContainer = UncheckedThumbDark,
    background = BackgroundDark,
    onBackground = Color.White,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF39393C),
    error = Color.Red
)

private val LightColorScheme = lightColorScheme(
    primary = OrangeLight,
    secondary = PurpleGrey40,
    tertiary = CheckedThumbLight,
    tertiaryContainer = UncheckedThumbLight,
    background = Color.White,
    onBackground = Color.Black,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFFEDEEF3),
    error = Color(0xFFB00909)
)

@Composable
fun EncodingAppTheme(
    settings: LiveData<Settings>,
    statusBarColor: Color? = MaterialTheme.colorScheme.primary,
    content: @Composable (theme: Themes) -> Unit
) {
    val settingsState = settings.observeAsState(initial = Settings())
    val dynamicColor = settingsState.value.dynamicColor
    val isDarkTheme = settingsState.value.theme.isDark()

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDarkTheme)
                dynamicDarkColorScheme(context)
            else
                dynamicLightColorScheme(context)
        }
        isDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val statusBarColorArgb = (statusBarColor ?: colorScheme.primary).animate().toArgb()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = statusBarColorArgb
            if (!dynamicColor)
                WindowCompat.getInsetsController(window, view)
                    .isAppearanceLightStatusBars = !isDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = {
            content(settingsState.value.theme)
        }
    )
}


@Composable
fun Color.animate(stiffness: Float = Spring.StiffnessMediumLow) =
    animateColorAsState(
        targetValue = this,
        animationSpec = spring(stiffness = stiffness)
    ).value

@Deprecated("Use Color.animate() function instead", ReplaceWith("targetColor.animate()"))
@Composable
fun animateColor(
    targetColor: Color,
    stiffness: Float = Spring.StiffnessMediumLow
) = animateColorAsState(
    targetValue = targetColor,
    animationSpec = spring(stiffness = stiffness)
).value


val mediumCornerSize = 13.dp
val smallCornerSize = 6.dp


//var ColorScheme.checkedThumb: Color by mutableStateOf(CheckedThumbLight)
//var ColorScheme.uncheckedThumb: Color by mutableStateOf(UncheckedThumbLight)