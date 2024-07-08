package com.homework.todolist.data.datasource.remote.dto

import com.homework.todolist.data.model.TodoItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TodosListDTO(
    @SerialName("status")
    val status: String,
    @SerialName("list")
    val items: List<TodoDTO>,
    @SerialName("revision")
    val revision: Long
)

internal fun TodosListDTO.toTodoList() : List<TodoItem> =
    items.map { it.toTodo() }