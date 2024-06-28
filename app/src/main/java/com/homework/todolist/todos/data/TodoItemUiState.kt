package com.homework.todolist.todos.data

import com.homework.todolist.data.model.TodoItem
import com.homework.todolist.shared.UiState
import com.homework.todolist.tododetails.data.TodoItemUiState
import java.time.LocalDate

/**
 * Todo item details state for UI
 */
data class TodoListUiState(
    val isDoneShown: Boolean = false,
    val todoList: List<TodoItem> = emptyList(),
    val doneCount: Int = 0
): UiState