package com.homework.todolist.auth

import androidx.activity.ComponentActivity
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthResult
import com.yandex.authsdk.YandexAuthSdk

class YandexAuth(activity: ComponentActivity,
    private val resultHandler: IAuthHandler) {
    init {
        val sdk = YandexAuthSdk.create(YandexAuthOptions(activity))

        val launcher = activity.registerForActivityResult(sdk.contract) { result -> handleResult(result) }
        val loginOptions = YandexAuthLoginOptions()
        launcher.launch(loginOptions)
    }

    private fun handleResult(result: YandexAuthResult) {
        when (result) {
            is YandexAuthResult.Success -> resultHandler.onAuthSuccess(result.token.value)
            is YandexAuthResult.Failure -> resultHandler.onProcessError(result.exception)
            YandexAuthResult.Cancelled -> resultHandler.onAuthCanceled()
        }
    }
}