package com.homework.todolist.tododetails

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homework.todolist.R
import com.homework.todolist.data.model.Importance
import com.homework.todolist.ui.theme.LocalTodoColorsPalette
import com.homework.todolist.ui.theme.TodolistTheme
import com.homework.todolist.ui.theme.body
import com.homework.todolist.ui.theme.button
import com.homework.todolist.ui.theme.subhead
import com.homework.todolist.util.DateFormatter.asString
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TodoListDetailsScreen(
    onActionClick: () -> Unit,
    viewModel: TodoDetailsViewModel = hiltViewModel()
) {
    val itemUiState by viewModel.todoItemState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                modifier = if (scrollState.value == 0) Modifier else Modifier.shadow(8.dp),
                navigationIcon = {
                    IconButton(onClick = onActionClick) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.todo_item_details_close_icon_description),
                            tint = LocalTodoColorsPalette.current.labelPrimaryColor
                        )
                    }
                },
                actions = {
                    if (!itemUiState.isDone) {
                        TextButton(
                            onClick = {
                                viewModel.replaceItem()
                                onActionClick()
                            }
                        ) {
                            Text(
                                text = stringResource(id = R.string.todo_item_create_save_icon_text),
                                color = LocalTodoColorsPalette.current.blueColor
                            )
                        }
                    }
                })
        }
    ) { paddingValues ->
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
                    viewModel.setSnapshotText(it)
                }

                ImportanceView(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(),
                    importance = itemUiState.importance,
                    enabled = !itemUiState.isDone
                ) {
                    viewModel.setSnapshotImportance(it)
                }

                HorizontalDivider(
                    modifier = Modifier,
                    color = LocalTodoColorsPalette.current.separatorColor
                )

                DoUntilView(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .padding(bottom = 24.dp),
                    isDoUntilSet = itemUiState.isDeadlineSet,
                    doUntil = itemUiState.doUntil,
                    enabled = !itemUiState.isDone
                ) {
                    viewModel.setSnapshotDoUntil(it)
                }
            }

            HorizontalDivider(color = LocalTodoColorsPalette.current.separatorColor)

            DeleteTaskView(
                modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp),
                isActive = itemUiState.id != null
            ) {
                if (itemUiState.id != null) {
                    viewModel.deleteItem()
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
                    color = LocalTodoColorsPalette.current.labelTertiaryColor
                )
            },
            enabled = enabled,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = LocalTodoColorsPalette.current.backSecondaryColor,
                unfocusedContainerColor = LocalTodoColorsPalette.current.backSecondaryColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                disabledContainerColor = LocalTodoColorsPalette.current.backSecondaryColor
            ),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 104.dp)
        )
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
                modifier = Modifier.background(LocalTodoColorsPalette.current.backElevatedColor)
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(id = R.string.todo_item_importance_ordinary),
                            style = MaterialTheme.typography.body()
                        )
                    },
                    onClick = {
                        setItemImportance(Importance.ORDINARY)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = LocalTodoColorsPalette.current.labelPrimaryColor
                    )
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(id = R.string.todo_item_importance_low),
                            style = MaterialTheme.typography.body()
                        )
                    },
                    onClick = {
                        setItemImportance(Importance.LOW)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = LocalTodoColorsPalette.current.labelPrimaryColor
                    )
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(id = R.string.todo_item_importance_urgent),
                            style = MaterialTheme.typography.body()
                        )
                    },
                    onClick = {
                        setItemImportance(Importance.URGENT)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = LocalTodoColorsPalette.current.redColor
                    )
                )
            }
        }

        Text(
            text = stringResource(id = R.string.todo_item_create_importance_title),
            color = LocalTodoColorsPalette.current.labelPrimaryColor,
            style = MaterialTheme.typography.body()
        )

        val importanceProps = getImportanceValues(importance = importance)

        Text(
            text = stringResource(id = importanceProps.stringRes),
            color = if (importance == Importance.URGENT) LocalTodoColorsPalette.current.redColor
            else LocalTodoColorsPalette.current.labelTertiaryColor,
            style = MaterialTheme.typography.subhead()
        )
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
                color = LocalTodoColorsPalette.current.labelPrimaryColor,
                style = MaterialTheme.typography.body()
            )
            if (isDoUntilSet) {
                Text(
                    text = doUntil.asString(),
                    color = LocalTodoColorsPalette.current.blueColor,
                    style = MaterialTheme.typography.subhead()
                )
            }
        }

        DoUntilSwitch(enabled = enabled, isDoUntilSet) { newState ->
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
                    color = LocalTodoColorsPalette.current.blueColor,
                    style = MaterialTheme.typography.button()
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissed) {
                Text(
                    text = stringResource(id = R.string.todo_calendar_cancel_button_text),
                    color = LocalTodoColorsPalette.current.blueColor,
                    style = MaterialTheme.typography.button()
                )
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                titleContentColor = LocalTodoColorsPalette.current.blueColor,
                selectedDayContainerColor = LocalTodoColorsPalette.current.blueColor,

                )
        )
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
    onClick: (Boolean) -> Unit
) {
    Switch(
        enabled = enabled,
        checked = isDoUntilSet,
        onCheckedChange = { onClick(!isDoUntilSet) },
        colors = SwitchDefaults.colors(
            checkedThumbColor = LocalTodoColorsPalette.current.blueColor,
            checkedTrackColor = LocalTodoColorsPalette.current.blueColor.copy(ContentAlpha.disabled),
            uncheckedThumbColor = LocalTodoColorsPalette.current.backElevatedColor,
            uncheckedTrackColor = LocalTodoColorsPalette.current.overlayColor,
        )
    )
}

@Composable
private fun DeleteTaskView(
    modifier: Modifier = Modifier,
    isActive: Boolean,
    onDeleteClick: () -> Unit
) {
    Row(modifier = Modifier
        .then(modifier)
        .clickable { onDeleteClick() }) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = stringResource(id = R.string.todo_item_importance_remove_icon_description),
            tint = if (isActive) LocalTodoColorsPalette.current.redColor
            else LocalTodoColorsPalette.current.labelDisableColor
        )

        Text(
            text = stringResource(id = R.string.todo_item_create_delete_button_text),
            color = if (isActive) LocalTodoColorsPalette.current.redColor
            else LocalTodoColorsPalette.current.labelDisableColor
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun TodoListDetailsScreenPreview() {
    TodolistTheme {
        TodoListDetailsScreen({})
    }
}