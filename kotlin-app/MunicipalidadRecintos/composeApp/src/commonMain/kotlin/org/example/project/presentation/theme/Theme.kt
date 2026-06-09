package org.example.project.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LightColorScheme =
    lightColorScheme(
        primary = MuniColors.primaryBlue,
        onPrimary = Color.White,
        secondary = MuniColors.secondaryCyan,
        onSecondary = Color.White,
        tertiary = MuniColors.accentEmerald,
        onTertiary = Color.White,
        error = MuniColors.errorRed,
        onError = Color.White,
        background = MuniColors.offWhite,
        onBackground = MuniColors.darkGray,
        surface = MuniColors.surfaceCard,
        onSurface = MuniColors.darkGray,
        surfaceVariant = MuniColors.ultraLightGray,
        onSurfaceVariant = MuniColors.mediumGray,
        outline = MuniColors.dividerColor
    )

val DarkColorScheme =
    darkColorScheme(
        primary = Color(0xFF3A83DF),
        onPrimary = Color.White,
        secondary = MuniColors.secondaryCyan,
        onSecondary = Color.Black,
        tertiary = MuniColors.accentEmerald,
        onTertiary = Color.White,
        error = MuniColors.errorRed,
        onError = Color.White,
        background = Color(0xFF121212),
        onBackground = Color.White,
        surface = Color(0xFF1E1E1E),
        onSurface = Color.White,
        surfaceVariant = Color(0xFF2C2C2C),
        onSurfaceVariant = Color(0xFFBDBDBD),
        outline = Color(0xFF424242)
    )
