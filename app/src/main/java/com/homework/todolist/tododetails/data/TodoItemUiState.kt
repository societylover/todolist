package com.homework.todolist.tododetails.data

import com.homework.todolist.data.model.Importance
import com.homework.todolist.data.model.TodoItem
import com.homework.todolist.shared.ui.UiState
import java.time.LocalDate

/**
 * Todo item details state for UI
 */
data class TodoItemUiState(
    val id: String? = null,
    val text: String = "",
    val importance: Importance = Importance.ORDINARY,
    val isDone: Boolean = false,
    val doUntil: LocalDate = LocalDate.now(),
    val isDeadlineSet: Boolean = false
) : UiState

/**
 * From model to Ui state converter
 */
internal fun TodoItem.toTodoItemUiState() =
    TodoItemUiState(
        id = this.id,
        text = this.text,
        importance = this.importance,
        isDone = this.done,
        doUntil = this.deadlineAt ?: LocalDate.now(),
        isDeadlineSet = this.deadlineAt != null
    )