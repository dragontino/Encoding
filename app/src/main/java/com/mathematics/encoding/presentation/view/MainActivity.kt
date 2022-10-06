package com.mathematics.encoding.presentation.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.ui.unit.dp
import com.mathematics.encoding.EncodingApplication
import com.mathematics.encoding.presentation.theme.EncodingAppTheme
import com.mathematics.encoding.presentation.viewmodel.EncodingViewModel
import com.mathematics.encoding.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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
            
            LaunchedEffect(key1 = "") {
                launch {
                    isLoading = true
                    delay(400)
                    isLoading = false
                }
            }

            EncodingAppTheme(settings = settingsViewModel.currentSettings) { theme ->
                if (isLoading) {
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

                else
                    MainScreen(
                        theme = theme,
                        settingsViewModel = settingsViewModel,
                        encodingViewModel = encodingViewModel,
                        onClickToButton = { encodingViewModel.generateCodesByFano(it) },
                    )
            }
        }
    }
}