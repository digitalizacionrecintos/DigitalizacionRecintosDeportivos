package org.example.project.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val LightColorScheme =
        lightColorScheme(
                primary = Color(0xFF043CC7),
                secondary = Color(0xFF3DBAD7),
                background = Color(0xFFF5F5F5),
                surface = Color.White,
                onPrimary = Color.White,
                onSecondary = Color.White,
                onBackground = Color.Black,
                onSurface = Color.Black
        )

val DarkColorScheme =
        darkColorScheme(
                primary = Color(0xFF3A83DF),
                secondary = Color(0xFF3DBAD7),
                background = Color(0xFF121212),
                surface = Color(0xFF1E1E1E),
                onPrimary = Color.White,
                onSecondary = Color.Black,
                onBackground = Color.White,
                onSurface = Color.White
        )
