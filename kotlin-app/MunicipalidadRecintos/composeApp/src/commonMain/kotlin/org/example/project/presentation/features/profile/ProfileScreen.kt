package org.example.project.presentation.features.profile

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import org.example.project.domain.manager.SessionManager
import org.example.project.domain.model.UserRole
import org.example.project.domain.usecase.user.UpdateProfileUseCase
import org.example.project.presentation.components.MuniTopBarLogo
import org.example.project.presentation.features.auth.login.LoginScreen
import org.example.project.presentation.theme.MuniColors
import org.example.project.presentation.theme.MuniGradients
import org.example.project.presentation.theme.MuniShapes
import org.example.project.presentation.theme.MuniSpacing
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

class ProfileScreen : Screen {
        @Composable
        override fun Content() {
                val navigator = LocalNavigator.currentOrThrow
                val updateProfileUseCase: UpdateProfileUseCase = koinInject<UpdateProfileUseCase>()
                val viewModel = remember { ProfileViewModel(updateProfileUseCase) }
                val state by viewModel.state.collectAsState()

                LaunchedEffect(Unit) { viewModel.getProfileData() }

                var rootNavigator = navigator.parent
                while (rootNavigator?.parent != null) {
                        rootNavigator = rootNavigator.parent
                }

                ProfileScreenContent(
                        state = state,
                        onLogout = {

                                SessionManager.endSession()
                                SessionManager.switchRole(UserRole.USER)

                                rootNavigator?.replaceAll(LoginScreen())
                        },
                        onOptionClick = { option ->
                                when (option) {
                                        "Editar" -> navigator.push(EditProfileScreen())
                                        "Config" -> navigator.push(SettingsScreen())
                                }
                        }
                )
        }
}

