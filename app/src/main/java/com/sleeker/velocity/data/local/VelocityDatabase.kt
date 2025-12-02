package com.sleeker.velocity.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sleeker.velocity.data.model.RunEntity
//import com.sleeker.velocity.velocity.data.local.LocalDateTimeConverter

@Database(
    entities = [RunEntity::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(LocalDateTimeConverter::class)
abstract class VelocityDatabase : RoomDatabase() {
    abstract fun runDao(): RunDao

    companion object {
        const val DB_NAME = "velocity.db"
    }
}