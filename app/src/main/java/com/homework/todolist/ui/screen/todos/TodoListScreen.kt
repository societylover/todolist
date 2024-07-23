package com.homework.todolist.ui.screen.todos

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.SwipeToDismissBoxValue.EndToStart
import androidx.compose.material3.SwipeToDismissBoxValue.Settled
import androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homework.todolist.R
import com.homework.todolist.data.model.Importance
import com.homework.todolist.data.model.TodoItem
import com.homework.todolist.data.repository.TodoItemsRepositoryStub
import com.homework.todolist.ui.screen.todos.TodoListViewModel.Companion.ListItemEffects.ErrorOccurredToast
import com.homework.todolist.ui.screen.todos.TodoListViewModel.Companion.ListItemEffects.RepeatableErrorOccurredToast
import com.homework.todolist.ui.screen.todos.data.TodoListUiState
import com.homework.todolist.ui.theme.LocalTodoAppTypography
import com.homework.todolist.ui.theme.LocalTodoColorsPalette
import com.homework.todolist.ui.theme.TodolistTheme
import com.homework.todolist.utils.DateFormatter.asString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * List of todo items
 * @param onItemClick Item selection click handler
 * @param onCreateItemClick Handler for creating new item
 * @param viewModel List screen view-model
 */
@Composable
internal fun TodoListScreen(
    onItemClick: (String) -> Unit,
    onCreateItemClick: () -> Unit,
    onSettingsClicked: () -> Unit,
    viewModel: TodoListViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    val expandedHeight by remember { derivedStateOf { 164.dp } }
    val collapsedHeight by remember { derivedStateOf { 56.dp } }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val topBarHeight by remember {
        derivedStateOf {
            if (lazyListState.firstVisibleItemIndex == 0) {
                (expandedHeight - lazyListState.firstVisibleItemScrollOffset.dp)
                    .coerceIn(collapsedHeight, expandedHeight)
            } else {
                collapsedHeight
            }
        }
    }

    val iconParams by remember {
        derivedStateOf {
            if (uiState.isDoneShown) {
                VisibilityIconParams(
                    R.drawable.visibility_24,
                    R.string.todo_list_visibility_on_description
                )
            } else {
                VisibilityIconParams(
                    R.drawable.visibility_off_24,
                    R.string.todo_list_visibility_off_description
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBarContent(
                topBarHeight = topBarHeight,
                expandedHeight = expandedHeight,
                collapsedHeight = collapsedHeight,
                uiState = uiState,
                onSettingsClicked = { onSettingsClicked() },
                onVisibilityClicked = { viewModel.handleEvent(TodoListViewModel.Companion.ListEvent.OnVisibilityStateClicked) },
                iconParams = iconParams
            )
        },
        floatingActionButton = {
            FABContent(onCreateItemClick = onCreateItemClick)
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddings ->
        ListContent(paddings, lazyListState, uiState, onItemClick, viewModel, onCreateItemClick)
    }

    HandleEffects(viewModel, scope, snackbarHostState)
}

@Composable
private fun FABContent(onCreateItemClick: () -> Unit) {
    FloatingActionButton(
        onClick = onCreateItemClick,
        modifier = Modifier.padding(
            end = dimensionResource(id = R.dimen.todo_list_fab_end_padding),
            bottom = dimensionResource(id = R.dimen.todo_list_fab_bottom_padding)
        ),
        shape = CircleShape.copy(CornerSize(dimensionResource(id = R.dimen.todo_list_fab_size))),
        containerColor = LocalTodoColorsPalette.current.blueColor,
        contentColor = LocalTodoColorsPalette.current.whiteColor
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(id = R.string.todo_list_create_button_description)
        )
    }
}

@Composable
private fun TopAppBarContent(
    topBarHeight: Dp,
    expandedHeight: Dp,
    collapsedHeight: Dp,
    uiState: TodoListUiState,
    onVisibilityClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    iconParams: VisibilityIconParams
) {
    val dynamicTopPadding by animateDpAsState(
        targetValue = 1.dp,
        animationSpec = spring(dampingRatio = 0.9f, stiffness = Spring.StiffnessHigh),
        label = "ExpandableTopAppBar.animateDpAsState dynamicTopPadding"
    )

    ExpandableTopAppBar(
        modifier = Modifier.padding(start = 16.dp),
        topBarHeight = topBarHeight,
        collapsedHeight = collapsedHeight,
        actionTopPadding = dynamicTopPadding,
        collapsedContent = {
            Text(
                text = stringResource(id = R.string.todo_list_screen_title),
                color = LocalTodoColorsPalette.current.labelPrimaryColor,
                fontSize = LocalTodoAppTypography.current.title.fontSize,
                fontWeight = LocalTodoAppTypography.current.title.fontWeight
            )
        },
        expandedContent = {
            Text(
                text = stringResource(
                    id = R.string.todo_list_completed_subtitle,
                    uiState.doneCount
                ),
                color = LocalTodoColorsPalette.current.labelTertiaryColor,
                style = LocalTodoAppTypography.current.body
            )
        },
        scrollableAction = {
            VisibilityButton(
                onItemsVisibilityActionClicked = onVisibilityClicked,
                visibilityIconResId = iconParams.iconRes,
                descriptionResId = iconParams.textRes
            )
        },
        pinnedAction = {
            IconButton(onClick = onSettingsClicked) {
                Icon(
                    imageVector = Icons.Default.Settings, stringResource(id = R.string.setting_screen_open_button_desc),
                )
            }
        }
    )
}

@Composable
private fun HandleEffects(
    viewModel: TodoListViewModel,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect {
            when (it) {
                is RepeatableErrorOccurredToast -> {
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            context.getString(it.textResId),
                            actionLabel = context.getString(it.actionResId),
                            duration = SnackbarDuration.Indefinite
                        )

                        when (result) {
                            SnackbarResult.ActionPerformed -> {
                                it.callback()
                            }
                            else -> { }
                        }
                    }
                }

                is ErrorOccurredToast -> {
                    snackbarHostState.showSnackbar(context.getString(it.textResId))
                }
            }
        }
    }
}

