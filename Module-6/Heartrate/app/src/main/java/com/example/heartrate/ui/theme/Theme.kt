package com.example.heartrate.ui.theme
import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFFE53935),       // красный
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFFFCDD2),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFB71C1C),
    errorContainer = androidx.compose.ui.graphics.Color(0xFFFFCDD2),
    onErrorContainer = androidx.compose.ui.graphics.Color(0xFFC62828),
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFFE3F2FD)
)

private val DarkColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFFEF9A9A),
    onPrimary = androidx.compose.ui.graphics.Color(0xFF7F0000),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFB71C1C),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFFFCDD2),
    errorContainer = androidx.compose.ui.graphics.Color(0xFF93000A),
    onErrorContainer = androidx.compose.ui.graphics.Color(0xFFFFDAD6),
)

@Composable
fun HeartRateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,   // Material You на Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}