package org.example.project.presentation.features.notificaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import org.example.project.domain.model.Notificacion
import org.example.project.domain.usecase.notificacion.GetNotificacionesUseCase
import org.example.project.domain.usecase.notificacion.MarkNotificacionReadUseCase
import org.example.project.presentation.components.MuniTopBarLogo
import org.example.project.presentation.theme.MuniColors
import org.example.project.presentation.theme.MuniGradients
import org.example.project.presentation.theme.MuniSpacing
import org.koin.compose.koinInject

class NotificacionesScreen : Screen {
    @Composable
    override fun Content() {
        val getNotificacionesUseCase: GetNotificacionesUseCase = koinInject()
        val markNotificacionReadUseCase: MarkNotificacionReadUseCase = koinInject()
        val viewModel = remember { NotificacionesViewModel(getNotificacionesUseCase, markNotificacionReadUseCase) }
        val state by viewModel.state.collectAsState()

        NotificacionesContent(state = state, onEvent = viewModel::onEvent)
    }
}

@Composable
fun NotificacionesContent(state: NotificacionesState, onEvent: (NotificacionesEvent) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().background(MuniGradients.header)
                .statusBarsPadding().padding(horizontal = MuniSpacing.lg, vertical = MuniSpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MuniTopBarLogo()
            Spacer(Modifier.width(MuniSpacing.lg))
            Column {
                Text("Notificaciones", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                Text("Mantente informado", color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodySmall)
            }
        }

        Column(Modifier.background(MuniGradients.contentBackground).fillMaxSize().weight(1f)) {
            when {
                state.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MuniColors.primaryBlue)
                    }
                }
                state.notificaciones.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.NotificationsNone, contentDescription = null, modifier = Modifier.size(64.dp), tint = MuniColors.mediumGray)
                            Spacer(Modifier.height(16.dp))
                            Text("No hay notificaciones", color = MuniColors.mediumGray)
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.notificaciones, key = { it.idNotificacion }) { notif ->
                            NotificacionCard(notif, onClick = {
                                if (!notif.leida) onEvent(NotificacionesEvent.MarkAsRead(notif.idNotificacion))
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificacionCard(notificacion: Notificacion, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (notificacion.leida) Color(0xFFF5F5F5) else Color.White),
        elevation = CardDefaults.cardElevation(if (notificacion.leida) 0.dp else 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Icon(
                if (notificacion.leida) Icons.Default.NotificationsNone else Icons.Default.NotificationsActive,
                contentDescription = null,
                tint = if (notificacion.leida) MuniColors.mediumGray else Color(0xFF043CC7),
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(notificacion.titulo, fontWeight = if (notificacion.leida) FontWeight.Normal else FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium, color = Color(0xFF333333))
                Spacer(Modifier.height(4.dp))
                Text(notificacion.mensaje, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                notificacion.fechaCreacion?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(it, style = MaterialTheme.typography.labelSmall, color = MuniColors.mediumGray)
                }
            }
            if (!notificacion.leida) {
                Box(Modifier.size(8.dp).background(Color(0xFF043CC7), RoundedCornerShape(4.dp)))
            }
        }
    }
}
