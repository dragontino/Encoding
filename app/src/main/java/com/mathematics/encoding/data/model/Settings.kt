package com.mathematics.encoding.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SettingsTable")
data class Settings(
    @PrimaryKey var id: Int = 1,
    var theme: Themes = Themes.System,
    var dynamicColor: Boolean = false,
    var startCount: Int = 2,
    var autoInputProbabilities: Boolean = false,
    var considerGap: Boolean = true
)