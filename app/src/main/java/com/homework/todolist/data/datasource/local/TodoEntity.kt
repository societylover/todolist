package com.homework.todolist.data.datasource.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.homework.todolist.data.model.Importance
import com.homework.todolist.data.model.TodoItem
import com.homework.todolist.utils.LocalDateConverter.toLocalDate
import com.homework.todolist.utils.LocalDateConverter.toMillis

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey val id: String,
    val text: String,
    val importance: Importance,
    val done: Boolean,
    val createdAt: Long,
    val deadlineAt: Long? = null,
    val updatedAt: Long
)

internal fun TodoItem.toTodoEntity(): TodoEntity =
    TodoEntity(
        id = id,
        text = text,
        importance = importance,
        done = done,
        createdAt = createdAt.toMillis(),
        deadlineAt = deadlineAt.toMillis(),
        updatedAt = updatedAt.toMillis()
    )

internal fun TodoEntity.toTodoItem() : TodoItem =
    TodoItem(
        id = id,
        text = text,
        importance = importance,
        done = done,
        createdAt = createdAt.toLocalDate(),
        deadlineAt = deadlineAt.toLocalDate(),
        updatedAt = updatedAt.toLocalDate()
    )