@Composable
private fun ListContent(
    paddings: PaddingValues,
    lazyListState: LazyListState,
    uiState: TodoListUiState,
    onItemClick: (String) -> Unit,
    viewModel: TodoListViewModel,
    onCreateItemClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(paddings)
            .padding(horizontal = 8.dp)
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
        )
        {

            item { TodosTopBorder() }

            items(uiState.todoList, key = { it.id }) {
                TodoListItem(
                    item = it,
                    onItemClick = { onItemClick(it.id) },
                    onItemDelete = {
                        viewModel.handleEvent(
                            TodoListViewModel.Companion.ListEvent.OnDeleteClicked(
                                it.id
                            )
                        )
                    },
                    onItemDoneStateChange = {
                        viewModel.handleEvent(
                            TodoListViewModel.Companion.ListEvent.OnStateChangeClicked(
                                it
                            )
                        )
                    })
            }

            item {
                TodosNewItemView(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .padding(start = 52.dp)
                ) {
                    onCreateItemClick()
                }
            }

            item { TodosBottomBorder() }
        }
    }
}

@Composable
private fun ExpandableTopAppBar(
    modifier: Modifier = Modifier,
    topBarHeight: Dp = 150.dp,
    collapsedHeight: Dp = 56.dp,
    dampingRatio: Float = 0.75f,
    actionTopPadding: Dp = 100.dp,
    stiffness: Float = Spring.StiffnessMedium,
    collapsedContent: @Composable () -> Unit = { },
    expandedContent: @Composable () -> Unit = { },
    scrollableAction: @Composable () -> Unit = { },
    pinnedAction: @Composable () -> Unit = { }
    ) {
    val animatedHeight by animateDpAsState(
        targetValue = topBarHeight,
        animationSpec = spring(dampingRatio = dampingRatio, stiffness = stiffness),
        label = "ExpandableTopAppBar.animateDpAsState height"
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(animatedHeight)
            .background(LocalTodoColorsPalette.current.backPrimaryColor),
        contentAlignment = Alignment.CenterStart
    ) {
        val boxWithConstraintsScope = this
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 50.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = modifier.weight(10f)
            ) {
                collapsedContent()
                if (boxWithConstraintsScope.maxHeight - 56.dp > collapsedHeight) {
                    expandedContent()
                }
            }
            Column(
                modifier = Modifier
                    .padding(top = actionTopPadding, end = 12.dp),
                horizontalAlignment = Alignment.End
            ) {
                scrollableAction()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 16.dp, top = 4.dp),
            contentAlignment = TopEnd
        ) {
            pinnedAction()
        }
    }
}

@Composable
private fun TodosTopBorder() {
    Box(
        modifier = Modifier
            .padding(top = 2.dp)
            .fillMaxWidth()
            .height(8.dp)
            .background(
                color = LocalTodoColorsPalette.current.backSecondaryColor,
                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
            )
    )
}

@Composable
private fun TodosBottomBorder() {
    Box(
        modifier = Modifier
            .padding(bottom = 28.dp)
            .fillMaxWidth()
            .height(8.dp)
            .background(
                color = LocalTodoColorsPalette.current.backSecondaryColor,
                shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
            )
    )
}

