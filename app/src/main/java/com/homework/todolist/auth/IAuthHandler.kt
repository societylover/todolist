package com.homework.todolist.auth

import com.yandex.authsdk.YandexAuthResult

interface IAuthHandler {
    fun handleResult(result: YandexAuthResult)
}