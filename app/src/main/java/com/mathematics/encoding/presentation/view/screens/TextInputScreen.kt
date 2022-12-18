package com.mathematics.encoding.presentation.view.screens

import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mathematics.encoding.data.support.mainScreenTabsAnimationSpec
import com.mathematics.encoding.presentation.theme.animate
import com.mathematics.encoding.presentation.viewmodel.TextInputViewModel

@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
@Composable
internal fun AnimatedVisibilityScope.TextInputScreen(
    viewModel: TextInputViewModel,
    clearResult: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    OrientatedTextInputScreen(
        textField = { modifier ->
            OutlinedTextField(
                value = viewModel.text,
                onValueChange = {
                    viewModel.text = it
                    viewModel.checkInputText(context)
                    clearResult()
                },
                placeholder = {
                    Text(
                        "Введите текст...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                supportingText = {
                    AnimatedVisibility(
                        visible = viewModel.errorMessage.isNotBlank()
                                && viewModel.isError
                                && viewModel.showErrorMessage,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Text(
                            text = viewModel.errorMessage,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp
                            )
                        )
                    }
                },
                trailingIcon = if (viewModel.isError) {
                    {
                        IconToggleButton(
                            checked = viewModel.showErrorMessage,
                            onCheckedChange = {
                                viewModel.showErrorMessage = !viewModel.showErrorMessage
                            },
                            colors = IconButtonDefaults.iconToggleButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.error,
                                checkedContentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                imageVector = if (viewModel.showErrorMessage) {
                                    Icons.Rounded.Error
                                } else {
                                    Icons.Rounded.ErrorOutline
                                },
                                contentDescription = "error"
                            )
                        }
                    }
                } else null,
                shape = MaterialTheme.shapes.medium,
                isError = viewModel.isError,
                textStyle = MaterialTheme.typography.bodyMedium,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = MaterialTheme.colorScheme.onBackground.animate(),
                    placeholderColor = MaterialTheme.colorScheme.onBackground.animate(),
                    cursorColor = MaterialTheme.colorScheme.primary.animate(),
                    focusedBorderColor = MaterialTheme.colorScheme.primary.animate(),
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.animate(),
                    errorBorderColor = MaterialTheme.colorScheme.error.animate(),
                    errorSupportingTextColor = MaterialTheme.colorScheme.error.animate(),
                    errorTrailingIconColor = MaterialTheme.colorScheme.error.animate()
                ),
                modifier = modifier.clickable {
                    if (viewModel.isKeyboardShowing) {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    } else {
                        keyboardController?.show()
                    }
                    viewModel.isKeyboardShowing = !viewModel.isKeyboardShowing
                }
            )
        },
        checkBox = { modifier ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
            ) {
                Checkbox(
                    checked = viewModel.considerGap,
                    onCheckedChange = {
                        clearResult()
                        viewModel.updateConsiderGap(it)
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary.animate(),
                        checkmarkColor = MaterialTheme.colorScheme.onPrimary.animate(),
                        uncheckedColor = MaterialTheme.colorScheme.onBackground.animate()
                    ),
                    modifier = Modifier.scale(1.1f)
                )

                Text(
                    "Учитывать пробел",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground.animate(),
                        fontSize = 17.sp
                    ),
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
    )
}



@ExperimentalAnimationApi
@Composable
private fun AnimatedVisibilityScope.OrientatedTextInputScreen(
    textField: @Composable (Modifier) -> Unit,
    checkBox: @Composable (Modifier) -> Unit,
) {
    val orientation = LocalConfiguration.current.orientation

    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
        ) {
            textField(
                Modifier
                    .animateEnterExit(
                        enter = slideInVertically(
                            animationSpec = mainScreenTabsAnimationSpec()
                        ),
                        exit = slideOutVertically(
                            animationSpec = mainScreenTabsAnimationSpec()
                        )
                    )
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            checkBox(
                Modifier
                    .animateEnterExit(
                        enter = slideInVertically(
                            animationSpec = mainScreenTabsAnimationSpec(),
                        ) { it },
                        exit = slideOutVertically(
                            animationSpec = mainScreenTabsAnimationSpec(),
                        ) { it },
                    )
                    .fillMaxWidth(),
            )
        }
    } else {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            textField(
                Modifier
                    .animateEnterExit(
                        enter = slideInVertically(
                            animationSpec = mainScreenTabsAnimationSpec(),
                        ),
                        exit = slideOutVertically(
                            animationSpec = mainScreenTabsAnimationSpec(),
                        ) + slideOutHorizontally(
                            animationSpec = mainScreenTabsAnimationSpec()
                        ) { it },
                    )
                    .weight(2f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            checkBox(
                Modifier
                    .animateEnterExit(
                        enter = slideInVertically(
                            animationSpec = mainScreenTabsAnimationSpec(),
                        ) + slideInHorizontally(
                            animationSpec = mainScreenTabsAnimationSpec()
                        ) { it / 2 },
                        exit = slideOutVertically(
                            animationSpec = mainScreenTabsAnimationSpec(),
                        ) + slideOutHorizontally(
                            animationSpec = mainScreenTabsAnimationSpec()
                        ),
                    )
            )
        }
    }
}



@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@Preview
@Composable
private fun TextInputPreview() {
    AnimatedVisibility(visible = true, modifier = Modifier.background(Color.White)) {
        TextInputScreen(
            viewModel = viewModel(modelClass = TextInputViewModel::class.java),
            clearResult = {},
        )
    }
}