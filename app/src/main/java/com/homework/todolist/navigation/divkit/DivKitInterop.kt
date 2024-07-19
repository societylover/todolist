package com.homework.todolist.navigation.divkit

import android.view.ContextThemeWrapper
import javax.inject.Qualifier

@Retention(AnnotationRetention.SOURCE)
@Qualifier
annotation class DivKitInteroperability

/**
 * Parameters for executing DivKit UI screen
 */
@DivKitInteroperability
data class DivKitInterop(
    val context: ContextThemeWrapper
)