package com.homework.todolist.tododetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homework.todolist.data.model.Importance
import com.homework.todolist.data.model.TodoItem
import com.homework.todolist.data.repository.TodoItemsRepository
import com.homework.todolist.navigation.TodoDestinationsArgs
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
data class TodoItemUiStateOld(
    val text: String = "",
    val importance: Importance = Importance.ORDINARY,
    val isDone: Boolean = false,
    val id: String? = null,
    val doUntil: LocalDate = LocalDate.now(),
    val isDeadlineSet: Boolean = false
)

@HiltViewModel
class TodoDetailsViewModel @Inject constructor(
    private val todoItemsRepository: TodoItemsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _todoItemState = MutableStateFlow(TodoItemUiStateOld())
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
     * If text is empty and item is not exist do nothing
     */
    fun replaceItem() {
        if (todoItemState.value.id == null) {

            // Do nothing if text is empty
            if (_todoItemState.value.text.isEmpty()) return

            viewModelScope.launch(Dispatchers.IO) {
                with(_todoItemState.value) {
                    todoItemsRepository.createItem(
                        text, importance,
                        if (isDeadlineSet) doUntil else null
                    )
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
                            importance = importance,
                            deadlineAt = if (isDeadlineSet) doUntil else null
                        )
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
            importance = importance
        )
    }

    /**
     * Set to-do item data snapshot do until date
     * @param doUntil New do until date. If date is null, LocalDate.now is set
     */
    fun setSnapshotDoUntil(doUntil: LocalDate?) {
        _todoItemState.value = _todoItemState.value.copy(
            doUntil = doUntil ?: LocalDate.now(),
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
            TodoItemUiStateOld(
                text = text,
                id = id,
                isDone = done,
                importance = importance,
                doUntil = deadlineAt ?: LocalDate.now(),
                isDeadlineSet = deadlineAt != null
            )
    }
}