package com.homework.todolist.data.datasource.remote.dto

import com.homework.todolist.data.model.Importance
import com.homework.todolist.data.model.TodoItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

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
    val lastUpdatedBy: String
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
        else -> "important"
    }

internal fun TodoItem.toTodoDTO(androidId: String) : TodoDTO =
    TodoDTO(
        id = id,
        text = text,
        importance = importance.toDtoImportance(),
        deadline = deadlineAt.toMillis(),
        createdAt = createdAt.toMillis(),
        changedAt = updateAt.toMillis(),
        done = done,
        lastUpdatedBy = androidId)

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

private fun LocalDate?.toMillis() : Long? =
    this?.atStartOfDay()?.toInstant(ZoneOffset.UTC)?.toEpochMilli()

private fun LocalDate.toMillis() : Long =
    this.atStartOfDay()?.toInstant(ZoneOffset.UTC)?.toEpochMilli() ?: 0L