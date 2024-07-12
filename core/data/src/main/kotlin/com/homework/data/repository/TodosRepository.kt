package com.homework.data.repository

import com.homework.common.result.Result
import com.homework.model.data.TodoItem
import kotlinx.coroutines.flow.Flow

interface TodosRepository {

    /**
     * Get to do items list
     * @return Flow with exists to do items
     */
    fun getTodosList(): Flow<List<TodoItem>>

    /**
     * Remove item from list by it's id
     * @param id To do item id
     * @return Result of execution
     */
    suspend fun removeItemById(id: String) : Result<Boolean>

    /**
     * Update to-do item done state by it's id
     * @param id Item id
     * @param done New done state
     * @return Result of done state changing
     */
    suspend fun changeItemDoneState(
        id: String,
        done: Boolean
    ) : Result<Boolean>

    /**
     * Force getting fetch items
     * @return Result of fetching
     */
    suspend fun fetchItems() : Result<Boolean>
}