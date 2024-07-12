package com.homework.network.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface NetworkBinds {

//    @Binds
//    fun provideRemoteDataSource(impl: RemoteDataSourceImpl) : RemoteDataSource
}