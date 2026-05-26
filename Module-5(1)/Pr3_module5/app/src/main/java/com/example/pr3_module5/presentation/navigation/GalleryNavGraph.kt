package com.example.pr3_module5.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pr3_module5.presentation.gallery.GalleryScreen

@Composable
fun GalleryNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "gallery") {
        composable("gallery") {
            GalleryScreen()
        }
    }
}