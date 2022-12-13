package com.mathematics.encoding.presentation.viewmodel

import androidx.lifecycle.*
import com.mathematics.encoding.data.repository.SettingsRepository
import com.mathematics.encoding.data.model.Settings
import com.mathematics.encoding.data.model.Themes
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