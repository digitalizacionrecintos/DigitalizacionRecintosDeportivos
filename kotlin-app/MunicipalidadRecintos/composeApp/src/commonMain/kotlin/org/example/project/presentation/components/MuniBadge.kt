package org.example.project.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.presentation.theme.MuniColors
import org.example.project.presentation.theme.MuniSpacing

enum class BadgeStyle {
    Info, Location, Category, Success, Warning, Error, Status
}

@Composable
fun MuniBadge(
    text: String,
    style: BadgeStyle = BadgeStyle.Info,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (style) {
        BadgeStyle.Info -> MuniColors.badgeBlueBg to MuniColors.badgeBlueText
        BadgeStyle.Location -> MuniColors.badgeCyanBg to MuniColors.badgeCyanText
        BadgeStyle.Category -> MuniColors.badgeYellowBg to MuniColors.badgeYellowText
        BadgeStyle.Success -> MuniColors.badgeCyanBg to Color(0xFF006064)
        BadgeStyle.Warning -> MuniColors.badgeOrangeBg to MuniColors.warningOrange
        BadgeStyle.Error -> MuniColors.badgeRedBg to MuniColors.errorRed
        BadgeStyle.Status -> MuniColors.primaryBlue.copy(alpha = 0.1f) to MuniColors.primaryBlue
    }

    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(20.dp))
            .padding(horizontal = MuniSpacing.sm, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
fun MuniStatusDot(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(8.dp)
            .background(
                if (isActive) MuniColors.accentEmerald else MuniColors.lightGray,
                shape = RoundedCornerShape(50)
            )
    )
}
