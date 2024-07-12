package com.homework.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TodoDetailsInput(
    @SerialName("element")
    val todo: Todo
)
