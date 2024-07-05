package com.homework.todolist.startdetails

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.homework.todolist.R
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
    val state by viewModel.state.collectAsState()
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

    val authLauncher = rememberLauncherForActivityResult(
        contract = viewModel.yandexAuth.sdk.contract,
        onResult = { result: YandexAuthResult ->
            authViewModel.yandexAuth.handleResult(result)
        }
    )

    handleEffects(viewModel, scope, snackbarHostState, context)
    handleState(state, onFailureAuthed, onSuccessAuthed)
}

@Composable
private fun handleState(
    state: StartDetailsUIState,
    onFailureAuthed: () -> Unit,
    onSuccessAuthed: () -> Unit,
    onAuthStart:
) {
    LaunchedEffect(key1 = state.state) {
        if (state.state == StartState.AUTH_FAILURE
            || state.state == StartState.AUTH_CANCEL
        ) {
            delay(2000L)
            onFailureAuthed()
        } else if (state.state == StartState.AUTH_SUCCESS) {
            delay(1000L)
            onSuccessAuthed()
        } else if (state.state == StartState.AUTH_START) {

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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = text,
            color = TodoColorsPalette.current.labelPrimaryColor,
            style = TodoAppTypography.current.largeTitle)
    }
}