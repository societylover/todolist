package com.homework.todolist.data.datasource.remote.dto

import com.homework.todolist.data.model.TodoItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TodoDetailsDTO(
    @SerialName("status")
    val status: String,
    @SerialName("element")
    val element: TodoDTO,
    @SerialName("revision")
    val revision: Long
)

internal fun TodoDetailsDTO.toTodo() : TodoItem =
    element.toTodo()

