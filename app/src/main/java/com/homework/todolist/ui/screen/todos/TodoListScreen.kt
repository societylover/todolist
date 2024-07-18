package com.homework.todolist.ui.screen.todos

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homework.todolist.R
import com.homework.todolist.data.model.Importance
import com.homework.todolist.data.model.TodoItem
import com.homework.todolist.data.repository.TodoItemsRepositoryStub
import com.homework.todolist.ui.screen.todos.TodoListViewModel.Companion.ListItemEffects.*
import com.homework.todolist.ui.screen.todos.data.TodoListUiState
import com.homework.todolist.ui.theme.TodoAppTypography
import com.homework.todolist.ui.theme.TodoColorsPalette
import com.homework.todolist.ui.theme.TodolistTheme
import com.homework.todolist.utils.DateFormatter.asString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate


@Composable
internal fun TodoListScreen(
    onItemClick: (String) -> Unit,
    onCreateItemClick: () -> Unit,
    viewModel: TodoListViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    val expandedHeight = 164.dp
    val collapsedHeight = 56.dp

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

    val startPadding by animateDpAsState(
        targetValue = if (topBarHeight == expandedHeight) 60.dp else 16.dp,
        animationSpec = tween(durationMillis = 300),
        label = "ExpandableTopAppBar.animateDpAsState startPadding"
    )

    val fontSize by animateFloatAsState(
        targetValue = if (topBarHeight == expandedHeight) TodoAppTypography.current.largeTitle.fontSize.value else TodoAppTypography.current.title.fontSize.value,
        animationSpec = spring(dampingRatio = 0.9f, stiffness = Spring.StiffnessHigh),
        label = "ExpandableTopAppBar.animateFloatAsState fontSize"
    )

    val fontWeight by animateIntAsState(
        targetValue = if (topBarHeight == expandedHeight) TodoAppTypography.current.largeTitle.fontWeight!!.weight else TodoAppTypography.current.title.fontWeight!!.weight,
        animationSpec = spring(dampingRatio = 0.9f, stiffness = Spring.StiffnessHigh),
        label = "ExpandableTopAppBar.animateIntAsState fontWeight"
    )

    val dynamicTopPadding by animateDpAsState(
        targetValue = if (topBarHeight == expandedHeight) {
            val extraPadding = with(LocalDensity.current) {
                fontSize.sp.toDp().value
            }
            80.dp - extraPadding.dp
        } else {
            1.dp
        },
        animationSpec = spring(dampingRatio = 0.9f, stiffness = Spring.StiffnessHigh),
        label = "ExpandableTopAppBar.animateDpAsState dynamicTopPadding"
    )

    val iconParams by derivedStateOf {
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

    Scaffold(
        topBar = {
            TopAppBarContent(
                startPadding = startPadding,
                topBarHeight = topBarHeight,
                collapsedHeight = collapsedHeight,
                dynamicTopPadding = dynamicTopPadding,
                fontSize = fontSize,
                fontWeight = fontWeight,
                uiState = uiState,
                viewModel = viewModel,
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

    handleEffects(viewModel, scope, snackbarHostState)
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
        containerColor = TodoColorsPalette.current.blueColor,
        contentColor = TodoColorsPalette.current.whiteColor
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(id = R.string.todo_list_create_button_description)
        )
    }
}

@Composable
private fun TopAppBarContent(
    startPadding: Dp,
    topBarHeight: Dp,
    collapsedHeight: Dp,
    dynamicTopPadding: Dp,
    fontSize: Float,
    fontWeight: Int,
    uiState: TodoListUiState,
    viewModel: TodoListViewModel,
    iconParams: VisibilityIconParams
) {
    ExpandableTopAppBar(
        modifier = Modifier.padding(start = startPadding),
        topBarHeight = topBarHeight,
        collapsedHeight = collapsedHeight,
        actionTopPadding = dynamicTopPadding,
        collapsedContent = {
            androidx.compose.material.Text(
                text = stringResource(id = R.string.todo_list_screen_title),
                color = TodoColorsPalette.current.labelPrimaryColor,
                fontSize = fontSize.sp,
                fontWeight = FontWeight(fontWeight)
            )
        },
        expandedContent = {
            androidx.compose.material.Text(
                text = stringResource(
                    id = R.string.todo_list_completed_subtitle,
                    uiState.doneCount
                ),
                color = TodoColorsPalette.current.labelTertiaryColor,
                style = TodoAppTypography.current.body
            )
        },
        action = {
            VisibilityButton(
                onItemsVisibilityActionClicked = { viewModel.handleEvent(TodoListViewModel.Companion.ListEvent.OnVisibilityStateClicked) },
                visibilityIconResId = iconParams.iconRes,
                descriptionResId = iconParams.textRes
            )
        }
    )
}

@Composable
private fun handleEffects(
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
    dampingRatio: Float = 0.9f,
    actionTopPadding: Dp = 100.dp,
    stiffness: Float = Spring.StiffnessHigh,
    collapsedContent: @Composable () -> Unit = { },
    expandedContent: @Composable () -> Unit = { },
    action: @Composable () -> Unit = { }
) {
    val animatedHeight by animateDpAsState(
        targetValue = topBarHeight,
        animationSpec = spring(dampingRatio = dampingRatio, stiffness = stiffness),
        label = "ExpandableTopAppBar.animateDpAsState height"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(animatedHeight)
            .background(TodoColorsPalette.current.backPrimaryColor),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = modifier.weight(10f)
            ) {
                collapsedContent()
                if (topBarHeight - 50.dp > collapsedHeight) {
                    expandedContent()
                }
            }
            Column(
                modifier = Modifier
                    .padding(top = actionTopPadding)
                    .weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                action()
            }
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
                color = TodoColorsPalette.current.backSecondaryColor,
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
                color = TodoColorsPalette.current.backSecondaryColor,
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
        color = TodoColorsPalette.current.labelTertiaryColor,
        style = TodoAppTypography.current.body,
        modifier = Modifier
            .fillMaxWidth()
            .background(TodoColorsPalette.current.backSecondaryColor)
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
            tint = TodoColorsPalette.current.blueColor
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
@Composable
private fun VisibilityButtonPreview() {
    Column {
        val iconVisible = VisibilityIconParams(
            R.drawable.visibility_24,
            R.string.todo_list_visibility_on_description
        )
        VisibilityButtonPreview(iconVisible, true)
        VisibilityButtonPreview(iconVisible, false)

        val iconInvisible = VisibilityIconParams(
            R.drawable.visibility_off_24,
            R.string.todo_list_visibility_off_description
        )
        VisibilityButtonPreview(iconInvisible, true)
        VisibilityButtonPreview(iconInvisible, false)
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
                startToEndColor = if (item.done) TodoColorsPalette.current.yellowColor else
                    TodoColorsPalette.current.greenColor
            )
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onItemClick() }
                .background(TodoColorsPalette.current.backSecondaryColor)
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
                                tint = TodoColorsPalette.current.redColor
                            )
                        }

                        Importance.LOW -> {
                            Icon(
                                painter = painterResource(id = R.drawable.importance_low),
                                contentDescription = stringResource(id = R.string.todo_item_importance_low_icon_description),
                                modifier = Modifier.padding(end = 3.dp),
                                tint = TodoColorsPalette.current.grayLightColor
                            )
                        }

                        else -> {}
                    }
                    Text(
                        text = item.text,
                        color = if (item.done) TodoColorsPalette.current.labelTertiaryColor
                        else TodoColorsPalette.current.labelPrimaryColor,
                        style = if (!item.done) LocalTextStyle.current
                        else LocalTextStyle.current.copy(
                            textDecoration = TextDecoration.LineThrough,
                            color = TodoColorsPalette.current.labelTertiaryColor
                        ),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (item.deadlineAt != null) {
                    Text(
                        text = item.deadlineAt.asString(),
                        color = TodoColorsPalette.current.labelTertiaryColor,
                        overflow = TextOverflow.Ellipsis,
                        style = TodoAppTypography.current.body
                    )
                }
            }

            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = stringResource(id = R.string.todo_list_info_description),
                tint = TodoColorsPalette.current.labelTertiaryColor
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
    val backgroundColor = if (done) TodoColorsPalette.current.greenColor
    else if (importance == Importance.URGENT) TodoColorsPalette.current.redColor.copy(alpha = ContentAlpha.disabled)
    else Color.Transparent

    val borderColor = if (done) Color.Transparent
    else if (importance == Importance.URGENT) TodoColorsPalette.current.redColor
    else TodoColorsPalette.current.separatorColor

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
                tint = TodoColorsPalette.current.whiteColor
            )
        }
    }
}

@Composable
@Preview
private fun TodoStateBoxPreview(darkTheme: Boolean = false) {
    TodolistTheme(darkTheme = darkTheme, dynamicColor = false) {
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

@Composable
@Preview
private fun TodoStateBoxPreview() {
    TodoStateBoxPreview(false)
    TodoStateBoxPreview(true)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DismissBackground(
    dismissDirection: SwipeToDismissBoxValue,
    endToStartColor: Color = Color(TodoColorsPalette.current.redColor.value),
    startToEndColor: Color = Color(TodoColorsPalette.current.greenColor.value),
    startToEndIcon: ImageVector = Icons.Default.Check
) {
    val color by derivedStateOf {
        when (dismissDirection) {
            StartToEnd -> startToEndColor
            EndToStart -> endToStartColor
            Settled -> Color.Transparent
        }
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
            tint = TodoColorsPalette.current.whiteColor,
            modifier = Modifier
                .padding(start = 24.dp)
                .size(24.dp)
        )
        Spacer(modifier = Modifier)
        Icon(
            Icons.Default.Delete,
            contentDescription = stringResource(id = R.string.todo_item_importance_remove_icon_description),
            tint = TodoColorsPalette.current.whiteColor,
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
