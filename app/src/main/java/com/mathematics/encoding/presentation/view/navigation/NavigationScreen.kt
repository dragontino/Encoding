package com.mathematics.encoding.presentation.view.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mathematics.encoding.EncodingApplication
import com.mathematics.encoding.data.model.parseToCodedSymbolList
import com.mathematics.encoding.data.support.*
import com.mathematics.encoding.data.support.Loading
import com.mathematics.encoding.presentation.theme.EncodingAppTheme
import com.mathematics.encoding.presentation.theme.animate
import com.mathematics.encoding.presentation.view.SettingsScreen
import com.mathematics.encoding.presentation.view.screens.MainScreen
import com.mathematics.encoding.presentation.view.screens.ResultScreen
import com.mathematics.encoding.presentation.viewmodel.EncodingViewModel
import com.mathematics.encoding.presentation.viewmodel.SettingsViewModel
import com.mathematics.encoding.presentation.viewmodel.SymbolsViewModel
import com.mathematics.encoding.presentation.viewmodel.TextInputViewModel
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay



const val SettingsResultDelay = 200
const val SettingsResultDuration = 500


@ExperimentalPagerApi
@ExperimentalMaterial3Api
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
internal fun NavigationScreen(application: EncodingApplication) {

    val navController = rememberAnimatedNavController()
    val encodingViewModel = viewModel<EncodingViewModel>(factory = application.viewModelFactory)
    val settingsViewModel = viewModel<SettingsViewModel>(factory = application.viewModelFactory)
    val symbolsViewModel = viewModel<SymbolsViewModel>(factory = application.viewModelFactory)
    val textInputViewModel = viewModel<TextInputViewModel>(factory = application.viewModelFactory)
    val systemUiController = rememberSystemUiController()
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var whiteStatusBar by rememberSaveable { mutableStateOf(false) }

    val navigateTo = { route: String ->
        navController.navigate(route) {
            launchSingleTop = true
        }
    }


    LaunchedEffect(Unit) {
        isLoading = true
        whiteStatusBar = true
        delay(loadingTimeMillis)
        isLoading = false
        whiteStatusBar = false
        awaitCancellation()
    }


    EncodingAppTheme {
        systemUiController.setStatusBarColor(
            color = when {
                whiteStatusBar -> MaterialTheme.colorScheme.background
                else -> MaterialTheme.colorScheme.primary
            }.animate(durationMills = 800)
        )


        Loading(isLoading)
        if (isLoading) return@EncodingAppTheme


        AnimatedNavHost(
            navController = navController,
            startDestination = EncodingScreens.Main.routeWithArgs,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
        ) {
            composable(
                route = EncodingScreens.Main.routeWithArgs,
                enterTransition = {
                    fadeIn(tween(durationMillis = 400, easing = LinearEasing))
                },
                exitTransition = {
                    fadeOut(tween(durationMillis = 400, easing = LinearEasing))
                }
            ) {
                whiteStatusBar = false

                MainScreen(
                    title = stringResource(EncodingScreens.Main.title),
                    encodingViewModel = encodingViewModel,
                    settingsViewModel = settingsViewModel,
                    symbolsViewModel = symbolsViewModel,
                    textInputViewModel = textInputViewModel,
                    navigateTo = navigateTo
                )
            }

            composable(
                route = EncodingScreens.Settings.routeWithArgs,
                enterTransition = {
                    if (initialState.destination.route != EncodingScreens.Result.routeWithArgs) {
                        fadeIn(
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = FastOutLinearInEasing
                            )
                        ) + slideInVertically(
                            animationSpec = tween(
                                durationMillis = 600,
                                easing = FastOutSlowInEasing
                            )
                        ) { it / 2 }
                    }
                    else {
                        fadeIn(
                            animationSpec = tween(
                                durationMillis = SettingsResultDuration,
                                delayMillis = SettingsResultDelay,
                                easing = LinearEasing
                            )
                        )
                    }
                },
                exitTransition = {
                    if (targetState.destination.route != EncodingScreens.Result.routeWithArgs) {
                        fadeOut(
                            animationSpec = tween(
                                durationMillis = 600,
                                delayMillis = 200,
                                easing = LinearOutSlowInEasing
                            )
                        ) + slideOutVertically(
                            animationSpec = tween(
                                durationMillis = 600,
                                easing = FastOutSlowInEasing
                            )
                        ) { it / 2 }
                    }
                    else {
                        fadeOut(
                            animationSpec = tween(
                                durationMillis = SettingsResultDuration,
                                delayMillis = SettingsResultDelay,
                                easing = LinearEasing
                            )
                        )
                    }
                }
            ) {
                whiteStatusBar = true

                SettingsScreen(
                    title = stringResource(EncodingScreens.Settings.title),
                    viewModel = settingsViewModel,
                    popBackStack = navController::popBackStack
                )
            }



            composable(
                route = EncodingScreens.Result.routeWithArgs,
                arguments = listOf(
                    navArgument(EncodingScreens.Result.Arguments.resultList) {
                        type = NavType.StringType
                    },
                    navArgument(EncodingScreens.Result.Arguments.defaultMessage) {
                        type = NavType.StringType
                        defaultValue = ""
                    },
                    navArgument(EncodingScreens.Result.Arguments.encodedMessage) {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                ),
                enterTransition = {
                    if (initialState.destination.route != EncodingScreens.Settings.routeWithArgs) {
                        fadeIn(
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = FastOutLinearInEasing
                            )
                        ) + slideInVertically(
                            animationSpec = tween(
                                durationMillis = 600,
                                easing = FastOutSlowInEasing
                            )
                        ) { it / 2 }
                    }
                    else {
                        fadeIn(
                            animationSpec = tween(
                                durationMillis = SettingsResultDuration,
                                delayMillis = SettingsResultDelay,
                                easing = LinearEasing
                            )
                        )
                    }
                },
                exitTransition = {
                    if (targetState.destination.route != EncodingScreens.Settings.routeWithArgs) {
                        fadeOut(
                            animationSpec = tween(
                                durationMillis = 600,
                                delayMillis = 200,
                                easing = LinearOutSlowInEasing
                            )
                        ) + slideOutVertically(
                            animationSpec = tween(
                                durationMillis = 600,
                                easing = FastOutSlowInEasing
                            )
                        ) { it / 2 }
                    } else {
                        fadeOut(
                            animationSpec = tween(
                                durationMillis = SettingsResultDuration,
                                delayMillis = SettingsResultDelay,
                                easing = LinearEasing
                            )
                        )
                    }
                },
            ) {
                whiteStatusBar = true

                val resultList = it.arguments
                    .getString(
                        key = EncodingScreens.Result.Arguments.resultList,
                        defaultValue = ""
                    )
                    .parseToCodedSymbolList()

                val defaultMessage = it.arguments
                    ?.getString(EncodingScreens.Result.Arguments.defaultMessage)
                    ?: ""

                val encodedMessage = it.arguments
                    ?.getString(EncodingScreens.Result.Arguments.encodedMessage)
                    ?: ""



                ResultScreen(
                    title = stringResource(EncodingScreens.Result.title),
                    resultList = resultList,
                    defaultMessage = defaultMessage,
                    encodedMessage = encodedMessage,
                    navigateTo = navigateTo,
                    popBackStack = navController::popBackStack,
                )
            }
        }
    }
}