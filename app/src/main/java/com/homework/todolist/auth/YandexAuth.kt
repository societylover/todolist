package com.homework.todolist.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthResult
import com.yandex.authsdk.YandexAuthSdk

/**
 * Yandex authentication
 */
@Composable
internal fun MakeAuth(authHandler: IAuthHandler) {
    val context = LocalContext.current

    val sdk by remember {
        derivedStateOf { YandexAuthSdk.create(YandexAuthOptions(context)) }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = sdk.contract,
        onResult = { result: YandexAuthResult ->
            authHandler.handleResult(result)
        }
    )

    LaunchedEffect(key1 = Unit) {
        launcher.launch(YandexAuthLoginOptions())
    }
}