@Composable
fun ProfileScreenContent(
        state: ProfileState,
        onLogout: () -> Unit,
        onOptionClick: (String) -> Unit
) {
        var showLogoutDialog by remember { mutableStateOf(false) }
        var isVisible by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
                delay(100)
                isVisible = true
        }

        Box(modifier = Modifier.fillMaxSize().background(MuniColors.surfaceOverlay)) {
                Column(modifier = Modifier.fillMaxSize()) {

                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .background(MuniGradients.header)
                                                .statusBarsPadding()
                                                .padding(horizontal = MuniSpacing.lg, vertical = MuniSpacing.sm),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
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
                                                color = Color.White.copy(alpha = 0.9f),
                                                style = MaterialTheme.typography.bodySmall
                                        )
                                }
                        }

                        AnimatedVisibility(
                                visible = isVisible,
                                enter =
                                        fadeIn(animationSpec = tween(600)) +
                                                slideInVertically(initialOffsetY = { -100 })
                        ) {
                                Box(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .height(300.dp)
                                                        .clip(MuniShapes.topBar)
                                                        .background(MuniGradients.profileHeader),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.padding(24.dp)
                                        ) {

                                                var avatarScale by remember { mutableStateOf(0.7f) }
                                                LaunchedEffect(Unit) {
                                                        delay(200)
                                                        avatarScale = 1f
                                                }

                                                Box(
                                                        modifier =
                                                                Modifier.size(120.dp)
                                                                        .scale(
                                                                                animateFloatAsState(
                                                                                                targetValue =
                                                                                                        avatarScale,
                                                                                                animationSpec =
                                                                                                        spring(
                                                                                                                dampingRatio =
                                                                                                                        Spring.DampingRatioMediumBouncy,
                                                                                                                stiffness =
                                                                                                                        Spring.StiffnessLow
                                                                                                        )
                                                                                        )
                                                                                        .value
                                                                        )
                                                ) {

                                                        Box(
                                                                modifier =
                                                                        Modifier.fillMaxSize()
                                                                                .clip(CircleShape)
                                                                                .background(
                                                                                        Brush.linearGradient(
                                                                                                colors =
                                                                                                        listOf(
                                                                                                                Color.White
                                                                                                                        .copy(
                                                                                                                                alpha =
                                                                                                                                        0.3f
                                                                                                                        ),
                                                                                                                Color.White
                                                                                                                        .copy(
                                                                                                                                alpha =
                                                                                                                                        0.1f
                                                                                                                        )
                                                                                                        )
                                                                                        )
                                                                                )
                                                        )

                                                        Box(
                                                                modifier =
                                                                        Modifier.fillMaxSize()
                                                                                .padding(6.dp)
                                                                                .clip(CircleShape)
                                                                                .background(
                                                                                        Color.White
                                                                                ),
                                                                contentAlignment = Alignment.Center
                                                        ) {
                                                                Box(
                                                                        modifier =
                                                                                Modifier.fillMaxSize()
                                                                                        .padding(
                                                                                                4.dp
                                                                                        )
                                                                                        .clip(
                                                                                                CircleShape
                                                                                        )
                                                                                        .background(
                                                                                                Brush.linearGradient(
                                                                                                        colors =
                                                                                                                listOf(
                                                                                                                        Color(
                                                                                                                                0xFFE3F2FD
                                                                                                                        ),
                                                                                                                        Color(
                                                                                                                                0xFFBBDEFB
                                                                                                                        )
                                                                                                                )
                                                                                                )
                                                                                        ),
                                                                        contentAlignment =
                                                                                Alignment.Center
                                                                ) {
                                                                        Icon(
                                                                                imageVector =
                                                                                        Icons.Default
                                                                                                .Person,
                                                                                contentDescription =
                                                                                        null,
                                                                                tint =
                                                                                        Color(
                                                                                                0xFF043CC7
                                                                                        ),
                                                                                modifier =
                                                                                        Modifier.size(
                                                                                                64.dp
                                                                                        )
                                                                        )
                                                                }
                                                        }

                                                        Surface(
                                                                modifier =
                                                                        Modifier.align(
                                                                                        Alignment
                                                                                                .BottomEnd
                                                                                )
                                                                                .size(36.dp)
                                                                                .clickable {
                                                                                        onOptionClick(
                                                                                                "Editar"
                                                                                        )
                                                                                },
                                                                shape = CircleShape,
                                                                color = Color.White,
                                                                shadowElevation = 4.dp
                                                        ) {
                                                                Icon(
                                                                        imageVector =
                                                                                Icons.Default.Edit,
                                                                        contentDescription =
                                                                                "Editar",
                                                                        tint = Color(0xFF043CC7),
                                                                        modifier =
                                                                                Modifier.padding(
                                                                                        8.dp
                                                                                )
                                                                )
                                                        }
                                                }

                                                Spacer(modifier = Modifier.height(20.dp))

                                                Text(
                                                        text = state.name,
                                                        color = Color.White,
                                                        fontSize = 26.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        letterSpacing = 0.5.sp
                                                )

                                                Spacer(modifier = Modifier.height(4.dp))

                                                Text(
                                                        text = state.email,
                                                        color = Color.White.copy(alpha = 0.9f),
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.Medium
                                                )
                                        }
                                }
                        }

                        Column(modifier = Modifier.fillMaxWidth().weight(1f).padding(24.dp)) {
                                AnimatedVisibility(
                                        visible = isVisible,
                                        enter =
                                                fadeIn(
                                                        animationSpec =
                                                                tween(800, delayMillis = 200)
                                                ) +
                                                        slideInVertically(
                                                                initialOffsetY = { 100 },
                                                                animationSpec =
                                                                        tween(
                                                                                800,
                                                                                delayMillis = 200
                                                                        )
                                                        )
                                ) {
                                        Column {

                                                if (state.about.isNotBlank()) {
                                                        Text(
                                                                text = "Acerca de",
                                                                color = Color(0xFF212121),
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 18.sp,
                                                                modifier =
                                                                        Modifier.padding(
                                                                                bottom = 12.dp
                                                                        )
                                                        )
                                                        Card(
                                                                colors =
                                                                        CardDefaults.cardColors(
                                                                                containerColor =
                                                                                        Color.White
                                                                        ),
                                                                shape = RoundedCornerShape(20.dp),
                                                                elevation =
                                                                        CardDefaults.cardElevation(
                                                                                2.dp
                                                                        ),
                                                                modifier =
                                                                        Modifier.fillMaxWidth()
                                                                                .padding(
                                                                                        bottom =
                                                                                                24.dp
                                                                                )
                                                        ) {
                                                                Text(
                                                                        text = state.about,
                                                                        modifier =
                                                                                Modifier.padding(
                                                                                        16.dp
                                                                                ),
                                                                        color = Color(0xFF616161),
                                                                        fontSize = 14.sp,
                                                                        lineHeight = 20.sp
                                                                )
                                                        }
                                                }

                                                Text(
                                                        text = "Configuración de cuenta",
                                                        color = Color(0xFF212121),
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 18.sp,
                                                        modifier = Modifier.padding(bottom = 16.dp)
                                                )

                                                Card(
                                                        colors =
                                                                CardDefaults.cardColors(
                                                                        containerColor = Color.White
                                                                ),
                                                        shape = RoundedCornerShape(20.dp),
                                                        elevation = CardDefaults.cardElevation(4.dp)
                                                ) {
                                                        Column {
                                                                ProfileOptionItem(
                                                                        icon = Icons.Default.Edit,
                                                                        title = "Editar Perfil",
                                                                        subtitle =
                                                                                "Actualiza tu información personal",
                                                                        iconBackground =
                                                                                Color(0xFFE3F2FD),
                                                                        iconTint = Color(0xFF1976D2)
                                                                ) { onOptionClick("Editar") }

                                                                HorizontalDivider(
                                                                        color = Color(0xFFF0F0F0),
                                                                        modifier =
                                                                                Modifier.padding(
                                                                                        horizontal =
                                                                                                16.dp
                                                                                )
                                                                )

                                                                ProfileOptionItem(
                                                                        icon =
                                                                                Icons.Default
                                                                                        .Notifications,
                                                                        title = "Notificaciones",
                                                                        subtitle =
                                                                                "Gestiona tus preferencias",
                                                                        iconBackground =
                                                                                Color(0xFFFFF3E0),
                                                                        iconTint = Color(0xFFF57C00)
                                                                ) {
                                                                        onOptionClick(
                                                                                "Notificaciones"
                                                                        )
                                                                }

                                                                HorizontalDivider(
                                                                        color = Color(0xFFF0F0F0),
                                                                        modifier =
                                                                                Modifier.padding(
                                                                                        horizontal =
                                                                                                16.dp
                                                                                )
                                                                )

                                                                ProfileOptionItem(
                                                                        icon =
                                                                                Icons.Default
                                                                                        .Settings,
                                                                        title = "Configuración",
                                                                        subtitle =
                                                                                "Ajustes de la aplicación",
                                                                        iconBackground =
                                                                                Color(0xFFF3E5F5),
                                                                        iconTint = Color(0xFF7B1FA2)
                                                                ) { onOptionClick("Config") }
                                                        }
                                                }

                                                Spacer(modifier = Modifier.height(24.dp))

                                                Button(
                                                        onClick = { showLogoutDialog = true },
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .height(56.dp),
                                                        shape = RoundedCornerShape(16.dp),
                                                        colors =
                                                                ButtonDefaults.buttonColors(
                                                                        containerColor =
                                                                                Color(0xFFFFEBEE),
                                                                        contentColor =
                                                                                Color(0xFFD32F2F)
                                                                ),
                                                        elevation =
                                                                ButtonDefaults.buttonElevation(
                                                                        defaultElevation = 0.dp,
                                                                        pressedElevation = 4.dp
                                                                )
                                                ) {
                                                        Icon(
                                                                Icons.AutoMirrored.Filled.ExitToApp,
                                                                contentDescription = null,
                                                                modifier = Modifier.size(22.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(12.dp))
                                                        Text(
                                                                "Cerrar Sesión",
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 16.sp
                                                        )
                                                }
                                        }
                                }
                        }
                }

                if (showLogoutDialog) {
                        LogoutConfirmationDialog(
                                onConfirm = {
                                        showLogoutDialog = false
                                        onLogout()
                                },
                                onDismiss = { showLogoutDialog = false }
                        )
                }
        }
}

