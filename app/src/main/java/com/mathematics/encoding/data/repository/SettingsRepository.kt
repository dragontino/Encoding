package com.mathematics.encoding.data.repository

import com.mathematics.encoding.data.room.SettingsDao
import com.mathematics.encoding.presentation.model.Settings
import kotlinx.coroutines.flow.map

class SettingsRepository(private val settingsDao: SettingsDao) {
    suspend fun addSettings(settings: Settings) {
        settingsDao.addSettings(settings)
    }

//    suspend fun updateSettings(settings: Settings) =
//        settingsDao.updateSettings(settings)

    fun getSettings() =
        settingsDao.getSettings().map { it ?: Settings() }
}