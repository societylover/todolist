package com.homework.todolist.ui.screen.tododetails.data

import androidx.annotation.StringRes
import com.homework.todolist.R
import com.homework.todolist.data.model.Importance

/**
 * Ui state for importance item
 */
data class ImportanceItem(
    val importance: Importance = Importance.LOW,
    @StringRes val importanceResId: Int = R.string.todo_item_importance_low,
    val isHighlighted: Boolean = false
)