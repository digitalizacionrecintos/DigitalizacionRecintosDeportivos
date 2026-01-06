package org.example.project.presentation.features.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import municipalidadrecintos.composeapp.generated.resources.Res
import municipalidadrecintos.composeapp.generated.resources.logo_muni_arica
import org.example.project.domain.manager.ThemeManager
import org.example.project.presentation.features.config.ServerConfigScreen
import org.jetbrains.compose.resources.painterResource

class SettingsScreen : Screen {
        @Composable
        override fun Content() {
                val navigator = LocalNavigator.currentOrThrow
                var notificationsEnabled by remember { mutableStateOf(true) }

                val isDarkMode by ThemeManager.isDarkMode.collectAsState()
                val fontScale by ThemeManager.fontScale.collectAsState()

                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .background(MaterialTheme.colorScheme.background)
                ) {

                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .background(
                                                        Brush.horizontalGradient(
                                                                colors =
                                                                        listOf(
                                                                                Color(
                                                                                        0xFF001F5C
                                                                                ),

                                                                                Color(
                                                                                        0xFF023075
                                                                                ),
                                                                                Color(
                                                                                        0xFF0D47A1
                                                                                )

                                                                                )
                                                        )
                                                )
                                                .statusBarsPadding()
                                                .padding(horizontal = 20.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Volver",
                                        tint = Color.White,
                                        modifier =
                                                Modifier.size(24.dp).clickable { navigator.pop() }
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Image(
                                        painter = painterResource(Res.drawable.logo_muni_arica),
                                        contentDescription = "Logo",
                                        modifier =
                                                Modifier.size(48.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(Color.White.copy(alpha = 0.1f))
                                                        .padding(4.dp),
                                        contentScale = ContentScale.Fit
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                        Text(
                                                text = "Configuración",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.titleLarge,
                                                fontSize = 22.sp
                                        )
                                        Text(
                                                text = "Ajustes de la aplicación",
                                                color = Color.White.copy(alpha = 0.9f),
                                                style = MaterialTheme.typography.bodySmall,
                                                fontSize = 12.sp
                                        )
                                }
                        }

                        Column(modifier = Modifier.padding(16.dp)) {
                                SettingsItem(
                                        title = "Notificaciones",
                                        checked = notificationsEnabled,
                                        onCheckedChange = { notificationsEnabled = it }
                                )
                                HorizontalDivider(color = Color(0xFFF0F0F0))
                                SettingsItem(
                                        title = "Modo Oscuro",
                                        checked = isDarkMode,
                                        onCheckedChange = { ThemeManager.toggleDarkMode(it) }
                                )
                                HorizontalDivider(color = Color(0xFFF0F0F0))

                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                        "Servidor",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                        onClick = { navigator.push(ServerConfigScreen()) },
                                        modifier = Modifier.fillMaxWidth()
                                ) { Text("Configurar Servidor") }

                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                        "Tamaño de letra",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onBackground
                                )
                                Slider(
                                        value = fontScale,
                                        onValueChange = { ThemeManager.setFontScale(it) },
                                        valueRange = 0.8f..1.4f,
                                        steps = 5,
                                        colors =
                                                SliderDefaults.colors(
                                                        thumbColor =
                                                                MaterialTheme.colorScheme.primary,
                                                        activeTrackColor =
                                                                MaterialTheme.colorScheme.primary,
                                                        inactiveTrackColor = Color.LightGray
                                                )
                                )
                                Text(
                                        text = "Escala actual: ${((fontScale * 100).toInt())}%",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                )
                        }
                }
        }
}

@Composable
fun SettingsItem(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
        ) {
                Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                )
                Switch(
                        checked = checked,
                        onCheckedChange = onCheckedChange,
                        colors =
                                SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color(0xFF02BB94),
                                        uncheckedThumbColor = Color.White,
                                        uncheckedTrackColor = Color.LightGray
                                )
                )
        }
}
