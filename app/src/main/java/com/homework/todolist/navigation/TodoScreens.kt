package com.homework.todolist.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.homework.todolist.navigation.TodoDestinationsArgs.TODO_ID
import com.homework.todolist.navigation.TodoScreens.ABOUT_SCREEN
import com.homework.todolist.navigation.TodoScreens.AUTH_SCREEN
import com.homework.todolist.navigation.TodoScreens.SETTINGS_SCREEN
import com.homework.todolist.navigation.TodoScreens.TODO_LIST_DETAILS
import com.homework.todolist.navigation.TodoScreens.TODO_LIST_SCREEN

/**
 * Screens used in application
 */
private object TodoScreens {
    const val AUTH_SCREEN = "auth"
    const val ABOUT_SCREEN = "about"
    const val SETTINGS_SCREEN = "settings"
    const val TODO_LIST_SCREEN = "todos"
    const val TODO_LIST_DETAILS = "todo-details"
}

/**
 * Arguments used in [TodoScreens] routes
 */
internal object TodoDestinationsArgs {
    const val TODO_ID = "todo_id"
}

/**
 * Destinations used in the application
 */
internal object TodoDestinations {
    const val AUTH_ROUTE = AUTH_SCREEN
    const val ABOUT_ROUTE = ABOUT_SCREEN
    const val SETTINGS_ROUTE = SETTINGS_SCREEN
    const val TODO_LIST_ROUTE = TODO_LIST_SCREEN
    const val DETAILS_ROUTE = "$TODO_LIST_DETAILS?$TODO_ID={$TODO_ID}"
}

/**
 * Models the navigation actions in the app.
 * @param navController Navigation controller
 */
internal class TodoNavigationActions(private val navController: NavHostController) {

    /**
     * Navigates to todo list screen
     */
    internal fun navigateToTodoList(isInclusive: Boolean = false) {
        navController.navigate(TodoDestinations.TODO_LIST_ROUTE) {
            popUpTo(0)
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
                inclusive = isInclusive
            }
            launchSingleTop = true
        }
    }

    /**
     * Navigates to todo details
     * @param id Id of todo, or null if to-do must be created
     */
    internal fun navigateToTodoDetails(id: String? = null) {
        val isItemExist = id != null
        navController.navigate(
            TODO_LIST_DETAILS.let {
                if (isItemExist) "$TODO_LIST_DETAILS?$TODO_ID=$id" else it
            }
        ) {
            popUpTo(navController.graph.findStartDestination().id)
        }
    }

    /**
     * Navigates to app settings screen
     */
    internal fun navigateToSettings() {
        navController.navigate(TodoDestinations.SETTINGS_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id)
        }
    }

    /**
     * Navigates to about screen
     */
    internal fun navigateToAbout() {
        navController.navigate(TodoDestinations.ABOUT_ROUTE)
    }
}