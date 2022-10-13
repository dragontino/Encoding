package com.mathematics.encoding.presentation.view.mainscreen

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.liveData
import com.mathematics.encoding.EncodingApplication
import com.mathematics.encoding.R
import com.mathematics.encoding.data.repository.EncodingRepository
import com.mathematics.encoding.data.support.*
import com.mathematics.encoding.presentation.model.*
import com.mathematics.encoding.presentation.theme.EncodingAppTheme
import com.mathematics.encoding.presentation.theme.animate
import com.mathematics.encoding.presentation.theme.mediumCornerSize
import com.mathematics.encoding.presentation.view.BottomSheet
import com.mathematics.encoding.presentation.view.Settings
import com.mathematics.encoding.presentation.view.Themes
import com.mathematics.encoding.presentation.viewmodel.EncodingViewModel
import com.mathematics.encoding.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs


@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@Composable
fun MainScreen(
    settingsViewModel: SettingsViewModel,
    encodingViewModel: EncodingViewModel,
    theme: Themes,
    updateStatusBarColor: (Color) -> Unit = {}
) {
    val symbols by encodingViewModel.symbols.observeAsState(emptyArray())
    val settings by settingsViewModel.currentSettings.observeAsState(Settings())
    var resultList by remember { mutableStateOf(listOf<SymbolWithCode>()) }


    val scope = rememberCoroutineScope { Dispatchers.Default }
    val context = LocalContext.current
    val sheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val lazyListState = rememberLazyListState()

    var isLoading by remember { mutableStateOf(false) }


    var sheetContent: @Composable () -> Unit by remember { mutableStateOf({}) }


    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }

    var isAnimationRunning by remember { mutableStateOf(false) }

    fun openBottomSheet(title: String = "", content: @Composable ColumnScope.() -> Unit) {
        sheetContent = {
            BottomSheet(title = title, bottomSheetContent = content)
        }
        scope.launch { sheetState.open() }
    }

    val closeBottomSheet = {
        scope.launch { sheetState.close() }
    }


    var inputtedText by rememberSaveable { mutableStateOf("") }

    fun showResult() {
        if (settings.autoInputProbabilities) {
            when {
                inputtedText.isBlank() -> {
                    showToast(context, "Введите текст!")
                    return
                }
                inputtedText.find {
                    it.toString()
                        .matches(Regex("[^\\da-zA-Zа-яёА-ЯЁ(.*\\\\!)?\n ]"))
                } != null -> {
                    showToast(context, "Введите корректные символы!")
                    return
                }
                resultList.isEmpty() -> {
                    scope.launch(Dispatchers.Default) {
                        resultList = encodingViewModel
                            .generateCodesByFano(inputtedText, settings.considerGap)
                    }
                }
            }
        } else {
            val errorSymbol = symbols.find { it.hasError }
            when {
                errorSymbol != null -> {
                    showToast(context, errorSymbol.error)
                    return
                }
                abs(symbols.sumProbabilities - 1) > 0.005 -> {
                    showToast(context, "Сумма вероятностей должна равнятся 1")
                    return
                }
                resultList.isEmpty() -> {
                    scope.launch(Dispatchers.Default) {
                        resultList = encodingViewModel
                            .generateCodesByFano(symbols.toList())
                    }
                }
            }
        }

        openBottomSheet { CodesList(resultList) }
    }


    when {
        sheetState.isExpanded() -> updateStatusBarColor(MaterialTheme.colorScheme.background)
        sheetState.isHalfExpanded() -> updateStatusBarColor(
            MaterialTheme.colorScheme.surface.copy(alpha = 0.32f) +
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
        )
        else -> updateStatusBarColor(MaterialTheme.colorScheme.primary)
    }

    LaunchedEffect(key1 = "") {
        isLoading = true
        delay(loadingTimeMillis)
        isLoading = false
        delay(200)
        awaitCancellation()
    }


    LaunchedEffect(key1 = symbols.size, key2 = settings.startCount) {
        if (symbols.size < settings.startCount)
            encodingViewModel.addSymbols(settings.startCount - symbols.size)
    }


    val bottomSheetCornerSize by animateDpAsState(
        targetValue = if (sheetState.isExpanded()) 0.dp else mediumCornerSize,
        animationSpec = spring(stiffness = Spring.StiffnessMedium)
    )

    val bottomSheetBorderColor =
        if (sheetState.isExpanded()) Color.Transparent
        else MaterialTheme.colorScheme.primary


    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            Column(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = bottomSheetBorderColor.animate(),
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
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                )
            }
        },
        sheetShape = RoundedCornerShape(
            topStart = bottomSheetCornerSize,
            topEnd = bottomSheetCornerSize
        ),
        sheetBackgroundColor = MaterialTheme.colorScheme.background.animate()
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
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
                                openBottomSheet("Настройки") {
                                    Settings(
                                        settingsViewModel,
                                        isSwitchEnabled = !isAnimationRunning
                                    )
                                }
                            }
                        ) {
                            Icon(Icons.Rounded.Settings, contentDescription = "settings")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            openBottomSheet("Тема") {
                                Themes(theme) {
                                    closeBottomSheet()
                                    settingsViewModel.updateTheme(it)
                                }
                            }
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
                    scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary.animate(),
                        titleContentColor = MaterialTheme.colorScheme.onPrimary.animate(),
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary.animate()
                    )
                )
            },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = !lazyListState.isScrollInProgress,
                    enter = scaleIn(
                        transformOrigin = TransformOrigin(0.5f, 1f),
                        animationSpec = spring(stiffness = Spring.StiffnessVeryLow)
                    ) + slideInVertically(
                        animationSpec = spring(stiffness = Spring.StiffnessLow)
                    ) { it / 2 },
                    exit = scaleOut(
                        transformOrigin = TransformOrigin(0.5f, 1f),
                        animationSpec = spring(stiffness = Spring.StiffnessMedium)
                    ) + slideOutVertically(
                        animationSpec = spring(stiffness = Spring.StiffnessMedium)
                    ) { it / 2 }
                ) {
                    ExtendedFloatingActionButton(
                        text = {
                            Text(
                                text = stringResource(R.string.calculate_codes),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Calculate,
                                contentDescription = "add symbol",
                            )
                        },
                        onClick = { showResult() },
                        containerColor = MaterialTheme.colorScheme.primary.animate(),
                        contentColor = MaterialTheme.colorScheme.onPrimary.animate(),
                        modifier = Modifier
                            .padding(8.dp)
                            .border(
                                width = 1.1.dp,
                                color = MaterialTheme.colorScheme.onBackground.animate(),
                                shape = FloatingActionButtonDefaults.extendedFabShape,
                            )
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.Center,
            containerColor = MaterialTheme.colorScheme.background.animate(),
            contentWindowInsets = WindowInsets(2.dp),
            modifier = Modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { keyboardController?.hide() })
                .fillMaxSize()
        ) { contentPadding ->

            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Column(
                    modifier = Modifier
                        .padding(contentPadding)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .scale(1.3f)
                            .padding(20.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (isLoading) return@Scaffold


            AnimatedVisibility(
                visible = settings.autoInputProbabilities,
                enter = slideInHorizontally(spring(stiffness = Spring.StiffnessLow)),
                exit = slideOutHorizontally(spring(stiffness = Spring.StiffnessLow)) { it },
                modifier = Modifier.padding(contentPadding)
            ) {
                isAnimationRunning = this.transition.isRunning

                TextInput(
                    considerGap = settings.considerGap,
                    updateConsiderGap = settingsViewModel::updateConsiderGap,
                    onTextChange = { text ->
                        inputtedText = text
                        resultList = emptyList()
                    },
                    onCheckedChange = {
                        if (resultList.isNotEmpty()) resultList = emptyList()
                    }
                )
            }


            AnimatedVisibility(
                visible = !settings.autoInputProbabilities,
                enter = slideInHorizontally(spring(stiffness = Spring.StiffnessLow)),
                exit = slideOutHorizontally(spring(stiffness = Spring.StiffnessLow)) { it },
                modifier = Modifier.padding(contentPadding)
            ) {
                isAnimationRunning = this.transition.isRunning

                SymbolsInput(
                    symbols = symbols,
                    listState = lazyListState,
                    onChangeValue = { resultList = emptyList() },
                    addSymbol = {
                        resultList = emptyList()
                        encodingViewModel.addSymbol(Symbol())
                    },
                    deleteSymbol = { index ->
                        if (symbols.size >= settings.startCount) {
                            resultList = emptyList()
                            encodingViewModel.deleteSymbol(index)
                        }
                    }
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
            theme = theme
        )
    }
}