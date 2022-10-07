package com.mathematics.encoding.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mathematics.encoding.presentation.model.Settings
import com.mathematics.encoding.presentation.model.Themes
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Insert(entity = Settings::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSettings(settings: Settings)

    @Query("UPDATE SettingsTable SET theme = :themes")
    suspend fun updateTheme(themes: Themes?)

    @Query("UPDATE SettingsTable SET dynamicColor = :dynamicColor")
    suspend fun updateDynamicColor(dynamicColor: Boolean)

    @Query("UPDATE SettingsTable SET autoInputProbabilities = :autoInputProbabilities")
    suspend fun updateAutoInputProbabilities(autoInputProbabilities: Boolean)

    @Query("UPDATE SettingsTable SET considerGap = :considerGap")
    suspend fun updateConsiderGap(considerGap: Boolean)

    @Update(entity = Settings::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSettings(settings: Settings)

    @Query("SELECT * FROM SettingsTable WHERE id = 1")
    fun getSettings(): Flow<Settings?>
}