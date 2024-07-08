package com.homework.todolist.data.datasource.remote

import com.homework.todolist.data.model.TodoItem
import com.homework.todolist.shared.data.result.Result

/**
 * Interface of basic remote data source
 */
interface RemoteDataSource {

    /**
     * Get current to-do item list
     * @return List of to-do item at remote source or error details (see [Result])
     */
    suspend fun loadTodos(): Result<List<TodoItem>>

    /**
     * Get details of to-do by it's id
     * @param todoId Id of to-do
     * @return Details of to-do or error details (see [Result])
     */
    suspend fun loadTodoById(todoId: String): Result<TodoItem>

    /**
     * Create new to-do on remote source
     * @param todo Details of new to-do
     * @return Details of created to-do or error details (see [Result])
     */
    suspend fun createNewTodo(todo: TodoItem): Result<TodoItem>

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
    suspend fun updateTodoById(updatedTodo: TodoItem) : Result<TodoItem>

    /**
     * Set new to-do items list
     * @param todos New remote to-do list
     * @return List of new to-do list on remote source or error details (see [Result])
     */
    suspend fun setTodosList(todos: List<TodoItem>) : Result<List<TodoItem>>
}

internal fun RemoteDataSourceStub() : RemoteDataSource {
    return object : RemoteDataSource {
        override suspend fun loadTodos(): Result<List<TodoItem>> { TODO("Not yet implemented") }
        override suspend fun loadTodoById(todoId: String): Result<TodoItem> { TODO("Not yet implemented") }
        override suspend fun createNewTodo(todo: TodoItem): Result<TodoItem> { TODO("Not yet implemented") }
        override suspend fun deleteTodoById(todoId: String): Result<Boolean> { TODO("Not yet implemented") }
        override suspend fun updateTodoById(updatedTodo: TodoItem): Result<TodoItem> { TODO("Not yet implemented") }
        override suspend fun setTodosList(todos: List<TodoItem>): Result<List<TodoItem>> { TODO("Not yet implemented") }
    }
}