package com.homework.todolist.ui.screen.main

import androidx.lifecycle.ViewModel
import com.homework.todolist.data.userpreferences.UserPreferencesProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Main activity view-model
 * @param userPreferencesProvider UserPreferences provider (themes for now)
 */
@HiltViewModel
class MainViewModel @Inject constructor(userPreferencesProvider: UserPreferencesProvider): ViewModel() {

    /**
     * Flow of current user theme
     */
    val currentThemeFlow = userPreferencesProvider.getUserPreferableThemeFlow()
}