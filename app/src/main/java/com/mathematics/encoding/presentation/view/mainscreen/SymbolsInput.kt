package com.mathematics.encoding.presentation.view.mainscreen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mathematics.encoding.R
import com.mathematics.encoding.data.model.Settings
import com.mathematics.encoding.data.support.smoothScrollToItem
import com.mathematics.encoding.presentation.model.ObservableSymbol
import com.mathematics.encoding.presentation.theme.animate
import com.mathematics.encoding.presentation.view.ConfirmDialog
import com.mathematics.encoding.presentation.viewmodel.SymbolsViewModel

@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
internal fun AnimatedVisibilityScope.SymbolsInput(
    viewModel: SymbolsViewModel,
    settings: Settings,
    listState: LazyListState,
    clearResult: () -> Unit,
) {
    val symbols = viewModel.symbolsList
    val keyboardController = LocalSoftwareKeyboardController.current


    LaunchedEffect(key1 = viewModel.symbolsList.size) {
        listState.smoothScrollToItem(viewModel.symbolsList.lastIndex)
    }

    LaunchedEffect(key1 = viewModel.symbolsList.size, key2 = settings.startCount) {
        if (viewModel.symbolsList.size < settings.startCount) {
            val countItemsToAdd = settings.startCount - viewModel.symbolsList.size
            viewModel.symbolsList.addAll(Array(countItemsToAdd) { ObservableSymbol() })
        }
    }


    if (viewModel.showDialog) {
        ConfirmDialog(
            text = buildString {
                val name = symbols.getOrNull(viewModel.currentSymbolPosition)?.name
                val text =
                    if (name == null || name.isBlank()) "в ${viewModel.currentSymbolPosition + 1} строке"
                    else "«$name»"

                append("Вы собираетесь удалить элемент ")
                append("$text.")
                append("\nПродолжить?")
            },
            closeDialog = { viewModel.showDialog = false },
            onConfirm = {
                clearResult()
                viewModel.symbolsList.removeAt(viewModel.currentSymbolPosition)
            }
        )
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth()
    ) {
        itemsIndexed(symbols) { index, symbol ->
            EncodingItem(
                symbol = symbol,
                imeAction = if (index == symbols.lastIndex) ImeAction.Done else ImeAction.Next,
                modifier = Modifier
                    .combinedClickable(
                        onLongClick = {
                            if (symbols.size > settings.startCount) {
                                keyboardController?.hide()
                                viewModel.openDialog(index)
                            }
                        },
                        onDoubleClick = { viewModel.symbolsList[index].clear() },
                        onClick = {}
                    )
                    .animateEnterExit(
                        enter = slideInVertically(
                            tween(
                                durationMillis = 500,
                                easing = FastOutSlowInEasing,
                            ),
                        ) + fadeIn(),
                        exit = slideOutVertically(
                            tween(
                                durationMillis = 500,
                                easing = FastOutSlowInEasing
                            )
                        ) + fadeOut()
                    )
                    .animateContentSize(spring(stiffness = Spring.StiffnessLow))
                    .animateItemPlacement(tween(durationMillis = 200, easing = FastOutSlowInEasing))
            )
        }

        item {
            Column {
                IconButton(
                    onClick = {
                        clearResult()
                        viewModel.symbolsList.add(ObservableSymbol())
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onBackground.animate()
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 8.dp, top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "add symbol",
                        modifier = Modifier
                            .padding(4.dp)
                            .scale(1.5f)
                    )
                }

                Spacer(modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth())
            }
        }
    }

}





@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
private fun AnimatedVisibilityScope.EncodingItem(
    symbol: ObservableSymbol,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next
) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
    ) {
        OutlinedField(
            text = symbol.name,
            isError = symbol.hasNameError,
            placeholderText = stringResource(R.string.symbol),
            onTextChange = symbol::name::set,
            modifier = Modifier.animateEnterExit(
                enter = slideInHorizontally(
                    animationSpec = tween(
                        durationMillis = 300,
                        delayMillis = 10,
                        easing = FastOutSlowInEasing,
                    ),
                ) { it },
                exit = slideOutHorizontally(
                    animationSpec = tween(
                        durationMillis = 300,
                        delayMillis = 10,
                        easing = LinearOutSlowInEasing,
                    ),
                ) { it }
            ),
            errorMessage = symbol.nameErrorMessage
        )

        Text(
            "=",
            color = MaterialTheme.colorScheme.onBackground.animate(),
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .animateEnterExit(
                    enter = slideInVertically(spring(stiffness = Spring.StiffnessLow)),
                    exit = slideOutVertically(spring(stiffness = Spring.StiffnessLow)) { it }
                )
                .padding(horizontal = 16.dp)
        )

        OutlinedField(
            text = symbol.probabilityString,
            placeholderText = "Вероятность",
            isError = symbol.hasProbabilityError,
            keyboardType = KeyboardType.Decimal,
            imeAction = imeAction,
            onTextChange = symbol::probabilityString::set,
            modifier = Modifier.animateEnterExit(
                enter = slideInHorizontally(spring(stiffness = Spring.StiffnessLow)),
                exit = slideOutHorizontally(spring(stiffness = Spring.StiffnessLow))
            ),
            errorMessage = symbol.probabilityErrorMessage
        )
    }
}




@ExperimentalMaterial3Api
@Composable
private fun RowScope.OutlinedField(
    text: String,
    modifier: Modifier = Modifier,
    placeholderText: String = "",
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    errorMessage: String = "",
    onTextChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var showSupportingText by rememberSaveable { mutableStateOf(true) }

    OutlinedTextField(
        value = text,
        onValueChange = {
            onTextChange(it)
        },
        placeholder = {
            Text(
                text = placeholderText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = if (isError) 14.sp else 16.sp
                ),
                maxLines = 1
            )
        },
        trailingIcon = if (isError) {
            {
                IconToggleButton(
                    checked = showSupportingText,
                    onCheckedChange = {
                        showSupportingText = !showSupportingText
                    },
                    colors = IconButtonDefaults.iconToggleButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.error,
                        checkedContentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = if (showSupportingText) Icons.Rounded.Error else Icons.Rounded.ErrorOutline,
                        contentDescription = "error"
                    )
                }
            }
        } else null,
        supportingText = {
            AnimatedVisibility(
                visible = errorMessage.isNotBlank() && showSupportingText,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp
                    )
                )
            }
        },
        textStyle = MaterialTheme.typography.bodyMedium,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Next) },
            onDone = { focusManager.clearFocus() }
        ),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = MaterialTheme.colorScheme.onBackground.animate(),
            containerColor = MaterialTheme.colorScheme.primaryContainer.animate(),
            placeholderColor = MaterialTheme.colorScheme.onBackground
                .copy(alpha = 0.6f)
                .animate(),
            focusedBorderColor = MaterialTheme.colorScheme.primary.animate(),
            disabledBorderColor = MaterialTheme.colorScheme.primary.animate(),
            unfocusedBorderColor = MaterialTheme.colorScheme.primary.animate(),
            errorBorderColor = MaterialTheme.colorScheme.error.animate(),
            errorCursorColor = MaterialTheme.colorScheme.error.animate()
        ),
        isError = isError,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.weight(1f)
    )
}