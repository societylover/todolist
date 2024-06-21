package com.homework.todolist

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homework.todolist.ui.theme.LocalCustomColorsPalette
import com.homework.todolist.ui.theme.TodolistTheme
import com.homework.todolist.ui.theme.body
import com.homework.todolist.ui.theme.subhead
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListDetailsScreen(
    onActionClick: () -> Unit,
    viewModel: TodoDetailsViewModel = hiltViewModel()
) {
    val itemUiState by viewModel.todoItemState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onActionClick) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "close",
                            tint = LocalCustomColorsPalette.current.labelPrimaryColor
                        )
                    }
                },
                actions = {
                    Text(
                        text = stringResource(id = R.string.todo_item_create_save_icon_text),
                        color = LocalCustomColorsPalette.current.blueColor,
                        modifier = Modifier
                            .clickable {
                                viewModel.updateItem()
                                onActionClick()
                                println("OnSave clicked!") }
                            .padding(end = 16.dp)
                    )
                })
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues)
            .fillMaxWidth()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                TodoTextInput(
                    modifier = Modifier.padding(bottom = 12.dp),
                    text = itemUiState.text
                ) {
                    viewModel.setSnapshotText(it)
                }

                ImportanceView(itemImportanceResId = itemUiState.importanceResId) {
                    viewModel.setSnapshotImportance(it)
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = LocalCustomColorsPalette.current.separatorColor
                )

                DoUntilView(
                    isDoUntilSet = itemUiState.isDeadlineSet,
                    doUntil = itemUiState.doUntil
                ) {
                    viewModel.setSnapshotDoUntil(it)
                }
            }

            HorizontalDivider(color = LocalCustomColorsPalette.current.separatorColor)

            DeleteTaskView(modifier = Modifier.padding(start = 16.dp),
                isActive = itemUiState.id != null) {
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
    text: String,
    onValueChange: (String) -> Unit
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(8)
    ) {
        TextField(
            value = text, onValueChange = onValueChange, placeholder = {
                Text(
                    text = stringResource(id = R.string.todo_item_create_text_input_hint),
                    color = LocalCustomColorsPalette.current.labelTertiaryColor
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = LocalCustomColorsPalette.current.backSecondaryColor,
                unfocusedContainerColor = LocalCustomColorsPalette.current.backSecondaryColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ImportanceView(
    modifier: Modifier = Modifier,
    @StringRes itemImportanceResId: Int,
    setItemImportance: (Importance) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .then(modifier)
        .clickable { expanded = !expanded })
    {

        Text(
            text = stringResource(id = R.string.todo_item_create_importance_title),
            color = LocalCustomColorsPalette.current.labelPrimaryColor,
            style = MaterialTheme.typography.body()
        )

        Text(
            text = stringResource(id = itemImportanceResId),
            color = LocalCustomColorsPalette.current.labelTertiaryColor,
            style = MaterialTheme.typography.subhead()
        )

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { stringResource(id = R.string.todo_item_importance_ordinary) },
                onClick = { setItemImportance(Importance.ORDINARY) },
            )
            DropdownMenuItem(
                text = { stringResource(id = R.string.todo_item_importance_low) },
                onClick = { setItemImportance(Importance.LOW) },
            )
            DropdownMenuItem(
                text = { stringResource(id = R.string.todo_item_importance_urgent) },
                onClick = { setItemImportance(Importance.URGENT) },
                colors = MenuDefaults.itemColors(
                    textColor = LocalCustomColorsPalette.current.redColor
                )
            )
        }
    }
}

@Composable
private fun DoUntilView(
    modifier: Modifier = Modifier,
    isDoUntilSet: Boolean,
    doUntil: LocalDate,
    onDoUntilSelected: (LocalDate?) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val datePickerDialog = DatePickerDialog(
        context,
        R.style.TodoDatePicker,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            onDoUntilSelected(LocalDate.of(mYear, mMonth, mDayOfMonth))
        }, doUntil.year, doUntil.monthValue - 1, doUntil.dayOfMonth
    )

    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.todo_item_create_do_until_title),
                modifier = Modifier.weight(1f),
                color = LocalCustomColorsPalette.current.labelPrimaryColor,
                style = MaterialTheme.typography.body()
            )

            DoUntilSwitch(isDoUntilSet) {
                if (isDoUntilSet) {
                    showDatePicker = false
                    onDoUntilSelected(null)
                }
                else {
                    datePickerDialog.show()
                }
            }
        }

        if (isDoUntilSet) {
            Text(
                text = doUntil.toString(),
                color = LocalCustomColorsPalette.current.blueColor,
                style = MaterialTheme.typography.subhead()
            )
        }
    }

