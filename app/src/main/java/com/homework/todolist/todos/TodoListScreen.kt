package com.homework.todolist.todos

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue.EndToStart
import androidx.compose.material3.SwipeToDismissBoxValue.Settled
import androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homework.todolist.R
import com.homework.todolist.data.model.Importance
import com.homework.todolist.data.model.TodoItem
import com.homework.todolist.data.model.TodoItemId
import com.homework.todolist.ui.theme.TodoAppTypography
import com.homework.todolist.ui.theme.TodoColorsPalette
import com.homework.todolist.ui.theme.TodolistTheme
import com.homework.todolist.ui.theme.Typography
import com.homework.todolist.utils.DateFormatter.asString
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TodoListScreen(
    onItemClick: (TodoItemId) -> Unit,
    onCreateItemClick: () -> Unit,
    viewModel: TodoListViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = rememberTopAppBarState(),
        snapAnimationSpec = spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMedium)
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    TopAppBarContent(
                        isCollapsed = scrollBehavior.isCollapsed,
                        completedCount = uiState.doneCount,
                        isAllItemsShows = uiState.isDoneShown,
                        onItemsVisibilityActionClicked = { viewModel.triggerShowDoneItemsVisibilityState() }
                    )
                },
                scrollBehavior = scrollBehavior,
                modifier = Modifier.shadow(elevation = if (scrollBehavior.isCollapsed) 0.dp else 30.dp),
                colors = TopAppBarDefaults.largeTopAppBarColors().copy(
                    scrolledContainerColor = TodoColorsPalette.current.backPrimaryColor
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateItemClick,
                modifier = Modifier.padding(
                    end = dimensionResource(id = R.dimen.todo_list_fab_end_padding),
                    bottom = dimensionResource(id = R.dimen.todo_list_fab_bottom_padding)
                ),
                shape = CircleShape.copy(CornerSize(dimensionResource(id = R.dimen.todo_list_fab_size))),
                containerColor = TodoColorsPalette.current.blueColor,
                contentColor = TodoColorsPalette.current.whiteColor
            )
            {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.todo_list_create_button_description)
                )
            }
        }
    ) { paddings ->
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
                        onItemDelete = { viewModel.removeTodo(it.id) },
                        onItemDoneStateChange = { viewModel.changeTodoDoneState(it) })
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


@Composable
private fun TopAppBarContent(
    isCollapsed: Boolean,
    completedCount: Int,
    isAllItemsShows: Boolean,
    onItemsVisibilityActionClicked: () -> Unit
) {
    val (visibilityIconResId, descriptionResId) = if (!isAllItemsShows) {
        Pair(R.drawable.visibility_24, R.string.todo_list_visibility_on_description)
    } else {
        Pair(R.drawable.visibility_off_24, R.string.todo_list_visibility_off_description)
    }

    if (isCollapsed) {
        Column(modifier = Modifier.padding(start = 60.dp, end = 24.dp)) {
            Text(
                text = stringResource(id = R.string.todo_list_screen_title),
                style = Typography.titleLarge
            )
            Row {
                Text(
                    text = stringResource(
                        id = R.string.todo_list_completed_subtitle,
                        completedCount
                    ),
                    color = TodoColorsPalette.current.labelTertiaryColor,
                    style = TodoAppTypography.current.body,
                    modifier = Modifier.weight(1f)
                )
                VisibilityButton(
                    onItemsVisibilityActionClicked,
                    visibilityIconResId,
                    descriptionResId
                )
            }
        }
    } else {
        Row(modifier = Modifier.padding(end = 24.dp)) {
            Text(
                text = stringResource(id = R.string.todo_list_screen_title),
                style = TodoAppTypography.current.body,
                modifier = Modifier.weight(1f)
            )
            VisibilityButton(onItemsVisibilityActionClicked, visibilityIconResId, descriptionResId)

        }
    }
}

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

@Preview(showBackground = true)
@Composable
fun TodoListScreenPreview() {
    TodolistTheme {
        TodoListScreen({}, {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private val TopAppBarScrollBehavior.isCollapsed: Boolean
    get() = this.state.collapsedFraction < 0.4f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodoListItem(
    item: TodoItem,
    onItemClick: () -> Unit,
    onItemDelete: () -> Unit,
    onItemDoneStateChange: () -> Unit
) {

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                StartToEnd -> {
                    onItemDoneStateChange()
                }

                EndToStart -> {
                    onItemDelete()
                }

                Settled -> return@rememberSwipeToDismissBoxState false
            }
            return@rememberSwipeToDismissBoxState true
        },
        positionalThreshold = { it * .25f }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = { DismissBackground(dismissState, item.done) },
        enableDismissFromStartToEnd = !item.done
    )
    {
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DismissBackground(
    dismissState: SwipeToDismissBoxState, itemIsDone: Boolean) {
    val color = when (dismissState.dismissDirection) {
        StartToEnd -> if (!itemIsDone) Color(TodoColorsPalette.current.greenColor.value) else Color.Transparent
        EndToStart -> Color(TodoColorsPalette.current.redColor.value)
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
            Icons.Default.Check,
            contentDescription = stringResource(id = R.string.todo_item_importance_done_icon_description),
            tint = TodoColorsPalette.current.whiteColor,
            modifier = Modifier.padding(start = 24.dp).size(24.dp)
        )
        Spacer(modifier = Modifier)
        Icon(
            Icons.Default.Delete,
            contentDescription = stringResource(id = R.string.todo_item_importance_remove_icon_description),
            tint = TodoColorsPalette.current.whiteColor,
            modifier = Modifier.padding(end = 24.dp).size(24.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun TodoListItemPreview() {
    TodolistTheme {
        TodoListItem(
            item = TodoItem(
                "2",
                "Необходимо закупиться продуктами на неделю",
                Importance.URGENT,
                true,
                LocalDate.now().minusDays(3),
                LocalDate.now().plusDays(2)
            ), { }, { }
        ) { }
    }
}