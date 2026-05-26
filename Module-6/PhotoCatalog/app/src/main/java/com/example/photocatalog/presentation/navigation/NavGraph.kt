package com.example.photocatalog.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.photocatalog.presentation.screen.PhotoDetailScreen
import com.example.photocatalog.presentation.screen.PhotoListScreen

object NavArgs {
    const val PHOTO_ID = "photoId"
}

object Routes {
    const val LIST = "photos"
    const val DETAIL = "photo/{${NavArgs.PHOTO_ID}}"
    fun detail(photoId: String) = "photo/$photoId"
}

@Composable
fun PhotoCatalogNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.LIST) {
        composable(Routes.LIST) {
            PhotoListScreen(
                onPhotoClick = { photoId ->
                    navController.navigate(Routes.detail(photoId))
                }
            )
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument(NavArgs.PHOTO_ID) { type = NavType.StringType })
        ) {
            PhotoDetailScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
