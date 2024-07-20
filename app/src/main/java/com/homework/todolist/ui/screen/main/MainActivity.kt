package com.homework.todolist.ui.screen.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homework.todolist.data.userpreferences.theme.UserTheme
import com.homework.todolist.navigation.TodoNavGraph
import com.homework.todolist.navigation.divkit.DivKitInterop
import com.homework.todolist.ui.theme.TodolistTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val themeViewModel: MainViewModel = hiltViewModel()
            val currentTheme by themeViewModel.currentThemeFlow.collectAsStateWithLifecycle(
                initialValue = UserTheme.SYSTEM)

            val useDarkColors = when (currentTheme) {
                UserTheme.SYSTEM -> isSystemInDarkTheme()
                UserTheme.LIGHT -> false
                UserTheme.DARK -> true
            }

            TodolistTheme(darkTheme = useDarkColors) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background)
                {
                    TodoNavGraph(divKitInterop = DivKitInterop(this, isDark = useDarkColors))
                }
            }
        }
    }
}