package com.homework.todolist.data.datasource.local

import com.homework.todolist.data.model.TodoItem
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    /**
     * Get to do items list
     * @return Flow with exists to do items
     */
    fun getItemsList(): Flow<List<TodoItem>>

    /**
     * Set local data items list
     * @param items New local items
     */
    suspend fun setItemsList(items: List<TodoItem>)

    /**
     * Remove item from list by it's id
     * @param id To do item id
     */
    suspend fun removeItemById(id: String) : Boolean

    /**
     * Get item details by it's id
     * @param id Item id
     * @return To-do item info or null
     */
    suspend fun getItemDetails(id: String): TodoItem?

    /**
     * Add item to source
     * @param todoItem Item details
     */
    suspend fun addItem(todoItem: TodoItem) : Boolean

    /**
     * Update item details
     * @param todoItem Item details
     * @return Update result
     */
    suspend fun updateItem(todoItem: TodoItem) : Boolean
}

internal fun LocalDataSourceStub() : LocalDataSource {
    return object : LocalDataSource {
        override fun getItemsList(): Flow<List<TodoItem>> { TODO("Not yet implemented") }
        override suspend fun setItemsList(items: List<TodoItem>) { TODO("Not yet implemented") }
        override suspend fun removeItemById(id: String): Boolean { TODO("Not yet implemented") }
        override suspend fun getItemDetails(id: String): TodoItem? { TODO("Not yet implemented") }
        override suspend fun addItem(todoItem: TodoItem): Boolean { TODO("Not yet implemented") }
        override suspend fun updateItem(todoItem: TodoItem): Boolean { TODO("Not yet implemented") }
    }
}