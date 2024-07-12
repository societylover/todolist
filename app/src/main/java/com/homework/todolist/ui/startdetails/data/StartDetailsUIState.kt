package com.homework.todolist.ui.startdetails.data

import androidx.annotation.StringRes
import com.homework.todolist.R
import com.homework.ui.UiState

/**
 * Start screen UI state
 */
data class StartDetailsUIState(
    @StringRes val textResId: Int = R.string.start_auth_not_authed_text,
    val state: StartState = StartState.NOT_AUTHED
) : UiState

enum class StartState {
    NOT_AUTHED,
    ALREADY_AUTHED,
    AUTH_START,
    AUTH_SUCCESS,
    AUTH_FAILURE,
    AUTH_CANCEL
}

internal fun StartDetailsUIState(state: StartState) : StartDetailsUIState {
    return when(state) {
        StartState.NOT_AUTHED -> StartDetailsUIState(R.string.start_auth_not_authed_text, state)
        StartState.ALREADY_AUTHED -> StartDetailsUIState(R.string.start_already_authed_text, state)
        StartState.AUTH_START -> StartDetailsUIState(R.string.start_auth_start_text, state)
        StartState.AUTH_SUCCESS -> StartDetailsUIState(R.string.start_auth_successfully_text , state)
        StartState.AUTH_FAILURE -> StartDetailsUIState(R.string.start_auth_failure_text , state)
        StartState.AUTH_CANCEL -> StartDetailsUIState(R.string.start_auth_cancel_text , state)
    }
}