package com.homework.todolist.auth

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.Composable
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthResult
import com.yandex.authsdk.YandexAuthSdk

/**
 * Yandex authentication
 */
internal class YandexAuth(
    context: Context,
    private val resultHandler: IAuthHandler)
{
    private val sdk = YandexAuthSdk.create(YandexAuthOptions(context))
    /**
     * Launch auth form
     * @param resultCaller Activity result caller
     */
    fun makeAuth(resultCaller: ActivityResultCaller) {
        val launcher = resultCaller.registerForActivityResult(sdk.contract) { result -> handleResult(result) }
        val loginOptions = YandexAuthLoginOptions()
        launcher.launch(loginOptions)
    }

    @Composable
    fun MakeAuth(authHandler: IAuthHandler) {
        rememberLauncherForActivityResult(
            contract = sdk.contract,
            onResult = { result: YandexAuthResult ->
                authHandler.handleResult(result)
            }
        )
    }

    private fun handleResult(result: YandexAuthResult) {
        when (result) {
            is YandexAuthResult.Success -> {
                resultHandler.onAuthSuccess(result.token.value)
            }
            is YandexAuthResult.Failure -> resultHandler.onProcessError(result.exception)
            YandexAuthResult.Cancelled -> resultHandler.onAuthCanceled()
        }
    }
}