@Composable
fun ProfileOptionItem(
        icon: ImageVector,
        title: String,
        subtitle: String,
        iconBackground: Color,
        iconTint: Color,
        onClick: () -> Unit
) {
        var isPressed by remember { mutableStateOf(false) }

        Surface(
                modifier =
                        Modifier.fillMaxWidth().clickable {
                                isPressed = true
                                onClick()
                        },
                color = if (isPressed) Color(0xFFF8F9FA) else Color.Transparent
        ) {
                LaunchedEffect(isPressed) {
                        if (isPressed) {
                                delay(100)
                                isPressed = false
                        }
                }

                Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {

                        Surface(
                                modifier = Modifier.size(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                color = iconBackground
                        ) {
                                Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = iconTint,
                                        modifier = Modifier.padding(12.dp)
                                )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = title,
                                        color = Color(0xFF212121),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                        text = subtitle,
                                        color = Color(0xFF757575),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Normal
                                )
                        }

                        Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = Color(0xFFBDBDBD),
                                modifier = Modifier.size(24.dp)
                        )
                }
        }
}

@Composable
fun LogoutConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
        AlertDialog(
                onDismissRequest = onDismiss,
                icon = {
                        Surface(
                                shape = CircleShape,
                                color = Color(0xFFFFEBEE),
                                modifier = Modifier.size(64.dp)
                        ) {
                                Icon(
                                        Icons.AutoMirrored.Filled.ExitToApp,
                                        contentDescription = null,
                                        tint = Color(0xFFD32F2F),
                                        modifier = Modifier.padding(16.dp)
                                )
                        }
                },
                title = {
                        Text(
                                text = "¿Cerrar sesión?",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF212121)
                        )
                },
                text = {
                        Text(
                                text = "¿Estás seguro de que deseas salir de tu cuenta?",
                                fontSize = 15.sp,
                                color = Color(0xFF757575)
                        )
                },
                confirmButton = {
                        Button(
                                onClick = onConfirm,
                                colors =
                                        ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFFD32F2F)
                                        ),
                                shape = RoundedCornerShape(12.dp)
                        ) { Text("Cerrar sesión", fontWeight = FontWeight.SemiBold) }
                },
                dismissButton = {
                        TextButton(
                                onClick = onDismiss,
                                colors =
                                        ButtonDefaults.textButtonColors(
                                                contentColor = Color(0xFF757575)
                                        )
                        ) { Text("Cancelar", fontWeight = FontWeight.Medium) }
                },
                shape = RoundedCornerShape(24.dp),
                containerColor = Color.White
        )
}
