package com.homework.todolist.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homework.todolist.R
import com.homework.todolist.ui.screen.settings.data.SettingsScreenState
import com.homework.todolist.ui.screen.settings.data.ThemeParams
import com.homework.todolist.ui.theme.LocalTodoAppTypography

/**
 * Application settings screen
 */
@Composable
internal fun SettingsScreen(
    onActionClick: () -> Unit,
    onAboutAppClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            IconButton(onClick = onActionClick) {
                Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = stringResource(
                    id = R.string.setting_select_back_text
                ))
            }
        }
    ) { paddings ->
        state.value.let {
            if (it is SettingsScreenState.Loaded) {
                ShowSelectableModes(Modifier.padding(paddings), it) { mode ->
                    viewModel.handleEvent(SettingsViewModel.Companion.SettingEvent.OnThemeSelected(mode))
                }
            }
        }

        ShowAboutScreenAction { onAboutAppClick() }
    }

}

@Composable
private fun ShowSelectableModes(
    modifier: Modifier = Modifier,
    screenState: SettingsScreenState.Loaded,
    onSectionClick: (ThemeParams) -> Unit
) {
    Column(modifier) {
        Text(text = stringResource(id = R.string.setting_select_app_theme))

        LazyColumn {
            items(items = screenState.appThemes, key = { it.themeMode }) {
                ThemeItem(
                    modifier = Modifier,
                    isSelected = screenState.current == it,
                    text = stringResource(id = it.titleResId)
                ) {
                    onSectionClick(it)
                }
            }
        }
    }
}

@Composable
private fun ThemeItem(modifier: Modifier = Modifier,
                      isSelected: Boolean,
                      text: String, onClick: () -> Unit) {
    Row(modifier.then(if (isSelected) Modifier.background(Color.Gray) else Modifier).clickable { onClick() }) {
        Text(text = text, style = LocalTodoAppTypography.current.body)
    }
}

@Composable
private fun ShowAboutScreenAction(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(modifier = modifier.clickable { onClick() })
    {
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = stringResource(id = R.string.setting_open_about)
        )
        Text(text = stringResource(id = R.string.setting_about_button))
    }
}