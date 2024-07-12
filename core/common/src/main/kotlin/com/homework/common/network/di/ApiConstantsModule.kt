package com.homework.common.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ServiceUrl

@Module
@InstallIn(SingletonComponent::class)
object ApiConstantsModule {

    @Provides
    @ServiceUrl
    fun provideServiceUrl() = "" // BuildConfig.BASE_URL
}