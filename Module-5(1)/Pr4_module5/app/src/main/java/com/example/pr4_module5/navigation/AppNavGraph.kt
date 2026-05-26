package com.example.pr4_module5.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pr4_module5.domain.model.TodoItem
import com.example.pr4_module5.presentation.ui.screen.TodoEditScreen
import com.example.pr4_module5.presentation.ui.screen.TodoListScreen
import com.example.pr4_module5.presentation.viewmodel.TodoViewModel

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val viewModel: TodoViewModel = viewModel()

    var editingItem by remember { mutableStateOf<TodoItem?>(null) }

    NavHost(navController = navController, startDestination = "list") {

        composable("list") {
            TodoListScreen(
                onNewTodo = {
                    editingItem = null
                    navController.navigate("edit")
                },
                onEditTodo = { item ->
                    editingItem = item
                    navController.navigate("edit")
                },
                viewModel = viewModel
            )
        }

        composable("edit") {
            TodoEditScreen(
                item = editingItem,
                onSave = { title, description ->
                    if (editingItem == null) {
                        viewModel.addTodo(title, description)
                    } else {
                        viewModel.updateTodo(editingItem!!.copy(title = title, description = description))
                    }
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}