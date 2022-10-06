package com.mathematics.encoding.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.liveData
import com.mathematics.encoding.R
import com.mathematics.encoding.presentation.model.Settings
import com.mathematics.encoding.presentation.model.Themes
import com.mathematics.encoding.presentation.theme.EncodingAppTheme
import com.mathematics.encoding.presentation.theme.animateColor
import com.mathematics.encoding.presentation.viewmodel.SettingsViewModel

@Composable
fun Settings(settingsViewModel: SettingsViewModel) {
    val settings by settingsViewModel.currentSettings.observeAsState(Settings())

    Column {
        // TODO: смена темы

        SwitchItem(
            text = stringResource(R.string.use_dynamic_color),
            checked = settings.dynamicColor,
            onCheckedChange = {
                settingsViewModel.updateSettings { dynamicColor = it }
            }
        )

        // TODO: смена стартого количества элементов
    }
}


@Composable
private fun SwitchItem(
    text: String,
    checked: Boolean,
    onCheckedChange: (checked: Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .clickable { onCheckedChange(!checked) }
            .background(animateColor(MaterialTheme.colorScheme.background))
            .padding(16.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = text,
            color = animateColor(MaterialTheme.colorScheme.onBackground),
            textAlign = TextAlign.Start,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .padding(start = 4.dp, end = 20.dp)
                .weight(2f)
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            thumbContent = {
                Icon(
                    imageVector = if (checked) Icons.Rounded.Check else Icons.Rounded.Close,
                    contentDescription = "",
                    modifier = Modifier.scale(0.7f)
                )
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = animateColor(MaterialTheme.colorScheme.tertiary),
                checkedIconColor = animateColor(MaterialTheme.colorScheme.primary),
                checkedTrackColor = animateColor(MaterialTheme.colorScheme.primary),
                uncheckedTrackColor = animateColor(MaterialTheme.colorScheme.primaryContainer),
                uncheckedThumbColor = animateColor(MaterialTheme.colorScheme.tertiaryContainer),
                uncheckedBorderColor = animateColor(MaterialTheme.colorScheme.primary),
                uncheckedIconColor = animateColor(MaterialTheme.colorScheme.background)
            )
        )
    }
}


@Preview
@Composable
private fun SwitchItemPreview() {
    EncodingAppTheme(settings = liveData { Settings(theme = Themes.Dark) }) {
        var checked by remember { mutableStateOf(false) }
        SwitchItem(text = "Использовать динамические цвета", checked = checked, onCheckedChange = { checked = it })
    }
}