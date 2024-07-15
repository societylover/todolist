package com.homework.todolist.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

object LocalDateConverter {

    internal fun Long?.toLocalDate() : LocalDate? =
        this?.toLocalDate()

    internal fun Long.toLocalDate() : LocalDate =
        Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

    internal fun LocalDate?.toMillis() : Long? =
        this?.atStartOfDay()?.toInstant(ZoneOffset.UTC)?.toEpochMilli()

    internal fun LocalDate.toMillis() : Long =
        this.atStartOfDay()?.toInstant(ZoneOffset.UTC)?.toEpochMilli() ?: 0L
}