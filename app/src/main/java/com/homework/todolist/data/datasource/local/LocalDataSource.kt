package com.homework.todolist.data.datasource.local

import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    /**
     * Get to do items list
     * @return Flow with exists to do items
     */
    fun getItemsList(): Flow<List<TodoEntity>>

    /**
     * Return items snapshot
     */
    fun getItemsSnapshot() : List<TodoEntity>

    /**
     * Set local data items list
     * @param items New local items
     */
    suspend fun setItemsList(items: List<TodoEntity>)

    /**
     * Remove item from list by it's id
     * @param id To do item id
     */
    suspend fun removeItemById(id: String) : Boolean

    /**
     * Mark todo item as deleted by it's id
     */
    suspend fun markItemAsDeleted(id: String) : Boolean

    /**
     * Get item details by it's id
     * @param id Item id
     * @return To-do item info or null
     */
    suspend fun getItemDetails(id: String): TodoEntity?

    /**
     * Add item to source
     * @param todoItem Item details
     */
    suspend fun addItem(todoItem: TodoEntity) : Boolean

    /**
     * Update item details
     * @param todoItem Item details
     * @return Update result
     */
    suspend fun updateItem(todoItem: TodoEntity) : Boolean
}

internal fun LocalDataSourceStub() : LocalDataSource {
    return object : LocalDataSource {
        override fun getItemsList(): Flow<List<TodoEntity>> {
            TODO("Not yet implemented")
        }

        override fun getItemsSnapshot(): List<TodoEntity> {
            TODO("Not yet implemented")
        }

        override suspend fun setItemsList(items: List<TodoEntity>) {
            TODO("Not yet implemented")
        }

        override suspend fun removeItemById(id: String): Boolean {
            TODO("Not yet implemented")
        }

        override suspend fun markItemAsDeleted(id: String): Boolean {
            TODO("Not yet implemented")
        }

        override suspend fun getItemDetails(id: String): TodoEntity? {
            TODO("Not yet implemented")
        }

        override suspend fun addItem(todoItem: TodoEntity): Boolean {
            TODO("Not yet implemented")
        }

        override suspend fun updateItem(todoItem: TodoEntity): Boolean {
            TODO("Not yet implemented")
        }

    }
}