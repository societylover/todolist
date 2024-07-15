package com.homework.todolist.di

import com.homework.todolist.data.datasource.local.LocalDataSource
import com.homework.todolist.data.datasource.local.LocalDataSourceImpl
import com.homework.todolist.data.datasource.remote.RemoteDataSource
import com.homework.todolist.data.datasource.remote.RemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TodosBinds {

    @Binds
    abstract fun bindLocalDataSource(implementation: LocalDataSourceImpl): LocalDataSource

    @Binds
    abstract fun bindRemoteDataSource(implementation: RemoteDataSourceImpl): RemoteDataSource
}