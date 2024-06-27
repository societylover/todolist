package com.homework.todolist.todos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homework.todolist.data.model.TodoItem
import com.homework.todolist.data.model.TodoItemId
import com.homework.todolist.data.repository.TodoItemsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val isDoneShown: Boolean = false,
    val todoList: List<TodoItem> = emptyList(),
    val doneCount: Int = 0
)

/**
 * To do list item viewmodel
 */
@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val todoListRepository: TodoItemsRepository
) : ViewModel() {
    private var _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            combine(_uiState, todoListRepository.getItemsList()) { state, items ->
                UiState(
                    isDoneShown = state.isDoneShown,
                    todoList = items.filter { state.isDoneShown || !it.done },
                    doneCount = items.count { it.done }
                )
            }.collectLatest {
                _uiState.update { it }
            }
        }
    }

    /**
     * Change items visibility state
     */
    fun triggerShowDoneItemsVisibilityState() {
        _uiState.update { it.copy(isDoneShown = !it.isDoneShown) }
    }

    /**
     * Delete to-do task by it's id
     * @param id To-do task identifier
     */
    fun removeTodo(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            todoListRepository.removeItemById(id)
        }
    }

    /**
     * Change to-do task done state
     * @param todoItem Item to state updating
     */
    fun changeTodoDoneState(todoItem: TodoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            todoListRepository.updateItem(
                id = todoItem.id,
                text = todoItem.text,
                done = todoItem.done,
                importance = todoItem.importance,
                deadlineAt = todoItem.deadlineAt)
        }
    }
}