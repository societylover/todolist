package com.homework.todolist.ui.screen.tododetails

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homework.todolist.R
import com.homework.todolist.data.datasource.local.LocalDataSourceStub
import com.homework.todolist.data.datasource.remote.RemoteDataSourceStub
import com.homework.todolist.data.model.Importance
import com.homework.todolist.data.repository.TodoItemsRepositoryImpl
import com.homework.todolist.data.repository.TodoItemsRepositoryStub
import com.homework.todolist.ui.screen.tododetails.data.TodoItemUiState
import com.homework.todolist.ui.screen.tododetails.viewmodel.TodoDetailsViewModel
import com.homework.todolist.ui.theme.TodoAppTypography
import com.homework.todolist.ui.theme.TodoColorsPalette
import com.homework.todolist.ui.theme.TodolistTheme
import com.homework.todolist.utils.DateFormatter.asString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
internal fun TodoListDetailsScreen(
    onActionClick: () -> Unit,
    viewModel: TodoDetailsViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val itemUiState by viewModel.state.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBarContent(
                scrollState = scrollState,
                viewModel = viewModel,
                itemUiState = itemUiState
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        DetailsContent(paddingValues, scrollState, itemUiState, viewModel::handleEvent, onActionClick)
    }

    val context = LocalContext.current
    handleEffects(viewModel, scope, snackbarHostState, context, onActionClick)
}

@Composable
private fun DetailsContent(
    paddingValues: PaddingValues,
    scrollState: ScrollState,
    itemUiState: TodoItemUiState,
    eventHandler: (TodoDetailsViewModel.Companion.DetailsEvent) -> Unit,
    onActionClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            TodoTextInput(
                modifier = Modifier.padding(bottom = 12.dp, top = 2.dp),
                text = itemUiState.text,
                enabled = !itemUiState.isDone
            ) {
                eventHandler(
                    TodoDetailsViewModel.Companion.DetailsEvent.OnDescriptionUpdate(
                    it
                ))
            }

            ImportanceView(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
                importance = itemUiState.importance,
                enabled = !itemUiState.isDone
            ) {
                eventHandler(
                    TodoDetailsViewModel.Companion.DetailsEvent.OnImportanceChosen(
                        it
                    )
                )
            }

            HorizontalDivider(
                modifier = Modifier,
                color = TodoColorsPalette.current.separatorColor
            )

            DoUntilView(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .padding(bottom = 24.dp),
                isDoUntilSet = itemUiState.isDeadlineSet,
                doUntil = itemUiState.doUntil,
                enabled = !itemUiState.isDone
            ) {
                eventHandler(
                    TodoDetailsViewModel.Companion.DetailsEvent.OnDeadlinePicked(
                        it
                    )
                )
            }
        }

        HorizontalDivider(color = TodoColorsPalette.current.separatorColor)

        DeleteTaskView(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp),
            isActive = itemUiState.id != null
        ) {
            eventHandler(TodoDetailsViewModel.Companion.DetailsEvent.OnDeleteEvent)
            onActionClick()
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopAppBarContent(
    scrollState: ScrollState,
    viewModel: TodoDetailsViewModel,
    itemUiState: TodoItemUiState
) {
    TopAppBar(
        title = { },
        modifier = if (scrollState.value == 0) Modifier else Modifier.shadow(8.dp),
        navigationIcon = {
            IconButton(onClick = { viewModel.handleEvent(TodoDetailsViewModel.Companion.DetailsEvent.OnCloseEvent) }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(id = R.string.todo_item_details_close_icon_description),
                    tint = TodoColorsPalette.current.labelPrimaryColor
                )
            }
        },
        actions = {
            if (!itemUiState.isDone) {
                TextButton(
                    onClick = {
                        viewModel.handleEvent(TodoDetailsViewModel.Companion.DetailsEvent.OnSaveEvent)
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.todo_item_create_save_icon_text),
                        color = TodoColorsPalette.current.blueColor
                    )
                }
            }
        })
}

@Composable
private fun handleEffects(
    viewModel: TodoDetailsViewModel,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    context: Context,
    onActionClick: () -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect {
            when (it) {
                is TodoDetailsViewModel.Companion.DetailsEffects.UnknownErrorToast -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(context.getString(R.string.todo_details_unknown_error_text))
                    }
                }

                is TodoDetailsViewModel.Companion.DetailsEffects.ShowSaveErrorToast -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(context.getString(it.errorErrorResId))
                    }
                }

                is TodoDetailsViewModel.Companion.DetailsEffects.OnItemSaved -> {
                    onActionClick()
                }

                is TodoDetailsViewModel.Companion.DetailsEffects.OnItemDeleted -> {
                    onActionClick()
                }

                is TodoDetailsViewModel.Companion.DetailsEffects.OnFormClosed -> {
                    onActionClick()
                }
            }

        }
    }
}

