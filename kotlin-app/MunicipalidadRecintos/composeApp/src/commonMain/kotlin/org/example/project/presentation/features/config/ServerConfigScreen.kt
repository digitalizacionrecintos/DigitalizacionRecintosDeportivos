package org.example.project.presentation.features.config

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.domain.manager.ServerConfigManager

class ServerConfigScreen : Screen {
        @Composable
        override fun Content() {
                val navigator = LocalNavigator.currentOrThrow
                var ipAddress by remember { mutableStateOf("192.168.1.144") }
                var port by remember { mutableStateOf("8080") }
                var showError by remember { mutableStateOf(false) }

                Box(
                        modifier =
                                Modifier.fillMaxSize()
                                        .background(
                                                Brush.verticalGradient(
                                                        colors =
                                                                listOf(
                                                                        MaterialTheme.colorScheme
                                                                                .primaryContainer,
                                                                        MaterialTheme.colorScheme
                                                                                .background
                                                                )
                                                )
                                        )
                ) {
                        Column(
                                modifier = Modifier.fillMaxSize().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                        ) {
                                Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Configuración",
                                        modifier = Modifier.size(80.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                        text = "Configuración del Servidor",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                        text =
                                                "Ingrese la dirección IP y puerto del servidor backend",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color =
                                                MaterialTheme.colorScheme.onBackground.copy(
                                                        alpha = 0.7f
                                                )
                                )

                                Spacer(modifier = Modifier.height(32.dp))

                                Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        elevation =
                                                CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                        Column(modifier = Modifier.padding(24.dp)) {
                                                OutlinedTextField(
                                                        value = ipAddress,
                                                        onValueChange = {
                                                                ipAddress = it
                                                                showError = false
                                                        },
                                                        label = { Text("Dirección IP") },
                                                        placeholder = { Text("192.168.1.144") },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        singleLine = true,
                                                        isError = showError && ipAddress.isBlank(),
                                                        supportingText = {
                                                                if (showError && ipAddress.isBlank()
                                                                ) {
                                                                        Text(
                                                                                "La dirección IP es requerida"
                                                                        )
                                                                }
                                                        }
                                                )

                                                Spacer(modifier = Modifier.height(16.dp))

                                                OutlinedTextField(
                                                        value = port,
                                                        onValueChange = {
                                                                port = it
                                                                showError = false
                                                        },
                                                        label = { Text("Puerto") },
                                                        placeholder = { Text("8080") },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        singleLine = true,
                                                        keyboardOptions =
                                                                KeyboardOptions(
                                                                        keyboardType =
                                                                                KeyboardType.Number
                                                                ),
                                                        isError = showError && port.isBlank(),
                                                        supportingText = {
                                                                if (showError && port.isBlank()) {
                                                                        Text(
                                                                                "El puerto es requerido"
                                                                        )
                                                                }
                                                        }
                                                )

                                                Spacer(modifier = Modifier.height(24.dp))

                                                Surface(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .secondaryContainer,
                                                        shape = RoundedCornerShape(8.dp)
                                                ) {
                                                        Column(modifier = Modifier.padding(12.dp)) {
                                                                Text(
                                                                        text = "URL del servidor:",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .bodySmall,
                                                                        color =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .onSecondaryContainer
                                                                                        .copy(
                                                                                                alpha =
                                                                                                        0.7f
                                                                                        )
                                                                )
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        4.dp
                                                                                )
                                                                )
                                                                Text(
                                                                        text =
                                                                                if (ipAddress
                                                                                                .isNotBlank() &&
                                                                                                port.isNotBlank()
                                                                                ) {
                                                                                        "http://$ipAddress:$port/api/"
                                                                                } else {
                                                                                        "Ingrese IP y puerto"
                                                                                },
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .bodyMedium,
                                                                        fontWeight =
                                                                                FontWeight.Medium,
                                                                        color =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .onSecondaryContainer
                                                                )
                                                        }
                                                }

                                                Spacer(modifier = Modifier.height(24.dp))

                                                Button(
                                                        onClick = {
                                                                if (ipAddress.isBlank() ||
                                                                                port.isBlank()
                                                                ) {
                                                                        showError = true
                                                                } else {
                                                                        ServerConfigManager
                                                                                .setServerConfig(
                                                                                        ipAddress,
                                                                                        port
                                                                                )
                                                                        navigator.pop()
                                                                }
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        shape = RoundedCornerShape(12.dp)
                                                ) {
                                                        Icon(
                                                                imageVector = Icons.Default.Check,
                                                                contentDescription = null,
                                                                modifier = Modifier.size(20.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text("Guardar Configuración")
                                                }
                                        }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                        text =
                                                "Puede cambiar esta configuración más tarde desde el menú de perfil",
                                        style = MaterialTheme.typography.bodySmall,
                                        color =
                                                MaterialTheme.colorScheme.onBackground.copy(
                                                        alpha = 0.5f
                                                )
                                )
                        }
                }
        }
}
