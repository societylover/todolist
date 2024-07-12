package com.homework.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.homework.model.data.Importance
import java.time.Instant
import java.time.LocalDate

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey val id: String,
    val text: String,
    val importance: Importance,
    val done: Boolean,
    val createdAt: Instant,
    val deadlineAt: LocalDate? = null,
    val updatedAt: Instant,
    val localOnly: Boolean,
    val deletedLocally: Boolean = false
)