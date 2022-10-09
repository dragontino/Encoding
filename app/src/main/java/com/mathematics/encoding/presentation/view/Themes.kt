package com.mathematics.encoding.presentation.view

import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.liveData
import com.mathematics.encoding.presentation.model.Settings
import com.mathematics.encoding.presentation.model.Themes
import com.mathematics.encoding.presentation.theme.EncodingAppTheme
import com.mathematics.encoding.presentation.theme.animate

@Composable
fun ColumnScope.Themes(currentTheme: Themes, onThemeChange: (Themes) -> Unit) {
    for (theme in Themes.values()) {
        ThemeItem(
            icon = theme.icon,
            text = theme.themeName,
            isSelected = theme == currentTheme,
            iconScale = if (theme == Themes.System) 1.2f else 0.9f
        ) {
            onThemeChange(theme)
        }
    }
}


@Composable
private fun ColumnScope.ThemeItem(
    icon: ImageVector,
    text: String,
    isSelected: Boolean,
    iconScale: Float = 1f,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.background

    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(color = backgroundColor.animate(stiffness = Spring.StiffnessMedium))
            .align(Alignment.CenterHorizontally)
            .fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = MaterialTheme.colorScheme.primary.animate(),
            modifier = Modifier
                .scale(iconScale)
                .padding(vertical = 16.dp, horizontal = 8.dp)
        )

        Text(
            text = text,
            color = MaterialTheme.colorScheme.onBackground.animate(),
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


@Preview
@Composable
private fun ThemesPreview() {
    EncodingAppTheme(settings = liveData { emit(Settings()) }) {
        var theme by remember { mutableStateOf(it) }

        Column {
            Themes(
                currentTheme = theme,
                onThemeChange = { theme = it })
        }
    }

}


@Preview
@Composable
private fun ThemeItemPreview() {
    EncodingAppTheme(settings = liveData { emit(Settings()) }) {
        Column {
            ThemeItem(icon = Icons.Rounded.LightMode, text = "Светлая тема", isSelected = false) { }
            ThemeItem(icon = Icons.Rounded.DarkMode, text = "Темная тема", isSelected = true) { }
        }
    }

}