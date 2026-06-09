package org.example.project.presentation.features.auth.login

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import org.example.project.presentation.theme.MuniColors
import org.example.project.presentation.theme.MuniGradients
import org.example.project.presentation.theme.MuniShapes
import org.example.project.presentation.theme.MuniSpacing
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import municipalidadrecintos.composeapp.generated.resources.Res
import municipalidadrecintos.composeapp.generated.resources.logo_muni_arica
import org.example.project.presentation.MainScreen
import org.example.project.domain.usecase.auth.LoginUseCase
import org.example.project.domain.usecase.auth.RegisterUseCase
import org.example.project.presentation.components.MuniTextField
import org.example.project.presentation.features.config.ServerConfigScreen
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

class LoginScreen : Screen {
        @Composable
        override fun Content() {
                val navigator = LocalNavigator.currentOrThrow
                val loginUseCase: LoginUseCase = koinInject()
                val viewModel = remember { LoginViewModel(loginUseCase) }
                val state by viewModel.state.collectAsState()

                LaunchedEffect(state.isLoggedIn) {
                        if (state.isLoggedIn) {
                                navigator.replaceAll(MainScreen())
                        }
                }

                LoginScreenContent(
                        state = state,
                        onEvent = viewModel::onEvent,
                        onSettingsClick = { navigator.push(ServerConfigScreen()) }
                )
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenContent(
        state: LoginState,
        onEvent: (LoginEvent) -> Unit,
        onSettingsClick: () -> Unit
) {
        val gradientBrush = MuniGradients.primaryVertical

        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        var showRegisterSheet by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        var passwordVisible by remember { mutableStateOf(false) }

        Box(
                modifier =
                        Modifier.fillMaxSize().background(brush = gradientBrush).statusBarsPadding()
        ) {
                Box(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.TopEnd
                ) {
                        IconButton(onClick = onSettingsClick) {
                                Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Configuración del Servidor",
                                        tint = MuniColors.surfaceCard.copy(alpha = 0.7f)
                                )
                        }
                }
                Column(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                ) {
                        Image(
                                painter = painterResource(Res.drawable.logo_muni_arica),
                                contentDescription = "Logo Municipalidad",
                                modifier = Modifier.width(220.dp),
                                contentScale = ContentScale.FillWidth
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        Text(
                                text = "Bienvenido",
                                color = MuniColors.surfaceCard,
                                style = MaterialTheme.typography.displayLarge
                        )
                        Text(
                                text = "Inicia sesión para continuar",
                                color = MuniColors.surfaceCard.copy(alpha = 0.8f),
                                fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        MuniTextField(
                                value = state.email,
                                onValueChange = { onEvent(LoginEvent.OnEmailChange(it)) },
                                placeholder = "Correo electrónico",
                                leadingIcon = {
                                        Icon(Icons.Default.Email, null, tint = MuniColors.primaryBlue)
                                },
                                modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        MuniTextField(
                                value = state.password,
                                onValueChange = { onEvent(LoginEvent.OnPasswordChange(it)) },
                                placeholder = "Contraseña",
                                leadingIcon = {
                                        Icon(Icons.Default.Lock, null, tint = MuniColors.primaryBlue)
                                },
                                trailingIcon = {
                                        IconButton(
                                                onClick = { passwordVisible = !passwordVisible }
                                        ) {
                                                Icon(
                                                        imageVector =
                                                                if (passwordVisible)
                                                                        Icons.Default.VisibilityOff
                                                                else Icons.Default.Visibility,
                                                        contentDescription =
                                                                if (passwordVisible)
                                                                        "Ocultar contraseña"
                                                                else "Mostrar contraseña",
                                                        tint = MuniColors.primaryBlue
                                                )
                                        }
                                },
                                visualTransformation =
                                        if (passwordVisible) VisualTransformation.None
                                        else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                        )

                        if (state.error != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(state.error, color = MuniColors.errorRed, fontSize = 14.sp)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                                onClick = { onEvent(LoginEvent.OnLoginClick) },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = MuniShapes.button,
                                colors = ButtonDefaults.buttonColors(containerColor = MuniColors.surfaceCard),
                                enabled = !state.isLoading
                        ) {
                                if (state.isLoading) {
                                        CircularProgressIndicator(
                                                color = MuniColors.primaryBlue,
                                                modifier = Modifier.size(24.dp)
                                        )
                                } else {
                                        Text(
                                                "Ingresar",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MuniColors.primaryBlue
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("¿No tienes cuenta? ", color = Color.White)
                                Text(
                                        text = "Regístrate aquí",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.clickable { showRegisterSheet = true }
                                )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        TextButton(onClick = { onEvent(LoginEvent.OnManagerLoginClick) }) {
                                Text(
                                        text = "Acceso Encargados",
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 14.sp
                                )
                        }
                }

                SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
                )
        }

        if (showRegisterSheet) {
                ModalBottomSheet(
                        onDismissRequest = { showRegisterSheet = false },
                        sheetState = sheetState,
                        containerColor = MuniColors.surfaceCard,
                        shape = MuniShapes.sheet,
                        dragHandle = {
                                Box(
                                        modifier =
                                                Modifier.padding(top = MuniSpacing.sm)
                                                        .width(40.dp)
                                                        .height(4.dp)
                                                        .clip(MuniShapes.pill)
                                                        .background(MuniColors.lightGray)
                                )
                        }
                ) {
                        RegisterBottomSheetContent(
                                onDismiss = { showRegisterSheet = false },
                                onRegisterSuccess = {
                                        showRegisterSheet = false
                                        scope.launch {
                                                snackbarHostState.showSnackbar(
                                                        "¡Cuenta creada con éxito! Ya puedes iniciar sesión."
                                                )
                                        }
                                }
                        )
                }
        }
}

@Composable
fun RegisterBottomSheetContent(onDismiss: () -> Unit, onRegisterSuccess: () -> Unit) {

        var nombre by remember { mutableStateOf("") }
        var apellido by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var telefono by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        val scope = rememberCoroutineScope()
        val registerUseCase: RegisterUseCase = koinInject()

        var passwordVisible by remember { mutableStateOf(false) }
        var confirmPasswordVisible by remember { mutableStateOf(false) }

        Column(
                modifier =
                        Modifier.fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .padding(bottom = 32.dp)
                                .verticalScroll(rememberScrollState())
                                .animateContentSize()
        ) {
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                        modifier =
                                                Modifier.size(48.dp)
                                                        .clip(CircleShape)
                                                        .background(MuniGradients.primaryVertical),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Icon(
                                                Icons.Default.Person,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(28.dp)
                                        )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                        Text(
                                                "Crear Cuenta",
                                                style = MaterialTheme.typography.headlineMedium,
                                                color = MuniColors.darkGray
                                        )
                                        Text(
                                                "Únete a los eventos municipales",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MuniColors.mediumGray
                                        )
                                }
                        }
                        IconButton(
                                onClick = onDismiss,
                                modifier =
                                        Modifier.size(36.dp)
                                                .clip(CircleShape)
                                                .background(MuniColors.offWhite)
                        ) {
                                Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Cerrar",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                )
                        }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = MuniColors.dividerColor)
                        Text(
                                "  Información personal  ",
                                style = MaterialTheme.typography.labelMedium,
                                color = MuniColors.mediumGray
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = MuniColors.dividerColor)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        OutlinedTextField(
                                value = nombre,
                                onValueChange = { nombre = it },
                                label = { Text("Nombre") },
                                leadingIcon = {
                                        Icon(Icons.Default.Person, null, tint = Color(0xFF043CC7))
                                },
                                modifier = Modifier.weight(1f),
                                shape = MuniShapes.textField,
                                colors =
                                        OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = MuniColors.primaryBlue,
                                                unfocusedBorderColor = MuniColors.dividerColor,
                                                focusedContainerColor = MuniColors.ultraLightGray,
                                                unfocusedContainerColor = MuniColors.ultraLightGray
                                        ),
                                singleLine = true
                        )

                        OutlinedTextField(
                                value = apellido,
                                onValueChange = { apellido = it },
                                label = { Text("Apellido") },
                                modifier = Modifier.weight(1f),
                                shape = MuniShapes.textField,
                                colors =
                                        OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = MuniColors.primaryBlue,
                                                unfocusedBorderColor = MuniColors.dividerColor,
                                                focusedContainerColor = MuniColors.ultraLightGray,
                                                unfocusedContainerColor = MuniColors.ultraLightGray
                                        ),
                                singleLine = true
                        )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico") },
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = MuniColors.primaryBlue) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MuniShapes.textField,
                        colors =
                                OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MuniColors.primaryBlue,
                                        unfocusedBorderColor = MuniColors.dividerColor,
                                        focusedContainerColor = MuniColors.ultraLightGray,
                                        unfocusedContainerColor = MuniColors.ultraLightGray
                                ),
                        singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Teléfono") },
                        leadingIcon = { Icon(Icons.Default.Call, null, tint = MuniColors.primaryBlue) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MuniShapes.textField,
                        colors =
                                OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MuniColors.primaryBlue,
                                        unfocusedBorderColor = MuniColors.dividerColor,
                                        focusedContainerColor = MuniColors.ultraLightGray,
                                        unfocusedContainerColor = MuniColors.ultraLightGray
                                ),
                        singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = MuniColors.dividerColor)
                        Text(
                                "  Seguridad  ",
                                style = MaterialTheme.typography.labelMedium,
                                color = MuniColors.mediumGray
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = MuniColors.dividerColor)
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = MuniColors.primaryBlue) },
                        trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                                imageVector =
                                                        if (passwordVisible)
                                                                Icons.Default.VisibilityOff
                                                        else Icons.Default.Visibility,
                                                contentDescription =
                                                        if (passwordVisible) "Ocultar"
                                                        else "Mostrar",
                                                tint = Color.Gray
                                        )
                                }
                        },
                        visualTransformation =
                                if (passwordVisible) VisualTransformation.None
                                else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = MuniShapes.textField,
                        colors =
                                OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MuniColors.primaryBlue,
                                        unfocusedBorderColor = MuniColors.dividerColor,
                                        focusedContainerColor = MuniColors.ultraLightGray,
                                        unfocusedContainerColor = MuniColors.ultraLightGray
                                ),
                        singleLine = true
                )

                Spacer(modifier = Modifier.height(MuniSpacing.sm))

                OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmar contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = MuniColors.primaryBlue) },
                        trailingIcon = {
                                IconButton(
                                        onClick = {
                                                confirmPasswordVisible = !confirmPasswordVisible
                                        }
                                ) {
                                        Icon(
                                                imageVector =
                                                        if (confirmPasswordVisible)
                                                                Icons.Default.VisibilityOff
                                                        else Icons.Default.Visibility,
                                                contentDescription =
                                                        if (confirmPasswordVisible) "Ocultar"
                                                        else "Mostrar",
                                                tint = Color.Gray
                                        )
                                }
                        },
                        visualTransformation =
                                if (confirmPasswordVisible) VisualTransformation.None
                                else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = MuniShapes.textField,
                        colors =
                                OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MuniColors.primaryBlue,
                                        unfocusedBorderColor = MuniColors.dividerColor,
                                        focusedContainerColor = MuniColors.ultraLightGray,
                                        unfocusedContainerColor = MuniColors.ultraLightGray
                                ),
                        singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 4.dp)
                ) {
                        Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint =
                                        if (password.length >= 6) MuniColors.successGreen
                                        else MuniColors.lightGray,
                                modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                                "Mínimo 6 caracteres",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (password.length >= 6) MuniColors.successGreen else MuniColors.mediumGray
                        )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 4.dp)
                ) {
                        Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint =
                                        if (password.isNotEmpty() && password == confirmPassword)
                                                MuniColors.successGreen
                                        else MuniColors.lightGray,
                                modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                                "Las contraseñas coinciden",
                                style = MaterialTheme.typography.bodySmall,
                                color =
                                        if (password.isNotEmpty() && password == confirmPassword)
                                                MuniColors.successGreen
                                        else MuniColors.mediumGray
                        )
                }

                        if (errorMessage != null) {
                                Spacer(modifier = Modifier.height(MuniSpacing.sm))
                                Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors =
                                                CardDefaults.cardColors(containerColor = MuniColors.badgeRedBg),
                                        shape = MuniShapes.buttonSmall
                                ) {
                                        Text(
                                                errorMessage!!,
                                                color = MuniColors.errorRed,
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.padding(MuniSpacing.sm),
                                        textAlign = TextAlign.Center
                                )
                        }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                        onClick = {
                                when {
                                        nombre.isBlank() ||
                                                apellido.isBlank() ||
                                                email.isBlank() ||
                                                telefono.isBlank() ||
                                                password.isBlank() -> {
                                                errorMessage = "Todos los campos son obligatorios"
                                        }
                                        password != confirmPassword -> {
                                                errorMessage = "Las contraseñas no coinciden"
                                        }
                                        password.length < 6 -> {
                                                errorMessage =
                                                        "La contraseña debe tener al menos 6 caracteres"
                                        }
                                        else -> {
                                                scope.launch {
                                                        isLoading = true
                                                        errorMessage = null
                                                        when (registerUseCase(email, password, nombre, apellido, telefono)) {
                                                            is org.example.project.core.error.Try.Success -> {
                                                                onRegisterSuccess()
                                                            }
                                                            is org.example.project.core.error.Try.Failure -> {
                                                                errorMessage = "Error al registrar"
                                                            }
                                                        }
                                                        isLoading = false
                                                }
                                        }
                                }
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = MuniShapes.button,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        enabled = !isLoading
                ) {
                        Box(
                                modifier =
                                        Modifier.fillMaxSize()
                                                .background(
                                                        MuniGradients.registerButton,
                                                        shape = MuniShapes.button
                                                ),
                                contentAlignment = Alignment.Center
                        ) {
                                if (isLoading) {
                                        CircularProgressIndicator(
                                                color = Color.White,
                                                modifier = Modifier.size(24.dp)
                                        )
                                } else {
                                        Text(
                                                "Crear mi cuenta",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                        )
                                }
                        }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                        "Al registrarte, aceptas nuestros términos y condiciones",
                        style = MaterialTheme.typography.bodySmall,
                        color = MuniColors.mediumGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                )
        }
}
