package org.example.project.presentation.features.manager.attendance

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import municipalidadrecintos.composeapp.generated.resources.Res
import municipalidadrecintos.composeapp.generated.resources.logo_muni_arica
import org.example.project.domain.model.AttendeeInfo
import org.example.project.domain.usecase.event.ChangeEventStatusUseCase
import org.example.project.domain.usecase.event.GetManagerEventsUseCase
import org.example.project.domain.usecase.event.UpdateAttendanceUseCase
import org.example.project.domain.util.DateTimeUtils
import org.example.project.presentation.components.MuniTopBarLogo
import org.example.project.presentation.theme.MuniColors
import org.example.project.presentation.theme.MuniGradients
import org.example.project.presentation.theme.MuniShapes
import org.example.project.presentation.theme.MuniSpacing
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

data class ManagerEventDetailScreen(val eventId: Int, val managerId: Int) : Screen {
        @Composable
        override fun Content() {
                val navigator = LocalNavigator.currentOrThrow
                val getManagerEventsUseCase: GetManagerEventsUseCase = koinInject()
                val changeEventStatusUseCase: ChangeEventStatusUseCase = koinInject()
                val updateAttendanceUseCase: UpdateAttendanceUseCase = koinInject()
                val viewModel = remember {
                    ManagerEventDetailViewModel(
                        managerId = managerId,
                        getManagerEventsUseCase = getManagerEventsUseCase,
                        changeEventStatusUseCase = changeEventStatusUseCase,
                        updateAttendanceUseCase = updateAttendanceUseCase
                    )
                }
                val state by viewModel.state.collectAsState()

                LaunchedEffect(eventId) { viewModel.loadEventDetail(eventId) }

                when (val currentState = state) {
                        is ManagerEventDetailState.Loading -> {
                                Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator(color = MuniColors.primaryBlue) }
                        }
                        is ManagerEventDetailState.Error -> {
                                Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.padding(MuniSpacing.xxl)
                                        ) {
                                                Text(
                                                        text = "Error",
                                                        style = MaterialTheme.typography.titleLarge,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MuniColors.errorRed
                                                )
                                                Spacer(modifier = Modifier.height(MuniSpacing.sm))
                                                Text(
                                                        text = currentState.message,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = MuniColors.mediumGray,
                                                        textAlign = TextAlign.Center
                                                )
                                                Spacer(modifier = Modifier.height(MuniSpacing.lg))
                                                Button(
                                                        onClick = {
                                                                viewModel.loadEventDetail(eventId)
                                                        },
                                                        colors =
                                                                ButtonDefaults.buttonColors(
                                                                        containerColor =
                                                                                MuniColors.primaryBlue
                                                                )
                                                ) { Text("Reintentar") }
                                        }
                                }
                        }
                        is ManagerEventDetailState.Success -> {
                                val eventDetail = currentState.eventDetail

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
                                                        imageVector =
                                                                Icons.AutoMirrored.Filled.ArrowBack,
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
                                                                text = "Detalle evento",
                                                                color = Color.White,
                                                                fontWeight = FontWeight.Bold,
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .titleLarge
                                                        )
                                                        Text(
                                                                text = "Gestión de asistentes",
                                                                color =
                                                                        Color.White.copy(
                                                                                alpha = 0.9f
                                                                        ),
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodySmall
                                                        )
                                                }
                                        }

                                        Column(
                                                modifier =
                                                        Modifier.weight(1f)
                                                                .verticalScroll(
                                                                        rememberScrollState()
                                                                )
                                        ) {

                                                if (!eventDetail.imagenUrl.isNullOrBlank()) {
                                                        AsyncImage(
                                                                model = eventDetail.imagenUrl,
                                                                contentDescription =
                                                                        "Imagen Evento",
                                                                modifier =
                                                                        Modifier.height(200.dp)
                                                                                .fillMaxWidth(),
                                                                contentScale = ContentScale.Crop
                                                        )
                                                } else {
                                                        Box(
                                                                modifier =
                                                                        Modifier.height(200.dp)
                                                                                .fillMaxWidth()
                                                                                .background(
                                                                                        Color.LightGray
                                                                                ),
                                                                contentAlignment = Alignment.Center
                                                        ) {
                                                                Icon(
                                                                        imageVector =
                                                                                Icons.Default
                                                                                        .Image,
                                                                        contentDescription =
                                                                                "Sin imagen",
                                                                        tint = Color.Gray,
                                                                        modifier =
                                                                                Modifier.size(64.dp)
                                                                )
                                                        }
                                                }

                                                Column(modifier = Modifier.padding(16.dp)) {
                                                        Text(
                                                                text = eventDetail.titulo,
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .titleLarge,
                                                                color = MuniColors.gradientBlue,
                                                                fontWeight = FontWeight.Black
                                                        )

                                                        Spacer(modifier = Modifier.height(8.dp))

                                                        Text(
                                                                text = eventDetail.categoriaName,
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelLarge,
                                                                color = MuniColors.badgeBlueText,
                                                                fontWeight = FontWeight.Bold
                                                        )

                                                        Spacer(modifier = Modifier.height(12.dp))

                                                        Row(
                                                                verticalAlignment =
                                                                        Alignment.CenterVertically
                                                        ) {
                                                                Icon(
                                                                        Icons.Default.LocationOn,
                                                                        null,
                                                                        tint = MuniColors.secondaryCyan,
                                                                        modifier =
                                                                                Modifier.size(20.dp)
                                                                )
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.width(8.dp)
                                                                )
                                                                Text(
                                                                        eventDetail
                                                                                .ubicacionRecinto,
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .bodyMedium,
                                                                        color = Color.Gray
                                                                )
                                                        }
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        Row(
                                                                verticalAlignment =
                                                                        Alignment.CenterVertically
                                                        ) {
                                                                Icon(
                                                                        Icons.Default.DateRange,
                                                                        null,
                                                                        tint = MuniColors.badgeBlueText,
                                                                        modifier =
                                                                                Modifier.size(20.dp)
                                                                )
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.width(8.dp)
                                                                )
                                                                Text(
                                                                        "${DateTimeUtils.formatEventDate(eventDetail.fechaInicio, eventDetail.horaInicio)} - ${DateTimeUtils.formatTime(eventDetail.horaFin)}",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .bodyMedium,
                                                                        color = Color.Gray
                                                                )
                                                        }

                                                        Spacer(modifier = Modifier.height(16.dp))

                                                        Text(
                                                                text =
                                                                        "Estado: ${eventDetail.estado}",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyMedium,
                                                                fontWeight = FontWeight.Bold,
                                                                color =
                                                                        when (eventDetail.estado
                                                                                        .uppercase()
                                                                        ) {
                                                                                "EN_PROGRESO" ->
                                                                                        Color(
                                                                                                0xFF02BB94
                                                                                        )
                                                                                "FINALIZADO" ->
                                                                                        Color(
                                                                                                0xFF757575
                                                                                        )
                                                                                "PENDIENTE" ->
                                                                                        Color(
                                                                                                0xFFFFA726
                                                                                        )
                                                                                else ->
                                                                                        Color(
                                                                                                0xFF1976D2
                                                                                        )
                                                                        }
                                                        )

                                                        if (eventDetail.estado.uppercase() ==
                                                                        "FINALIZADO" ||
                                                                        eventDetail.estado
                                                                                .uppercase() ==
                                                                                "TERMINADO"
                                                        ) {
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        16.dp
                                                                                )
                                                                )
                                                                Card(
                                                                        colors =
                                                                                CardDefaults
                                                                                        .cardColors(
                                                                                                containerColor =
                                                                                                        Color(
                                                                                                                0xFFFEE2E2
                                                                                                        )
                                                                                        ),
                                                                        modifier =
                                                                                Modifier.fillMaxWidth(),
                                                                        shape =
                                                                                RoundedCornerShape(
                                                                                        8.dp
                                                                                )
                                                                ) {
                                                                        Row(
                                                                                modifier =
                                                                                        Modifier.padding(
                                                                                                16.dp
                                                                                        ),
                                                                                verticalAlignment =
                                                                                        Alignment
                                                                                                .CenterVertically
                                                                        ) {
                                                                                Icon(
                                                                                        imageVector =
                                                                                                Icons.Default
                                                                                                        .DateRange,
                                                                                        contentDescription =
                                                                                                null,
                                                                                        tint =
                                                                                                Color(
                                                                                                        0xFFD32F2F
                                                                                                )
                                                                                )
                                                                                Spacer(
                                                                                        modifier =
                                                                                                Modifier.width(
                                                                                                        8.dp
                                                                                                )
                                                                                )
                                                                                Text(
                                                                                        text =
                                                                                                "Este evento ha terminado. No se pueden realizar más acciones.",
                                                                                        color =
                                                                                                Color(
                                                                                                        0xFFD32F2F
                                                                                                ),
                                                                                        style =
                                                                                                MaterialTheme
                                                                                                        .typography
                                                                                                        .bodyMedium,
                                                                                        fontWeight =
                                                                                                FontWeight
                                                                                                        .Medium
                                                                                )
                                                                        }
                                                                }
                                                        }

                                                        Spacer(modifier = Modifier.height(24.dp))

                                                        val eventStatusUpper =
                                                                eventDetail.estado.uppercase()

                                                        if (eventStatusUpper == "PENDIENTE" ||
                                                                        eventStatusUpper ==
                                                                                "DISPONIBLE"
                                                        ) {
                                                                Button(
                                                                        onClick = {
                                                                                viewModel
                                                                                        .changeEventStatus(
                                                                                                "TRANSCURRIENDO"
                                                                                        )
                                                                        },
                                                                        colors =
                                                                                ButtonDefaults
                                                                                        .buttonColors(
                                                                                                containerColor =
                                                                                                        Color(
                                                                                                                0xFF02BB94
                                                                                                        )
                                                                                        ),
                                                                        shape =
                                                                                RoundedCornerShape(
                                                                                        8.dp
                                                                                ),
                                                                        modifier =
                                                                                Modifier.fillMaxWidth()
                                                                ) {
                                                                        Text(
                                                                                "Iniciar Evento",
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .Bold,
                                                                                fontSize = 16.sp
                                                                        )
                                                                }
                                                        }

                                                        if (eventStatusUpper == "EN_PROGRESO" ||
                                                                        eventStatusUpper ==
                                                                                "TRANSCURRIENDO"
                                                        ) {
                                                                Button(
                                                                        onClick = {
                                                                                viewModel
                                                                                        .changeEventStatus(
                                                                                                "FINALIZADO"
                                                                                        )
                                                                        },
                                                                        colors =
                                                                                ButtonDefaults
                                                                                        .buttonColors(
                                                                                                containerColor =
                                                                                                        Color(
                                                                                                                0xFFD32F2F
                                                                                                        )
                                                                                        ),
                                                                        shape =
                                                                                RoundedCornerShape(
                                                                                        8.dp
                                                                                ),
                                                                        modifier =
                                                                                Modifier.fillMaxWidth()
                                                                ) {
                                                                        Text(
                                                                                "Finalizar Evento",
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .Bold,
                                                                                fontSize = 16.sp
                                                                        )
                                                                }
                                                        }

                                                        Spacer(modifier = Modifier.height(24.dp))

                                                        Text(
                                                                text =
                                                                        "Lista de Asistentes (${eventDetail.asistentes.size})",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .titleMedium,
                                                                fontWeight = FontWeight.Bold,
                                                                color = MuniColors.badgeBlueText
                                                        )

                                                        Spacer(modifier = Modifier.height(16.dp))

                                                        if (eventDetail.asistentes.isEmpty()) {
                                                                Card(
                                                                        modifier =
                                                                                Modifier.fillMaxWidth()
                                                                                        .padding(
                                                                                                vertical =
                                                                                                        16.dp
                                                                                        ),
                                                                        colors =
                                                                                CardDefaults
                                                                                        .cardColors(
                                                                                                containerColor =
                                                                                                        Color(
                                                                                                                0xFFF5F5F5
                                                                                                        )
                                                                                        )
                                                                ) {
                                                                        Box(
                                                                                modifier =
                                                                                        Modifier.fillMaxWidth()
                                                                                                .padding(
                                                                                                        24.dp
                                                                                                ),
                                                                                contentAlignment =
                                                                                        Alignment
                                                                                                .Center
                                                                        ) {
                                                                                Text(
                                                                                        text =
                                                                                                "No hay asistentes registrados",
                                                                                        style =
                                                                                                MaterialTheme
                                                                                                        .typography
                                                                                                        .bodyMedium,
                                                                                        color =
                                                                                                Color.Gray,
                                                                                        textAlign =
                                                                                                TextAlign
                                                                                                        .Center
                                                                                )
                                                                        }
                                                                }
                                                        } else {
                                                                val isEventFinished =
                                                                        eventDetail.estado
                                                                                .uppercase() ==
                                                                                "FINALIZADO" ||
                                                                                eventDetail.estado
                                                                                        .uppercase() ==
                                                                                        "TERMINADO"
                                                                eventDetail.asistentes.forEach {
                                                                        attendee ->
                                                                        AttendeeCard(
                                                                                attendee = attendee,
                                                                                isReadOnly =
                                                                                        isEventFinished ||
                                                                                                eventDetail
                                                                                                        .estado
                                                                                                        .uppercase() !=
                                                                                                        "TRANSCURRIENDO",
                                                                                onAttendanceChanged = {
                                                                                        isPresent ->
                                                                                        if (!isEventFinished &&
                                                                                                        eventDetail
                                                                                                                .estado
                                                                                                                .uppercase() ==
                                                                                                                "TRANSCURRIENDO"
                                                                                        ) {
                                                                                                val newStatus =
                                                                                                        if (isPresent
                                                                                                        )
                                                                                                                "PRESENTE"
                                                                                                        else
                                                                                                                "AUSENTE"
                                                                                                viewModel
                                                                                                        .updateAttendanceStatus(
                                                                                                                attendee.idInscripcion,
                                                                                                                newStatus
                                                                                                        )
                                                                                        }
                                                                                }
                                                                        )
                                                                        Spacer(
                                                                                modifier =
                                                                                        Modifier.height(
                                                                                                8.dp
                                                                                        )
                                                                        )
                                                                }
                                                        }
                                                }
                                        }
                                }
                        }
                }
        }
}

@Composable
fun AttendeeCard(
        attendee: AttendeeInfo,
        isReadOnly: Boolean = false,
        onAttendanceChanged: (Boolean) -> Unit
) {
        Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
                Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                ) {
                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = attendee.nombreCompleto,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                        text = attendee.correo,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                        text = "Estado: ${attendee.estadoAsistencia}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color =
                                                when (attendee.estadoAsistencia.uppercase()) {
                                                        "PRESENTE" -> MuniColors.accentEmerald
                                                        "AUSENTE" -> MuniColors.errorRed
                                                        else -> MuniColors.warningOrange
                                                },
                                        fontWeight = FontWeight.Medium
                                )
                        }

                        Checkbox(
                                checked = attendee.estadoAsistencia.uppercase() == "PRESENTE",
                                onCheckedChange = if (isReadOnly) null else onAttendanceChanged,
                                enabled = !isReadOnly,
                                colors =
                                        CheckboxDefaults.colors(
                                                checkedColor = MuniColors.accentEmerald,
                                                uncheckedColor =
                                                        MuniColors.accentEmerald.copy(alpha = 0.6f),
                                                disabledUncheckedColor =
                                                        Color.Gray.copy(alpha = 0.6f)
                                        )
                        )
                }
        }
}
