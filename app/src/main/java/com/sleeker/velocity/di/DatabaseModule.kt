// File: app/src/main/java/com/velocity/di/DatabaseModule.kt
package com.velocity.di

import android.content.Context
import androidx.room.Room
import com.sleeker.velocity.data.local.VelocityDatabase
import com.sleeker.velocity.data.local.RunDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideVelocityDatabase(
        @ApplicationContext context: Context
    ): VelocityDatabase {
        return Room.databaseBuilder(
            context,
            VelocityDatabase::class.java,
            VelocityDatabase.DB_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideRunDao(database: VelocityDatabase): RunDao {
        return database.runDao()
    }
}