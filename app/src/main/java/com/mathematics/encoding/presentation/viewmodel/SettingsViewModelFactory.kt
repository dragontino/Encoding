package com.mathematics.encoding.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.mathematics.encoding.data.repository.SettingsRepository
import com.mathematics.encoding.data.room.EncodingDatabase

@Suppress("UNCHECKED_CAST")
class SettingsViewModelFactory(private val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(
                settingsRepository = SettingsRepository(
                    settingsDao = EncodingDatabase.getDatabase(application).settingsDao
                )
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}