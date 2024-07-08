package com.homework.todolist.shared.ui


/**
 * Basic UI event action (intent)
 * Using in MVI ViewModel basic
 * Used in [ViewModelBase]
 */
interface UiEvent

/**
 * Basic UI state produces by ViewModel.
 * Contains current state of views
 * Used in [ViewModelBase]
 */
interface UiState

/**
 * Basic UI state effects produced by ViewModel.
 * Contains any side effects like error messages what must be displayed
 * Used in [ViewModelBase]
 */
interface UiEffect