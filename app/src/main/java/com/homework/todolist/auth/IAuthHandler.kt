package com.homework.todolist.auth

interface IAuthHandler {
    fun onAuthSuccess(token: String)
    fun onProcessError(error: Throwable)
    fun onAuthCanceled()
}