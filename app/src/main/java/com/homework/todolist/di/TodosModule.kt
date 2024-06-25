package com.homework.todolist.di

import com.homework.todolist.data.repository.TodoItemsRepository
import com.homework.todolist.data.repository.TodoItemsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TodosModule {

    @Provides
    @Singleton
    fun provideTodoItemsRepository() : TodoItemsRepository =
        TodoItemsRepositoryImpl()
}