package com.example.nobellaureatesclient.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nobellaureatesclient.presentation.auth.AuthScreen
import com.example.nobellaureatesclient.presentation.details.PrizeDetailsScreen
import com.example.nobellaureatesclient.presentation.favorites.FavoritesScreen
import com.example.nobellaureatesclient.presentation.list.PrizesListScreen
import com.example.nobellaureatesclient.presentation.profile.ProfileScreen

@Composable
fun NobelNavGraph(
    rootViewModel: RootViewModel = hiltViewModel(),
) {
    val isAuthenticated by rootViewModel.isAuthenticated.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val currentRoute by navController.currentBackStackEntryAsState()

    LaunchedEffect(isAuthenticated) {
        val auth = isAuthenticated ?: return@LaunchedEffect
        val currentDestination = currentRoute?.destination?.route
        if (!auth && currentDestination != NobelDestinations.AUTH) {
            navController.navigate(NobelDestinations.AUTH) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        } else if (auth && currentDestination == NobelDestinations.AUTH) {
            navController.navigate(NobelDestinations.PRIZES_LIST) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    val startDestination = when (isAuthenticated) {
        null -> NobelDestinations.AUTH
        true -> NobelDestinations.PRIZES_LIST
        false -> NobelDestinations.AUTH
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(NobelDestinations.AUTH) {
            AuthScreen(
                onAuthenticated = {
                    navController.navigate(NobelDestinations.PRIZES_LIST) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(NobelDestinations.PRIZES_LIST) {
            PrizesListScreen(
                onPrizeClick = { prize ->
                    navController.navigate(
                        NobelDestinations.prizeDetails(prize.year, prize.category.apiCode),
                    )
                },
                onFavoritesClick = { navController.navigate(NobelDestinations.FAVORITES) },
                onProfileClick = { navController.navigate(NobelDestinations.PROFILE) },
            )
        }

        composable(
            route = NobelDestinations.PRIZE_DETAILS_PATTERN,
            arguments = listOf(
                navArgument(NobelDestinations.ARG_YEAR) { type = NavType.StringType },
                navArgument(NobelDestinations.ARG_CATEGORY) { type = NavType.StringType },
            ),
        ) {
            PrizeDetailsScreen(onBack = { navController.popBackStack() })
        }

        composable(NobelDestinations.FAVORITES) {
            FavoritesScreen(
                onBack = { navController.popBackStack() },
                onFavoriteClick = { favorite ->
                    navController.navigate(
                        NobelDestinations.prizeDetails(favorite.year, favorite.category.apiCode),
                    )
                },
            )
        }

        composable(NobelDestinations.PROFILE) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onFavoritesClick = { navController.navigate(NobelDestinations.FAVORITES) },
                onLoggedOut = {
                    navController.navigate(NobelDestinations.AUTH) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}
