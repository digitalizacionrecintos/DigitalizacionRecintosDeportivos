package org.example.project.presentation.features.cursos.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import org.example.project.domain.repository.CursoRepository
import org.example.project.domain.usecase.curso.CheckCursoEnrollmentUseCase
import org.example.project.domain.usecase.curso.EnrollCursoUseCase
import org.example.project.domain.usecase.curso.GetCursoDetailUseCase
import org.example.project.presentation.components.MuniTopBarLogo
import org.example.project.presentation.theme.MuniColors
import org.example.project.presentation.theme.MuniGradients
import org.example.project.presentation.theme.MuniSpacing
import org.koin.compose.koinInject

data class CursoDetailScreen(val cursoId: Int) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val getCursoDetailUseCase: GetCursoDetailUseCase = koinInject()
        val enrollCursoUseCase: EnrollCursoUseCase = koinInject()
        val checkEnrollmentUseCase: CheckCursoEnrollmentUseCase = koinInject()
        val cursoRepository: CursoRepository = koinInject()
        val viewModel = remember {
            CursoDetailViewModel(
                getCursoDetailUseCase = getCursoDetailUseCase,
                enrollCursoUseCase = enrollCursoUseCase,
                checkEnrollmentUseCase = checkEnrollmentUseCase,
                cursoRepository = cursoRepository
            )
        }
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        LaunchedEffect(cursoId) { viewModel.loadCurso(cursoId) }
        LaunchedEffect(state.enrollmentMessage) {
            state.enrollmentMessage?.let {
                snackbarHostState.showSnackbar(it)
                viewModel.onEvent(CursoDetailEvent.ClearMessage)
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            CursoDetailContent(
                state = state,
                onBackClick = { navigator.pop() },
                onEvent = viewModel::onEvent
            )
            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

@Composable
fun CursoDetailContent(
    state: CursoDetailState,
    onBackClick: () -> Unit,
    onEvent: (CursoDetailEvent) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().background(MuniColors.surfaceCard)) {
        Row(
            modifier = Modifier.fillMaxWidth().background(MuniGradients.header)
                .statusBarsPadding().padding(horizontal = MuniSpacing.lg, vertical = MuniSpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver",
                tint = Color.White, modifier = Modifier.size(24.dp).clickable { onBackClick() })
            Spacer(Modifier.width(MuniSpacing.lg))
            MuniTopBarLogo()
            Spacer(Modifier.width(MuniSpacing.lg))
            Column {
                Text("Detalle curso", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                Text("Información completa", color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodySmall)
            }
        }

        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MuniColors.primaryBlue)
                }
            }
            state.curso == null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se pudo cargar el curso", color = MuniColors.mediumGray)
                }
            }
            else -> {
                val curso = state.curso
                Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(curso.nombre, fontSize = 24.sp,
                            color = Color(0xFF0D47A1), fontWeight = FontWeight.Black, lineHeight = 28.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(curso.descripcion, fontSize = 14.sp, color = Color.Gray, lineHeight = 20.sp)

                        Spacer(Modifier.height(16.dp))

                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FF))) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                InfoRow(Icons.Default.DateRange, "Inicio: ${curso.fechaInicio}", Color(0xFF1976D2))
                                InfoRow(Icons.Default.DateRange, "Fin: ${curso.fechaFin}", Color(0xFF1976D2))
                                curso.cupo?.let { InfoRow(Icons.Default.People, "Cupo: $it", Color(0xFF02BB94)) }
                                curso.recinto?.nombre?.let { InfoRow(Icons.Default.LocationOn, "Recinto: $it", Color(0xFF00BCD4)) }
                                curso.categoria?.nombre?.let { InfoRow(Icons.Default.Label, "Categoría: $it", Color(0xFFFFCB4A)) }
                            }
                        }

                        if (curso.horarios.isNotEmpty()) {
                            Spacer(Modifier.height(16.dp))
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Horarios", fontWeight = FontWeight.Bold, color = Color(0xFF85690F))
                                    Spacer(Modifier.height(8.dp))
                                    curso.horarios.forEach { horario ->
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                                            Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF85690F))
                                            Spacer(Modifier.width(6.dp))
                                            Text("${horario.dia}: ${horario.horaInicio} - ${horario.horaFin}",
                                                style = MaterialTheme.typography.bodySmall, color = Color(0xFF85690F))
                                        }
                                    }
                                }
                            }
                        }

                        if (curso.sesiones.isNotEmpty()) {
                            Spacer(Modifier.height(16.dp))
                            Text("Sesiones del curso", fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                            Spacer(Modifier.height(8.dp))
                            curso.sesiones.forEach { sesion ->
                                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(sesion.title, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
                                        Text(sesion.date, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        val yaInscrito = state.enrollmentStatus?.inscrito == true
                        if (state.isCheckingEnrollment) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                CircularProgressIndicator(color = MuniColors.primaryBlue, modifier = Modifier.size(24.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Verificando inscripción...", color = MuniColors.mediumGray, style = MaterialTheme.typography.bodySmall)
                            }
                        } else if (yaInscrito) {
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF02BB94))
                                        Spacer(Modifier.width(8.dp))
                                        Text("Ya estás inscrito en este curso", color = Color(0xFF02BB94), fontWeight = FontWeight.Medium)
                                    }
                                    if (state.participantesRegistrados.isNotEmpty()) {
                                        Spacer(Modifier.height(12.dp))
                                        Text("Participantes registrados:", fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF333333), style = MaterialTheme.typography.bodyMedium)
                                        Spacer(Modifier.height(6.dp))
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            state.participantesRegistrados.forEachIndexed { i, p ->
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Person, contentDescription = null,
                                                        modifier = Modifier.size(16.dp), tint = Color(0xFF02BB94))
                                                    Spacer(Modifier.width(6.dp))
                                                    Text("${i + 1}. ${p.nombre} ${p.apellido}${if (p.edad.isNotBlank()) " (${p.edad} años)" else ""}",
                                                        style = MaterialTheme.typography.bodySmall, color = Color(0xFF555555))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (curso.estado.uppercase() in listOf("PUBLICADO", "EN_PROGRESO", "BORRADOR")) {
                            Text("Participantes", fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                            Spacer(Modifier.height(8.dp))
                            state.participantes.forEachIndexed { index, p ->
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        OutlinedTextField(value = p.nombre, onValueChange = { onEvent(CursoDetailEvent.UpdateParticipanteNombre(index, it)) },
                                            label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                                        Spacer(Modifier.height(4.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            OutlinedTextField(value = p.apellido, onValueChange = { onEvent(CursoDetailEvent.UpdateParticipanteApellido(index, it)) },
                                                label = { Text("Apellido") }, modifier = Modifier.weight(1f), singleLine = true)
                                            OutlinedTextField(value = p.edad, onValueChange = { onEvent(CursoDetailEvent.UpdateParticipanteEdad(index, it)) },
                                                label = { Text("Edad") }, modifier = Modifier.width(80.dp), singleLine = true)
                                        }
                                    }
                                    if (state.participantes.size > 1) {
                                        IconButton(onClick = { onEvent(CursoDetailEvent.RemoveParticipante(index)) }) {
                                            Icon(Icons.Default.RemoveCircle, contentDescription = "Eliminar", tint = Color.Red)
                                        }
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                            }
                            TextButton(onClick = { onEvent(CursoDetailEvent.AddParticipante(state.participantes.size)) }) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Agregar participante")
                            }
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = { onEvent(CursoDetailEvent.Enroll) },
                                enabled = !state.isEnrolling && state.participantes.all { it.nombre.isNotBlank() && it.apellido.isNotBlank() },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF02BB94))
                            ) {
                                if (state.isEnrolling) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                else Text("Inscribirse al curso", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = color)
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF333333))
    }
}
