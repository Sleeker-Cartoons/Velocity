package com.sleeker.velocity.di


import com.sleeker.velocity.domain.repository.RunRepositoryImpl
import com.sleeker.velocity.domain.repository.RunRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindRunRepository(impl: RunRepositoryImpl): RunRepository
}