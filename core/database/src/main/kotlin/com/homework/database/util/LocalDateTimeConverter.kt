package com.homework.database.util

import androidx.room.TypeConverter
import java.time.Instant

class InstantTimeConverter {

    @TypeConverter
    fun longToInstant(value: Long?): Instant? {
        return if (value == null) null else Instant.ofEpochMilli(value)
    }

    @TypeConverter
    fun instantToLong(instant: Instant?): Long? =
        instant?.toEpochMilli()
}