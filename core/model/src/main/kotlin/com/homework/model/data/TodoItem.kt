package com.homework.model.data

import java.time.Instant
import java.time.LocalDate

/**
 * To Do list item data
 */
data class TodoItem(
    val id: String,
    val text: String,
    val importance: Importance,
    val done: Boolean,
    val createdAt: Instant = Instant.now(),
    val deadlineAt: LocalDate? = null,
    val updatedAt: Instant = Instant.now()
)