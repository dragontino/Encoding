package com.mathematics.encoding.presentation.view.screens

import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mathematics.encoding.R
import com.mathematics.encoding.data.model.Settings
import com.mathematics.encoding.data.model.encode
import com.mathematics.encoding.data.model.isDark
import com.mathematics.encoding.data.support.Loading
import com.mathematics.encoding.data.support.isScrollingUp
import com.mathematics.encoding.data.support.loadingTimeMillis
import com.mathematics.encoding.data.support.mainScreenTabsAnimationSpec
import com.mathematics.encoding.presentation.model.ObservableSymbol
import com.mathematics.encoding.presentation.theme.animate
import com.mathematics.encoding.presentation.theme.removeBottomCorners
import com.mathematics.encoding.presentation.view.navigation.EncodingScreens
import com.mathematics.encoding.presentation.view.navigation.TabIconComposable
import com.mathematics.encoding.presentation.view.navigation.TabItems
import com.mathematics.encoding.presentation.view.navigation.createRouteToResultScreen
import com.mathematics.encoding.presentation.viewmodel.EncodingViewModel
import com.mathematics.encoding.presentation.viewmodel.SettingsViewModel
import com.mathematics.encoding.presentation.viewmodel.SymbolsViewModel
import com.mathematics.encoding.presentation.viewmodel.TextInputViewModel
import kotlinx.coroutines.*

@ExperimentalPagerApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@Composable
fun MainScreen(
    encodingViewModel: EncodingViewModel,
    settingsViewModel: SettingsViewModel,
    symbolsViewModel: SymbolsViewModel,
    textInputViewModel: TextInputViewModel,
    title: String,
    navigateTo: (route: String) -> Unit
) {
    val settings by settingsViewModel.currentSettings.observeAsState(Settings())

    encodingViewModel.currentTabItem = if (settings.autoInputProbabilities) {
        TabItems.TextInput
    } else {
        TabItems.SymbolsProbabilities
    }
    textInputViewModel.considerGap = settings.considerGap
    textInputViewModel.updateConsiderGap = settingsViewModel::updateConsiderGap


    val scope = rememberCoroutineScope { Dispatchers.Default }
    val context = LocalContext.current

    val lazyListState = rememberLazyListState()
//    val pagerState = rememberPagerState(initialPage = TabItems.values().size)
    val systemUiController = rememberSystemUiController()

    systemUiController.setStatusBarColor(
        color = MaterialTheme.colorScheme.primary.animate(),
        darkIcons = !settings.theme.isDark()
    )


    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current


    fun showResult() {
        if (settings.autoInputProbabilities) {

            when {
                !textInputViewModel.checkInputText(context) -> return

                encodingViewModel.resultList.isEmpty() -> {
                    scope.launch(Dispatchers.Default) {
                        encodingViewModel.calculateCodesByFano(
                            text = textInputViewModel.text,
                            considerGap = settings.considerGap
                        )

                        withContext(Dispatchers.Main) {
                            navigateTo(
                                createRouteToResultScreen(
                                    resultList = encodingViewModel.resultList,
                                    defaultMessage = textInputViewModel.text,
                                    encodedMessage = textInputViewModel.text.encode(encodingViewModel.resultList),
                                ),
                            )
                        }
                    }
                }

                else -> {
                    // TODO: 17.12.2022 переделать
                    navigateTo(
                        createRouteToResultScreen(
                            resultList = encodingViewModel.resultList,
                            defaultMessage = textInputViewModel.text,
                            encodedMessage = textInputViewModel.text.encode(encodingViewModel.resultList),
                        ),
                    )
                }
            }
        } else {
            when {
                !symbolsViewModel.checkSymbolsInput(context) -> return

                encodingViewModel.resultList.isEmpty() -> {
                    scope.launch(Dispatchers.Default) {
                        encodingViewModel.calculateCodesByFano(
                            symbols = symbolsViewModel.symbolsList.map { it.toSymbol() }
                        )

                        withContext(Dispatchers.Main) {
                            navigateTo(
                                createRouteToResultScreen(encodingViewModel.resultList),
                            )
                        }
                    }
                }

                else -> {
                    // TODO: 17.12.2022 переделать
                    navigateTo(
                        createRouteToResultScreen(encodingViewModel.resultList),
                    )
                }
            }
        }
    }


    LaunchedEffect(Unit) {
        encodingViewModel.isLoading = true
        delay(loadingTimeMillis)
        encodingViewModel.isLoading = false
        delay(100)
        awaitCancellation()
    }


    Scaffold(
        topBar = {
            MainTopAppBar(
                title = title,
                actionButton = {
                    AnimatedVisibility(
                        visible = !settings.autoInputProbabilities,
                        enter = fadeIn(mainScreenTabsAnimationSpec()),
                        exit = fadeOut(mainScreenTabsAnimationSpec())
                    ) {
                        IconButton(
                            onClick = {
                                encodingViewModel.clearResult()
                                symbolsViewModel.symbolsList.add(ObservableSymbol())
                            },
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = "add symbol",
                                modifier = Modifier.scale(1.3f)
                            )
                        }
                    }
                },
                navigateTo = navigateTo
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = lazyListState.isScrollingUp(),
                enter = scaleIn(
                    transformOrigin = TransformOrigin(0.5f, 1f),
                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
                ) + slideInVertically(
                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
                ) { it / 2 },
                exit = scaleOut(
                    transformOrigin = TransformOrigin(0.5f, 1f),
                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
                ) + slideOutVertically(
                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
                ) { it / 2 },
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
                            contentDescription = "calculate",
                        )
                    },
                    onClick = {
                        showResult()
                    },
                    containerColor = MaterialTheme.colorScheme.primary.animate(),
                    contentColor = MaterialTheme.colorScheme.onPrimary.animate(),
                    modifier = Modifier
                        .padding(8.dp)
                        .border(
                            width = 1.1.dp,
                            color = MaterialTheme.colorScheme.onBackground.animate(),
                            shape = FloatingActionButtonDefaults.extendedFabShape,
                        ),
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        containerColor = MaterialTheme.colorScheme.primary.animate(),
        contentWindowInsets = WindowInsets(2.dp),
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                },
            )
            .fillMaxSize(),
    ) { contentPadding ->

        Loading(
            isLoading = encodingViewModel.isLoading,
            modifier = Modifier.padding(contentPadding)
        )
        if (encodingViewModel.isLoading) return@Scaffold


        Column(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        0.3f to MaterialTheme.colorScheme.primary.animate(),
                        0.75f to MaterialTheme.colorScheme.background.animate(),
                    )
                )
                .padding(contentPadding)
                .fillMaxSize()
        ) {

            // TODO: 15.12.2022 доделать tabs

//                TabRow(
//                    selectedTabIndex = pagerState.currentPage,
//                    containerColor = MaterialTheme.colorScheme.primary.animate(),
//                    contentColor = MaterialTheme.colorScheme.onPrimary.animate(),
//                    indicator = { tabPositions ->
//                        TabRowDefaults.Indicator(
//                            color = MaterialTheme.colorScheme.onPrimary.animate(),
//                            modifier = Modifier.tabIndicatorOffset(
//                                pagerState = pagerState,
//                                tabPositions = tabPositions
//                            )
//                        )
//                    },
//                    divider = {
//                        Divider(
//                            thickness = 1.2.dp,
//                            color = Color.Transparent
//                        )
//                    },
//                    modifier = Modifier
//                        .padding(horizontal = 16.dp)
//                        .padding(bottom = 16.dp)
//                ) {
//                    TabItems.values().forEachIndexed { index, tab ->
//                        LeadingIconTab(
//                            selected = pagerState.currentPage == index,
//                            onClick = {
//                                scope.launch {
//                                    pagerState.animateScrollToPage(index % pagerState.pageCount)
//                                }
//                            },
//                            text = {
//                                Text(
//                                    text = stringResource(tab.title),
//                                    style = MaterialTheme.typography.labelSmall.copy(
//                                        fontWeight = if (pagerState.currentPage == index)
//                                            FontWeight.Bold
//                                        else null
//                                    )
//                                )
//                            },
//                            icon = tab.icon,
//                            selectedContentColor = MaterialTheme.colorScheme.onPrimary.animate()
//                        )
//                    }
//                }


            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()
            ) {
                TabItems.values().forEach {
                    TabRowItem(
                        text = stringResource(it.title),
                        icon = it.icon,
                        isSelected = it == TabItems.TextInput == settings.autoInputProbabilities,
                        onClick = {
                            settingsViewModel.updateAutoInputProbabilities(
                                it == TabItems.TextInput
                            )
                            encodingViewModel.clearResult()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !encodingViewModel.isAnimationRunning
                    )
                }
            }


            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .padding(start = 3.dp, end = 4.dp, bottom = 4.dp)
                    .clip(
                        shape = MaterialTheme.shapes.medium.removeBottomCorners(),
                    )
                    .background(
                        color = MaterialTheme.colorScheme.background.animate(),
                        shape = MaterialTheme.shapes.medium.removeBottomCorners(),
                    )
                    .fillMaxSize()
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = encodingViewModel.currentTabItem == TabItems.TextInput,
                    enter = slideInHorizontally(
                        animationSpec = mainScreenTabsAnimationSpec()
                    ) + fadeIn(
                        animationSpec = mainScreenTabsAnimationSpec()
                    ),
                    exit = slideOutHorizontally(
                        animationSpec = mainScreenTabsAnimationSpec()
                    ) + fadeOut(
                        animationSpec = mainScreenTabsAnimationSpec()
                    ),
                    modifier = Modifier.fillMaxSize()
                ) {
                    encodingViewModel.isAnimationRunning = this.transition.isRunning

                    TextInputScreen(
                        viewModel = textInputViewModel,
                        clearResult = encodingViewModel::clearResult
                    )
                }



                androidx.compose.animation.AnimatedVisibility(
                    visible = !settings.autoInputProbabilities,
                    enter = slideInHorizontally(
                        animationSpec = mainScreenTabsAnimationSpec()
                    ) { it / 2 } + fadeIn(
                        animationSpec = mainScreenTabsAnimationSpec()
                    ),
                    exit = slideOutHorizontally(
                        animationSpec = mainScreenTabsAnimationSpec()
                    ) { it / 2 } + fadeOut(
                        animationSpec = mainScreenTabsAnimationSpec()
                    ),
                    modifier = Modifier.fillMaxHeight()
                ) {
                    encodingViewModel.isAnimationRunning = this.transition.isRunning

                    SymbolsProbabilityScreen(
                        viewModel = symbolsViewModel,
                        settings = settings,
                        listState = lazyListState,
                        clearResult = encodingViewModel::clearResult
                    )
                }
            }
        }
    }
}



