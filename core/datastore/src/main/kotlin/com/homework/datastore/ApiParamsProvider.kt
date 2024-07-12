package com.homework.datastore

/**
 * Basic interface of necessary parameters for executing server requests
 */
interface ApiParamsProvider {
    suspend fun setClientToken(token: String?)
    fun getClientToken(): String?

    fun setKnownRevision(revision: Long)
    fun getKnownRevision(): Long

    fun getAndroidId() : String
}