@Composable
private fun TodoTextInput(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    text: String,
    onValueChange: (String) -> Unit
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        TextField(
            value = text, onValueChange = onValueChange, placeholder = {
                Text(
                    text = stringResource(id = R.string.todo_item_create_text_input_hint),
                    color = TodoColorsPalette.current.labelTertiaryColor
                )
            },
            enabled = enabled,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = TodoColorsPalette.current.backSecondaryColor,
                unfocusedContainerColor = TodoColorsPalette.current.backSecondaryColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                disabledContainerColor = TodoColorsPalette.current.backSecondaryColor
            ),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 104.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TodoTextInputPreview() {
    TodolistTheme(
        darkTheme = false,
        dynamicColor = false
    ) {
        Column {
            TodoTextInput(
                enabled = false,
                text = "Test text",
                onValueChange = { }
            )
            TodoTextInput(
                enabled = true,
                modifier = Modifier.padding(vertical = 30.dp),
                text = "Test text",
                onValueChange = { }
            )
            TodoTextInput(
                enabled = true,
                text = "",
                onValueChange = { }
            )
        }
    }
}

@Composable
private fun ImportanceView(
    modifier: Modifier = Modifier,
    importance: Importance,
    enabled: Boolean,
    setItemImportance: (Importance) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .then(modifier)
        .then(if (enabled) Modifier.clickable { expanded = !expanded } else Modifier))
    {
        if (enabled) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(TodoColorsPalette.current.backElevatedColor)
            ) {

                ImportanceDropdownMenuItem(R.string.todo_item_importance_ordinary, TodoColorsPalette.current.labelPrimaryColor) {
                    setItemImportance(Importance.ORDINARY)
                    expanded = false
                }

                ImportanceDropdownMenuItem(R.string.todo_item_importance_low, TodoColorsPalette.current.labelPrimaryColor) {
                    setItemImportance(Importance.LOW)
                    expanded = false
                }

                ImportanceDropdownMenuItem(R.string.todo_item_importance_urgent, TodoColorsPalette.current.redColor) {
                    setItemImportance(Importance.URGENT)
                    expanded = false
                }
            }
        }

        Text(
            text = stringResource(id = R.string.todo_item_create_importance_title),
            color = TodoColorsPalette.current.labelPrimaryColor,
            style = TodoAppTypography.current.body
        )

        val importanceProps = getImportanceValues(importance = importance)

        Text(
            text = stringResource(id = importanceProps.stringRes),
            color = if (importance == Importance.URGENT) TodoColorsPalette.current.redColor
            else TodoColorsPalette.current.labelTertiaryColor,
            style = TodoAppTypography.current.subhead
        )
    }
}

@Composable
private fun ImportanceDropdownMenuItem(
    @StringRes importanceResId: Int,
    importanceTextColor: Color,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Text(
                text = stringResource(id = importanceResId),
                style = TodoAppTypography.current.body
            )
        },
        onClick = onClick,
        colors = MenuDefaults.itemColors(
            textColor = importanceTextColor
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun ImportanceViewPreview() {
    TodolistTheme(
        darkTheme = false,
        dynamicColor = false
    ) {
        Column {
            ImportanceView(
                importance = Importance.LOW,
                enabled = false,
                setItemImportance = { }
            )
            ImportanceView(
                importance = Importance.URGENT,
                enabled = false,
                setItemImportance = { }
            )
            ImportanceView(
                importance = Importance.ORDINARY,
                enabled = true,
                setItemImportance = { }
            )
        }
    }
}

private fun getImportanceValues(importance: Importance): ImportanceProps {
    return ImportanceProps(
        importance = importance,
        stringRes = importance.getImportanceStringResId()
    )
}

/**
 * Convert importance to it's string res representation
 */
private fun Importance.getImportanceStringResId() =
    when (this) {
        Importance.URGENT -> {
            R.string.todo_item_importance_urgent
        }

        Importance.ORDINARY -> {
            R.string.todo_item_importance_ordinary
        }

        Importance.LOW -> {
            R.string.todo_item_importance_low
        }
    }

private data class ImportanceProps(
    val importance: Importance,
    @StringRes val stringRes: Int
)

@Composable
private fun DoUntilView(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    isDoUntilSet: Boolean,
    doUntil: LocalDate,
    onDoUntilSelected: (LocalDate?) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = stringResource(id = R.string.todo_item_create_do_until_title),
                color = TodoColorsPalette.current.labelPrimaryColor,
                style = TodoAppTypography.current.body
            )
            if (isDoUntilSet) {
                Text(
                    text = doUntil.asString(),
                    color = TodoColorsPalette.current.blueColor,
                    style = TodoAppTypography.current.subhead
                )
            }
        }

        DoUntilSwitch(enabled = enabled, isDoUntilSet) {
            if (isDoUntilSet) {
                showDatePicker = false
                onDoUntilSelected(null)
            } else {
                showDatePicker = true
            }
        }

        if (showDatePicker) {
            DateSelectingView(
                onDateSelected = {
                    showDatePicker = false
                    onDoUntilSelected(it)
                },
                onDismissed = { showDatePicker = false },
            )
        }
    }
}

