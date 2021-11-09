package com.example.actionsfun.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.actionsfun.data.ActionsConverter
import com.example.actionsfun.model.Action

@Database(
    entities = [Action::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    ActionsConverter::class
)

abstract class ActionsDatabase : RoomDatabase() {
    abstract fun actionDao(): ActionDao

    companion object {
        const val DATABASE_NAME: String = "actions_db"
    }
}