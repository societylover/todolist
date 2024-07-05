package com.homework.todolist.shared.data.result

/**
 * Response representation
 */
sealed class Result<out T> {

    /**
     * Success result of network request.
     * Contains response data.
     */
    data class Success<out T>(val data: T) : Result<T>()

    /**
     * Failure result of network request.
     * Contains details error details.
     */
    data class Error(
        val errorType: StorageError,
        val message: String,
        val cause: Throwable? = null) : Result<Nothing>()
}