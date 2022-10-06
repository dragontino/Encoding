package com.mathematics.encoding.presentation.view

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.liveData
import com.mathematics.encoding.*
import com.mathematics.encoding.R
import com.mathematics.encoding.data.repository.EncodingRepository
import com.mathematics.encoding.presentation.model.*
import com.mathematics.encoding.presentation.theme.*
import com.mathematics.encoding.presentation.viewmodel.EncodingViewModel
import com.mathematics.encoding.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@Composable
fun MainScreen(
    settingsViewModel: SettingsViewModel,
    encodingViewModel: EncodingViewModel,
    theme: Themes,
    onClickToButton: suspend (List<Symbol>) -> List<SymbolWithCode>
) {
    val symbols by encodingViewModel.symbols.observeAsState(emptyArray())
    val settings by settingsViewModel.currentSettings.observeAsState(Settings())
    var resultList by remember { mutableStateOf(listOf<SymbolWithCode>()) }

    val scope = rememberCoroutineScope { Dispatchers.Default }
    val context = LocalContext.current
    val modalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)


    var sheetContent: @Composable () -> Unit by remember { mutableStateOf({}) }


    val openBottomSheet: (sheetContent: @Composable () -> Unit) -> Job = {
        sheetContent = it
        scope.launch {
            modalBottomSheetState.animateTo(
                targetValue = ModalBottomSheetValue.HalfExpanded,
                anim = spring(stiffness = Spring.StiffnessMediumLow)
            )
        }
    }

    val closeBottomSheet = {
        scope.launch {
            modalBottomSheetState.animateTo(
                targetValue = ModalBottomSheetValue.Hidden,
                anim = spring(stiffness = Spring.StiffnessMediumLow)
            )
        }
    }

    LaunchedEffect(key1 = symbols.size, key2 = settings.startCount) {
        if (symbols.size < settings.startCount)
            encodingViewModel.addSymbols(settings.startCount - symbols.size)
    }

    val bottomSheetCornerSize = animateDpAsState(
        targetValue = if (modalBottomSheetState.isExpanded()) 0.dp else mediumCornerSize,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    ).value


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.app_name),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            openBottomSheet { Settings(settingsViewModel) }
                        }
                    ) {
                        Icon(Icons.Rounded.Settings, contentDescription = "settings")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        settingsViewModel.updateSettings { this.theme = theme.switch() }
                    }) {
                        Icon(
                            imageVector = if (settings.theme.isDark())
                                Icons.Rounded.LightMode
                            else
                                Icons.Rounded.DarkMode,
                            contentDescription = "switch theme",
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = animateColor(MaterialTheme.colorScheme.primary),
                    titleContentColor = animateColor(MaterialTheme.colorScheme.onPrimary),
                    actionIconContentColor = animateColor(MaterialTheme.colorScheme.onPrimary)
                )
            )
        },
        containerColor = animateColor(MaterialTheme.colorScheme.background),
        contentWindowInsets = WindowInsets(2.dp),
        modifier = Modifier.fillMaxSize()
    ) { contentPadding ->

        ModalBottomSheetLayout(
            sheetState = modalBottomSheetState,
            sheetContent = {
                Column(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = animateColor(MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(
                                topStart = bottomSheetCornerSize,
                                topEnd = bottomSheetCornerSize
                            )
                        )
                        .fillMaxSize()
                ) {
                    Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.weight(2f)) {
                        sheetContent()
                    }

                    Text(
                        text = stringResource(R.string.app_version),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(8.dp).fillMaxWidth()
                    )
                }
            },
            sheetShape = RoundedCornerShape(
                topStart = bottomSheetCornerSize,
                topEnd = bottomSheetCornerSize
            ),
            sheetBackgroundColor = animateColor(MaterialTheme.colorScheme.background),
            modifier = Modifier.padding(contentPadding)
        ) {
            EncodingItems(
                symbols = symbols,
                onChangeName = { resultList = emptyList() },
                onChangeProbability = {
                    if (it == null)
                        Toast.makeText(context, "Некорректная вероятность", Toast.LENGTH_SHORT)
                            .show()
                    resultList = emptyList()
                }
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 8.dp)
                        .fillMaxWidth()
                ) {
                    TextButton(
                        text = "Удалить элемент",
                        shape = RoundedCornerShape(
                            topStart = mediumCornerSize,
                            bottomStart = mediumCornerSize,
                            topEnd = smallCornerSize,
                            bottomEnd = smallCornerSize
                        ),
                        modifier = Modifier
                            .padding(start = 8.dp, end = 6.dp)
                            .weight(1f)
                    ) {
                        if (symbols.size > 2) {
                            closeBottomSheet()
                            resultList = emptyList()
                            encodingViewModel.deleteSymbolsFromLast()
                        }
                    }

//                    TextButton(
//                        text = "Очистить значения",
//                        shape = RoundedCornerShape(smallCornerSize),
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        closeBottomSheet()
//                        resultList = emptyList()
//                        encodingViewModel.clearSymbols(settings.startCount)
//                    }

                    TextButton(
                        text = "Добавить элемент",
                        shape = RoundedCornerShape(
                            topEnd = mediumCornerSize,
                            bottomEnd = mediumCornerSize,
                            topStart = smallCornerSize,
                            bottomStart = smallCornerSize
                        ),
                        modifier = Modifier
                            .padding(end = 8.dp, start = 6.dp)
                            .weight(1f)
                    ) {
                        closeBottomSheet()
                        resultList = emptyList()
                        encodingViewModel.addSymbol(Symbol())
                    }
                }


                TextButton(
                    text = "Вычислить коды символов по методу Фано",
                    shape = RoundedCornerShape(mediumCornerSize),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth()
                ) {
                    if (symbols.find { it.probability > 1 || it.probability < 0 } != null) {
                        Toast.makeText(context, "Некорректные вероятности!", Toast.LENGTH_SHORT)
                            .show()
                        return@TextButton
                    } else if (abs(symbols.sumOf { it.probability } - 1) > 0.005) {
                        Toast.makeText(
                            context,
                            "Сумма вероятностей должна равнятся 1",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@TextButton
                    }

                    if (resultList.isEmpty()) {
                        scope.launch(Dispatchers.Default) {
                            resultList =  onClickToButton(symbols.toList())
                        }
                    }
                    openBottomSheet { CodesList(resultList) }
                }
            }
        }
    }
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
private fun EncodingItems(
    symbols: Array<Symbol>,
    modifier: Modifier = Modifier,
    onChangeName: (String) -> Unit = {},
    onChangeProbability: (Double?) -> Unit = {},
    itemsAfter: @Composable LazyItemScope.() -> Unit = {}
) {
    Log.d("EncodingItems", "first symbol = ${symbols.getOrNull(0)}")
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        itemsIndexed(symbols) { index, symbol ->
            EncodingItem(
                symbol = symbol,
                isError = symbol.probability > 1 || symbol.probability < 0,
                isLast = index == symbols.lastIndex,
                onChangeName = onChangeName,
                onChangeProbability = onChangeProbability,
                modifier = Modifier
                    .animateContentSize(spring(stiffness = Spring.StiffnessLow))
                    .animateItemPlacement(spring(stiffness = Spring.StiffnessLow))
            )
        }

        item(content = itemsAfter)
    }
}

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
private fun EncodingItem(
    symbol: Symbol,
    isError: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier,
    onChangeName: (String) -> Unit = {},
    onChangeProbability: (Double?) -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
    ) {
        OutlinedField(
            text = symbol.name,
            placeholderText = "Символ",
            onTextChange = {
                symbol.name = it
                onChangeName(it)
            }
        )

        Text(
            "=",
            color = animateColor(MaterialTheme.colorScheme.onBackground),
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        val probability = when {
            symbol.probability < 0 -> ""
            symbol.probability % 1 == 0.0 -> symbol.probability.toInt().toString()
            else -> symbol.probability.toString()
        }

        OutlinedField(
            text = probability,
            placeholderText = "Вероятность",
            isError = isError,
            keyboardType = KeyboardType.Decimal,
            focusDirection = if (isLast) FocusDirection.Exit else FocusDirection.Next,
            onTextChange = {
                it.toDoubleOrNull().let { probability ->
                    symbol.probability = probability ?: 0.0
                    if (it.isEmpty())
                        onChangeProbability(0.0)
                    else
                        onChangeProbability(probability)
                }
            },
        )
    }
}

