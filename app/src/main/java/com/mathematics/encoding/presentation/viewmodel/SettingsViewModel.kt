package com.mathematics.encoding.presentation.viewmodel

import androidx.lifecycle.*
import com.mathematics.encoding.data.repository.SettingsRepository
import com.mathematics.encoding.presentation.model.Settings
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {
    companion object {
        @Volatile
        private var INSTANCE: SettingsViewModel? = null

        fun getInstance(
            owner: ViewModelStoreOwner,
            factory: SettingsViewModelFactory
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
        val settings = (currentSettings.value ?: Settings()).apply(update)
        viewModelScope.launch {
            settingsRepository.addSettings(settings)
        }
    }
}