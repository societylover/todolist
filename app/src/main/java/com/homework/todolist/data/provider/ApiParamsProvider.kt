package com.homework.todolist.data.provider

import kotlinx.coroutines.flow.Flow

/**
 * Basic interface of necessary parameters for executing server requests
 */
interface ApiParamsProvider {
    fun getClientTokenFlow() : Flow<String?>

    suspend fun setClientToken(token: String?)
    suspend fun getClientToken() : String?
    fun getClientTokenBlocking(): String?

    suspend fun setKnownRevision(revision: Long?)
    fun setKnownRevisionBlocking(revision: Long?)
    suspend fun getKnownRevision() : Long?
    fun getKnownRevisionBlocking(): Long?

    fun getAndroidId() : String
}