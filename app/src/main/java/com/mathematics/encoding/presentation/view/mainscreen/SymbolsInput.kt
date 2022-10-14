package com.mathematics.encoding.presentation.view.mainscreen

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mathematics.encoding.R
import com.mathematics.encoding.presentation.model.Symbol
import com.mathematics.encoding.presentation.model.emptySymbolNameMessage
import com.mathematics.encoding.presentation.model.incorrectSymbolProbabilityMessage
import com.mathematics.encoding.presentation.theme.RobotoFont
import com.mathematics.encoding.presentation.theme.animate
import com.mathematics.encoding.presentation.view.ConfirmDialog

@ExperimentalMaterial3Api
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
internal fun AnimatedVisibilityScope.SymbolsInput(
    listState: LazyListState,
    symbols: Array<Symbol>,
    startCount: Int,
    clearResult: () -> Unit,
    deleteSymbol: (index: Int) -> Unit,
    addSymbol: () -> Unit,
    clearSymbol: (index: Int) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    EncodingItems(
        state = listState,
        symbols = symbols,
        startCount = startCount,
        clearResult = clearResult,
        deleteSymbol = deleteSymbol,
        clearSymbol = clearSymbol,
        hideKeyboard = { keyboardController?.hide() },
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Column {
            IconButton(
                onClick = addSymbol,
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


@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
private fun AnimatedVisibilityScope.EncodingItems(
    symbols: Array<Symbol>,
    startCount: Int,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    clearResult: () -> Unit = {},
    deleteSymbol: (index: Int) -> Unit = {},
    clearSymbol: (index: Int) -> Unit = {},
    hideKeyboard: () -> Unit = {},
    itemsAfter: @Composable LazyItemScope.() -> Unit = {}
) {
    var indexOfSymbol by rememberSaveable { mutableStateOf(-1) }
    var showDialog by remember { mutableStateOf(false) }

    fun badNameMessage(name: String, index: Int): String = when {
        name.isBlank() -> emptySymbolNameMessage(index)
        symbols
            .indexOfFirst { it.name == name }
            .let { it != index && it != -1 } -> "Символ «$name» уже существует!"
        else -> ""
    }

    fun badProbabilityMessage(probability: Double, index: Int): String =
        if (probability < 0 || probability > 1)
            incorrectSymbolProbabilityMessage(index)
        else ""


    AnimatedVisibility(
        visible = showDialog,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        ConfirmDialog(
            text = buildString {
                val name = symbols.getOrNull(indexOfSymbol)?.name
                val text =
                    if (name == null || name.isBlank()) "на позиции «${indexOfSymbol + 1}»"
                    else "«$name»"

                append("Вы собираетесь удалить элемент ")
                append("$text.")
                append("\nПродолжить?")
            },
            closeDialog = { showDialog = false },
            onConfirm = {
                showDialog = false
                deleteSymbol(indexOfSymbol)
            }
        )
    }


    LazyColumn(
        state = state,
        modifier = modifier.fillMaxWidth()
    ) {
        itemsIndexed(symbols) { index, symbol ->
            EncodingItem(
                symbol = symbol.apply {
                    nameError = badNameMessage(name, index)
                    probabilityError = badProbabilityMessage(probability, index)
                },
                isLast = index == symbols.lastIndex,
                badName = { name ->
                    badNameMessage(name, index)
                        .also { symbol.nameError = it }
                        .isNotBlank()
                },
                badProbability = { probabilityString ->
                    with(probabilityString.toDoubleOrNull() ?: -1.0) {
                        badProbabilityMessage(this, index)
                            .also { symbol.probabilityError = it }
                            .isNotBlank()
                    }
                },
                onChangeValueWithoutCheck = { clearResult() },
                modifier = Modifier
                    .combinedClickable(
                        onLongClick = {
                            if (symbols.size > startCount) {
                                hideKeyboard()
                                indexOfSymbol = index
                                showDialog = true
                            }
                        },
                        onDoubleClick = { clearSymbol(index) },
                        onClick = {}
                    )
                    .animateContentSize(spring(stiffness = Spring.StiffnessLow))
//                    .animateItemPlacement(spring(stiffness = Spring.StiffnessLow))
            )
        }

        item(content = itemsAfter)
    }
}


@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
private fun AnimatedVisibilityScope.EncodingItem(
    symbol: Symbol,
    isLast: Boolean,
    modifier: Modifier = Modifier,
    badName: (String) -> Boolean,
    badProbability: (String) -> Boolean,
    onChangeValueWithoutCheck: (String) -> Unit = {}
) {

    val name by symbol.nameLiveData.observeAsState("")
    val stringProbability by symbol.probabilityLiveData.observeAsState("")

    var isErrorProbability by remember { mutableStateOf(false) }
    var isErrorName by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
    ) {
        OutlinedField(
            text = name,
            isError = isErrorName,
            placeholderText = stringResource(R.string.symbol),
            onTextChange = {
                symbol.updateNameWithoutCheck(it)
                onChangeValueWithoutCheck(it)
                isErrorName = badName(it)
                if (!isErrorName) {
                    symbol.name = it
                }
            },
            modifier = Modifier.animateEnterExit(
                enter = slideInHorizontally(spring(stiffness = Spring.StiffnessLow)) { it },
                exit = slideOutHorizontally(spring(stiffness = Spring.StiffnessLow)) { it }
            )
        )

        Text(
            "=",
            color = MaterialTheme.colorScheme.onBackground.animate(),
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .animateEnterExit(
                    enter = slideInVertically(spring(stiffness = Spring.StiffnessLow)),
                    exit = slideOutVertically(spring(stiffness = Spring.StiffnessLow)) { it }
                )
                .padding(horizontal = 16.dp)
        )

        OutlinedField(
            text = stringProbability,
            placeholderText = "Вероятность",
            isError = isErrorProbability,
            keyboardType = KeyboardType.Decimal,
            imeAction = if (isLast) ImeAction.Done else ImeAction.Next,
            onTextChange = {
                symbol.updateStringProbability(it)
                onChangeValueWithoutCheck(it)
                isErrorProbability = badProbability(it)
                if (!isErrorProbability) {
                    symbol.probability = it.toDouble()
                }
            },
            modifier = Modifier.animateEnterExit(
                enter = slideInHorizontally(spring(stiffness = Spring.StiffnessLow)),
                exit = slideOutHorizontally(spring(stiffness = Spring.StiffnessLow))
            )
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
    onTextChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = text,
        onValueChange = {
            onTextChange(it)
        },
        placeholder = {
            Text(
                placeholderText,
                fontFamily = FontFamily(RobotoFont),
                fontSize = 16.sp
            )
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




@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Preview
@Composable
private fun SymbolsInputPreview() {
    AnimatedVisibility(visible = true) {
        SymbolsInput(
            symbols = arrayOf(
                Symbol("Wrecked", 0.45),
                Symbol("Imagine Dragons", 0.55)
            ),
            startCount = 2,
            clearResult = {},
            deleteSymbol = {},
            addSymbol = {},
            clearSymbol = {},
            listState = rememberLazyListState()
        )
    }
}