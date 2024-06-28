package com.homework.todolist.tododetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homework.todolist.R
import com.homework.todolist.tododetails.viewmodel.TodoDetailsViewModelR
import com.homework.todolist.ui.theme.TodoColorsPalette
import com.homework.todolist.tododetails.viewmodel.TodoDetailsViewModelR.Companion.DetailsEvent.*
import com.homework.todolist.tododetails.viewmodel.TodoDetailsViewModelR.Companion.DetailsEffects.*
import kotlinx.coroutines.launch

/**
 * Todo details screen view
 * @param onActionClick Click handler for important actions (close, save, delete and etc)
 * @param viewModel Details view-model
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailsScreenRef(
    onActionClick: () -> Unit,
    viewModel: TodoDetailsViewModelR = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val itemUiState by viewModel.state.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                modifier = if (scrollState.value == 0) Modifier else Modifier.shadow(8.dp),
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleEvent(OnCloseClick) }) {
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
                                viewModel.handleEvent(OnSaveClick)
                            }
                        ) {
                            Text(
                                text = stringResource(id = R.string.todo_item_create_save_icon_text),
                                color = TodoColorsPalette.current.blueColor
                            )
                        }
                    }
                })
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
                    viewModel.handleEvent(OnDescriptionUpdate(it))
                }

                ImportanceView(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(),
                    importance = itemUiState.importance,
                    enabled = !itemUiState.isDone
                ) {
                    viewModel.handleEvent(OnImportanceChosen(it))
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
                    viewModel.handleEvent(OnDeadlinePicked(it))
                }
            }

            HorizontalDivider(color = TodoColorsPalette.current.separatorColor)

            DeleteTaskView(
                modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp),
                isActive = itemUiState.id != null
            ) {
                viewModel.handleEvent(OnDeleteClick)
                onActionClick()
            }
        }
    }

    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect {
            when (it) {
                is UnknownErrorToast -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(context.getString(R.string.todo_details_unknown_error_text))
                    }
                }

                is ShowSaveErrorToast -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(context.getString(R.string.todo_details_save_error_text))
                    }
                }

                is OnItemSaved -> {
                    onActionClick()
                }

                is OnItemDeleted -> {
                    onActionClick()
                }

                is OnFormClosed -> {
                    onActionClick()
                }
            }

        }
    }
}