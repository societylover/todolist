package com.homework.data.model

import com.homework.data.util.LocalDateConverter.toLocalDate
import com.homework.data.util.LocalDateConverter.toMillis
import com.homework.database.model.TodoEntity
import com.homework.model.data.Importance
import com.homework.model.data.TodoItem
import com.homework.network.model.Todo
import java.time.Instant

internal fun TodoItem.toTodoEntity(localOnly: Boolean = true, deletedLocally: Boolean = false) : TodoEntity {
    return TodoEntity(
        id = id,
        text = text,
        importance = importance,
        done = done,
        createdAt = createdAt,
        deadlineAt = deadlineAt,
        updatedAt = updatedAt,
        localOnly = localOnly,
        deletedLocally = deletedLocally
    )
}

internal fun TodoEntity.toTodoItem() : TodoItem {
    return TodoItem(
        id = id,
        text = text,
        importance = importance,
        done = done,
        createdAt = createdAt,
        deadlineAt = deadlineAt,
        updatedAt = updatedAt
    )
}


internal fun TodoEntity.toTodo(lastUpdatedBy: String) : Todo {
    return Todo(
        id = id,
        text = text,
        importance = importance.asString(),
        deadline = deadlineAt.toMillis(),
        done = done,
        color = null,
        createdAt = createdAt.toEpochMilli(),
        changedAt = updatedAt.toEpochMilli(),
        lastUpdatedBy = lastUpdatedBy
    )
}

internal fun Todo.toTodoEntity(localOnly: Boolean = false, deletedLocally: Boolean = false) : TodoEntity {
    return TodoEntity(
        id = id,
        text = text,
        importance = getImportanceString(),
        done = done,
        createdAt = Instant.ofEpochMilli(createdAt),
        deadlineAt = deadline.toLocalDate(),
        updatedAt = Instant.ofEpochMilli(changedAt),
        localOnly = localOnly,
        deletedLocally = deletedLocally
    )
}

private fun Importance.asString() : String =
    when(this) {
        Importance.LOW -> "low"
        Importance.ORDINARY -> "basic"
        else -> "important"
    }

private fun Todo.getImportanceString() : Importance =
    when(importance.lowercase()) {
        "low" -> Importance.LOW
        "basic" -> Importance.ORDINARY
        else -> Importance.URGENT
    }