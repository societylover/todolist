package com.homework.domain.todos

import com.homework.domain.shared.TodoDomainItem

/**
 * Todo items state
 */
data class TodoItems(
    val isDoneShown: Boolean = false,
    val todoList: List<TodoDomainItem> = emptyList(),
    val doneCount: Int = 0
)
