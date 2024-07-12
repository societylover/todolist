package com.homework.todolist.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.homework.todolist.navigation.TodoDestinationsArgs.TODO_ID
import com.homework.todolist.ui.startdetails.StartScreen
import com.homework.todolist.ui.tododetails.TodoListDetailsScreen
import com.homework.todolist.ui.todos.TodoListScreen
import kotlin.system.exitProcess

/**
 * Application navigation handler
 * @param navController Application navigation controller
 * @param startDestination Start destination of application
 * @param navActions Available navigation actions
 */
@Composable
internal fun TodoNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = TodoDestinations.AUTH_ROUTE,
    navActions: TodoNavigationActions = remember(navController) {
        TodoNavigationActions(
            navController
        )
    }
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(TodoDestinations.AUTH_ROUTE)
        { _ ->
            StartScreen(
                onSuccessAuthed = { navActions.navigateToTodoList(true) },
                onFailureAuthed = { exitProcess(1) })
        }

        composable(TodoDestinations.TODO_LIST_ROUTE)
        { _ ->
            TodoListScreen(
                onItemClick = { navActions.navigateToTodoDetails(it) },
                onCreateItemClick = { navActions.navigateToTodoDetails() }
            )
        }

        /* Navigation to details screen
         * Open create form if null as item id passed
         */
        composable(
            TodoDestinations.DETAILS_ROUTE,
            arguments = listOf(
                navArgument(TODO_ID) {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                }
            )
        ) { _ ->
            TodoListDetailsScreen(onActionClick = { navActions.navigateToTodoList() })
        }
    }
}