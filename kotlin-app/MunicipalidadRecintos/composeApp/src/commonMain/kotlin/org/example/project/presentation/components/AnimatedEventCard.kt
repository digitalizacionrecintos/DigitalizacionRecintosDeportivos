package org.example.project.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedEventCard(
        index: Int,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        content: @Composable ColumnScope.() -> Unit
) {

        var visible by remember { mutableStateOf(false) }

        var isPressed by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
                kotlinx.coroutines.delay((index * 15L).coerceAtMost(60L))
                visible = true
        }

        val scale by
                animateFloatAsState(
                        targetValue = if (isPressed) 0.95f else 1f,
                        animationSpec =
                                spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                ),
                        label = "card_scale"
                )

        val elevation by
                animateDpAsState(
                        targetValue = if (isPressed) 2.dp else 4.dp,
                        animationSpec =
                                spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                ),
                        label = "card_elevation"
                )

        AnimatedVisibility(
                visible = visible,
                enter =
                        fadeIn(
                                animationSpec =
                                        tween(durationMillis = 150, easing = LinearOutSlowInEasing)
                        ),
                exit = fadeOut(animationSpec = tween(durationMillis = 100))
        ) {
                Card(
                        modifier =
                                modifier.fillMaxWidth().scale(scale).pointerInput(Unit) {
                                        detectTapGestures(
                                                onPress = {
                                                        isPressed = true
                                                        tryAwaitRelease()
                                                        isPressed = false
                                                },
                                                onTap = { onClick() }
                                        )
                                },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
                ) { Column(content = content) }
        }
}
