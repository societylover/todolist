package com.homework.todolist.ui.screen.tododetails.data

import com.homework.todolist.R
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
    val importanceState: ImportanceItem = ImportanceItem(),
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
        importanceState =  this.importance.toImportanceItem(),
        isDone = this.done,
        doUntil = this.deadlineAt ?: LocalDate.now(),
        isDeadlineSet = this.deadlineAt != null
    )

internal fun Importance.toImportanceItem() : ImportanceItem {
    val (resId, descId, isHighlighted) =
        when(this) {
            Importance.LOW -> Triple(R.string.todo_item_importance_low, R.string.todo_item_importance_low_desc, false)
            Importance.ORDINARY -> Triple(R.string.todo_item_importance_ordinary, R.string.todo_item_importance_ordinary_desc,false)
            Importance.URGENT -> Triple(R.string.todo_item_importance_urgent, R.string.todo_item_importance_urgent_desc, true)
        }

    return ImportanceItem(this, resId, descId, isHighlighted)
}