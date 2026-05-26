package com.example.pr4_module5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.pr4_module5.navigation.AppNavGraph
import com.example.pr4_module5.ui.theme.Pr4_module5Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Pr4_module5Theme {
                AppNavGraph()
            }
        }
    }
}