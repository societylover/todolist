package com.homework.domain.shared

import com.homework.model.data.Importance
import java.time.LocalDate

/**
 * Todo list domain item
 */
data class TodoDomainItem(
    val id: String,
    val text: String,
    val isDone: Boolean,
    val deadlineAt: LocalDate? = null,
    val importance: Importance)