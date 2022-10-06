package com.mathematics.encoding.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.mathematics.encoding.presentation.model.toNotNullableThemes

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

    val statusBarColor = animateColor(colorScheme.primary).toArgb()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = statusBarColor
            if (!dynamicColor)
                WindowCompat.getInsetsController(window, view)
                    .isAppearanceLightStatusBars = !isDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = {
            content(settingsState.value.theme.toNotNullableThemes())
        }
    )
}


@Composable
fun animateColor(
    targetColor: Color,
    animationSpec: AnimationSpec<Color> = spring(stiffness = Spring.StiffnessMediumLow)
) = animateColorAsState(targetValue = targetColor, animationSpec = animationSpec).value


val mediumCornerSize = 13.dp
val smallCornerSize = 6.dp


//var ColorScheme.checkedThumb: Color by mutableStateOf(CheckedThumbLight)
//var ColorScheme.uncheckedThumb: Color by mutableStateOf(UncheckedThumbLight)