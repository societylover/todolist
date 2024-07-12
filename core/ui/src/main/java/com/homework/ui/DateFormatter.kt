package com.homework.ui

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Date formatter extension
 */
object DateFormatter {

    /**
     * Convert LocalDate to string representation
     * @param pattern formatter pattern
     * @param locale Current locale
     * @return Converted date to string
     */
    fun LocalDate.asString(
        pattern: String = "d MMMM yyyy",
        locale: Locale = Locale.getDefault()
    ): String =
        this.format(DateTimeFormatter.ofPattern(pattern, locale))
}