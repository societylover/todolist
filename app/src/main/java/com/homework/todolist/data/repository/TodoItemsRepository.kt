package com.homework.todolist.data.repository

import com.homework.todolist.data.model.Importance
import com.homework.todolist.data.model.TodoItem
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import com.homework.todolist.shared.data.result.Result
/**
 * Interface for to do items repository
 */
interface TodoItemsRepository {
    /**
     * Get to do items list
     * @return Flow with exists to do items
     */
    fun getItemsList(): Flow<List<TodoItem>>

    /**
     * Force update item list from remote
     * @return Result of execution
     */
    suspend fun fetchItems() : Result<Boolean>

    /**
     * Get item details by it's id
     * @param id Item id
     * @return To-do item info or null
     */
    suspend fun getItemDetails(id: String): Result<TodoItem?>

    /**
     * Remove item from list by it's id
     * @param id To do item id
     * @return Result of execution
     */
    suspend fun removeItemById(id: String) : Result<Boolean>

    /**
     * Create new to do list item
     * @param text To do item text
     * @param importance To do item importance
     * @param deadlineAt To do item deadline date time
     * @return Result of execution with new to-do item
     */
    suspend fun createItem(
        text: String,
        importance: Importance,
        deadlineAt: LocalDate? = null
    ): Result<Boolean>

    /**
     * Update to do item state
     * @param id To-do item id
     * @param text To-do item text
     * @param done To-do item done state
     * @param importance To-do item importance
     * @param deadlineAt To-do deadline
     * @param updated Updated date
     * @return Update result, where true for success update
     */
    suspend fun updateItem(
        id: String,
        text: String,
        done: Boolean,
        importance: Importance,
        deadlineAt: LocalDate?,
        updated: LocalDate = LocalDate.now()
    ): Result<Boolean>
}