package com.homework.todolist.data.userpreferences

import com.homework.todolist.data.userpreferences.theme.UserTheme
import kotlinx.coroutines.flow.Flow

/**
 * User application theme provider
 */
interface UserPreferencesProvider {
    /**
     * Set user theme
     * @param theme User preferable theme
     */
    suspend fun setUserTheme(theme: UserTheme)

    /**
     * Flow of user selected theme
     * @return Flow of selected themes
     */
    fun getUserPreferableThemeFlow() : Flow<UserTheme>
}