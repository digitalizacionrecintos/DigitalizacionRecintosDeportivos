package org.example.project.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun HoldToConfirmButton(
        text: String = "Mantener para Inscribir",
        holdingText: String = "Mantén presionado...",
        backgroundColor: Color = Color(0xFF000080),
        progressColor: Color = Color(0xFF004ECC),
        durationMillis: Int = 1500,
        onConfirm: () -> Unit,
        modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    Box(
            modifier =
                    modifier.height(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(backgroundColor)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                        onPress = {

                                            val animationJob =
                                                    scope.launch {
                                                        progress.animateTo(
                                                                targetValue = 1f,
                                                                animationSpec =
                                                                        tween(
                                                                                durationMillis =
                                                                                        durationMillis,
                                                                                easing =
                                                                                        LinearEasing
                                                                        )
                                                        )

                                                        haptic.performHapticFeedback(
                                                                HapticFeedbackType.LongPress
                                                        )
                                                        onConfirm()
                                                    }

                                            tryAwaitRelease()

                                            animationJob.cancel()
                                            if (progress.value < 1f) {
                                                progress.animateTo(0f, tween(200))
                                            }
                                        }
                                )
                            },
            contentAlignment = Alignment.CenterStart
    ) {

        Box(
                modifier =
                        Modifier.fillMaxHeight()
                                .fillMaxWidth(progress.value)
                                .background(progressColor)
        )

        Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
        ) {
            Text(
                    text = if (progress.value > 0.01f) holdingText else text,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
            )
        }
    }
}
