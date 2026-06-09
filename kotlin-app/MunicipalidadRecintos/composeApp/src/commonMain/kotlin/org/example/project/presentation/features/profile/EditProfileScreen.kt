package org.example.project.presentation.features.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import org.example.project.domain.usecase.user.UpdateProfileUseCase
import org.example.project.presentation.components.MuniTopBarLogo
import org.example.project.presentation.theme.MuniColors
import org.example.project.presentation.theme.MuniGradients
import org.example.project.presentation.theme.MuniShapes
import org.example.project.presentation.theme.MuniSpacing
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import municipalidadrecintos.composeapp.generated.resources.Res
import municipalidadrecintos.composeapp.generated.resources.logo_muni_arica
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

class EditProfileScreen : Screen {
        @Composable
        override fun Content() {
                val navigator = LocalNavigator.currentOrThrow
                val updateProfileUseCase: UpdateProfileUseCase = koinInject<UpdateProfileUseCase>()
                val viewModel = remember { ProfileViewModel(updateProfileUseCase) }
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
                Column(modifier = Modifier.fillMaxSize().background(MuniColors.surfaceCard)) {

                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .background(MuniGradients.header)
                                                .statusBarsPadding()
                                                .padding(
                                                        horizontal = MuniSpacing.lg,
                                                        vertical = MuniSpacing.sm
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

                                Spacer(modifier = Modifier.width(MuniSpacing.lg))

                                        MuniTopBarLogo()

                                        Spacer(modifier = Modifier.width(MuniSpacing.lg))

                                        Column {
                                                Text(
                                                        text = "Mi Perfil",
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        style = MaterialTheme.typography.titleLarge
                                                )
                                                Text(
                                                        text = "Gestiona tu cuenta",
                                                        color = Color.White.copy(alpha = 0.8f),
                                                        style = MaterialTheme.typography.bodySmall
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
                                                shape = MuniShapes.textField,
                                                colors =
                                                        OutlinedTextFieldDefaults.colors(
                                                                focusedBorderColor = MuniColors.primaryBlue,
                                                                focusedLabelColor = MuniColors.primaryBlue
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
                                                shape = MuniShapes.textField,
                                                colors =
                                                        OutlinedTextFieldDefaults.colors(
                                                                focusedBorderColor = MuniColors.primaryBlue,
                                                                focusedLabelColor = MuniColors.primaryBlue
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
                                                shape = MuniShapes.textField,
                                                colors =
                                                        OutlinedTextFieldDefaults.colors(
                                                                focusedBorderColor = MuniColors.primaryBlue,
                                                                focusedLabelColor = MuniColors.primaryBlue
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
                                                shape = MuniShapes.textField,
                                                colors =
                                                        OutlinedTextFieldDefaults.colors(
                                                                focusedBorderColor = MuniColors.primaryBlue,
                                                                focusedLabelColor = MuniColors.primaryBlue
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
                                                shape = MuniShapes.textField,
                                                maxLines = 5,
                                                colors =
                                                        OutlinedTextFieldDefaults.colors(
                                                                focusedBorderColor = MuniColors.primaryBlue,
                                                                focusedLabelColor = MuniColors.primaryBlue
                                                        )
                                        )

                                        Spacer(modifier = Modifier.height(32.dp))

                                        Button(
                                                onClick = {
                                                        viewModel.onEvent(ProfileEvent.SaveProfile)
                                                },
                                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                                shape = MuniShapes.textField,
                                                enabled = !state.isLoading,
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor = MuniColors.accentEmerald
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
