package com.homework.todolist.data.datasource.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TodoDetailsInputDTO(
    @SerialName("element")
    val todoDTO: TodoDTO
)