/**
 * Bottom to-do item 'item'. Shortcut for item creating
 * @param modifier Modifier
 * @param onItemClick On item click callback
 */
@Composable
private fun TodosNewItemView(
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit
) {
    Text(
        text = stringResource(id = R.string.todo_list_bottom_item_text),
        color = LocalTodoColorsPalette.current.labelTertiaryColor,
        style = LocalTodoAppTypography.current.body,
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalTodoColorsPalette.current.backSecondaryColor)
            .clickable { onItemClick() }
            .then(modifier)
    )
}

private data class VisibilityIconParams(
    @DrawableRes val iconRes: Int,
    @StringRes val textRes: Int
)

@Composable
private fun VisibilityButton(
    onItemsVisibilityActionClicked: () -> Unit,
    visibilityIconResId: Int,
    descriptionResId: Int
) {
    IconButton(
        modifier = Modifier.size(24.dp),
        onClick = { onItemsVisibilityActionClicked() }) {
        Icon(
            painter = painterResource(id = visibilityIconResId),
            contentDescription = stringResource(id = descriptionResId),
            tint = LocalTodoColorsPalette.current.blueColor
        )
    }
}

@Preview
@Composable
private fun VisibilityButtonPreview(
    iconParams: VisibilityIconParams = VisibilityIconParams(
        R.drawable.visibility_24,
        R.string.todo_list_visibility_on_description
    ), darkTheme: Boolean = false
) {
    Column {
        TodolistTheme(darkTheme = darkTheme, dynamicColor = false) {
            VisibilityButton(
                onItemsVisibilityActionClicked = { },
                visibilityIconResId = iconParams.iconRes,
                descriptionResId = iconParams.textRes
            )
        }
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun VisibilityButtonPreview() {
    Column {
        val iconVisible = VisibilityIconParams(
            R.drawable.visibility_24,
            R.string.todo_list_visibility_on_description
        )

        VisibilityButton(
            onItemsVisibilityActionClicked = { },
            visibilityIconResId = iconVisible.iconRes,
            descriptionResId = iconVisible.textRes
        )

        val iconInvisible = VisibilityIconParams(
            R.drawable.visibility_off_24,
            R.string.todo_list_visibility_off_description
        )

        VisibilityButton(
            onItemsVisibilityActionClicked = { },
            visibilityIconResId = iconInvisible.iconRes,
            descriptionResId = iconInvisible.textRes
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodoListItem(
    item: TodoItem,
    onItemClick: () -> Unit,
    onItemDelete: () -> Unit,
    onItemDoneStateChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = SwipeToDismissBoxState(
        initialValue = Settled,
        density = LocalDensity.current,
        confirmValueChange = {
            when (it) {
                StartToEnd -> {
                    onItemDoneStateChange()
                }

                EndToStart -> {
                    onItemDelete()
                }

                Settled -> return@SwipeToDismissBoxState false
            }
            return@SwipeToDismissBoxState false
        },
        positionalThreshold = { it * .25f })

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        backgroundContent = {
            DismissBackground(
                dismissDirection = dismissState.dismissDirection,
                startToEndIcon = if (item.done) Icons.Default.Close else Icons.Default.Check,
                startToEndColor = if (item.done) LocalTodoColorsPalette.current.yellowColor else
                    LocalTodoColorsPalette.current.greenColor,
                endToStartColor = LocalTodoColorsPalette.current.redColor
            )
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onItemClick() }
                .background(LocalTodoColorsPalette.current.backSecondaryColor)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TodoStateBox(
                importance = item.importance, done = item.done, modifier = Modifier
                    .padding(end = 12.dp)
            )

            Column(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .weight(1f)
            ) {
                Row {
                    when (item.importance) {
                        Importance.URGENT -> {
                            Icon(
                                painter = painterResource(id = R.drawable.importance_high),
                                contentDescription = stringResource(id = R.string.todo_item_importance_urgent_icon_description),
                                modifier = Modifier.padding(end = 3.dp),
                                tint = LocalTodoColorsPalette.current.redColor
                            )
                        }

                        Importance.LOW -> {
                            Icon(
                                painter = painterResource(id = R.drawable.importance_low),
                                contentDescription = stringResource(id = R.string.todo_item_importance_low_icon_description),
                                modifier = Modifier.padding(end = 3.dp),
                                tint = LocalTodoColorsPalette.current.grayLightColor
                            )
                        }

                        else -> {}
                    }
                    Text(
                        text = item.text,
                        color = if (item.done) LocalTodoColorsPalette.current.labelTertiaryColor
                        else LocalTodoColorsPalette.current.labelPrimaryColor,
                        style = if (!item.done) LocalTextStyle.current
                        else LocalTextStyle.current.copy(
                            textDecoration = TextDecoration.LineThrough,
                            color = LocalTodoColorsPalette.current.labelTertiaryColor
                        ),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (item.deadlineAt != null) {
                    Text(
                        text = item.deadlineAt.asString(),
                        color = LocalTodoColorsPalette.current.labelTertiaryColor,
                        overflow = TextOverflow.Ellipsis,
                        style = LocalTodoAppTypography.current.body
                    )
                }
            }

            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = stringResource(id = R.string.todo_list_info_description),
                tint = LocalTodoColorsPalette.current.labelTertiaryColor
            )
        }
    }
}

@Preview
@Composable
private fun TodoListItemPreview() {
    TodolistTheme(darkTheme = false, dynamicColor = false) {
        Column {
            TodoListItem(
                item = TodoItem(
                    id = "1",
                    done = true,
                    importance = Importance.URGENT,
                    text = "Hello!",
                    deadlineAt = LocalDate.now()
                ),
                onItemClick = { },
                onItemDelete = { },
                onItemDoneStateChange = { })
            TodoListItem(
                item = TodoItem(
                    id = "1",
                    done = false,
                    importance = Importance.ORDINARY,
                    text = "Hello!"
                ),
                onItemClick = { },
                onItemDelete = { },
                onItemDoneStateChange = { })
            TodoListItem(
                item = TodoItem(
                    id = "1",
                    done = false,
                    importance = Importance.LOW,
                    text = "Hi!",
                    deadlineAt = LocalDate.now()
                ),
                onItemClick = { },
                onItemDelete = { },
                onItemDoneStateChange = { })
        }

    }
}

@Composable
private fun TodoStateBox(importance: Importance, done: Boolean, modifier: Modifier = Modifier) {
    val backgroundColor = if (done) LocalTodoColorsPalette.current.greenColor
    else if (importance == Importance.URGENT) LocalTodoColorsPalette.current.redColor.copy(alpha = ContentAlpha.disabled)
    else Color.Transparent

    val borderColor = if (done) Color.Transparent
    else if (importance == Importance.URGENT) LocalTodoColorsPalette.current.redColor
    else LocalTodoColorsPalette.current.separatorColor

    Box(
        contentAlignment = Center,
        modifier = modifier
            .size(18.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(4.dp))
    ) {
        if (done) {
            Icon(
                Icons.Default.Check,
                contentDescription = stringResource(id = R.string.todo_item_importance_done_check_box_description),
                tint = LocalTodoColorsPalette.current.whiteColor
            )
        }
    }
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
private fun TodoStateBoxPreview() {
    TodolistTheme {
        Column {
            TodoStateBox(Importance.LOW, false)
            TodoStateBox(Importance.URGENT, false)
            TodoStateBox(Importance.ORDINARY, false)

            TodoStateBox(Importance.LOW, true)
            TodoStateBox(Importance.URGENT, true)
            TodoStateBox(Importance.ORDINARY, true)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DismissBackground(
    dismissDirection: SwipeToDismissBoxValue,
    endToStartColor: Color = LocalTodoColorsPalette.current.redColor,
    startToEndColor: Color = LocalTodoColorsPalette.current.greenColor,
    startToEndIcon: ImageVector = Icons.Default.Check
) {
    val color = when (dismissDirection) {
        StartToEnd -> startToEndColor
        EndToStart -> endToStartColor
        Settled -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = startToEndIcon,
            contentDescription = stringResource(id = R.string.todo_item_importance_done_icon_description),
            tint = LocalTodoColorsPalette.current.whiteColor,
            modifier = Modifier
                .padding(start = 24.dp)
                .size(24.dp)
        )
        Spacer(modifier = Modifier)
        Icon(
            Icons.Default.Delete,
            contentDescription = stringResource(id = R.string.todo_item_importance_remove_icon_description),
            tint = LocalTodoColorsPalette.current.whiteColor,
            modifier = Modifier
                .padding(end = 24.dp)
                .size(24.dp)
        )
    }
}

@Composable
private fun TodoListWithThemePreview(darkTheme: Boolean = false) {
    TodolistTheme(darkTheme = darkTheme, dynamicColor = false) {
        val vm = TodoListViewModel(TodoItemsRepositoryStub())
        TodoListScreen(
            onItemClick = { },
            onCreateItemClick = { },
            onSettingsClicked = { },
            viewModel = vm
        )
        vm.handleEvent(TodoListViewModel.Companion.ListEvent.OnVisibilityStateClicked)
    }
}

@Preview(showBackground = false)
@Composable
private fun TodoListDarkTheme() {
    TodoListWithThemePreview(true)
}

@Preview(showBackground = false)
@Composable
private fun TodoListLightTheme() {
    TodoListWithThemePreview(false)
}