@ExperimentalMaterial3Api
@Composable
private fun MainTopAppBar(
    title: String,
    navigateTo: (route: String) -> Unit,
    actionButton: @Composable () -> Unit,
) {
    val orientation = LocalConfiguration.current.orientation

    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        navigateTo(EncodingScreens.Settings.route)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "settings",
                        modifier = Modifier.scale(1.3f)
                    )
                }
            },
            actions = {
                actionButton()
            },
            scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary.animate(),
                titleContentColor = MaterialTheme.colorScheme.onPrimary.animate(),
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary.animate(),
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary.animate()
            )
        )
    } else {
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
                        navigateTo(EncodingScreens.Settings.route)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "settings",
                        modifier = Modifier.scale(1.3f)
                    )
                }
            },
            actions = {
                actionButton()
            },
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary.animate(),
                titleContentColor = MaterialTheme.colorScheme.onPrimary.animate(),
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary.animate(),
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary.animate()
            )
        )
    }
}





@Composable
private fun TabRowItem(
    text: String,
    icon: TabIconComposable,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {

    Column(
        modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onClick, enabled = enabled),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(3.dp))
                .align(Alignment.CenterHorizontally),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .border(
                        width = if (isSelected) 1.5.dp else 0.4.dp,
                        color = MaterialTheme.colorScheme.onPrimary.animate(),
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .padding(4.dp)
            ) {
                icon(isSelected)
            }

            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary.animate(),
                fontWeight = if (isSelected) FontWeight.Bold else null
            )
        }

        if (isSelected) {
            Divider(
                color = MaterialTheme.colorScheme.onPrimary,
                thickness = 1.8.dp
            )
        }
    }

}