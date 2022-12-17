package com.mathematics.encoding.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.mathematics.encoding.data.repository.EncodingRepository
import com.mathematics.encoding.data.repository.SettingsRepository
import com.mathematics.encoding.data.room.EncodingDatabase

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when {
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(
                settingsRepository = SettingsRepository(
                    settingsDao = EncodingDatabase.getDatabase(application).settingsDao
                )
            ) as T
            modelClass.isAssignableFrom(EncodingViewModel::class.java) -> EncodingViewModel(
                encodingRepository = EncodingRepository()
            ) as T
            modelClass.isAssignableFrom(SymbolsViewModel::class.java) -> SymbolsViewModel() as T
            modelClass.isAssignableFrom(TextInputViewModel::class.java) -> TextInputViewModel() as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}