@ExperimentalMaterial3Api
@Composable
private fun RowScope.OutlinedField(
    text: String,
    placeholderText: String = "",
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    focusDirection: FocusDirection = FocusDirection.Next,
    onTextChange: (String) -> Unit
) {
    var savedText by remember { mutableStateOf(text) }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = savedText,
        onValueChange = {
            savedText = it
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
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(focusDirection) }),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = animateColor(MaterialTheme.colorScheme.onBackground),
            containerColor = animateColor(MaterialTheme.colorScheme.primaryContainer),
            placeholderColor = animateColor(
                MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.6f
                )
            ),
            focusedBorderColor = animateColor(MaterialTheme.colorScheme.primary),
            disabledBorderColor = animateColor(MaterialTheme.colorScheme.primary),
            unfocusedBorderColor = animateColor(MaterialTheme.colorScheme.primary),
            errorBorderColor = animateColor(MaterialTheme.colorScheme.error),
            errorCursorColor = animateColor(MaterialTheme.colorScheme.error)
        ),
        isError = isError,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.weight(1f)
    )
}

@Composable
private fun TextButton(
    text: String,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        shape = shape,
        colors = ButtonDefaults.textButtonColors(
            containerColor = animateColor(MaterialTheme.colorScheme.primary),
            contentColor = animateColor(MaterialTheme.colorScheme.onPrimary)
        ),
        modifier = modifier,
        border = BorderStroke(
            1.2.dp,
            animateColor(MaterialTheme.colorScheme.onBackground)
        ),
    ) {
        Text(text = text, fontSize = 16.sp, textAlign = TextAlign.Center)
    }
}

