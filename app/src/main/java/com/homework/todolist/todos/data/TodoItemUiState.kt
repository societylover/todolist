package com.homework.todolist.todos.data

import com.homework.todolist.data.model.TodoItem
import com.homework.todolist.shared.ui.UiState

/**
 * Todo item details state for UI
 */
data class TodoListUiState(
    val isDoneShown: Boolean = false,
    val todoList: List<TodoItem> = emptyList(),
    val doneCount: Int = 0
) : UiState