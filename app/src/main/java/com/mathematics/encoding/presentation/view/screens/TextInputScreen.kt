package com.mathematics.encoding.presentation.view.screens

import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mathematics.encoding.data.support.mainScreenTabsAnimationSpec
import com.mathematics.encoding.presentation.theme.animate
import com.mathematics.encoding.presentation.theme.mediumCornerSize
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

    OrientatedTextInputScreen(
        textField = { modifier ->
            OutlinedTextField(
                value = viewModel.text,
                onValueChange = {
                    viewModel.text = it
                    clearResult()
                },
                placeholder = {
                    Text(
                        "Введите текст...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                shape = RoundedCornerShape(mediumCornerSize),
                textStyle = MaterialTheme.typography.bodyMedium,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = MaterialTheme.colorScheme.onBackground.animate(),
                    placeholderColor = MaterialTheme.colorScheme.onBackground.animate(),
                    cursorColor = MaterialTheme.colorScheme.primary.animate(),
                    focusedBorderColor = MaterialTheme.colorScheme.primary.animate(),
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.animate()
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