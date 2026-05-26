package com.example.pr1_module5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.pr1_module5.presentation.navigation.AppNavGraph
import com.example.pr1_module5.ui.theme.Pr1_module5Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Pr1_module5Theme {
                AppNavGraph()
            }
        }
    }
}