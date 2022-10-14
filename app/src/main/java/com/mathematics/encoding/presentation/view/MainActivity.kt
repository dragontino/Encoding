package com.mathematics.encoding.presentation.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mathematics.encoding.EncodingApplication
import com.mathematics.encoding.data.support.loadingTimeMillis
import com.mathematics.encoding.presentation.theme.EncodingAppTheme
import com.mathematics.encoding.presentation.theme.animate
import com.mathematics.encoding.presentation.view.mainscreen.MainScreen
import com.mathematics.encoding.presentation.viewmodel.EncodingViewModel
import com.mathematics.encoding.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@FlowPreview
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsViewModel = SettingsViewModel.getInstance(
            this,
            (application as EncodingApplication).settingsViewModelFactory
        )

        val encodingViewModel = EncodingViewModel.getInstance(
            this,
            (application as EncodingApplication).encodingViewModelFactory
        )

        
        setContent {
            var isLoading by remember { mutableStateOf(false) }
            var statusBarColor: Color? by remember { mutableStateOf(null) }
            
            LaunchedEffect(key1 = "") {
                launch {
                    isLoading = true
                    delay(loadingTimeMillis)
                    isLoading = false
                }
            }

            EncodingAppTheme(
                settings = settingsViewModel.currentSettings,
                statusBarColor = statusBarColor?.animate()
            ) { theme ->

                AnimatedVisibility(
                    visible = isLoading,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
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
                if (isLoading) return@EncodingAppTheme

                MainScreen(
                    theme = theme,
                    settingsViewModel = settingsViewModel,
                    encodingViewModel = encodingViewModel,
                    updateStatusBarColor = { statusBarColor = it }
                )
            }
        }
    }
}