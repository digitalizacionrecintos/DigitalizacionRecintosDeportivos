package org.example.project.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

@Composable
fun SpotlightCoachMark(
        isVisible: Boolean,
        targetRect: Rect?,
        text: String,
        onDismiss: () -> Unit
) {
        if (!isVisible || targetRect == null) return

        Box(
                modifier =
                        Modifier.fillMaxSize()

                                .zIndex(10f)

                                .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = onDismiss
                                )
        ) {
                Box(
                        modifier =
                                Modifier.fillMaxSize()

                                        .clickable(
                                                interactionSource =
                                                        remember { MutableInteractionSource() },
                                                indication = null,
                                                onClick = onDismiss
                                        )
                ) {

                        Canvas(modifier = Modifier.fillMaxSize().graphicsLayer { alpha = 0.99f }) {

                                drawRect(Color.Black.copy(alpha = 0.7f))

                                drawRoundRect(
                                        color = Color.Transparent,
                                        topLeft = Offset(targetRect.left, targetRect.top),
                                        size =
                                                androidx.compose.ui.geometry.Size(
                                                        targetRect.width,
                                                        targetRect.height
                                                ),
                                        cornerRadius =
                                                CornerRadius(
                                                        12.dp.toPx(),
                                                        12.dp.toPx()
                                                ),
                                        blendMode = BlendMode.Clear
                                )
                        }

                        TooltipBubble(text = text, targetRect = targetRect, onDismiss = onDismiss)
                }
        }
}

@Composable
private fun TooltipBubble(text: String, targetRect: Rect, onDismiss: () -> Unit) {
        val density = LocalDensity.current

        val tooltipYOffset = with(density) { (targetRect.top - 160.dp.toPx()).roundToInt() }

        Layout(
                content = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {

                                Surface(
                                        color = Color(0xFF043CC7),
                                        shape = RoundedCornerShape(12.dp),
                                        shadowElevation = 8.dp,
                                        modifier =
                                                Modifier.widthIn(max = 280.dp).clickable {
                                                        onDismiss()
                                                }
                                ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                                Row(
                                                        verticalAlignment =
                                                                Alignment.CenterVertically
                                                ) {
                                                        Text(
                                                                " Información",
                                                                color = Color(0xFF4BAAEA),
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 12.sp
                                                        )
                                                        Spacer(Modifier.weight(1f))
                                                        Icon(
                                                                Icons.Default.Close,
                                                                "Cerrar",
                                                                tint = Color.White,
                                                                modifier = Modifier.size(16.dp)
                                                        )
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                        text,
                                                        color = Color.White,
                                                        fontSize = 14.sp,
                                                        lineHeight = 18.sp
                                                )
                                        }
                                }

                                Box(
                                        modifier =
                                                Modifier.size(16.dp)
                                                        .offset(y = (-1).dp)
                                                        .background(
                                                                Color(0xFF043CC7),
                                                                shape = TriangleShape
                                                        )
                                )
                        }
                }
        ) { measurables, constraints ->
                val placeable = measurables.first().measure(constraints)

                val tooltipX = (targetRect.center.x - placeable.width / 2).roundToInt()

                layout(constraints.maxWidth, constraints.maxHeight) {

                        placeable.placeRelative(x = tooltipX, y = tooltipYOffset)
                }
        }
}

private val TriangleShape = GenericShape { size, _ ->
        moveTo(0f, 0f)
        lineTo(size.width, 0f)
        lineTo(size.width / 2, size.height)
        close()
}
