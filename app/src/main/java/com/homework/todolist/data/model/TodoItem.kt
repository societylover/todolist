package com.homework.todolist.data.model

import java.time.LocalDate

/**
 * To Do list item data
 */
data class TodoItem(
    val id: String,
    val text: String,
    val importance: Importance,
    val done: Boolean,
    val createdAt: LocalDate = LocalDate.now(),
    val deadlineAt: LocalDate? = null,
    val updateAt: LocalDate = LocalDate.now()
)