package com.homework.todolist.data.userpreferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.homework.todolist.data.userpreferences.theme.UserTheme
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class UserPreferencesProviderImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesProvider {
    override suspend fun setUserTheme(theme: UserTheme) {
        TODO("Not yet implemented")
    }

    override fun getUserPreferableThemeFlow(): Flow<UserTheme> {
        TODO("Not yet implemented")
    }

    companion object {
        // Key for data-store instance
        internal const val USER_PREFERENCES = "user-preferences"
    }
}