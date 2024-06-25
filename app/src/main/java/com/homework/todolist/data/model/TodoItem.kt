package com.homework.todolist.data.model

import java.time.LocalDate
import java.time.LocalDateTime

typealias TodoItemId = String

/**
 * To Do list item data
 */
data class TodoItem(
    val id: TodoItemId,
    val text: String,
    val importance: Importance,
    val done: Boolean,
    val createdAt: LocalDate = LocalDate.now(),
    val deadlineAt: LocalDate? = null,
    val updateAt: LocalDateTime? = null
)