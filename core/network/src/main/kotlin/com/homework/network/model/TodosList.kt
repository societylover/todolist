package com.homework.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TodosList(
    @SerialName("status")
    val status: String,
    @SerialName("list")
    val items: List<Todo>,
    @SerialName("revision")
    val revision: Long
)