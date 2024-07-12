package com.homework.common.result.type

/**
 * Marker for remote errors
 */
interface RemoteError : ErrorType

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