@Preview
@Composable
private fun DoUntilViewPreview() {
    TodolistTheme(
        darkTheme = false,
        dynamicColor = false
    ) {
        DoUntilView(
            enabled = false,
            isDoUntilSet = true,
            doUntil = LocalDate.now(),
            onDoUntilSelected = { })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateSelectingView(
    onDateSelected: (LocalDate) -> Unit,
    onDismissed: () -> Unit
) {

    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = { onDismissed() },
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.also {
                        onDateSelected(convertMillisToLocalDate(it))
                    }
                },
            ) {
                Text(
                    text = stringResource(id = R.string.todo_calendar_done_button_text),
                    color = TodoColorsPalette.current.blueColor,
                    style = TodoAppTypography.current.button
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissed) {
                Text(
                    text = stringResource(id = R.string.todo_calendar_cancel_button_text),
                    color = TodoColorsPalette.current.blueColor,
                    style = TodoAppTypography.current.button
                )
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                titleContentColor = TodoColorsPalette.current.blueColor,
                selectedDayContainerColor = TodoColorsPalette.current.blueColor
            )
        )
    }
}

@Preview
@Composable
private fun DateSelectingViewPreview() {
    TodolistTheme(
        darkTheme = false,
        dynamicColor = false
    ) {
        DateSelectingView(onDateSelected = { }, onDismissed = { })
    }
}

private fun convertMillisToLocalDate(millis: Long): LocalDate {
    val instant = Instant.ofEpochMilli(millis)
    return instant.atZone(ZoneId.systemDefault()).toLocalDate()
}

@Composable
private fun DoUntilSwitch(
    enabled: Boolean,
    isDoUntilSet: Boolean,
    onClick: () -> Unit
) {
    Switch(
        enabled = enabled,
        checked = isDoUntilSet,
        onCheckedChange = { onClick() },
        colors = SwitchDefaults.colors(
            checkedThumbColor = TodoColorsPalette.current.blueColor,
            checkedTrackColor = TodoColorsPalette.current.blueColor.copy(ContentAlpha.disabled),
            uncheckedThumbColor = TodoColorsPalette.current.backElevatedColor,
            uncheckedTrackColor = TodoColorsPalette.current.overlayColor,
        )
    )
}

@Composable
@Preview
private fun DoUntilSwitchPreview() {
    Column {
        TodolistTheme(
            darkTheme = false,
            dynamicColor = false
        ) {
            Column {
                DoUntilSwitch(enabled = false, isDoUntilSet = true) { }
                DoUntilSwitch(enabled = true, isDoUntilSet = false) { }
            }
        }
        TodolistTheme(
            darkTheme = true,
            dynamicColor = false
        ) {
            Column {
                DoUntilSwitch(enabled = false, isDoUntilSet = true) { }
                DoUntilSwitch(enabled = true, isDoUntilSet = false) { }
            }
        }
    }
}


@Composable
internal fun DeleteTaskView(
    modifier: Modifier = Modifier,
    isActive: Boolean,
    onDeleteClick: () -> Unit
) {
    Row(modifier = Modifier
        .then(modifier)
        .then(if (isActive) Modifier.clickable { onDeleteClick() } else Modifier))
    {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = stringResource(id = R.string.todo_item_importance_remove_icon_description),
            tint = if (isActive) TodoColorsPalette.current.redColor
            else TodoColorsPalette.current.labelDisableColor
        )

        Text(
            text = stringResource(id = R.string.todo_item_create_delete_button_text),
            color = if (isActive) TodoColorsPalette.current.redColor
            else TodoColorsPalette.current.labelDisableColor
        )
    }
}

@Composable
private fun DeleteTaskViewPreview(
    isThemeDark: Boolean = false,
    isActive: Boolean = false,
) {
    TodolistTheme(
        darkTheme = isThemeDark,
        dynamicColor = false
    ) {
        Column {
            Text("Is active: $isActive")
            DeleteTaskView(
                modifier = Modifier,
                isActive = isActive,
                onDeleteClick = { }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DeleteTaskViewLightPreview() {
    Column {
        DeleteTaskViewPreview(isThemeDark = false, isActive = true)
        DeleteTaskViewPreview(isThemeDark = false, isActive = false)
    }
}

@Preview(showBackground = false)
@Composable
private fun DeleteTaskViewDarkPreview() {
    Column {
        DeleteTaskViewPreview(isThemeDark = true, isActive = true)
        DeleteTaskViewPreview(isThemeDark = true, isActive = false)
    }
}

@Preview(showBackground = true)
@Composable
private fun TodoListDetailsCreateScreenPreview() {
    TodolistTheme(
        darkTheme = false,
        dynamicColor = false
    ) {
        TodoListDetailsScreen(
            onActionClick = {},
            viewModel = TodoDetailsViewModel(
                todoItemsRepository = TodoItemsRepositoryStub(),
                savedStateHandle = SavedStateHandle.createHandle(null, null)
            )
        )
    }
}