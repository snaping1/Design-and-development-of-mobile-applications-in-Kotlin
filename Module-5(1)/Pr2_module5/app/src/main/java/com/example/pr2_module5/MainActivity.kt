package com.example.pr2_module5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.pr2_module5.presentation.navigation.GalleryNavGraph
import com.example.pr2_module5.ui.theme.Pr2_module5Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Pr2_module5Theme {
                GalleryNavGraph()
            }
        }
    }
}