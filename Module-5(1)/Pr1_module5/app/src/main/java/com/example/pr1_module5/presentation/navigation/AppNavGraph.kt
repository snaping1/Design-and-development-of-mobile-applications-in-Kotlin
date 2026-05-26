package com.example.pr1_module5.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pr1_module5.presentation.edit.DiaryEditScreen
import com.example.pr1_module5.presentation.list.DiaryListScreen
import com.example.pr1_module5.presentation.list.DiaryListViewModel

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val listViewModel: DiaryListViewModel = viewModel()

    NavHost(navController = navController, startDestination = "list") {

        composable("list") {
            DiaryListScreen(
                onNewEntry = { navController.navigate("edit") },
                onOpenEntry = { fileName ->
                    navController.navigate("edit?fileName=$fileName")
                },
                viewModel = listViewModel
            )
        }

        composable(
            route = "edit?fileName={fileName}",
            arguments = listOf(
                navArgument("fileName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val fileName = backStackEntry.arguments?.getString("fileName")
            DiaryEditScreen(
                fileName = fileName,
                onBack = { navController.popBackStack() },
                onSaved = { entry ->
                    if (fileName == null) {
                        listViewModel.addEntry(entry)
                    } else {
                        listViewModel.updateEntry(entry)
                    }
                    navController.popBackStack()
                }
            )
        }
    }
}