package com.homework.todolist.tododetails.viewmodel

import com.homework.todolist.data.model.Importance
import com.homework.todolist.shared.UiEffect
import com.homework.todolist.shared.UiEvent
import com.homework.todolist.shared.UiState
import java.time.LocalDate

internal class DetailsUiParameters {

    // Events that user performed
    sealed class Event : UiEvent {
        // Save user updates
        object OnSaveClicked : Event()

        // Delete user item
        object OnDeleteClicked : Event()

        // Update user entered text
        data class OnTextEntered(val text: String) : Event()

        // Update user importance
        data class OnImportanceSelected(val importance: Importance = Importance.ORDINARY) : Event()

        // Set item deadline
        data class OnDeadlineSelected(val deadlineAt: LocalDate? = null) : Event()
    }

    /**
     * Details screen ui state
     * @param text Item text
     * @param id Item identifier (null if item is new)
     * @param importance Item importance
     * @param deadlineAt Task deadline (null if is not set)
     * @param isCalendarOpen Is details date picker open
     */
    data class State(
        val text: String,
        val id: String? = null,
        val importance: Importance = Importance.ORDINARY,
        val deadlineAt: LocalDate? = LocalDate.now(),
        val isCalendarOpen: Boolean = false,
        ) : UiState {

        /**
         * If true item can be created, otherwise updated
         */
        val isNew: Boolean
                get() = id == null

        /**
         * If true item deadline was set, otherwise not
         */
        val deadlineWasSet: Boolean
                get() = deadlineAt == null
        }

    // Details effects
    sealed class Effect : UiEffect
}