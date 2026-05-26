package com.example.authapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.authapp.presentation.login.LoginScreen
import com.example.authapp.presentation.root.RootViewModel
import com.example.authapp.presentation.userdetail.UserDetailScreen
import com.example.authapp.presentation.users.UsersListScreen

@Composable
fun AppNavigation(rootViewModel: RootViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val isLoggedIn by rootViewModel.isLoggedIn.collectAsStateWithLifecycle()

    val startDestination = if (isLoggedIn) Routes.USERS else Routes.LOGIN

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.USERS) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.USERS) {
            UsersListScreen(
                onUserClick = { id ->
                    navController.navigate(Routes.userDetail(id))
                },
            )
        }

        composable(
            route = Routes.USER_DETAIL,
            arguments = listOf(navArgument(Routes.USER_ID_ARG) { type = NavType.IntType }),
        ) {
            UserDetailScreen(
                onBack = { navController.popBackStack() },
                onLoggedOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
            )
        }
    }
}
