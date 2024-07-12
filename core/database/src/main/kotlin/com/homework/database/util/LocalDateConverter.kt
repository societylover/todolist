package com.homework.database.util

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

internal class LocalDateConverter {

    @TypeConverter
    fun longToLocalDate(value: Long?): LocalDate? {
        return if (value == null) {
            null
        } else {
            Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalDate()
        }
    }

    @TypeConverter
    fun localDateToLong(localDate: LocalDate?): Long? =
        localDate?.atStartOfDay()?.toInstant(ZoneOffset.UTC)?.toEpochMilli()
}