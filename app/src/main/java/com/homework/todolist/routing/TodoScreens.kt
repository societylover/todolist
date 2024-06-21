package com.homework.todolist.routing

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.homework.todolist.TodoItemId
import com.homework.todolist.routing.TodoDestinationsArgs.TODO_ID
import com.homework.todolist.routing.TodoScreens.TODO_LIST_DETAILS
import com.homework.todolist.routing.TodoScreens.TODO_LIST_SCREEN

/**
 * Screens used in application
 */
private object TodoScreens {
    const val TODO_LIST_SCREEN = "todos"
    const val TODO_LIST_DETAILS = "todo-details"
}

/**
 * Arguments used in [TodoScreens] routes
 */
object TodoDestinationsArgs {
    const val TODO_ID = "todo_id"
}

/**
 * Destinations used in the application
 */
object TodoDestinations {
    const val TODO_LIST_ROUTE = TODO_LIST_SCREEN
    const val DETAILS_ROUTE = "$TODO_LIST_DETAILS?$TODO_ID={$TODO_ID}"
}

/**
 * Models the navigation actions in the app.
 */
class TodoNavigationActions(private val navController: NavHostController) {

    /**
     * Navigate to todo list screen
     */
    fun navigateToTodoList() {
        navController.navigate(TodoDestinations.TODO_LIST_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
        }
    }

    fun navigateToTodoDetails(id: TodoItemId? = null) {
        val isItemExist = id != null
        navController.navigate(
            TODO_LIST_DETAILS.let {
                if (isItemExist) "$TODO_LIST_DETAILS?$TODO_ID=$id" else it
            }
        ) {
            popUpTo(navController.graph.findStartDestination().id)
        }
    }
}