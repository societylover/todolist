package com.homework.todolist.startdetails

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.homework.todolist.auth.IAuthHandler
import com.homework.todolist.data.provider.ApiParamsProvider
import com.homework.todolist.shared.ui.UiEffect
import com.homework.todolist.shared.ui.UiEvent
import com.homework.todolist.shared.ui.ViewModelBase
import com.homework.todolist.startdetails.StartDetailsViewModel.Companion.StartEffects
import com.homework.todolist.startdetails.StartDetailsViewModel.Companion.StartEvent
import com.homework.todolist.startdetails.data.StartDetailsUIState
import com.homework.todolist.startdetails.data.StartState
import com.yandex.authsdk.YandexAuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class StartDetailsViewModel @Inject constructor(
    private val apiParamsProvider: ApiParamsProvider
) : ViewModelBase<StartEvent, StartDetailsUIState, StartEffects>(StartDetailsUIState()) {

    private val scope = viewModelScope + CoroutineExceptionHandler { _, throwable ->
        Log.d(START_DETAILS_TAG, throwable.stackTraceToString())
        setEffect { StartEffects.UnknownErrorToast }
    }

    init {
        if (apiParamsProvider.getClientToken() != null) {
            setEvent(StartEvent.OnAlreadyAuthed)
        } else {
            setEvent(StartEvent.OnAuthStart)
        }
    }

    val authHandler = object : IAuthHandler {
        override fun handleResult(result: YandexAuthResult) {
            scope.launch {
                when(result) {
                    is YandexAuthResult.Success -> {
                        apiParamsProvider.setClientToken(result.token.value)
                        setEvent(StartEvent.OnAuthedSuccessfully)
                    }
                    is YandexAuthResult.Cancelled -> {
                        apiParamsProvider.setClientToken(null)
                        setEvent(StartEvent.OnAuthedCanceled)
                    }
                    is YandexAuthResult.Failure -> {
                        apiParamsProvider.setClientToken(null)
                        setEvent(StartEvent.OnAuthedFailed)
                    }
                }
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
            data object OnNoAuthed : StartEvent()
            data object OnAuthStart : StartEvent()
            data object OnAlreadyAuthed : StartEvent()
            data object OnAuthedSuccessfully : StartEvent()
            data object OnAuthedCanceled : StartEvent()
            data object OnAuthedFailed : StartEvent()
        }

        /**
         * Start screen effects
         */
        sealed class StartEffects : UiEffect {
            data object UnknownErrorToast : StartEffects()
        }

        private const val START_DETAILS_TAG = "START_DETAILS"
    }
}