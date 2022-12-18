package com.mathematics.encoding.presentation.view

import android.os.Build
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStore
import com.mathematics.encoding.EncodingApplication
import com.mathematics.encoding.R
import com.mathematics.encoding.data.model.Settings
import com.mathematics.encoding.presentation.theme.EncodingAppTheme
import com.mathematics.encoding.presentation.theme.animate
import com.mathematics.encoding.presentation.viewmodel.SettingsViewModel

@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
internal fun AnimatedVisibilityScope.SettingsScreen(
    viewModel: SettingsViewModel,
    title: String,
    popBackStack: () -> Unit,
) {
    if (viewModel.showDialog) {
        viewModel.dialogContent()
    }

    val context = LocalContext.current


    RoundedCornersScreen(
        title = title,
        navigationIcon = {
            IconButton(onClick = popBackStack) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBackIosNew,
                    contentDescription = "back"
                )
            }
        },
        actionIcon = { modifier ->
            IconButton(
                onClick = {
                    viewModel.openDialog {
                        FeedbackDialog(closeDialog = viewModel::closeDialog) {
                            FeedbackButton(title = "Telegram") {
                                viewModel.openURL(context, "https://t.me/cepetroff")
                            }

                            FeedbackButton(title = "Вконтакте") {
                                viewModel.openURL(context, "https://vk.com/cepetroff")
                            }

                            FeedbackButton(title = "Почта") {
                                viewModel.openURL(context, "mailto:petrovsd2002@gmail.com")
                            }
                        }
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.Rounded.HelpOutline,
                    contentDescription = "help",
                    modifier = modifier
                )
            }
        }
    ) {
        Settings(viewModel)
    }
}




@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@Composable
private fun ColumnScope.Settings(viewModel: SettingsViewModel) {
    val settings by viewModel.currentSettings.observeAsState(Settings())

    Row(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .clickable {
                viewModel.openDialog {
                    ThemeDialog(
                        theme = settings.theme,
                        onDismiss = viewModel::closeDialog,
                        updateTheme = viewModel::updateTheme
                    )
                }
            }
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
                .padding(end = 5.dp)
        )
    }


//    SwitchItem(
//        text = {
//            Text(
//                text = stringResource(R.string.auto_input_symbols),
//                style = it
//            )
//
//            Text(
//                text = stringResource(R.string.auto_input_symbols_definition),
//                style = it.copy(
//                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
//                        .animate(),
//                    fontSize = 13.sp
//                )
//            )
//        },
//        checked = settings.autoInputProbabilities,
//        onCheckedChange = {
//            settingsViewModel.updateAutoInputProbabilities(it) //{ autoInputProbabilities = it }
//        },
//        enabled = isSwitchEnabled
//    )

//    AnimatedVisibility(
//        visible = !settings.autoInputProbabilities,
//        enter = scaleIn() + slideInVertically(),
//        exit = scaleOut() + slideOutVertically()
//    ) {
//        Row(
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxWidth()
//        ) {
//            Text(
//                text = "Стартовое количество элементов",
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.onBackground.animate(),
//                modifier = Modifier.weight(2f)
//            )
//
//            TextField(
//                value = settings.startCount.toString(),
//                onValueChange = {
//                    settingsViewModel.updateStartCount(it.toIntOrNull() ?: 2)
//                },
//                keyboardOptions = KeyboardOptions(
//                    keyboardType = KeyboardType.Number,
//                    imeAction = ImeAction.Done
//                ),
//                colors = TextFieldDefaults.textFieldColors(
//                    textColor = MaterialTheme.colorScheme.onBackground.animate(),
//                    containerColor = Color.Transparent
//                ),
//                textStyle = MaterialTheme.typography.bodyMedium,
//                singleLine = true,
//                modifier = Modifier.wrapContentWidth()
//            )
//        }
//    }





    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
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
                viewModel.updateDynamicColor(checked) //{ dynamicColor = checked }
            }
        )
    }
}





@Composable
private fun SwitchItem(
    text: @Composable ColumnScope.(TextStyle) -> Unit,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (checked: Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .clickable {
                if (enabled) onCheckedChange(!checked)
            }
            .background(MaterialTheme.colorScheme.background.animate())
            .padding(16.dp)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
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
            enabled = enabled,
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





@ExperimentalMaterial3Api
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Preview
@Composable
private fun SettingsPreview() {
    EncodingAppTheme {
        Column {
            Settings(
                viewModel = SettingsViewModel.getInstance(
                    owner = { ViewModelStore() },
                    factory = EncodingApplication().viewModelFactory,
                ),
            )
        }
    }
}


@Preview
@Composable
private fun SwitchItemPreview() {
    EncodingAppTheme {
        var checked by remember { mutableStateOf(false) }
        SwitchItem(
            text = { Text("Использовать динамические цвета") },
            checked = checked,
            onCheckedChange = { checked = it }
        )
    }
}