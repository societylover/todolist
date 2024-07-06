package com.homework.todolist.startdetails

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homework.todolist.R
import com.homework.todolist.auth.MakeAuth
import com.homework.todolist.startdetails.data.StartDetailsUIState
import com.homework.todolist.startdetails.data.StartState
import com.homework.todolist.ui.theme.TodoAppTypography
import com.homework.todolist.ui.theme.TodoColorsPalette
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StartScreen(
    onSuccessAuthed: () -> Unit,
    onFailureAuthed: () -> Unit,
    viewModel: StartDetailsViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            ShowAuthResult(text = stringResource(id = state.textResId))
        }
    }

    if (state.state == StartState.AUTH_START) {
        MakeAuth(authHandler = viewModel.authHandler)
    }

    handleState(state, onFailureAuthed, onSuccessAuthed)
    handleEffects(viewModel, scope, snackbarHostState, context)
}

@Composable
private fun handleState(
    state: StartDetailsUIState,
    onFailureAuthed: () -> Unit,
    onSuccessAuthed: () -> Unit
) {
    LaunchedEffect(key1 = state.state) {
        when (state.state) {
            StartState.AUTH_FAILURE, StartState.AUTH_CANCEL -> {
                delay(2000L)
                onFailureAuthed()
            }
            StartState.AUTH_SUCCESS, StartState.ALREADY_AUTHED -> {
                delay(2000L)
                onSuccessAuthed()
            }
            else -> { }
        }
    }
}

@Composable
private fun handleEffects(
    viewModel: StartDetailsViewModel,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    context: Context
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect {
            if (it is StartDetailsViewModel.Companion.StartEffects.UnknownErrorToast) {
                scope.launch {
                    snackbarHostState.showSnackbar(context.getString(R.string.start_auth_unknown_error))
                }
            }
        }
    }
}

@Composable
private fun ShowAuthResult(text: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = text,
            color = TodoColorsPalette.current.labelPrimaryColor,
            style = TodoAppTypography.current.title)
    }
}