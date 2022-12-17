package com.mathematics.encoding.presentation.viewmodel

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.*
import com.mathematics.encoding.data.model.Settings
import com.mathematics.encoding.data.model.Themes
import com.mathematics.encoding.data.repository.SettingsRepository
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {
    companion object {
        @Volatile
        private var INSTANCE: SettingsViewModel? = null

        fun getInstance(
            owner: ViewModelStoreOwner,
            factory: ViewModelFactory
        ): SettingsViewModel {
            val temp = INSTANCE
            if (temp != null)
                return temp

            synchronized(this) {
                val instance = ViewModelProvider(owner, factory)[SettingsViewModel::class.java]
                INSTANCE = instance

                return instance
            }
        }
    }

    val currentSettings get() = settingsRepository.getSettings().asLiveData()


    var showDialog by mutableStateOf(false)
        private set
    var dialogContent: @Composable () -> Unit by mutableStateOf({})
        private set



    fun openDialog(dialogContent: @Composable () -> Unit) {
        showDialog = true
        this.dialogContent = dialogContent
    }

    fun closeDialog() {
        showDialog = false
        this.dialogContent = {}
    }


    fun openURL(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        context.startActivity(intent)
    }




    fun updateSettings(update: Settings.() -> Unit) {
        // TODO: исправить баг
        val settings = (currentSettings.value ?: Settings()).apply(update)
        viewModelScope.launch {
            settingsRepository.addSettings(settings)
        }
    }

    fun updateTheme(theme: Themes) {
        viewModelScope.launch {
            settingsRepository.updateTheme(theme)
        }
    }

    fun updateDynamicColor(dynamicColor: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateDynamicColor(dynamicColor)
        }
    }

    fun updateAutoInputProbabilities(autoInputProbabilities: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateAutoInputProbabilities(autoInputProbabilities)
        }
    }

    fun updateConsiderGap(considerGap: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateConsiderGap(considerGap)
        }
    }

    fun updateStartCount(startCount: Int) {
        viewModelScope.launch {
            settingsRepository.updateStartCount(startCount)
        }
    }
}