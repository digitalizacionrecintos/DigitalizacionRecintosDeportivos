package org.example.project.presentation.features.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import kotlinx.coroutines.delay
import municipalidadrecintos.composeapp.generated.resources.Res
import municipalidadrecintos.composeapp.generated.resources.logo_muni_arica
import org.jetbrains.compose.resources.painterResource

class EditProfileScreen : Screen {
        @Composable
        override fun Content() {
                val navigator = LocalNavigator.currentOrThrow
                val viewModel = remember { ProfileViewModel() }
                val state by viewModel.state.collectAsState()

                LaunchedEffect(state.successMessage) {
                        if (state.successMessage != null) {
                                delay(1500)
                                navigator.pop()
                        }
                }

                val snackbarHostState = remember { SnackbarHostState() }
                LaunchedEffect(state.errorMessage) {
                        state.errorMessage?.let {
                                snackbarHostState.showSnackbar(it)
                                viewModel.onEvent(ProfileEvent.ClearMessages)
                        }
                }
                LaunchedEffect(state.successMessage) {
                        state.successMessage?.let {
                                snackbarHostState.showSnackbar(it)

                        }
                }

                Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
                        Column(modifier = Modifier.fillMaxSize().background(Color.White)) {

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
                                                        .padding(
                                                                horizontal = 20.dp,
                                                                vertical = 12.dp
                                                        ),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Volver",
                                                tint = Color.White,
                                                modifier =
                                                        Modifier.size(24.dp).clickable {
                                                                navigator.pop()
                                                        }
                                        )

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Image(
                                                painter =
                                                        painterResource(
                                                                Res.drawable.logo_muni_arica
                                                        ),
                                                contentDescription = "Logo",
                                                modifier =
                                                        Modifier.size(48.dp)
                                                                .clip(RoundedCornerShape(8.dp))
                                                                .background(
                                                                        Color.White.copy(
                                                                                alpha = 0.1f
                                                                        )
                                                                )
                                                                .padding(4.dp),
                                                contentScale = ContentScale.Fit
                                        )

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Column {
                                                Text(
                                                        text = "Editar Perfil",
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        style = MaterialTheme.typography.titleLarge,
                                                        fontSize = 22.sp
                                                )
                                                Text(
                                                        text = "Actualiza tu información",
                                                        color = Color.White.copy(alpha = 0.9f),
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontSize = 12.sp
                                                )
                                        }
                                }

                                val scrollState = rememberScrollState()
                                Column(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .weight(1f)
                                                        .verticalScroll(scrollState)
                                                        .padding(16.dp)
                                ) {
                                        OutlinedTextField(
                                                value = state.name,
                                                onValueChange = {
                                                        viewModel.onEvent(
                                                                ProfileEvent.OnNameChange(it)
                                                        )
                                                },
                                                label = { Text("Nombre") },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp),
                                                colors =
                                                        OutlinedTextFieldDefaults.colors(
                                                                focusedBorderColor =
                                                                        Color(0xFF043CC7),
                                                                focusedLabelColor =
                                                                        Color(0xFF043CC7)
                                                        )
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        OutlinedTextField(
                                                value = state.lastName,
                                                onValueChange = {
                                                        viewModel.onEvent(
                                                                ProfileEvent.OnLastNameChange(it)
                                                        )
                                                },
                                                label = { Text("Apellido") },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp),
                                                colors =
                                                        OutlinedTextFieldDefaults.colors(
                                                                focusedBorderColor =
                                                                        Color(0xFF043CC7),
                                                                focusedLabelColor =
                                                                        Color(0xFF043CC7)
                                                        )
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        OutlinedTextField(
                                                value = state.email,
                                                onValueChange = {
                                                        viewModel.onEvent(
                                                                ProfileEvent.OnEmailChange(it)
                                                        )
                                                },
                                                label = { Text("Correo Electrónico") },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp),
                                                colors =
                                                        OutlinedTextFieldDefaults.colors(
                                                                focusedBorderColor =
                                                                        Color(0xFF043CC7),
                                                                focusedLabelColor =
                                                                        Color(0xFF043CC7)
                                                        )
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        OutlinedTextField(
                                                value = state.phone,
                                                onValueChange = {
                                                        viewModel.onEvent(
                                                                ProfileEvent.OnPhoneChange(it)
                                                        )
                                                },
                                                label = { Text("Teléfono") },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp),
                                                colors =
                                                        OutlinedTextFieldDefaults.colors(
                                                                focusedBorderColor =
                                                                        Color(0xFF043CC7),
                                                                focusedLabelColor =
                                                                        Color(0xFF043CC7)
                                                        )
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        OutlinedTextField(
                                                value = state.about,
                                                onValueChange = {
                                                        viewModel.onEvent(
                                                                ProfileEvent.OnAboutChange(it)
                                                        )
                                                },
                                                label = { Text("Acerca de") },
                                                modifier = Modifier.fillMaxWidth().height(120.dp),
                                                shape = RoundedCornerShape(12.dp),
                                                maxLines = 5,
                                                colors =
                                                        OutlinedTextFieldDefaults.colors(
                                                                focusedBorderColor =
                                                                        Color(0xFF043CC7),
                                                                focusedLabelColor =
                                                                        Color(0xFF043CC7)
                                                        )
                                        )

                                        Spacer(modifier = Modifier.height(32.dp))

                                        Button(
                                                onClick = {
                                                        viewModel.onEvent(ProfileEvent.SaveProfile)
                                                },
                                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                                shape = RoundedCornerShape(12.dp),
                                                enabled = !state.isLoading,
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor = Color(0xFF02BB94)
                                                        )
                                        ) {
                                                if (state.isLoading) {
                                                        CircularProgressIndicator(
                                                                modifier = Modifier.size(24.dp),
                                                                color = Color.White,
                                                                strokeWidth = 2.dp
                                                        )
                                                } else {
                                                        Text(
                                                                "Guardar Cambios",
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 16.sp
                                                        )
                                                }
                                        }
                                }
                        }
                }
        }

}
