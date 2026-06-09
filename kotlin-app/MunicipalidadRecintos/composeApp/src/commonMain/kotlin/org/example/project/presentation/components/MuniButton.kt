package org.example.project.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.presentation.theme.MuniColors
import org.example.project.presentation.theme.MuniShapes
import org.example.project.presentation.theme.MuniSpacing

enum class ButtonStyle {
    Primary, Secondary, Danger, Ghost
}

@Composable
fun MuniButton(
    text: String,
    onClick: () -> Unit,
    style: ButtonStyle = ButtonStyle.Primary,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val containerColor = when (style) {
        ButtonStyle.Primary -> MuniColors.primaryBlue
        ButtonStyle.Secondary -> MuniColors.accentEmerald
        ButtonStyle.Danger -> MuniColors.errorRed
        ButtonStyle.Ghost -> Color.Transparent
    }
    val contentColor = when (style) {
        ButtonStyle.Ghost -> MuniColors.primaryBlue
        else -> Color.White
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = MuniShapes.button,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.4f),
            disabledContentColor = contentColor.copy(alpha = 0.6f)
        ),
        modifier = modifier.height(50.dp),
        contentPadding = PaddingValues(horizontal = MuniSpacing.xl)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
        } else {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(MuniSpacing.sm))
            }
            Text(text = text, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun MuniGradientButton(
    text: String,
    onClick: () -> Unit,
    brush: Brush,
    enabled: Boolean = true,
    loading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = MuniShapes.button,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
        ),
        modifier = modifier
            .height(50.dp)
            .background(brush, MuniShapes.button),
        contentPadding = PaddingValues(horizontal = MuniSpacing.xl)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(text = text, color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun MuniTextButton(
    text: String,
    onClick: () -> Unit,
    color: Color = MuniColors.primaryBlue,
    modifier: Modifier = Modifier
) {
    TextButton(onClick = onClick, modifier = modifier) {
        Text(text = text, color = color, fontWeight = FontWeight.Medium)
    }
}
