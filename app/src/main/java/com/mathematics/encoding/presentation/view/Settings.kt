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
import androidx.compose.ui.text.TextStyle
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
            text = {
                Text(
                    text = "Автоввод символов из текста",
                    style = it
                )

                Text(
                    text = "Если включён, вероятности символов высчитаются автоматически из введённого текста",
                    style = it.copy(
                        color = animateColor(
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        ),
                        fontSize = 13.sp
                    )
                )
            },
            checked = settings.autoInputProbabilities,
            onCheckedChange = {
                settingsViewModel.updateAutoInputProbabilities(it) //{ autoInputProbabilities = it }
            }
        )

        SwitchItem(
            text = {
                Text(
                    text = stringResource(R.string.use_dynamic_color),
                    style = it
                )
            },
            checked = settings.dynamicColor,
            onCheckedChange = { checked ->
                settingsViewModel.updateDynamicColor(checked) //{ dynamicColor = checked }
            }
        )

        // TODO: смена стартового количества элементов
    }
}


@Composable
private fun SwitchItem(
    text: @Composable ColumnScope.(TextStyle) -> Unit,
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
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .padding(start = 4.dp, end = 20.dp)
                .weight(2f),
        ) {
            text(
                MaterialTheme.typography.bodyMedium.copy(
                    color = animateColor(MaterialTheme.colorScheme.onBackground),
                    textAlign = TextAlign.Start
                )
            )
        }

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
        SwitchItem(
            text = { Text("Использовать динамические цвета") },
            checked = checked,
            onCheckedChange = { checked = it }
        )
    }
}