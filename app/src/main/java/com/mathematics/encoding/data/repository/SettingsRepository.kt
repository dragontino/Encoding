package com.mathematics.encoding.data.repository

import android.text.DynamicLayout
import com.mathematics.encoding.data.room.SettingsDao
import com.mathematics.encoding.presentation.model.Settings
import com.mathematics.encoding.presentation.model.Themes
import kotlinx.coroutines.flow.map

class SettingsRepository(private val settingsDao: SettingsDao) {
    suspend fun addSettings(settings: Settings) {
        settingsDao.addSettings(settings)
    }

    suspend fun updateTheme(theme: Themes) {
        settingsDao.updateTheme(theme)
    }

    suspend fun updateDynamicColor(dynamicColor: Boolean) {
        settingsDao.updateDynamicColor(dynamicColor)
    }

    suspend fun updateAutoInputProbabilities(autoInputProbabilities: Boolean) {
        settingsDao.updateAutoInputProbabilities(autoInputProbabilities)
    }

    suspend fun updateConsiderGap(considerGap: Boolean) {
        settingsDao.updateConsiderGap(considerGap)
    }

//    suspend fun updateSettings(settings: Settings) =
//        settingsDao.updateSettings(settings)

    fun getSettings() =
        settingsDao.getSettings().map {
            if (it == null) addSettings(Settings())
            it ?: Settings()
        }
}