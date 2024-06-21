package com.homework.todolist

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homework.todolist.routing.TodoDestinationsArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * To-do item UI state
 */
data class TodoItemUiState (
    val text: String = "",
    @StringRes val importanceResId: Int = R.string.todo_item_importance_ordinary,
    val isDone: Boolean = false,
    val id: TodoItemId? = null,
    val doUntil: LocalDate = LocalDate.now(),
    val isDeadlineSet: Boolean = false
)

@HiltViewModel
class TodoDetailsViewModel @Inject constructor(
    private val todoItemsRepository: TodoItemsRepository,
    savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _todoItemState = MutableStateFlow(TodoItemUiState())
    val todoItemState = _todoItemState.asStateFlow()

    init {
        savedStateHandle.get<String>(TodoDestinationsArgs.TODO_ID)?.also { id ->
            todoItemsRepository.getItemDetails(id)?.also { item ->
                _todoItemState.value = item.toItemUiState()
            }
        }
    }

    /**
     * Update selected item or create new if it's not exist
     */
    fun updateItem() {
        if (todoItemState.value.id == null) {
            viewModelScope.launch(Dispatchers.IO) {
                with(_todoItemState.value) {
                    todoItemsRepository.createItem(text, getImportance(), doUntil)
                }
            }
        } else {
            todoItemState.value.id?.also {
                viewModelScope.launch(Dispatchers.IO) {
                    with(_todoItemState.value) {
                        todoItemsRepository.updateItem(
                            id = it,
                            text = text,
                            done = isDone,
                            importance = getImportance(),
                            deadlineAt = if (isDeadlineSet) doUntil else null)
                    }
                }
            }
        }
    }

    /**
     * Set to-do item data snapshot text
     * @param todoText New to-do description text
     */
    fun setSnapshotText(todoText: String) {
        _todoItemState.value = _todoItemState.value.copy(
            text = todoText
        )
    }

    /**
     * Set to-do item data snapshot importance
     * @param importance New to-do description text
     */
    fun setSnapshotImportance(importance: Importance) {
        _todoItemState.value = _todoItemState.value.copy(
            importanceResId = importance.getImportanceStringResId()
        )
    }

    /**
     * Set to-do item data snapshot do until date
     * @param doUntil New do until date. If date is null, LocalDate.now is set
     */
    fun setSnapshotDoUntil(doUntil: LocalDate?) {
        _todoItemState.value = _todoItemState.value.copy(
            doUntil = doUntil?: LocalDate.now(),
            isDeadlineSet = doUntil != null
        )
    }

    /**
     * Delete shown to-do
     */
    fun deleteItem() {
        todoItemState.value.id?.also { todoId ->
            viewModelScope.launch(Dispatchers.IO) {
                todoItemsRepository.removeItemById(todoId)
            }
        }
    }

    companion object {

        /**
         * Convert TodoItem to it's details ui state
         */
        private fun TodoItem.toItemUiState() =
            TodoItemUiState(
                text = text,
                id = id,
                isDone = done,
                importanceResId = importance.getImportanceStringResId(),
                doUntil = deadlineAt?: LocalDate.now(),
                isDeadlineSet = deadlineAt != null
            )

        /**
         * Convert importance to it's string res representation
         */
        private fun Importance.getImportanceStringResId() =
            when(this) {
                Importance.URGENT -> { R.string.todo_item_importance_urgent }
                Importance.ORDINARY -> { R.string.todo_item_importance_ordinary }
                Importance.LOW -> { R.string.todo_item_importance_low }
            }

        private fun TodoItemUiState.getImportance() =
            when(this.importanceResId) {
                R.string.todo_item_importance_urgent -> Importance.URGENT
                R.string.todo_item_importance_ordinary -> Importance.ORDINARY
                else -> Importance.LOW
            }
    }

}