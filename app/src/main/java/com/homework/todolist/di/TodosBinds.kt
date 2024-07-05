package com.homework.todolist.di

import com.homework.todolist.data.datasource.local.LocalDataSource
import com.homework.todolist.data.datasource.remote.RemoteDataSource
import dagger.Binds
import dagger.Module

@Module
interface TodosBinds {

    @Binds
    fun bindLocalDataSource() : LocalDataSource

    @Binds
    fun bindRemoteDataSource() : RemoteDataSource
}