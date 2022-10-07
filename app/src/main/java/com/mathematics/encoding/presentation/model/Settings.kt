package com.mathematics.encoding.presentation.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SettingsTable")
data class Settings(
    @PrimaryKey var id: Int = 1,
    var theme: Themes? = null,
    var dynamicColor: Boolean = false,
    var startCount: Int = 2,
    var autoInputProbabilities: Boolean = false,
    var considerGap: Boolean = true
)