package com.homework.todolist.ui.tododetails.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.homework.todolist.R
import com.homework.todolist.data.model.Importance
import com.homework.todolist.data.repository.TodoItemsRepository
import com.homework.todolist.navigation.TodoDestinationsArgs
import com.homework.todolist.shared.data.result.BadRequestRemoteErrors
import com.homework.todolist.shared.data.result.DeviceError
import com.homework.todolist.shared.data.result.Result
import com.homework.todolist.shared.ui.UiEffect
import com.homework.todolist.shared.ui.UiEvent
import com.homework.todolist.shared.ui.ViewModelBase
import com.homework.todolist.ui.tododetails.data.TodoItemUiState
import com.homework.todolist.ui.tododetails.data.toTodoItemUiState
import com.homework.todolist.ui.tododetails.viewmodel.TodoDetailsViewModel.Companion.DetailsEffects
import com.homework.todolist.ui.tododetails.viewmodel.TodoDetailsViewModel.Companion.DetailsEvent
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
                // Interactor - state
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
            setEffect { DetailsEffects.ShowSaveErrorToast(R.string.todo_details_save_error_text) }
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
            } else {
                setEffect { DetailsEffects.OnItemSaved }
            }
        }
    }

    private fun deleteItem() {
        if (currentState.text.isEmpty() || currentState.id == null) {
            setEffect { DetailsEffects.ShowSaveErrorToast(R.string.todo_item_create_delete_button_text) }
        } else {
            val itemId = currentState.id ?: return
            scope.launch(Dispatchers.IO) {
                todoItemsRepository.removeItemById(itemId)
                setEffect { DetailsEffects.OnItemDeleted }
            }
        }
    }

    private fun handleError(error: Result.Error) {
        setEffect {
            when (error.errorType) {
                DeviceError.DEVICE_ERROR -> { DetailsEffects.ShowSaveErrorToast(R.string.device_error_text) }
                BadRequestRemoteErrors.UNAUTHORIZED -> { DetailsEffects.ShowSaveErrorToast(R.string.authentication_error_text) }
                BadRequestRemoteErrors.MALFORMED_REQUEST -> { DetailsEffects.ShowSaveErrorToast(R.string.malformed_request_error_text) }
                BadRequestRemoteErrors.NOT_FOUND -> { DetailsEffects.ShowSaveErrorToast(R.string.not_found_error_text) }
                BadRequestRemoteErrors.UNSYNCHRONIZED_DATA -> { DetailsEffects.ShowSaveErrorToast(R.string.bad_revision_error_text) }
                else -> { DetailsEffects.ShowSaveErrorToast(R.string.todo_details_unknown_error_text) }
            }
        }
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
            data object OnSaveEvent : DetailsEvent()
            data object OnCloseEvent : DetailsEvent()
            data class OnImportanceChosen(val importance: Importance) : DetailsEvent()
            data class OnDescriptionUpdate(val text: String) : DetailsEvent()
            data class OnDeadlinePicked(val deadlineAt: LocalDate? = null) : DetailsEvent()
            data object OnDeleteEvent : DetailsEvent()
        }

        /**
         * Details screen effects
         */
        sealed class DetailsEffects : UiEffect {
            data class ShowSaveErrorToast(@StringRes val errorErrorResId: Int) : DetailsEffects()
            data object UnknownErrorToast : DetailsEffects()
            data object OnItemSaved : DetailsEffects()
            data object OnFormClosed : DetailsEffects()
            data object OnItemDeleted : DetailsEffects()
        }
    }
}