@ExperimentalFoundationApi
@Composable
private fun CodesList(symbolWithCodes: List<SymbolWithCode>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(
                color = animateColor(MaterialTheme.colorScheme.background),
                shape = RoundedCornerShape(smallCornerSize)
            )
            .padding(9.dp)
            .fillMaxWidth()
    ) {
        Text(
            "Средняя длина кода = ${symbolWithCodes.averageCodeLength.round(2)}",
            fontSize = MaterialTheme.typography.labelSmall.fontSize,
            fontFamily = MaterialTheme.typography.labelMedium.fontFamily,
            color = animateColor(MaterialTheme.colorScheme.onBackground),
            modifier = Modifier.padding(top = 8.dp, bottom = 2.dp, start = 6.dp)
        )
        Text(
            "Энтропия = ${symbolWithCodes.entropy.round(2)}",
            fontSize = MaterialTheme.typography.labelSmall.fontSize,
            fontFamily = MaterialTheme.typography.labelMedium.fontFamily,
            color = animateColor(MaterialTheme.colorScheme.onBackground),
            modifier = Modifier.padding(bottom = 8.dp, top = 2.dp, start = 6.dp)
        )

        LazyColumn(
            Modifier
                .border(
                    1.5.dp,
                    animateColor(MaterialTheme.colorScheme.onBackground)
                )
                .padding(4.dp)
        ) {
            item {
                TableRow(items = arrayOf("Символ", "Вероятность", "Код"), fontSize = 19.sp)
                Divider(
                    color = animateColor(MaterialTheme.colorScheme.onBackground),
                    thickness = 1.5.dp
                )
            }

            items(symbolWithCodes.size) { index ->
                val symbol = symbolWithCodes[index].symbol
                val code = symbolWithCodes[index].code
                TableRow(
                    items = arrayOf(symbol.name, symbol.probability.toString(), code),
                    fontSize = 16.sp
                )
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun LazyItemScope.TableRow(items: Array<String>, fontSize: TextUnit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .animateContentSize(spring(stiffness = Spring.StiffnessVeryLow))
            .animateItemPlacement(spring(stiffness = Spring.StiffnessVeryLow))
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
    ) {
        items.forEach {
            SelectionContainer(
                modifier = Modifier
                    .padding(horizontal = 0.dp)
                    .weight(1f)
            ) {
                Text(
                    text = it,
                    color = animateColor(MaterialTheme.colorScheme.onBackground),
                    fontSize = fontSize,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    EncodingAppTheme(settings = liveData { emit(Settings(dynamicColor = true)) }) { theme ->
        MainScreen(
            settingsViewModel = SettingsViewModel.getInstance(
                { ViewModelStore() },
                EncodingApplication().settingsViewModelFactory
            ),
            encodingViewModel = EncodingViewModel(EncodingRepository()),
            onClickToButton = { listOf() },
            theme = theme
        )
    }
}

@ExperimentalFoundationApi
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CodesListPreview() {
    EncodingAppTheme(settings = liveData { emit(Settings()) }) {
        CodesList(
            symbolWithCodes = listOf(
                SymbolWithCode("A", 0.25, "01"),
                SymbolWithCode("B", 0.65, "1"),
                SymbolWithCode("C", 0.1, "00")
            ),
        )
    }

}