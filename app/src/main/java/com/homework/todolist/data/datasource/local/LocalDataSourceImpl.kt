package com.homework.todolist.data.datasource.local

import com.homework.todolist.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    private val todoItemDao: TodoItemDao,
    @ApplicationScope private val externalScope: CoroutineScope
): LocalDataSource {

    override fun getItemsList(): Flow<List<TodoEntity>> =
        todoItemDao.getItemsList()

    override fun getItemsSnapshot(): List<TodoEntity> {
        return todoItemDao.getItemsSnapshot()
    }

    override suspend fun setItemsList(items: List<TodoEntity>) {
        externalScope.launch {
            todoItemDao.setItemsList(items)
        }
    }

    override suspend fun removeItemById(id: String) : Boolean {
        return withContext(externalScope.coroutineContext) {
            return@withContext todoItemDao.removeItemById(id) > 0
        }
    }

    override suspend fun markItemAsDeleted(id: String): Boolean {
        return withContext(externalScope.coroutineContext) {
            return@withContext todoItemDao.markItemAsDeleted(id) > 0
        }
    }

    override suspend fun getItemDetails(id: String): TodoEntity? {
        return todoItemDao.getItemDetails(id)
    }

    override suspend fun addItem(todoItem: TodoEntity): Boolean {
        externalScope.launch {
            todoItemDao.addItem(todoItem)
        }
        return true
    }

    override suspend fun updateItem(todoItem: TodoEntity): Boolean {
        externalScope.launch {
            todoItemDao.updateItem(todoItem)
        }
        return true
    }
}