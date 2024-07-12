package com.homework.common.result

import com.homework.common.result.type.ErrorType

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
        val errorType: ErrorType,
        val message: String?,
        val cause: Throwable? = null) : Result<Nothing>()
}