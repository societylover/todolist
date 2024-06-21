package com.homework.todolist.routing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.homework.todolist.TodoListDetailsScreen
import com.homework.todolist.TodoListScreen
import com.homework.todolist.routing.TodoDestinationsArgs.TODO_ID

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