package com.homework.todolist.data.provider

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

internal class ApiParamsProviderImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ApiParamsProvider {

    override fun getClientTokenFlow(): Flow<String?> =
        dataStore.data.map {
            it[clientTokenKey]
        }

    override suspend fun setClientToken(token: String?) =
        setStoredValue(clientTokenKey, token, DEFAULT_CLIENT_TOKEN)

    override suspend fun getClientToken(): String? =
        getNullableValue(clientTokenKey, DEFAULT_CLIENT_TOKEN)

    override fun getClientTokenBlocking(): String? =
        runBlocking { getClientToken() }

    override suspend fun setKnownRevision(revision: Long?) =
        setStoredValue(revisionTokenKey, revision, DEFAULT_REVISION_TOKEN)

    override fun setKnownRevisionBlocking(revision: Long?) {
        runBlocking(Dispatchers.IO) {
            setKnownRevision(revision)
        }
    }

    override suspend fun getKnownRevision(): Long? =
        getNullableValue(revisionTokenKey, DEFAULT_REVISION_TOKEN)

    override fun getKnownRevisionBlocking(): Long? =
        runBlocking { getKnownRevision() }

    private suspend fun <T> setStoredValue(key: Preferences.Key<T>, value: T?, defaultValue: T) {
        dataStore.updateData { parameters ->
            val updated = parameters.toMutablePreferences()
            updated[key] = value ?: defaultValue
            updated
        }
    }

    private suspend fun <T> getNullableValue(key: Preferences.Key<T>, defaultValue: T): T? {
        val storedValue = dataStore.data.firstOrNull()?.get(key)
        return if (storedValue == null || storedValue == defaultValue) null else
            storedValue
    }

    companion object {
        // Key for data-store instance
        internal const val API_PREFERENCES = "api-preferences"

        private val clientTokenKey by lazy { stringPreferencesKey("client-token") }
        private val revisionTokenKey by lazy { longPreferencesKey("revision-token") }

        private const val DEFAULT_CLIENT_TOKEN = "none"
        private const val DEFAULT_REVISION_TOKEN = 0L
    }
}