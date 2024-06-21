package com.homework.todolist.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Date formatter extension
 */
object DateFormatter {
    private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

    /**
     * Convert LocalDate to string representation
     */
    internal fun LocalDate.asString() : String =
        this.format(dateFormatter)
}