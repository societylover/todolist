package com.homework.database.di

import com.homework.database.TodoDatabase
import com.homework.database.dao.TodoEntityDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {

    @Provides
    fun provideTodoDao(
        database: TodoDatabase
    ) : TodoEntityDao =
        database.todoItemDao()
}