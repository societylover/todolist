package com.homework.todolist.tododetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homework.todolist.shared.ViewModelBase

/**
 * Todo details screen view
 * @param onActionClick Click handler for important actions (close, save, delete and etc)
 * @param viewModel Details view-model
 */
@Composable
fun TodoDetailsScreenRef(
    onActionClick: () -> Unit,
    viewModel: TodoDetailsViewModel = hiltViewModel()
) {
    val itemUiState by viewModel.todoItemState.collectAsStateWithLifecycle()

}