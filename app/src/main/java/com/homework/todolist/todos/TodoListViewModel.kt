package com.homework.todolist.todos

import androidx.lifecycle.viewModelScope
import com.homework.todolist.data.model.TodoItem
import com.homework.todolist.data.repository.TodoItemsRepository
import com.homework.todolist.shared.UiEffect
import com.homework.todolist.shared.UiEvent
import com.homework.todolist.shared.ViewModelBase
import com.homework.todolist.todos.TodoListViewModel.Companion.ListEvent
import com.homework.todolist.todos.TodoListViewModel.Companion.ListItemEffects
import com.homework.todolist.todos.data.TodoListUiState
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
        }
    }

    private fun deleteItem(id: String) {
        scope.launch(Dispatchers.IO) {
            repository.removeItemById(id)
        }
    }

    private fun changeTodoDoneState(todoItem: TodoItem, newDoneState: Boolean = !todoItem.done) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateItem(
                id = todoItem.id,
                text = todoItem.text,
                done = newDoneState,
                importance = todoItem.importance,
                deadlineAt = todoItem.deadlineAt
            )
        }
    }

    private fun triggerShowDoneItemsVisibilityState() {
        setState { copy(isDoneShown = !isDoneShown) }
    }

    companion object {
        /**
         * List screen events
         */
        sealed class ListEvent : UiEvent {
            data class OnDeleteClicked(val id: String) : ListEvent()
            data class OnStateChangeClicked(
                val todoItem: TodoItem,
                val newDoneState: Boolean = !todoItem.done
            ) : ListEvent()

            object OnVisibilityStateClicked : ListEvent()
        }

        /**
         * List screen effects
         */
        sealed class ListItemEffects : UiEffect {
            object UnknownErrorToast : ListItemEffects()
        }
    }
}