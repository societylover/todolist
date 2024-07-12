package com.homework.network

import com.homework.network.model.Todo
import com.homework.network.model.TodoDetails
import com.homework.network.model.TodosList
import com.homework.common.result.Result

/**
 * Interface of basic remote data source
 */
interface RemoteDataSource {

    /**
     * Get current to-do item list
     * @return List of to-do item at remote source or error details (see [Result])
     */
    suspend fun loadTodos(): Result<TodosList>

    /**
     * Get details of to-do by it's id
     * @param todoId Id of to-do
     * @return Details of to-do or error details (see [Result])
     */
    suspend fun loadTodoById(todoId: String): Result<TodoDetails>

    /**
     * Create new to-do on remote source
     * @param todo Details of new to-do
     * @return Details of created to-do or error details (see [Result])
     */
    suspend fun createNewTodo(todo: Todo): Result<TodoDetails>

    /**
     * Delete to-do on remote source by it's id
     * @param todoId To-do id
     * @return True if item was deleted; otherwise, error details (see [Result])
     */
    suspend fun deleteTodoById(todoId: String): Result<Boolean>

    /**
     * Update to-do on remote source by it's id and new details
     * @param updatedTodo New updated to-do values
     * @return Details of updated to-do or error details (see [Result])
     */
    suspend fun updateTodoById(updatedTodo: Todo) : Result<TodoDetails>

    /**
     * Set new to-do items list
     * @param todos New remote to-do list
     * @return List of new to-do list on remote source or error details (see [Result])
     */
    suspend fun setTodosList(todos: List<Todo>) : Result<TodosList>
}