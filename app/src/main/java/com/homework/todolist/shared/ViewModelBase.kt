package com.homework.todolist.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Basic view model class
 */
abstract class ViewModelBase<I : UiEvent, S : UiState, E : UiEffect>(initialState: S) :
    ViewModel() {

    protected val currentState
        get() = state.value

    private val _state: MutableStateFlow<S> = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _event: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val event = _event.asSharedFlow()

    private val _effect: Channel<UiEffect> = Channel()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            event.collect {
                handleEvent(it)
            }
        }
    }

    /**
     * Set new event
     * @param event New event
     */
    fun setEvent(event: I) {
        val newEvent = event
        viewModelScope.launch { _event.emit(newEvent) }
    }


    /**
     * Set new Ui State
     * @param state State reduce callback
     */
    protected fun setState(state: S.() -> S) {
        val newState = currentState.state()
        _state.update {
            newState
        }
    }

    /**
     * Set new Effect
     * @param effect Effect builder
     */
    protected fun setEffect(effect: () -> E) {
        val effectValue = effect()
        viewModelScope.launch { _effect.send(effectValue) }
    }

    /**
     * Main handling function for each event
     */
    abstract fun handleEvent(event: UiEvent)
}