package com.homework.data.di

import com.homework.data.repository.OfflineFirstTodoDetailsRepository
import com.homework.data.repository.OfflineFirstTodosRepository
import com.homework.data.repository.TodoDetailsRepository
import com.homework.data.repository.TodosRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    internal abstract fun provideTodoDetailsRepository
                (impl: OfflineFirstTodoDetailsRepository): TodoDetailsRepository

    @Binds
    internal abstract fun provideTodosRepository
                (impl: OfflineFirstTodosRepository) : TodosRepository
}