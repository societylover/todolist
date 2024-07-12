package com.homework.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TodosListInput(
    @SerialName("list")
    val elements: List<Todo>
)