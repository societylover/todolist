package com.homework.todolist

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
import com.homework.todolist.ui.theme.LocalCustomColorsPalette
import com.homework.todolist.ui.theme.TodolistTheme
import com.homework.todolist.ui.theme.Typography
import com.homework.todolist.ui.theme.body
import com.homework.todolist.ui.theme.subhead
import com.homework.todolist.ui.theme.title
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
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
                    scrolledContainerColor = LocalCustomColorsPalette.current.backPrimaryColor
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
                containerColor = LocalCustomColorsPalette.current.blueColor,
                contentColor = LocalCustomColorsPalette.current.whiteColor
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
                .padding(top = 2.dp)
                .padding(horizontal = 8.dp)
                .shadow(2.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(3.dp))
                .background(LocalCustomColorsPalette.current.backSecondaryColor)
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
            )
            //.border(width = 1.dp, color = Color.Red, shape = RoundedCornerShape(1.dp)))
            {
                items(uiState.todoList, key = { it.id }) {
                    TodoListItem(
                        item = it,
                        onItemClick = { onItemClick(it.id) },
                        onItemDelete = { viewModel.removeTodo(it.id) },
                        onItemDoneStateChange = { viewModel.changeTodoDoneState(it) })
                }
            }
        }
    }
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
                        completedCount),
                    color = LocalCustomColorsPalette.current.labelTertiaryColor,
                    style = Typography.body(),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onItemsVisibilityActionClicked() }) {
                    Icon(
                        painter = painterResource(id = visibilityIconResId),
                        contentDescription = stringResource(id = descriptionResId),
                        tint = LocalCustomColorsPalette.current.blueColor)
                }
            }
        }
    } else {
        Row(modifier = Modifier.padding(end = 24.dp)) {
            Text(
                text = stringResource(id = R.string.todo_list_screen_title),
                style = Typography.title(),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { onItemsVisibilityActionClicked() }) {
                Icon(
                    painter = painterResource(id = visibilityIconResId),
                    contentDescription = stringResource(id = descriptionResId),
                    tint = LocalCustomColorsPalette.current.blueColor)
            }

        }
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
val TopAppBarScrollBehavior.isCollapsed: Boolean
    get() = this.state.collapsedFraction < 0.9f
//get() = this.state.collapsedFraction < 0.85f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListItem(
    item: TodoItem,
    onItemClick: () -> Unit,
    onItemDelete: () -> Unit,
    onItemDoneStateChange: () -> Unit) {

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when(it) {
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
        backgroundContent = { DismissBackground(dismissState, item.done)},
        enableDismissFromStartToEnd = !item.done)
    {
        Row(
            modifier = Modifier
                .background(LocalCustomColorsPalette.current.backSecondaryColor)
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .fillMaxWidth()
                .clickable { onItemClick() },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TodoStateBox(
                item.importance, item.done, Modifier
                    .padding(end = 12.dp)
            )

            Column(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .weight(1f)
            ) {
                Row {
                   when(item.importance) {
                       Importance.URGENT -> {
                           Icon(painter = painterResource(id = R.drawable.importance_high),
                               contentDescription = "high",
                               modifier = Modifier.padding(end = 3.dp),
                               tint = LocalCustomColorsPalette.current.redColor)
                       }
                       Importance.LOW -> {
                           Icon(painter = painterResource(id = R.drawable.importance_low),
                               contentDescription = "low",
                               modifier = Modifier.padding(end = 3.dp),
                               tint = LocalCustomColorsPalette.current.grayLightColor)
                       }
                       else -> { }
                   }
                    Text(
                        text = item.text,
                        color = if (item.done) LocalCustomColorsPalette.current.labelTertiaryColor
                        else LocalCustomColorsPalette.current.labelPrimaryColor,
                        style = if (!item.done) LocalTextStyle.current
                        else LocalTextStyle.current.copy(
                            textDecoration = TextDecoration.LineThrough,
                            color = LocalCustomColorsPalette.current.labelTertiaryColor
                        ),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (item.deadlineAt != null) {
                    Text(
                        text = "01.01.1970", //dateFormat.format(item.deadlineAt),
                        color = LocalCustomColorsPalette.current.labelTertiaryColor,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.subhead()
                    )
                }
            }

            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = stringResource(id = R.string.todo_list_info_description),
                tint = LocalCustomColorsPalette.current.labelTertiaryColor
            )
        }
    }


}

@Composable
fun TodoStateBox(importance: Importance, done: Boolean, modifier: Modifier = Modifier) {
    val backgroundColor = if (done) LocalCustomColorsPalette.current.greenColor
    else if (importance == Importance.URGENT) LocalCustomColorsPalette.current.redColor.copy(alpha = ContentAlpha.disabled)
    else Color.Transparent

    val borderColor = if (done) Color.Transparent
    else if (importance == Importance.URGENT) LocalCustomColorsPalette.current.redColor
    else LocalCustomColorsPalette.current.separatorColor

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
                contentDescription = "",
                tint = LocalCustomColorsPalette.current.whiteColor
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissBackground(dismissState: SwipeToDismissBoxState, itemIsDone: Boolean) {
    val color = when (dismissState.dismissDirection) {
        StartToEnd -> if (!itemIsDone) Color(LocalCustomColorsPalette.current.greenColor.value) else Color.Transparent
        EndToStart -> Color(LocalCustomColorsPalette.current.redColor.value)
        Settled -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(24.dp, 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            Icons.Default.Check,
            contentDescription = "Done",
            tint = LocalCustomColorsPalette.current.whiteColor
        )
        Spacer(modifier = Modifier)
        Icon(
            Icons.Default.Delete,
            contentDescription = "Delete",
            tint = LocalCustomColorsPalette.current.whiteColor
        )
    }
}


@Preview(showBackground = true)
@Composable
fun TodoListItemPreview() {
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