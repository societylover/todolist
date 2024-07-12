package com.homework.todolist.ui.todos

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.homework.domain.shared.TodoDomainItem
import com.homework.domain.todos.TodosInteractor
import com.homework.todolist.R
import com.homework.todolist.ui.todos.TodoListViewModel.Companion.ListEvent
import com.homework.todolist.ui.todos.TodoListViewModel.Companion.ListItemEffects
import com.homework.todolist.ui.todos.data.TodoListUiState
import com.homework.ui.UiEvent
import com.homework.ui.ViewModelBase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject
import com.homework.common.result.Result
import com.homework.common.result.type.BadRequestRemoteErrors
import com.homework.common.result.type.DeviceError
import com.homework.common.result.type.RepeatableRemoteErrors
import com.homework.ui.UiEffect

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val todosInteractor: TodosInteractor
) : ViewModelBase<ListEvent, TodoListUiState, ListItemEffects>(
    initialState = TodoListUiState()
) {
    private val scope = viewModelScope +
            CoroutineExceptionHandler { _, _ -> setEffect { ListItemEffects.UnknownErrorToast } }

    init {
        fetchItems()

        scope.launch(Dispatchers.IO) {
            combine(state, todosInteractor.getTodosList()) { state, items ->
                TodoListUiState(
                    isDoneShown = state.isDoneShown,
                    todoList = items.filter { state.isDoneShown || !it.isDone },
                    doneCount = items.count { it.isDone }
                )
            }.collect { newState ->
                setState { newState }
            }
        }
    }

    override fun handleEvent(event: UiEvent) {
        when (event) {
            is ListEvent.OnDeleteClicked -> {
                deleteItem(event.id)
            }

            is ListEvent.OnStateChangeClicked -> {
                changeTodoDoneState(event.todoItem)
            }

            is ListEvent.OnVisibilityStateClicked -> {
                triggerShowDoneItemsVisibilityState()
            }

            is ListEvent.ForceItemFetch -> {
                fetchItems()
            }
        }
    }

    private fun fetchItems() {
        scope.launch(Dispatchers.IO) {
            val result = todosInteractor.fetchItems()
            if (result is Result.Error) {
                handleError(result, ::fetchItems)
            }
        }
    }

    private fun deleteItem(id: String) {
        scope.launch(Dispatchers.IO) {
            val result = todosInteractor.removeItemById(id)
            if (result is Result.Error) {
                handleError(result, ::fetchItems)
            }
        }
    }

    private fun changeTodoDoneState(todoItem: TodoDomainItem, newDoneState: Boolean = !todoItem.isDone) {
        scope.launch(Dispatchers.IO) {
            val result = todosInteractor.changeTodoDoneState(todoItem.id, newDoneState)
            if (result is Result.Error) {
                handleError(result, ::fetchItems)
            }
        }
    }

    private fun triggerShowDoneItemsVisibilityState() {
        setState { copy(isDoneShown = !isDoneShown) }
    }

    private fun handleError(error: Result.Error, repeatableCallback: () -> Unit) {
        setEffect {
            when (error.errorType) {
                RepeatableRemoteErrors.SERVER_ERROR -> {
                    ListItemEffects.RepeatableErrorOccurredToast(
                        R.string.device_error_text,
                        callback = repeatableCallback
                    )
                }

                DeviceError.DEVICE_ERROR -> {
                    ListItemEffects.RepeatableErrorOccurredToast(
                        com.homework.todolist.R.string.device_error_text,
                        callback = repeatableCallback
                    )
                }

                BadRequestRemoteErrors.UNAUTHORIZED -> {
                    ListItemEffects.ErrorOccurredToast(
                        R.string.authentication_error_text
                    )
                }

                BadRequestRemoteErrors.MALFORMED_REQUEST -> {
                    ListItemEffects.ErrorOccurredToast(
                        R.string.malformed_request_error_text
                    )
                }

                BadRequestRemoteErrors.NOT_FOUND -> {
                    ListItemEffects.ErrorOccurredToast(
                        R.string.not_found_error_text
                    )
                }

                BadRequestRemoteErrors.UNSYNCHRONIZED_DATA -> {
                    ListItemEffects.ErrorOccurredToast(
                        R.string.bad_revision_error_text
                    )
                }

                else -> {
                    ListItemEffects.ErrorOccurredToast(R.string.todo_details_unknown_error_text)
                }
            }
        }
    }

    companion object {
        /**
         * List screen events
         */
        sealed class ListEvent : UiEvent {
            data object ForceItemFetch : ListEvent()
            data class OnDeleteClicked(val id: String) : ListEvent()
            data class OnStateChangeClicked(
                val todoItem: TodoDomainItem,
                val newDoneState: Boolean = !todoItem.isDone
            ) : ListEvent()

            data object OnVisibilityStateClicked : ListEvent()
        }

        /**
         * List screen effects
         */
        sealed class ListItemEffects : UiEffect {
            data class RepeatableErrorOccurredToast(
                @StringRes val textResId: Int,
                @StringRes val actionResId: Int = R.string.repeat_action_button_text,
                val callback: () -> Unit
            ) : ListItemEffects()

            data class ErrorOccurredToast(@StringRes val textResId: Int) : ListItemEffects()
            data object UnknownErrorToast : ListItemEffects()
        }
    }
}