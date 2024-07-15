package com.homework.todolist.utils

import androidx.room.TypeConverter
import java.time.LocalDate

object Converters {

    @JvmStatic
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }

    @JvmStatic
    @TypeConverter
    fun toLocalDate(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }
}