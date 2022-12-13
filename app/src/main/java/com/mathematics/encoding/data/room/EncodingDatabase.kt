package com.mathematics.encoding.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mathematics.encoding.data.model.Settings

@Database(entities = [Settings::class], version = 1, exportSchema = false)
abstract class EncodingDatabase: RoomDatabase() {
    abstract val settingsDao: SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: EncodingDatabase? = null

        fun getDatabase(context: Context): EncodingDatabase {
            val temp = INSTANCE

            if (temp != null)
                return temp

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EncodingDatabase::class.java,
                    "EncodingDatabase"
                ).build()

                INSTANCE = instance
                return instance
            }
        }
    }
}