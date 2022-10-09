package com.mathematics.encoding.presentation.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.liveData
import com.mathematics.encoding.EncodingApplication
import com.mathematics.encoding.R
import com.mathematics.encoding.presentation.model.Settings
import com.mathematics.encoding.presentation.model.Themes
import com.mathematics.encoding.presentation.theme.EncodingAppTheme
import com.mathematics.encoding.presentation.theme.animate
import com.mathematics.encoding.presentation.theme.mediumCornerSize
import com.mathematics.encoding.presentation.viewmodel.SettingsViewModel

@ExperimentalAnimationApi
@Composable
fun ColumnScope.Settings(settingsViewModel: SettingsViewModel) {
    val settings by settingsViewModel.currentSettings.observeAsState(Settings())
    var showDialog by remember { mutableStateOf(false) }

    val openDialog = {
        showDialog = true
    }

    val closeDialog = {
        showDialog = false
    }


    AnimatedVisibility(visible = showDialog) {
        ThemeDialog(
            theme = settings.theme,
            onDismiss = closeDialog,
            updateTheme = settingsViewModel::updateTheme
        )
    }


    Row(
        modifier = Modifier
            .clickable(onClick = openDialog)
            .background(MaterialTheme.colorScheme.background.animate())
            .padding(horizontal = 16.dp)
            .padding(top = 20.dp, bottom = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Тема приложения",
            color = MaterialTheme.colorScheme.onBackground.animate(),
            style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Justify),
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Rounded.Palette,
            contentDescription = "palette",
            tint = MaterialTheme.colorScheme.primary.animate(),
            modifier = Modifier
                .scale(1.2f)
                .padding(end = 8.dp)
        )
    }


    SwitchItem(
        text = {
            Text(
                text = stringResource(R.string.auto_input_symbols),
                style = it
            )

            Text(
                text = stringResource(R.string.auto_input_symbols_definition),
                style = it.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        .animate(),
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
                text = stringResource(R.string.dynamic_color),
                style = it
            )

            Text(
                text = stringResource(R.string.dynamic_color_definition),
                style = it.copy(
                    color = MaterialTheme.colorScheme.onBackground
                        .copy(alpha = 0.7f)
                        .animate(),
                    fontSize = 13.sp
                )
            )
        },
        checked = settings.dynamicColor,
        onCheckedChange = { checked ->
            settingsViewModel.updateDynamicColor(checked) //{ dynamicColor = checked }
        }
    )

    // TODO: смена стартового количества элементов
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
            .background(MaterialTheme.colorScheme.background.animate())
            .padding(16.dp)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
//                .padding(vertical = 16.dp)
                .padding(start = 4.dp, end = 20.dp)
                .weight(2f),
        ) {
            text(
                MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.animate(),
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
                checkedThumbColor = MaterialTheme.colorScheme.tertiary.animate(),
                checkedIconColor = MaterialTheme.colorScheme.primary.animate(),
                checkedTrackColor = MaterialTheme.colorScheme.primary.animate(),
                uncheckedTrackColor = MaterialTheme.colorScheme.primaryContainer.animate(),
                uncheckedThumbColor = MaterialTheme.colorScheme.tertiaryContainer.animate(),
                uncheckedBorderColor = MaterialTheme.colorScheme.primary.animate(),
                uncheckedIconColor = MaterialTheme.colorScheme.background.animate()
            )
        )
    }
}





@Composable
private fun ThemeDialog(
    theme: Themes,
    onDismiss: () -> Unit,
    updateTheme: (Themes) -> Unit
) {
    val tempTheme by remember { mutableStateOf(theme) }

    AlertDialog(
        onDismissRequest = {
            onDismiss()
            if (theme != tempTheme)
                updateTheme(tempTheme)
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary.animate()
                )
            ) {
                Text("Сохранить", style = MaterialTheme.typography.bodyMedium)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                    if (theme != tempTheme)
                        updateTheme(tempTheme)
                },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary.animate()
                )
            ) {
                Text("Отменить", style = MaterialTheme.typography.bodyMedium)
            }
        },
        title = {
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background.animate())
                    .padding(top = 16.dp, bottom = 8.dp, start = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Темы",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground.animate(),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(2f)
                )

                Icon(
                    imageVector = Icons.Rounded.Palette,
                    contentDescription = "palette",
                    tint = MaterialTheme.colorScheme.primary.animate(),
                    modifier = Modifier
                        .scale(1.2f)
                        .padding(end = 12.dp)
                )
            }

            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = colorResource(android.R.color.darker_gray),
                thickness = 1.2.dp
            )
        },
        text = {
            Column {
                Themes(theme, updateTheme)
            }
        },
        shape = RoundedCornerShape(mediumCornerSize),
        modifier = Modifier
            .border(
                width = 1.2.dp,
                color = MaterialTheme.colorScheme.primary.animate(),
                shape = RoundedCornerShape(mediumCornerSize)
            ),
        containerColor = MaterialTheme.colorScheme.background.animate(),
        textContentColor = MaterialTheme.colorScheme.onBackground.animate()
    )
}




@ExperimentalAnimationApi
@Preview
@Composable
private fun SettingsPreview() {
    EncodingAppTheme(settings = liveData { emit(Settings()) }) {
        Column {
            Settings(
                settingsViewModel = SettingsViewModel.getInstance(
                    owner = { ViewModelStore() },
                    factory = EncodingApplication().settingsViewModelFactory,
                ),
            )
        }
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