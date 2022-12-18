package com.mathematics.encoding.presentation.view

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mathematics.encoding.R
import com.mathematics.encoding.presentation.theme.animate
import com.mathematics.encoding.presentation.theme.removeBottomCorners
import com.mathematics.encoding.presentation.view.navigation.SettingsResultDelay
import com.mathematics.encoding.presentation.view.navigation.SettingsResultDuration

@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
internal fun AnimatedVisibilityScope.RoundedCornersScreen(
    title: String,
    navigationIcon: @Composable (Modifier) -> Unit = {},
    actionIcon: @Composable (Modifier) -> Unit = {},
    showVersion: Boolean = true,
    animateContent: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val topBarActionScale = 1.3f
    val screenShape = MaterialTheme.shapes.large.removeBottomCorners()

    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            navigationIcon = {
                navigationIcon(Modifier.scale(topBarActionScale))
            },
            actions = {
                actionIcon(Modifier.scale(topBarActionScale))
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background.animate(),
                navigationIconContentColor = MaterialTheme.colorScheme.primary.animate(),
                titleContentColor = MaterialTheme.colorScheme.onBackground.animate(),
                actionIconContentColor = MaterialTheme.colorScheme.primary.animate()
            ),
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
            modifier = if (animateContent) {
                Modifier
                    .animateEnterExit(
                        enter = fadeIn(
                            animationSpec = tween(
                                durationMillis = SettingsResultDuration,
                                delayMillis = SettingsResultDelay,
                                easing = LinearEasing,
                            ),
                        ),
                        exit = fadeOut(
                            animationSpec = tween(
                                durationMillis = SettingsResultDuration,
                                easing = LinearEasing,
                            ),
                        )
                    )
            } else Modifier
        )

        Box(
            modifier = Modifier
                .clip(screenShape)
                .border(
                    width = 1.3.dp,
                    brush = Brush.verticalGradient(
                        0f to MaterialTheme.colorScheme.onBackground.animate(),
                        0.2f to MaterialTheme.colorScheme.background.animate()
                    ),
                    shape = screenShape
                )
                .background(MaterialTheme.colorScheme.background.animate())
                .fillMaxSize()
        ) {
//            Box(
//                contentAlignment = Alignment.TopCenter,
//                modifier = Modifier
//                    .padding(top = 1.5.dp)
//                    .clip(screenShape)
//                    .background(
//                        color = MaterialTheme.colorScheme.background.animate(),
//                        shape = screenShape
//                    )
//                    .fillMaxSize()
//            ) {
                Column(
                    modifier = Modifier
                        .animateEnterExit(
                            enter = fadeIn(
                                animationSpec = tween(
                                    durationMillis = SettingsResultDuration / 2,
                                    delayMillis = SettingsResultDelay,
                                    easing = LinearOutSlowInEasing,
                                ),
                            ) + slideInVertically(
                                animationSpec = tween(
                                    durationMillis = SettingsResultDuration,
                                    delayMillis = SettingsResultDuration / 2 + SettingsResultDelay,
                                    easing = LinearEasing,
                                ),
                            ) { it / 2 },
                            exit = fadeOut(
                                animationSpec = tween(
                                    durationMillis = SettingsResultDelay,
                                    easing = LinearOutSlowInEasing,
                                ),
                                targetAlpha = 0.3f,
                            ) + slideOutVertically(
                                animationSpec = tween(
                                    durationMillis = SettingsResultDuration / 2,
                                    delayMillis = SettingsResultDelay,
                                    easing = LinearEasing,
                                ),
                            ) { it / 2 },
                        )
                        .fillMaxSize()
                ) {
                    content()
                }

                if (showVersion) {
                    Text(
                        text = stringResource(R.string.app_version),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f).animate(),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier
                            .animateEnterExit(
                                enter = fadeIn(
                                    animationSpec = tween(
                                        durationMillis = SettingsResultDuration,
                                        delayMillis = SettingsResultDelay,
                                        easing = LinearEasing,
                                    ),
                                ),
                                exit = fadeOut(
                                    animationSpec = tween(
                                        durationMillis = SettingsResultDuration,
                                        easing = LinearEasing,
                                    ),
                                )
                            )
                            .align(Alignment.BottomCenter)
                            .padding(8.dp)
                            .fillMaxWidth()
                    )
                }
//            }
        }
    }
}