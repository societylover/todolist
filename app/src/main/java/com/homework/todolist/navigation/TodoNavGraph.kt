package com.homework.todolist.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.homework.todolist.navigation.TodoDestinationsArgs.TODO_ID
import com.homework.todolist.navigation.divkit.DivKitInterop
import com.homework.todolist.ui.screen.about.AboutScreen
import com.homework.todolist.ui.screen.settings.SettingsScreen
import com.homework.todolist.ui.screen.startdetails.StartScreen
import com.homework.todolist.ui.screen.tododetails.TodoListDetailsScreen
import com.homework.todolist.ui.screen.todos.TodoListScreen
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
    },
    divKitInterop: DivKitInterop
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(TodoDestinations.AUTH_ROUTE)
        { _ ->
            StartScreen(
                onSuccessAuthed = { navActions.navigateToTodoList(true) },
                onFailureAuthed = { exitProcess(EXIT_FROM_APPLICATION_CODE) })
        }

        composable(TodoDestinations.TODO_LIST_ROUTE)
        { _ ->
            TodoListScreen(
                onItemClick = { navActions.navigateToTodoDetails(it) },
                onCreateItemClick = { navActions.navigateToTodoDetails() },
                onSettingsClicked = { navActions.navigateToSettings() }
            )
        }

        composable(TodoDestinations.SETTINGS_ROUTE)
        { _ ->
            SettingsScreen(
                onActionClick = { navActions.navigateToTodoList(false) },
                onAboutAppClick = { navActions.navigateToAbout() }
            )
        }

        composable(TodoDestinations.ABOUT_ROUTE)
        { _ ->
            AboutScreen(
                contextThemeWrapper = divKitInterop.context,
                isDark = divKitInterop.isDark,
                onBackPressed = { navActions.navigateToSettings() }
            )
        }

        /* Navigation to details screen
         * Open create form if null as item id passed
         */
        composable(
            TodoDestinations.DETAILS_ROUTE,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn()
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut()
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut()
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn()
            },
            arguments = listOf(
                navArgument(TODO_ID) {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                }
            )
        ) { _ ->
            TodoListDetailsScreen(onActionClick = { navActions.navigateToTodoList(false) })
        }
    }
}

private const val EXIT_FROM_APPLICATION_CODE = 1