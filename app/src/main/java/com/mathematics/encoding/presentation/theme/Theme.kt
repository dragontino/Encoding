package com.mathematics.encoding.presentation.theme

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mathematics.encoding.EncodingApplication
import com.mathematics.encoding.data.model.Settings
import com.mathematics.encoding.data.model.Themes
import com.mathematics.encoding.data.model.isDark
import com.mathematics.encoding.data.support.getActivity
import com.mathematics.encoding.presentation.viewmodel.SettingsViewModel

private val DarkColorScheme = darkColorScheme(
    primary = OrangeDark,
    secondary = PurpleGrey80,
    tertiary = CheckedThumbDark,
    tertiaryContainer = UncheckedThumbDark,
    background = BackgroundDark,
    onBackground = Color.White,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF39393C),
    error = Color(0xFFD93232)
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
fun EncodingAppTheme(content: @Composable (theme: Themes) -> Unit) {

    val application = LocalContext.current.getActivity()?.application as EncodingApplication?
    val settingsViewModel = viewModel<SettingsViewModel>(factory = application?.viewModelFactory)
    val settings by settingsViewModel.currentSettings.observeAsState(initial = Settings())
    val dynamicColor = settings.dynamicColor
    val isDarkTheme = settings.theme.isDark()

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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = {
            content(settings.theme)
        }
    )
}


@Composable
fun Color.animate(durationMills: Int = 600) =
    animateColorAsState(
        targetValue = this,
        animationSpec = tween(durationMills, easing = FastOutSlowInEasing)
    ).value