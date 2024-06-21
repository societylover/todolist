package com.homework.todolist.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.homework.todolist.tododetails.TodoListDetailsScreen
import com.homework.todolist.todos.TodoListScreen
import com.homework.todolist.navigation.TodoDestinationsArgs.TODO_ID

@Composable
fun TodoNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = TodoDestinations.TODO_LIST_ROUTE,
    navActions: TodoNavigationActions = remember(navController) { TodoNavigationActions(navController) })
{
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(TodoDestinations.TODO_LIST_ROUTE)
        { _ ->
            TodoListScreen(
                onItemClick = { navActions.navigateToTodoDetails(it) },
                onCreateItemClick = { navActions.navigateToTodoDetails() }
            )
        }
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