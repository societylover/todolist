package com.homework.todolist.auth

import com.yandex.authsdk.YandexAuthResult

/**
 * Basic interface for auth result handler
 */
interface IAuthHandler {
    fun handleResult(result: YandexAuthResult)
}