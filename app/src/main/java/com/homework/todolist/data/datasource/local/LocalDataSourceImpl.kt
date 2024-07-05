package com.homework.todolist.data.datasource.local

import com.homework.todolist.data.model.TodoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(): LocalDataSource {

    private val _itemsFlow =
        MutableStateFlow(emptyList<TodoItem>())

    override fun getItemsList(): Flow<List<TodoItem>> =
        _itemsFlow.asSharedFlow()

    override suspend fun setItemsList(items: List<TodoItem>) {
        withContext(Dispatchers.IO) {
            _itemsFlow.update { items }
        }
    }

    override suspend fun removeItemById(id: String) : Boolean {
        return withContext(Dispatchers.IO) {
            if (_itemsFlow.value.firstOrNull { it.id == id } == null)
                return@withContext false

            _itemsFlow.update { it.filter { item -> item.id != id } }
            true
        }
    }

    override suspend fun getItemDetails(id: String): TodoItem? {
        return _itemsFlow.value.firstOrNull { it.id == id }
    }

    override suspend fun addItem(todoItem: TodoItem) : Boolean {
        return withContext(Dispatchers.IO) {
            if (_itemsFlow.value.firstOrNull { it.id == todoItem.id } != null)
                return@withContext false

            _itemsFlow.update { it + todoItem }
            true
        }
    }

    override suspend fun updateItem(todoItem: TodoItem): Boolean {
        return withContext(Dispatchers.IO) {
            if (_itemsFlow.value.firstOrNull { it.id == todoItem.id } != null)
                return@withContext false

            _itemsFlow.update {
                val itemIndex = it.indexOfFirst { item -> item.id == todoItem.id }
                val mutableList = it.toMutableList()
                mutableList.apply {
                    this[itemIndex]  = todoItem
                }
            }
            true
        }
    }
}