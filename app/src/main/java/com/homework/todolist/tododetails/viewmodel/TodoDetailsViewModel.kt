package com.homework.todolist.tododetails.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.homework.todolist.data.model.Importance
import com.homework.todolist.data.repository.TodoItemsRepository
import com.homework.todolist.navigation.TodoDestinationsArgs
import com.homework.todolist.shared.data.result.Result
import com.homework.todolist.shared.ui.UiEffect
import com.homework.todolist.shared.ui.UiEvent
import com.homework.todolist.shared.ui.ViewModelBase
import com.homework.todolist.tododetails.data.TodoItemUiState
import com.homework.todolist.tododetails.data.toTodoItemUiState
import com.homework.todolist.tododetails.viewmodel.TodoDetailsViewModel.Companion.DetailsEffects
import com.homework.todolist.tododetails.viewmodel.TodoDetailsViewModel.Companion.DetailsEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.time.LocalDate
import javax.inject.Inject

/**
 * Details view-model
 * @param todoItemsRepository Todo items repository
 * @param savedStateHandle Passed state to viewmodel
 */
@HiltViewModel
class TodoDetailsViewModel @Inject constructor(
    private val todoItemsRepository: TodoItemsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModelBase<DetailsEvent, TodoItemUiState, DetailsEffects>(initialState = TodoItemUiState()) {

    private val scope = viewModelScope +
            CoroutineExceptionHandler { _, _ -> setEffect { DetailsEffects.UnknownErrorToast } }

    init {
        savedStateHandle.get<String>(TodoDestinationsArgs.TODO_ID)?.also { id ->
            scope.launch {
                todoItemsRepository.getItemDetails(id).also { item ->
                    if (item is Result.Error) {
                        handleError(item)
                        return@launch
                    }

                    if (item is Result.Success && item.data != null) {
                        setState { item.data.toTodoItemUiState() }
                    }
                }
            }
        }
    }

    override fun handleEvent(event: UiEvent) {
        when (event) {
            is DetailsEvent.OnSaveEvent -> {
                saveItem()
            }

            is DetailsEvent.OnCloseEvent -> {
                closeForm()
            }

            is DetailsEvent.OnImportanceChosen -> {
                chooseImportance(event.importance)
            }

            is DetailsEvent.OnDescriptionUpdate -> {
                setDescription(event.text)
            }

            is DetailsEvent.OnDeadlinePicked -> {
                setDeadlineDate(event.deadlineAt)
            }

            is DetailsEvent.OnDeleteEvent -> {
                deleteItem()
            }
        }
    }

    private fun closeForm() {
        setEffect { DetailsEffects.OnFormClosed }
    }

    private fun saveItem() {
        val itemUiState = state.value
        if (itemUiState.text.isEmpty()) {
            setEffect { DetailsEffects.ShowSaveErrorToast }
            return
        }

        scope.launch(Dispatchers.IO) {
            val result = if (itemUiState.id != null) {
                todoItemsRepository.updateItem(
                    id = itemUiState.id,
                    text = itemUiState.text,
                    done = itemUiState.isDone,
                    importance = itemUiState.importance,
                    deadlineAt = if (itemUiState.isDeadlineSet) itemUiState.doUntil else null
                )
            } else {
                todoItemsRepository.createItem(
                    text = itemUiState.text,
                    importance = itemUiState.importance,
                    deadlineAt = if (itemUiState.isDeadlineSet) itemUiState.doUntil else null
                )
            }

            if (result is Result.Error) {
                handleError(result)
                return@launch
            }

            if (result is Result.Success) {
                setEffect { DetailsEffects.OnItemSaved }
            } else {
                handleUnknownActionError(DetailsEvent.OnSaveEvent)
            }
        }
    }

    private fun deleteItem() {
        if (currentState.text.isEmpty() || currentState.id == null) {
            setEffect { DetailsEffects.ShowSaveErrorToast }
        } else {
            val itemId = currentState.id ?: return
            scope.launch(Dispatchers.IO) {
                todoItemsRepository.removeItemById(itemId)
                setEffect { DetailsEffects.OnItemDeleted }
            }
        }
    }

    private suspend fun handleError(error: Result.Error) {

    }

    private suspend fun handleUnknownActionError(event: DetailsEvent) {

    }

    private fun setDeadlineDate(selectedDate: LocalDate?) {
        if (selectedDate == null) {
            setState { copy(doUntil = LocalDate.now(), isDeadlineSet = false) }
            return
        }

        setState { copy(doUntil = selectedDate, isDeadlineSet = true) }
    }


    private fun setDescription(description: String) {
        setState { copy(text = description) }
    }

    private fun chooseImportance(importance: Importance) {
        setState { copy(importance = importance) }
    }

    companion object {
        /**
         * Details screen events
         */
        sealed class DetailsEvent : UiEvent {
            object OnSaveEvent : DetailsEvent()
            object OnCloseEvent : DetailsEvent()
            data class OnImportanceChosen(val importance: Importance) : DetailsEvent()
            data class OnDescriptionUpdate(val text: String) : DetailsEvent()
            data class OnDeadlinePicked(val deadlineAt: LocalDate? = null) : DetailsEvent()
            object OnDeleteEvent : DetailsEvent()
        }

        /**
         * Details screen effects
         */
        sealed class DetailsEffects : UiEffect {
            object ShowSaveErrorToast : DetailsEffects()
            object UnknownErrorToast : DetailsEffects()
            object OnItemSaved : DetailsEffects()
            object OnFormClosed : DetailsEffects()
            object OnItemDeleted : DetailsEffects()
        }
    }
}