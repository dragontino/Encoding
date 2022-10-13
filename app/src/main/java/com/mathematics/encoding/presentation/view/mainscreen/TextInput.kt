package com.mathematics.encoding.presentation.view.mainscreen

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mathematics.encoding.presentation.theme.animate
import com.mathematics.encoding.presentation.theme.mediumCornerSize

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
internal fun AnimatedVisibilityScope.TextInput(
    considerGap: Boolean,
    updateConsiderGap: (Boolean) -> Unit,
    onTextChange: (String) -> Unit,
    onCheckedChange: (Boolean) -> Unit
) {
    var text by rememberSaveable { mutableStateOf("") }
    var isShowKeyboard by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current

    val showKeyboard = {
        keyboardController?.show()
        isShowKeyboard = true
    }

    val hideKeyboard = {
        keyboardController?.hide()
        isShowKeyboard = false
    }

    Column(
        Modifier
            .fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = {
                onTextChange(it)
                text = it
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
            modifier = Modifier
                .clickable {
                    if (isShowKeyboard)
                        hideKeyboard()
                    else
                        showKeyboard()
                }
                .animateEnterExit(
                    enter = slideInVertically(spring(stiffness = Spring.StiffnessLow)),
                    exit = slideOutVertically(spring(stiffness = Spring.StiffnessLow))
                )
                .padding(bottom = 4.dp, top = 16.dp)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .animateEnterExit(
                    enter = slideInVertically(spring(stiffness = Spring.StiffnessLow)) { it },
                    exit = slideOutVertically(spring(stiffness = Spring.StiffnessLow)) { it }
                )
                .padding(bottom = 16.dp, top = 4.dp)
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
        ) {
            Checkbox(
                checked = considerGap,
                onCheckedChange = {
                    onCheckedChange(it)
                    updateConsiderGap(it)
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary.animate(),
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary.animate(),
                    uncheckedColor = MaterialTheme.colorScheme.onBackground.animate()
                ),
                modifier = Modifier
                    .scale(1.1f)
                    .padding(start = 8.dp)
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

//        TextButton(
//            onClick = {
//                hideKeyboard()
////                calculateCodes(text)
//            },
//            shape = RoundedCornerShape(mediumCornerSize),
//            colors = ButtonDefaults.textButtonColors(
//                containerColor = MaterialTheme.colorScheme.primary.animate(),
//                contentColor = MaterialTheme.colorScheme.onPrimary.animate()
//            ),
//            modifier = Modifier
//                .padding(horizontal = 8.dp, vertical = 16.dp)
//                .fillMaxWidth(),
//            border = BorderStroke(
//                1.2.dp,
//                MaterialTheme.colorScheme.onBackground.animate()
//            ),
//        ) {
//            Text(
//                text = stringResource(R.string.calculate_codes),
//                fontSize = 16.sp,
//                textAlign = TextAlign.Center
//            )
//        }
    }
}


@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@Preview
@Composable
private fun TextInputPreview() {
    AnimatedVisibility(visible = true, modifier = Modifier.background(Color.White)) {
        TextInput(
            considerGap = true,
            updateConsiderGap = {},
            onTextChange = {},
            onCheckedChange = {}
//            onValueChange = {},
//            calculateCodes = {}
        )
    }
}