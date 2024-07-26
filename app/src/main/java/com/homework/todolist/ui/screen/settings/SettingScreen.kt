package com.homework.todolist.ui.screen.settings

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homework.todolist.R
import com.homework.todolist.data.userpreferences.theme.UserTheme
import com.homework.todolist.ui.screen.settings.data.SettingsScreenState
import com.homework.todolist.ui.screen.settings.data.ThemeParams
import com.homework.todolist.ui.theme.LocalTodoAppTypography
import com.homework.todolist.ui.theme.LocalTodoColorsPalette

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
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(
                        id = R.string.setting_select_back_text
                    )
                )
            }
        }
    ) { paddings ->
        state.value.let {
            if (it is SettingsScreenState.Loaded) {
                ShowSelectableModes(
                    modifier = Modifier
                        .padding(paddings)
                        .padding(horizontal = 24.dp),
                    screenState = it
                ) { mode ->
                    viewModel.handleEvent(
                        SettingsViewModel.Companion.SettingEvent.OnThemeSelected(mode)
                    )
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
    val context = LocalContext.current

    Column(modifier) {
        androidx.compose.material3.Text(
            text = stringResource(id = R.string.setting_select_app_theme),
            modifier = Modifier.padding(bottom = 4.dp).semantics {
                contentDescription = context.getString(R.string.setting_select_app_theme_desc,
                    context.getString(screenState.current.titleResId))
            },
            style = LocalTodoAppTypography.current.title,
            fontWeight = FontWeight.Bold,
            color = LocalTodoColorsPalette.current.labelPrimaryColor
        )
            LazyColumn {
                items(items = screenState.appThemes, key = { it.themeMode }) {
                    ThemeItem(
                        modifier = Modifier,
                        isSelected = screenState.current.themeMode == it.themeMode,
                        text = stringResource(id = it.titleResId),
                        iconResId = it.iconResId,
                        iconColor = getIconColor(it.themeMode)
                    ) {
                        onSectionClick(it)
                    }
                }
            }
    }
}

@Composable
private fun getIconColor(userTheme: UserTheme) : androidx.compose.ui.graphics.Color {
    return when(userTheme) {
        UserTheme.SYSTEM -> LocalTodoColorsPalette.current.greenColor
        UserTheme.LIGHT -> LocalTodoColorsPalette.current.yellowColor
        UserTheme.DARK -> LocalTodoColorsPalette.current.blueColor
    }
}

@Composable
private fun ThemeItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    @DrawableRes iconResId: Int,
    iconColor: androidx.compose.ui.graphics.Color,
    text: String,
    onClick: () -> Unit
) {
    Row(modifier = modifier
        .fillMaxWidth()
        .clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            onClick = onClick
        )
        .padding(vertical = 4.dp))
    {
        androidx.compose.material3.Icon(painter = painterResource(id = iconResId), null,
            modifier = Modifier.padding(end = 6.dp),
            tint = iconColor)

        androidx.compose.material3.Text(
            text = text,
            color = if (isSelected) LocalTodoColorsPalette.current.redColor
            else LocalTodoColorsPalette.current.labelPrimaryColor,
            style = LocalTodoAppTypography.current.title,
            fontWeight = if (isSelected) LocalTodoAppTypography.current.title.fontWeight
            else LocalTodoAppTypography.current.body.fontWeight
        )
    }
}

@Composable
private fun ShowAboutScreenAction(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clickable { onClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Default.Info,
                contentDescription = stringResource(id = R.string.setting_open_about),
                modifier = Modifier.padding(end = 8.dp),
                tint = LocalTodoColorsPalette.current.labelPrimaryColor
            )
            androidx.compose.material3.Text(
                text = stringResource(id = R.string.setting_about_button),
                style = LocalTodoAppTypography.current.button,
                color = LocalTodoColorsPalette.current.labelPrimaryColor
            )
        }
    }
}