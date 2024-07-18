package com.homework.todolist.ui.screen.settings.data

import androidx.annotation.StringRes
import com.homework.todolist.R
import com.homework.todolist.data.userpreferences.theme.UserTheme
import com.homework.todolist.shared.ui.UiState

/**
 * View theme parameters
 */
data class ThemeParams(
    val themeMode: UserTheme = UserTheme.SYSTEM,
    @StringRes val titleResId: Int = R.string.setting_theme_system,
)

internal fun availableThemes() = listOf(
    ThemeParams(UserTheme.SYSTEM, R.string.setting_theme_system),
    ThemeParams(UserTheme.LIGHT, R.string.setting_theme_light),
    ThemeParams(UserTheme.DARK, R.string.setting_theme_dark)
)

/**
 * User settings screen
 */
sealed class SettingsScreenState(val appThemes: List<ThemeParams> = availableThemes()) : UiState {
    data class Loaded(val current: ThemeParams) : SettingsScreenState()
}

/**
 * Convert to UI params
 */
internal fun UserTheme.toThemeParams() : ThemeParams =
    when(this) {
        UserTheme.DARK -> { ThemeParams(UserTheme.DARK, R.string.setting_theme_dark) }
        UserTheme.SYSTEM -> { ThemeParams(UserTheme.SYSTEM, R.string.setting_theme_system) }
        UserTheme.LIGHT -> { ThemeParams(UserTheme.LIGHT, R.string.setting_theme_light) }
    }

