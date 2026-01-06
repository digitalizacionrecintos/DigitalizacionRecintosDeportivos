package org.example.project.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun EventTag(
        text: String,
        icon: ImageVector,
        backgroundColor: Color,
        textColor: Color = Color.White,
        delayMillis: Long = 0
) {
        var visible by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(delayMillis)
                visible = true
        }

        AnimatedVisibility(
                visible = visible,
                enter =
                        fadeIn(animationSpec = tween(200)) +
                                scaleIn(initialScale = 0.9f, animationSpec = tween(200))
        ) {
                Row(
                        modifier =
                                Modifier.clip(RoundedCornerShape(50))
                                        .background(backgroundColor)
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = textColor,
                                modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                                text = text,
                                style = MaterialTheme.typography.bodySmall,
                                color = textColor
                        )
                }
        }
}
