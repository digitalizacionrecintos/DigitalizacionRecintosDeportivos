package org.example.project.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import municipalidadrecintos.composeapp.generated.resources.DM_Sans
import municipalidadrecintos.composeapp.generated.resources.DM_Sans_Italic
import municipalidadrecintos.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font

@Composable
fun getMuniFontFamily(): FontFamily {
    return FontFamily(
        Font(resource = Res.font.DM_Sans_Italic, weight = FontWeight.Normal),
        Font(resource = Res.font.DM_Sans, weight = FontWeight.Bold)
    )
}

@Composable
fun getAppTypography(): Typography {
    val font = getMuniFontFamily()

    return Typography(
        displayLarge = TextStyle(
            fontFamily = font, fontWeight = FontWeight.Bold, fontSize = 32.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = font, fontWeight = FontWeight.Bold, fontSize = 28.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = font, fontWeight = FontWeight.Bold, fontSize = 24.sp
        ),
        titleLarge = TextStyle(
            fontFamily = font, fontWeight = FontWeight.Bold, fontSize = 22.sp
        ),
        titleMedium = TextStyle(
            fontFamily = font, fontWeight = FontWeight.Bold, fontSize = 18.sp
        ),
        titleSmall = TextStyle(
            fontFamily = font, fontWeight = FontWeight.SemiBold, fontSize = 16.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = font, fontWeight = FontWeight.Normal, fontSize = 16.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = font, fontWeight = FontWeight.Normal, fontSize = 14.sp
        ),
        bodySmall = TextStyle(
            fontFamily = font, fontWeight = FontWeight.Normal, fontSize = 12.sp
        ),
        labelLarge = TextStyle(
            fontFamily = font, fontWeight = FontWeight.SemiBold, fontSize = 14.sp
        ),
        labelMedium = TextStyle(
            fontFamily = font, fontWeight = FontWeight.Medium, fontSize = 12.sp
        ),
        labelSmall = TextStyle(
            fontFamily = font, fontWeight = FontWeight.Medium, fontSize = 10.sp
        )
    )
}
