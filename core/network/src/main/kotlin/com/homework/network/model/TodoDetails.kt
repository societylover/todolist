package com.homework.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TodoDetails(
    @SerialName("status")
    val status: String,
    @SerialName("element")
    val element: Todo,
    @SerialName("revision")
    val revision: Long
)

