package com.homework.todolist.ui.screen.settings

import androidx.lifecycle.viewModelScope
import com.homework.todolist.data.userpreferences.UserPreferencesProvider
import com.homework.todolist.data.userpreferences.theme.UserTheme
import com.homework.todolist.shared.ui.UiEffect
import com.homework.todolist.shared.ui.UiEvent
import com.homework.todolist.shared.ui.ViewModelBase
import com.homework.todolist.ui.screen.settings.SettingsViewModel.Companion.SettingEffects
import com.homework.todolist.ui.screen.settings.SettingsViewModel.Companion.SettingEvent
import com.homework.todolist.ui.screen.settings.data.SettingsScreenState
import com.homework.todolist.ui.screen.settings.data.ThemeParams
import com.homework.todolist.ui.screen.settings.data.toThemeParams
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

/**
 * Settings screen viewmodel
 * @param userPreferencesProvider User preferences provider
 */
class SettingsViewModel @Inject constructor(
    private val userPreferencesProvider: UserPreferencesProvider
) : ViewModelBase<SettingEvent, SettingsScreenState, SettingEffects>(SettingsScreenState.Loaded(
    ThemeParams()
)){

    private val scope = viewModelScope + CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        setEffect { SettingEffects.ErrorToast }
    }

    init {
        scope.launch(Dispatchers.IO) {
            userPreferencesProvider.getUserPreferableThemeFlow().collect { userTheme ->
                setState { SettingsScreenState.Loaded(userTheme.toThemeParams()) }
            }
        }
    }

    override fun handleEvent(event: UiEvent) {
        when (event) {
            is SettingEvent.OnThemeSelected -> { handleThemeSetting(event.params.themeMode) }
        }
    }

    private fun handleThemeSetting(params: UserTheme) {
        scope.launch(Dispatchers.IO) {
            userPreferencesProvider.setUserTheme(params)
        }
    }

    companion object {
        /**
         * Start screen events
         */
        sealed class SettingEvent : UiEvent {
            data class OnThemeSelected(val params: ThemeParams) : SettingEvent()
        }

        /**
         * Start screen effects
         */
        sealed class SettingEffects : UiEffect {
            data object ErrorToast : SettingEffects()
        }
    }
}