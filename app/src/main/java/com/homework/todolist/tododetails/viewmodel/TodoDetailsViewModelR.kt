package com.homework.todolist.tododetails.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.homework.todolist.data.model.Importance
import com.homework.todolist.data.repository.TodoItemsRepository
import com.homework.todolist.shared.UiEffect
import com.homework.todolist.shared.UiEvent
import com.homework.todolist.shared.UiState
import com.homework.todolist.shared.ViewModelBase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import com.homework.todolist.tododetails.viewmodel.TodoDetailsViewModelR.Companion.DetailsEvent
import com.homework.todolist.tododetails.viewmodel.TodoDetailsViewModelR.Companion.TodoItemUiState
import com.homework.todolist.tododetails.viewmodel.TodoDetailsViewModelR.Companion.DetailsEffects

/**
 * Details view-model
 * @param todoItemsRepository Todo items repository
 * @param savedStateHandle Passed state to viewmodel
 */
@HiltViewModel
class TodoDetailsViewModelR @Inject constructor(
    private val todoItemsRepository: TodoItemsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModelBase<DetailsEvent, TodoItemUiState, DetailsEffects>(TodoItemUiState()) {

    companion object {

        /**
         * Details screen main view state
         */
        data class TodoItemUiState (
            val text: String = "",
            val importance: Importance = Importance.ORDINARY,
            val isDone: Boolean = false,
            val id: String? = null,
            val doUntil: LocalDate = LocalDate.now(),
            val isDeadlineSet: Boolean = false
        ) : UiState

        /**
         * Details screen events
         */
        sealed class DetailsEvent : UiEvent {
            object Save : DetailsEvent()
            object Close : DetailsEvent()
            data class ChooseImportance(val importance: Importance) : DetailsEvent()
            data class SetDescription(val text: String) : DetailsEvent()
            data class PickDeadlineDate(val deadlineAt: LocalDate? = null) : DetailsEvent()
            object DeleteItem : DetailsEvent()
        }

        /**
         * Details screen effects
         */
        sealed class DetailsEffects : UiEffect {
            object SaveWithoutDescription : DetailsEffects()
        }

    }

    override fun handleEvent(event: UiEvent) {
        TODO("Not yet implemented")
    }
}