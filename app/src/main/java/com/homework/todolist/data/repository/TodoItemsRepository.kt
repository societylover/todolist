package com.homework.todolist.data.repository

import com.homework.todolist.data.model.Importance
import com.homework.todolist.data.model.TodoItem
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

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
     * Get item details by it's id
     * @param id Item id
     * @return To-do item info or null
     */
    fun getItemDetails(id: String) : TodoItem?

    /**
     * Remove item from list by it's id
     * @param id To do item id
     */
    suspend fun removeItemById(id: String)

    /**
     * Create new to do list item
     * @param text To do item text
     * @param importance To do item importance
     * @param deadlineAt To do item deadline date time
     * @return New item id
     */
    fun createItem(
        text: String,
        importance: Importance,
        deadlineAt: LocalDate? = null
    ): String

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
    suspend fun updateItem(id: String,
                           text: String,
                           done: Boolean,
                           importance: Importance,
                           deadlineAt: LocalDate?,
                           updated: LocalDateTime = LocalDateTime.now()) : Boolean
}