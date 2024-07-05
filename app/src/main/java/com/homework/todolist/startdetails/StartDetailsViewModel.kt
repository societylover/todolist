package com.homework.todolist.startdetails

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.homework.todolist.R
import com.homework.todolist.auth.IAuthHandler
import com.homework.todolist.data.provider.ApiParamsProvider
import com.homework.todolist.shared.ui.UiEffect
import com.homework.todolist.shared.ui.UiEvent
import com.homework.todolist.shared.ui.ViewModelBase
import com.homework.todolist.startdetails.StartDetailsViewModel.Companion.StartEffects
import com.homework.todolist.startdetails.StartDetailsViewModel.Companion.StartEvent
import com.homework.todolist.startdetails.data.StartDetailsUIState
import com.homework.todolist.startdetails.data.StartState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class StartDetailsViewModel @Inject constructor(
    private val apiParamsProvider: ApiParamsProvider
) : ViewModelBase<StartEvent, StartDetailsUIState, StartEffects>(

    if (apiParamsProvider.getClientTokenBlocking() != null)
        StartDetailsUIState(StartState.ALREADY_AUTHED)
    else
        StartDetailsUIState()
) {

    private val scope = viewModelScope + CoroutineExceptionHandler { _, throwable ->
        Log.d(START_DETAILS_TAG, throwable.stackTraceToString())
        setEffect { StartEffects.UnknownErrorToast }
    }

    init {
        if (apiParamsProvider.getClientTokenBlocking() != null) {
            setEvent(StartEvent.OnAlreadyAuthed)
        }
    }

    val authHandler = object : IAuthHandler {
        override fun onAuthSuccess(token: String) {
            scope.launch {
                apiParamsProvider.setClientToken(token)
                setEvent(StartEvent.OnAuthedSuccessfully)
            }
        }

        override fun onProcessError(error: Throwable) {
            scope.launch {
                apiParamsProvider.setClientToken(null)
                setEvent(StartEvent.OnAuthedFailed)
            }
        }

        override fun onAuthCanceled() {
            scope.launch {
                apiParamsProvider.setClientToken(null)
                setEvent(StartEvent.OnAuthedCanceled)
            }
        }
    }

    override fun handleEvent(event: UiEvent) {
        when (event) {
            is StartEvent.OnNoAuthed -> setState { StartDetailsUIState(state = StartState.NOT_AUTHED) }
            is StartEvent.OnAuthStart -> setState { StartDetailsUIState(state = StartState.AUTH_START) }
            is StartEvent.OnAlreadyAuthed -> setState { StartDetailsUIState(state = StartState.ALREADY_AUTHED) }
            is StartEvent.OnAuthedSuccessfully -> setState { StartDetailsUIState(state = StartState.AUTH_SUCCESS) }
            is StartEvent.OnAuthedCanceled -> setState { StartDetailsUIState(state = StartState.AUTH_CANCEL) }
            is StartEvent.OnAuthedFailed -> setState { StartDetailsUIState(state = StartState.AUTH_FAILURE) }
        }
    }

    companion object {

        /**
         * Start screen events
         */
        sealed class StartEvent : UiEvent {
            object OnNoAuthed : StartEvent()
            object OnAuthStart : StartEvent()
            object OnAlreadyAuthed : StartEvent()
            object OnAuthedSuccessfully : StartEvent()
            object OnAuthedCanceled : StartEvent()
            object OnAuthedFailed : StartEvent()
        }

        /**
         * Start screen effects
         */
        sealed class StartEffects : UiEffect {
            object UnknownErrorToast : StartEffects()
        }

        private const val START_DETAILS_TAG = "START_DETAILS"
    }
}