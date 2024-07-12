package com.homework.data.di

import com.homework.data.bus.ErrorBus
import com.homework.data.bus.ErrorBusImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ErrorBusModule {

    @Provides
    @Singleton
    fun provideErrorBus() : ErrorBus = ErrorBusImpl()
}