package com.homework.todolist.data.datasource.local

import com.homework.todolist.data.model.TodoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    private val todoItemDao: TodoItemDao
): LocalDataSource {

    override fun getItemsList(): Flow<List<TodoItem>> =
        todoItemDao.getItemsList()

    override suspend fun setItemsList(items: List<TodoItem>) {
        withContext(Dispatchers.IO) {
            todoItemDao.setItemsList(items)
        }
    }

    override suspend fun removeItemById(id: String) : Boolean {
        return withContext(Dispatchers.IO) {
            return@withContext todoItemDao.removeItemById(id) > 0
        }
    }

    override suspend fun getItemDetails(id: String): TodoItem? {
        return todoItemDao.getItemDetails(id)
    }

    override suspend fun addItem(todoItem: TodoItem) : Boolean {
        return withContext(Dispatchers.IO) {
            return@withContext todoItemDao.addItem(todoItem) != -1L

        }
    }

    override suspend fun updateItem(todoItem: TodoItem): Boolean {
        return withContext(Dispatchers.IO) {
            return@withContext todoItemDao.updateItem(todoItem) > 0
        }
    }
}