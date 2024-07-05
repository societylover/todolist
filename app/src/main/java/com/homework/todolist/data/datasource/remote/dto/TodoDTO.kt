package com.homework.todolist.data.datasource.remote.dto

import android.provider.Settings
import com.homework.todolist.data.model.Importance
import com.homework.todolist.data.model.TodoItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Serializable
data class TodoDTO(
    @SerialName("id")
    val id: String,
    @SerialName("text")
    val text: String,
    @SerialName("importance")
    val importance: String,
    @SerialName("deadline")
    val deadline: Long? = null,
    @SerialName("done")
    val done: Boolean,
    @SerialName("color")
    val color: String? = null,
    @SerialName("created_at")
    val createdAt: Long,
    @SerialName("changed_at")
    val changedAt: Long,
    @SerialName("last_updated_by")
    val lastUpdatedBy: String = Settings.Secure.ANDROID_ID
)

internal fun TodoDTO.toTodo() : TodoItem =
    TodoItem(
        id = id,
        text = text,
        importance = getImportance(),
        done = done,
        createdAt = createdAt.toLocalDate(),
        deadlineAt = deadline.toLocalDate(),
        updateAt = changedAt.toLocalDate())

internal fun Importance.toDtoImportance() : String =
    when(this) {
        Importance.LOW -> "low"
        Importance.ORDINARY -> "basic"
        else -> "importance"
    }

internal fun TodoItem.toTodoDTO() : TodoDTO =
    TodoDTO(
        id = id,
        text = text,
        importance = importance.toDtoImportance(),
        deadline = if (deadlineAt == null) 0 else Instant.from(deadlineAt).toEpochMilli(),
        createdAt = Instant.from(createdAt).toEpochMilli(),
        changedAt = if (updateAt == null) 0 else Instant.from(updateAt).toEpochMilli(),
        done = done)

private fun TodoDTO.getImportance() : Importance =
    when(importance.lowercase()) {
        "low" -> Importance.LOW
        "basic" -> Importance.ORDINARY
        else -> Importance.URGENT
    }

private fun Long?.toLocalDate() : LocalDate? =
    this?.toLocalDate()

private fun Long.toLocalDate() : LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()