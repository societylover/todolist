package com.homework.todolist.ui.screen.todos

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.homework.todolist.R
import com.homework.todolist.data.model.TodoItem
import com.homework.todolist.data.repository.TodoItemsRepository
import com.homework.todolist.shared.data.result.BadRequestRemoteErrors
import com.homework.todolist.shared.data.result.DeviceError
import com.homework.todolist.shared.data.result.RepeatableRemoteErrors
import com.homework.todolist.shared.data.result.Result
import com.homework.todolist.shared.ui.UiEffect
import com.homework.todolist.shared.ui.UiEvent
import com.homework.todolist.shared.ui.ViewModelBase
import com.homework.todolist.ui.screen.tododetails.viewmodel.TodoDetailsViewModel
import com.homework.todolist.ui.screen.todos.TodoListViewModel.Companion.ListEvent
import com.homework.todolist.ui.screen.todos.TodoListViewModel.Companion.ListItemEffects
import com.homework.todolist.ui.screen.todos.data.TodoListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val repository: TodoItemsRepository
) : ViewModelBase<ListEvent, TodoListUiState, ListItemEffects>(
    initialState = TodoListUiState()
) {
    private val scope = viewModelScope +
            CoroutineExceptionHandler { _, _ -> setEffect { ListItemEffects.UnknownErrorToast } }

    init {
        fetchItems()

        scope.launch(Dispatchers.IO) {
            combine(state, repository.getItemsList()) { state, items ->
                TodoListUiState(
                    isDoneShown = state.isDoneShown,
                    todoList = items.filter { state.isDoneShown || !it.done },
                    doneCount = items.count { it.done }
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
            val result = repository.fetchItems()
            if (result is Result.Error) {
                handleError(result, ::fetchItems)
            }
        }
    }

    private fun deleteItem(id: String) {
        scope.launch(Dispatchers.IO) {
            val result = repository.removeItemById(id)
            if (result is Result.Error) {
                handleError(result, ::fetchItems)
            }
        }
    }

    private fun changeTodoDoneState(todoItem: TodoItem, newDoneState: Boolean = !todoItem.done) {
        scope.launch(Dispatchers.IO) {
            val result = repository.updateItem(
                id = todoItem.id,
                text = todoItem.text,
                done = newDoneState,
                importance = todoItem.importance,
                deadlineAt = todoItem.deadlineAt
            )
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
                        R.string.device_error_text,
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
                val todoItem: TodoItem,
                val newDoneState: Boolean = !todoItem.done
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