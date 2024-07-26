package com.homework.todolist.ui.screen.tododetails

import android.annotation.SuppressLint
import android.content.Context
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homework.todolist.R
import com.homework.todolist.data.model.Importance
import com.homework.todolist.data.repository.TodoItemsRepositoryStub
import com.homework.todolist.ui.screen.tododetails.data.ImportanceItem
import com.homework.todolist.ui.screen.tododetails.data.TodoItemUiState
import com.homework.todolist.ui.screen.tododetails.data.toImportanceItem
import com.homework.todolist.ui.screen.tododetails.viewmodel.TodoDetailsViewModel
import com.homework.todolist.ui.screen.tododetails.viewmodel.TodoDetailsViewModel.Companion.DetailsEvent
import com.homework.todolist.ui.theme.LocalTodoAppTypography
import com.homework.todolist.ui.theme.LocalTodoColorsPalette
import com.homework.todolist.ui.theme.TodolistTheme
import com.homework.todolist.utils.DateFormatter.asString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
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
    EffectsHandler(viewModel, scope, snackbarHostState, context, onActionClick)
}

@Composable
private fun DetailsContent(
    paddingValues: PaddingValues,
    scrollState: ScrollState,
    itemUiState: TodoItemUiState,
    eventHandler: (DetailsEvent) -> Unit,
    onActionClick: () -> Unit
) {
    var showSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            TodoTextInput(
                modifier = Modifier
                    .padding(bottom = 12.dp, top = 2.dp)
                    ,
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
                isEnabled = !itemUiState.isDone,
                importance = itemUiState.importanceState,
                onClick = { showSheet = true }
            )

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
                eventHandler(
                    TodoDetailsViewModel.Companion.DetailsEvent.OnDeadlinePicked(
                        it
                    )
                )
            }
        }

        if (showSheet) {
            ImportanceSheet(
                onDismissed = { showSheet = false },
                current = itemUiState.importanceState,
                allImportance = remember {
                    listOf(
                        Importance.LOW.toImportanceItem(),
                        Importance.ORDINARY.toImportanceItem(),
                        Importance.URGENT.toImportanceItem()
                    )
                },
                setItemImportance = {
                    eventHandler(
                        TodoDetailsViewModel.Companion.DetailsEvent.OnImportanceChosen(it)
                    )
                }
            )
        }

        HorizontalDivider(color = LocalTodoColorsPalette.current.separatorColor)

        val context = LocalContext.current

        EpicPulsarButton(
            isActive = itemUiState.id != null,
            onDeleteClick = {
                eventHandler(TodoDetailsViewModel.Companion.DetailsEvent.OnDeleteEvent)
                onActionClick()
            },
            modifier = Modifier
                .padding(start = 16.dp, top = 12.dp, bottom = 12.dp)
                .semantics(mergeDescendants = true) {
                    contentDescription = context.getString(R.string.todo_item_create_delete_button_desc)
                }
        )
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
            IconButton(onClick = { viewModel.handleEvent(DetailsEvent.OnCloseEvent) }) {
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
                        viewModel.handleEvent(DetailsEvent.OnSaveEvent)
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

@Composable
private fun EffectsHandler(
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

@OptIn(ExperimentalComposeUiApi::class)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImportanceSheet(
    modifier: Modifier = Modifier,
    onDismissed: () -> Unit,
    current: ImportanceItem,
    allImportance: List<ImportanceItem>,
    setItemImportance: (ImportanceItem) -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState()
    var shouldAnimatedSelection by rememberSaveable { mutableStateOf(false) }
    val sheetHeight = remember { Animatable(0f) }

    val animationSpec = tween<Float>(
        durationMillis = 450,
        easing = {
            OvershootInterpolator(2f).getInterpolation(it)
        }
    )

    ModalBottomSheet(
        onDismissRequest = {
            shouldAnimatedSelection = false
            onDismissed() },
        modifier = modifier,
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        ImportanceSheetContent(
            sheetHeight = sheetHeight,
            allImportance = allImportance,
            current = current,
            shouldAnimatedSelection = shouldAnimatedSelection,
            setItemImportance = {
                shouldAnimatedSelection = it != current
                setItemImportance(it)
            }
        )
    }

    val sheetPickHeight by remember {
        derivedStateOf {
            (allImportance.size + 1) * 60f
        }
    }

    LaunchedEffect(modalBottomSheetState.isVisible) {
        if (modalBottomSheetState.isVisible) {
            sheetHeight.snapTo(0f)
            sheetHeight.animateTo(sheetPickHeight, animationSpec)
        } else {
            sheetHeight.snapTo(0f)
        }
    }

    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        scope.launch {
            modalBottomSheetState.show()
        }
    }
}

@Composable
private fun ImportanceSheetContent(
    sheetHeight: Animatable<Float, AnimationVector1D>,
    allImportance: List<ImportanceItem>,
    current: ImportanceItem,
    shouldAnimatedSelection: Boolean,
    setItemImportance: (ImportanceItem) -> Unit
) {
    Column(
        modifier = Modifier
            .height(sheetHeight.value.dp)
            .padding(bottom = 50.dp)
    ) {
        Text(
            text = stringResource(id = R.string.todo_item_select_importance_title),
            style = LocalTodoAppTypography.current.body,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.padding(vertical = 4.dp))

        LazyColumn {
            itemsIndexed(
                items = allImportance,
                key = { _, item -> item.importance }) { index, item ->
                ImportanceItemContent(
                    item = item,
                    current = current,
                    shouldAnimatedSelection = shouldAnimatedSelection,
                    setItemImportance = setItemImportance,
                    index = index,
                    allImportance = allImportance
                )
            }
        }
    }
}

@Composable
private fun ImportanceItemContent(
    item: ImportanceItem,
    current: ImportanceItem,
    shouldAnimatedSelection: Boolean,
    setItemImportance: (ImportanceItem) -> Unit,
    index: Int,
    allImportance: List<ImportanceItem>
) {
    ImportanceView(
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 24.dp),
        item = item,
        isItemSelected = item == current,
        isItemSelectionAnimated = item.isHighlighted
                && item == current
                && shouldAnimatedSelection,
        onItemSelected = {
            setItemImportance(item)
        }
    )

    if (index != allImportance.size - 1) {
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = LocalTodoColorsPalette.current.separatorColor
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ImportanceView(
    modifier: Modifier = Modifier,
    item: ImportanceItem,
    isItemSelected: Boolean,
    isItemSelectionAnimated: Boolean,
    onItemSelected: () -> Unit
) {
    var animationTriggered by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val colorAnimation by animateColorAsState(
        targetValue = if (animationTriggered) LocalTodoColorsPalette.current.redColor.copy(alpha = 0.1f)
        else LocalTodoColorsPalette.current.redColor.copy(alpha = 1f),
        animationSpec = tween(durationMillis = 200),
        label = "Highlight importance")

    Row(modifier = modifier
        .fillMaxWidth()
        .clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            onClick = onItemSelected
        ).semantics(mergeDescendants = false) {
            contentDescription = context.getString(item.importanceDescResId)
        }
        .padding(vertical = 10.dp)) {
        Text(text = stringResource(id = item.importanceResId),
            modifier = Modifier.semantics { invisibleToUser() },
            style = if (isItemSelected) LocalTodoAppTypography.current.body else LocalTodoAppTypography.current.subhead,
            fontWeight = if (isItemSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (item.isHighlighted) colorAnimation else LocalTodoColorsPalette.current.labelPrimaryColor)
    }

    LaunchedEffect(key1 = isItemSelectionAnimated) {
        if (isItemSelectionAnimated) {
            animationTriggered = true
            delay(300)
            animationTriggered = false
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ImportanceView(
    modifier: Modifier = Modifier,
    importance: ImportanceItem,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Column(modifier = modifier
        .semantics(mergeDescendants = true) {
            contentDescription =
                context.getString(R.string.todo_item_importance_description,
                    context.getString(importance.importanceDescResId))
        }
        .clickable(
            enabled = isEnabled,
            onClickLabel = context.getString(R.string.todo_item_select_importance_title),
            role = Role.DropdownList,
            onClick = onClick
        )
    ) {
        Text(
            text = stringResource(id = R.string.todo_item_create_importance_title),
            color = LocalTodoColorsPalette.current.labelPrimaryColor,
            style = LocalTodoAppTypography.current.body,
            modifier = Modifier.semantics { this.invisibleToUser() }
        )
        
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
        
        Text(
            text = stringResource(importance.importanceResId),
            color = if (importance.isHighlighted) LocalTodoColorsPalette.current.redColor else
            LocalTodoColorsPalette.current.labelTertiaryColor,
            style = LocalTodoAppTypography.current.subhead,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { this.invisibleToUser() }
                .padding(vertical = 4.dp)
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun DoUntilView(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    isDoUntilSet: Boolean,
    doUntil: LocalDate,
    onDoUntilSelected: (LocalDate?) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Row(modifier = modifier.semantics(mergeDescendants = true) {
            contentDescription =
                if (isDoUntilSet) context.getString(R.string.todo_item_create_do_until_set_desc, doUntil.asString())
                else context.getString(R.string.todo_item_create_do_until_unset_desc)
        },
        verticalAlignment = Alignment.CenterVertically) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = stringResource(id = R.string.todo_item_create_do_until_title),
                color = LocalTodoColorsPalette.current.labelPrimaryColor,
                style = LocalTodoAppTypography.current.body
            )
            if (isDoUntilSet) {
                Text(
                    text = doUntil.asString(),
                    color = LocalTodoColorsPalette.current.blueColor,
                    style = LocalTodoAppTypography.current.subhead
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
                    color = LocalTodoColorsPalette.current.blueColor,
                    style = LocalTodoAppTypography.current.button
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissed) {
                Text(
                    text = stringResource(id = R.string.todo_calendar_cancel_button_text),
                    color = LocalTodoColorsPalette.current.blueColor,
                    style = LocalTodoAppTypography.current.button
                )
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                titleContentColor = LocalTodoColorsPalette.current.blueColor,
                selectedDayContainerColor = LocalTodoColorsPalette.current.blueColor
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
    val context = LocalContext.current
    Switch(
        enabled = enabled,
        checked = isDoUntilSet,
        modifier = Modifier.semantics {
          contentDescription = context.getString(R.string.todo_item_create_do_until_switch_desc)
        },
        onCheckedChange = { onClick() },
        colors = SwitchDefaults.colors(
            checkedThumbColor = LocalTodoColorsPalette.current.blueColor,
            checkedTrackColor = LocalTodoColorsPalette.current.blueColor.copy(ContentAlpha.disabled),
            uncheckedThumbColor = LocalTodoColorsPalette.current.backElevatedColor,
            uncheckedTrackColor = LocalTodoColorsPalette.current.overlayColor,
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun EpicPulsarButton(
    isActive: Boolean,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale = remember { Animatable(1f) }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            scale.animateTo(
                targetValue = 1.2f,
                animationSpec = tween(durationMillis = 150)
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 150)
            )
            onDeleteClick()
            isPressed = false
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .then(modifier)
            .scale(scale.value)
            .clickable(
                enabled = isActive,
                onClick = {
                    if (!isPressed) {
                        isPressed = true
                    }
                },
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                    color = if (isActive) LocalTodoColorsPalette.current.redColor else LocalTodoColorsPalette.current.labelDisableColor
                )
            )
            .padding(8.dp) // Adding padding for better touch target
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.semantics { invisibleToUser() }
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(id = R.string.todo_item_importance_remove_icon_description),
                tint = if (isActive) LocalTodoColorsPalette.current.redColor
                else LocalTodoColorsPalette.current.labelDisableColor,
                modifier = Modifier.padding(end = 8.dp)
            )

            Text(
                text = stringResource(id = R.string.todo_item_create_delete_button_text),
                color = if (isActive) LocalTodoColorsPalette.current.redColor
                else LocalTodoColorsPalette.current.labelDisableColor,
                fontSize = 18.sp
            )
        }
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
            EpicPulsarButton(isActive = isActive, onDeleteClick = { })
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

@SuppressLint("RestrictedApi")
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