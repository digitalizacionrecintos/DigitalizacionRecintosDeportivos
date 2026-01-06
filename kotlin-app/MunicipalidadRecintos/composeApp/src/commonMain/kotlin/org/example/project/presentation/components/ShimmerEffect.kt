package org.example.project.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerEventCard() {
        val shimmerColors = listOf(Color(0xFFE0E0E0), Color(0xFFF5F5F5), Color(0xFFE0E0E0))

        val transition = rememberInfiniteTransition(label = "shimmer")
        val translateAnim by
                transition.animateFloat(
                        initialValue = 0f,
                        targetValue = 1000f,
                        animationSpec =
                                infiniteRepeatable(
                                        animation =
                                                tween(
                                                        durationMillis = 1200,
                                                        easing = FastOutSlowInEasing
                                                ),
                                        repeatMode = RepeatMode.Restart
                                ),
                        label = "shimmer_translate"
                )

        val brush =
                Brush.linearGradient(
                        colors = shimmerColors,
                        start = Offset(translateAnim - 1000f, translateAnim - 1000f),
                        end = Offset(translateAnim, translateAnim)
                )

        Card(
                modifier = Modifier.fillMaxWidth().height(270.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
                Column {

                        Box(modifier = Modifier.height(150.dp).fillMaxWidth().background(brush))

                        Column(modifier = Modifier.padding(12.dp)) {

                                Box(
                                        modifier =
                                                Modifier.fillMaxWidth(0.7f)
                                                        .height(24.dp)
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(brush)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Box(
                                        modifier =
                                                Modifier.fillMaxWidth(0.5f)
                                                        .height(20.dp)
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(brush)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Box(
                                        modifier =
                                                Modifier.fillMaxWidth(0.4f)
                                                        .height(20.dp)
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(brush)
                                )
                        }
                }
        }
}