//    if (showDatePicker) {
////        DateSelectingView()
//        datePickerDialog.show()
//    } else {
//        datePickerDialog.hide()
//    }

//    val datePickerState = rememberDatePickerState(selectableDates = object : SelectableDates {
//        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
//            return utcTimeMillis <= System.currentTimeMillis()
//        }
//    })
//
//    val selectedDate = datePickerState.selectedDateMillis?.let {
//        convertMillisToDate(it)
//    } ?: ""
//
//    if (showDatePicker) {
//        DatePickerDialog(
//            onDismissRequest = { showDatePicker = false },
//            confirmButton = {
//                Text(text = stringResource(id = R.string.todo_calendar_done_button_text),
//                    modifier = Modifier.clickable {
//                        showDatePicker = false })
//            },
//            dismissButton = {
//                Text(text = stringResource(id = R.string.todo_calendar_cancel_button_text),
//                    modifier = Modifier.clickable {
//                        showDatePicker = false
//                        onDoUntilSelected(selectedDate) })
//            }
//        ) {
//            DatePicker(state = )
//        }
//    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateSelectingView() {
    val snackState = remember { SnackbarHostState() }
    val snackScope = rememberCoroutineScope()
    SnackbarHost(hostState = snackState, Modifier)
    val openDialog = remember { mutableStateOf(true) }
    // TODO demo how to read the selected date from the state.
    if (openDialog.value) {
        val datePickerState = rememberDatePickerState()
        val confirmEnabled = remember {
            derivedStateOf { datePickerState.selectedDateMillis != null }
        }
        DatePickerDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                openDialog.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        snackScope.launch {
                            snackState.showSnackbar(
                                "Selected date timestamp: ${datePickerState.selectedDateMillis}"
                            )
                        }
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { openDialog.value = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}


@Composable
private fun DoUntilSwitch(
    isDoUntilSet: Boolean,
    onClick: () -> Unit
) {
    Switch(
        checked = isDoUntilSet,
        onCheckedChange = { onClick() },
        colors = SwitchDefaults.colors(
            checkedThumbColor = LocalCustomColorsPalette.current.blueColor,
            checkedTrackColor = LocalCustomColorsPalette.current.blueColor.copy(ContentAlpha.disabled),
            uncheckedThumbColor = LocalCustomColorsPalette.current.backElevatedColor,
            uncheckedTrackColor = LocalCustomColorsPalette.current.overlayColor
        )
    )
}

@Composable
private fun DoUntilSelectView(
    doUntilValue: LocalDate,
    onValueSelected: (LocalDate) -> Unit,
    onSelectCanceled: () -> Unit
) {

}


@Composable
private fun DeleteTaskView(
    modifier: Modifier = Modifier,
    isActive: Boolean,
    onDeleteClick: () -> Unit
) {
    Row(modifier = Modifier.then(modifier).clickable { onDeleteClick() }) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "delete to-do",
            tint = if (isActive) LocalCustomColorsPalette.current.redColor
            else LocalCustomColorsPalette.current.labelDisableColor
        )

        Text(
            text = stringResource(id = R.string.todo_item_create_delete_button_text),
            color = if (isActive) LocalCustomColorsPalette.current.redColor
            else LocalCustomColorsPalette.current.labelDisableColor
        )
    }
}


@Preview(showBackground = true)
@Composable
fun TodoListDetailsScreenPreview() {
    TodolistTheme {
        TodoListDetailsScreen({})
    }
}