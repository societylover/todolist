package com.homework.todolist.data.userpreferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.homework.todolist.data.userpreferences.theme.UserTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class UserPreferencesProviderImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesProvider {
    override suspend fun setUserTheme(theme: UserTheme) {
        dataStore.updateData { parameters ->
            val updated = parameters.toMutablePreferences()
            updated[userThemeKey] = theme.name
            updated
        }
    }

    override fun getUserPreferableThemeFlow(): Flow<UserTheme> {
        return dataStore.data.map {
            val value = it[userThemeKey]
            if (value == null) {
                UserTheme.SYSTEM
            } else {
                UserTheme.valueOf(value)
            }
        }
    }

    companion object {
        // Key for data-store instance
        internal const val USER_PREFERENCES = "user-preferences"

        private val userThemeKey by lazy { stringPreferencesKey("user-theme") }
    }
}