package com.homework.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.homework.todolist.auth.IAuthHandler
import com.homework.todolist.auth.YandexAuth
import com.homework.todolist.data.provider.ApiParamsProvider
import com.homework.todolist.navigation.TodoNavGraph
import com.homework.todolist.ui.theme.TodolistTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

var isAuthed = false

@AndroidEntryPoint
class MainActivity : ComponentActivity(), IAuthHandler {

    @Inject lateinit var apiParamsProvider: ApiParamsProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TodolistTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background)
                {
                    TodoNavGraph()
                }
            }
        }
    }

    override fun onStart() {
        savedStateRegistry.isRestored
        super.onStart()
        if (!isAuthed) {
            val auth: YandexAuth = YandexAuth(this, object : IAuthHandler {
                override fun onAuthSuccess(token: String) {
                    lifecycleScope.launch {
                        apiParamsProvider.setClientToken(token)
                    }

                    println("onAuthSuccess with token: $token")
                }

                override fun onProcessError(error: Throwable) {
                    println("onProcessError error: ${error.message}")
                }

                override fun onAuthCanceled() {
                    println("onAuthCanceled")
                }
            })
            isAuthed = true
        }
    }

    override fun onAuthSuccess(token: String) {
        TODO("Not yet implemented")
    }

    override fun onProcessError(error: Throwable) {
        TODO("Not yet implemented")
    }

    override fun onAuthCanceled() {
        TODO("Not yet implemented")
    }
}