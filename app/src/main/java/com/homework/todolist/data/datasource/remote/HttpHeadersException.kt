package com.homework.todolist.data.datasource.remote

/**
 * Exception while preparing http request
 */
class HttpPreparationException(message: String,
                               val reason: PreparationExceptionReason) : Exception(message)

/**
 * Reason of interceptor exception
 */
enum class PreparationExceptionReason {
    BAD_TOKEN,
    BAD_REVISION
}