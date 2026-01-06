package org.example.project.presentation.features.auth.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.presentation.components.MuniTextField

class RegisterScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        RegisterScreenContent(
                onBackClick = { navigator.pop() },
                onRegisterClick = {

                }
        )
    }
}

@Composable
fun RegisterScreenContent(onBackClick: () -> Unit, onRegisterClick: () -> Unit) {
    val gradientBrush = Brush.verticalGradient(0.0f to Color(0xFF043CC7), 1.0f to Color(0xFF3DBAD7))

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(gradientBrush).statusBarsPadding()) {
        Column(
                modifier =
                        Modifier.fillMaxSize()
                                .padding(horizontal = 24.dp)
                                .verticalScroll(
                                        rememberScrollState()
                                )
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp).clickable { onBackClick() }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                    "Crear Cuenta",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
            )
            Text(
                    "Únete a los eventos municipales",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            MuniTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Nombre completo",
                    leadingIcon = { Icon(Icons.Default.Person, null, tint = Color(0xFF043CC7)) },
                    modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            MuniTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Correo electrónico",
                    leadingIcon = { Icon(Icons.Default.Email, null, tint = Color(0xFF043CC7)) },
                    modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            MuniTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Contraseña",
                    visualTransformation = PasswordVisualTransformation(),
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFF043CC7)) },
                    modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            MuniTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = "Confirmar contraseña",
                    visualTransformation = PasswordVisualTransformation(),
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFF043CC7)) },
                    modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                    onClick = onRegisterClick,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            ) {
                Text(
                        "Registrarse",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF043CC7)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
