package com.homework.todolist.shared.data.result

/**
 * Marker interface for errors enums
 */
interface StorageError

interface LocalError : StorageError

interface RemoteError : StorageError

/**
 * Errors that can be resolved by re-querying
 */
enum class RepeatableRemoteErrors : RemoteError {
    // 5xx
    SERVER_ERROR
}

/**
 * Errors caused by incorrectly formulated requests
 */
enum class BadRequestRemoteErrors : RemoteError {
    // 400
    MALFORMED_REQUEST,
    UNSYNCHRONIZED_DATA,
    // 401
    UNAUTHORIZED,
    // 404
    NOT_FOUND
}

/**
 * Basic error when some device error occurred (timeout, no connection, etc.)
 */
enum class DeviceError : RemoteError {
    DEVICE_ERROR
}

/**
 * Error caused by bad local storage errors
 */
enum class LocalErrors : LocalError {
    // Element locally not found
    NOT_FOUND,

    // NOT_FOUND error
    UNKNOWN_ERROR
}