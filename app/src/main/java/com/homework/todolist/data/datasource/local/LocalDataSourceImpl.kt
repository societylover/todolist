package com.homework.todolist.data.datasource.local

import com.homework.todolist.data.model.TodoItem
import com.homework.todolist.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    private val todoItemDao: TodoItemDao,
    @ApplicationScope private val externalScope: CoroutineScope
): LocalDataSource {

    override fun getItemsList(): Flow<List<TodoItem>> =
        todoItemDao.getItemsList().map {
            it.map { entity ->
                entity.toTodoItem()
            }
        }

    override suspend fun setItemsList(items: List<TodoItem>) {
        withContext(externalScope.coroutineContext) {
            todoItemDao.setItemsList(items.map { it.toTodoEntity() })
        }
    }

    override suspend fun removeItemById(id: String) : Boolean {
        return withContext(externalScope.coroutineContext) {
            return@withContext todoItemDao.removeItemById(id) > 0
        }
    }

    override suspend fun getItemDetails(id: String): TodoItem? {
        return todoItemDao.getItemDetails(id)?.toTodoItem()
    }

    override suspend fun addItem(todoItem: TodoItem) : Boolean {
        return withContext(externalScope.coroutineContext) {
            return@withContext todoItemDao.addItem(todoItem.toTodoEntity()) != -1L
        }
    }

    override suspend fun updateItem(todoItem: TodoItem): Boolean {
        return withContext(externalScope.coroutineContext) {
            return@withContext todoItemDao.updateItem(todoItem.toTodoEntity()) > 0
        }
    }
}