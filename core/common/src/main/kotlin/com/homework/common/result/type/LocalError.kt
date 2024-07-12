package com.homework.common.result.type

/**
 * Marker for local errors (occur on the device)
 */
interface LocalError : ErrorType

/**
 * Error caused by bad local storage errors
 */
enum class LocalErrors(val message: String) : LocalError {
    // Element locally not found
    NOT_FOUND ("Item not found."),

    // Unknown local error error
    UNKNOWN_ERROR ("Unknown local error.")
}