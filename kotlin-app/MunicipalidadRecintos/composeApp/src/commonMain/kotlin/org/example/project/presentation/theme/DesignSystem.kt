package org.example.project.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



object MuniColors {
    val primaryBlue = Color(0xFF043CC7)
    val secondaryCyan = Color(0xFF3DBAD7)
    val accentEmerald = Color(0xFF02BB94)
    val errorRed = Color(0xFFD32F2F)
    val warningOrange = Color(0xFFFF9800)
    val successGreen = Color(0xFF4CAF50)
    val pendingYellow = Color(0xFFFFCB4A)

    val gradientNavy = Color(0xFF001F5C)
    val gradientDarkNavy = Color(0xFF023075)
    val gradientBlue = Color(0xFF0D47A1)

    val darkGray = Color(0xFF1A1A1A)
    val mediumGray = Color(0xFF6B7280)
    val lightGray = Color(0xFF9CA3AF)
    val offWhite = Color(0xFFF5F5F5)
    val ultraLightGray = Color(0xFFF3F4F6)

    val badgeBlueBg = Color(0xFFE3F2FD)
    val badgeBlueText = Color(0xFF1976D2)
    val badgeCyanBg = Color(0xFFE0F7FA)
    val badgeCyanText = Color(0xFF006064)
    val badgeYellowBg = Color(0xFFFFECB3)
    val badgeYellowText = Color(0xFF85690F)
    val badgeOrangeBg = Color(0xFFFFF3E0)
    val badgeRedBg = Color(0xFFFFEBEE)

    val surfaceCard = Color.White
    val surfaceOverlay = Color(0xFFF8F9FA)
    val dividerColor = Color(0xFFE0E0E0)
}



object MuniSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val xxl = 32.dp
    val xxxl = 48.dp
}



object MuniShapes {
    val card = RoundedCornerShape(MuniSpacing.md)
    val cardLarge = RoundedCornerShape(MuniSpacing.lg)
    val button = RoundedCornerShape(MuniSpacing.md)
    val buttonSmall = RoundedCornerShape(MuniSpacing.sm)
    val chip = RoundedCornerShape(20.dp)
    val pill = RoundedCornerShape(50)
    val textField = RoundedCornerShape(MuniSpacing.md)
    val topBar = RoundedCornerShape(bottomStart = MuniSpacing.lg, bottomEnd = MuniSpacing.lg)
    val sheet = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val dialog = RoundedCornerShape(MuniSpacing.xl)
    val avatar = RoundedCornerShape(50)

    @Composable
    fun material(): Shapes = Shapes(
        small = card,
        medium = cardLarge,
        large = RoundedCornerShape(MuniSpacing.xl)
    )
}



object MuniElevation {
    val flat = 0.dp
    val subtle = 2.dp
    val raised = 4.dp
    val elevated = 8.dp
}



object MuniGradients {
    val header: Brush
        get() = Brush.horizontalGradient(
            colors = listOf(
                MuniColors.gradientNavy,
                MuniColors.gradientDarkNavy,
                MuniColors.gradientBlue
            )
        )

    val primaryVertical: Brush
        get() = Brush.verticalGradient(
            colors = listOf(MuniColors.primaryBlue, MuniColors.secondaryCyan)
        )

    val contentBackground: Brush
        get() = Brush.verticalGradient(
            0.0f to MuniColors.primaryBlue,
            0.25f to Color(0xFF3A83DF),
            0.55f to Color(0xFF4BAAEA),
            0.92f to MuniColors.secondaryCyan
        )

    val profileHeader: Brush
        get() = Brush.verticalGradient(
            colors = listOf(
                MuniColors.primaryBlue,
                Color(0xFF0652DD),
                Color(0xFF00D2FF)
            )
        )

    val registerButton: Brush
        get() = Brush.horizontalGradient(
            colors = listOf(MuniColors.primaryBlue, MuniColors.secondaryCyan)
        )

    val cardAccent: Brush
        get() = Brush.verticalGradient(
            colors = listOf(MuniColors.accentEmerald, MuniColors.accentEmerald.copy(alpha = 0.7f))
        )

    val imageOverlay: Brush
        get() = Brush.verticalGradient(
            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f))
        )
}



object MuniTextSize {
    val display = 28.sp
    val heading = 24.sp
    val subheading = 22.sp
    val title = 18.sp
    val body = 16.sp
    val bodySmall = 14.sp
    val caption = 12.sp
    val micro = 10.sp
}



object MuniDims {
    val topBarHeight = 56.dp
    val topBarLogoSize = 48.dp
    val avatarSize = 120.dp
    val iconCircle = 48.dp
    val iconSmall = 24.dp
    val eventCardImageHeight = 160.dp
    val managerCardImageHeight = 180.dp
    val buttonHeight = 50.dp
    val shimmerCardHeight = 270.dp
}
