package org.example.project.presentation.features.events.detail

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import org.koin.compose.koinInject
import municipalidadrecintos.composeapp.generated.resources.Res
import municipalidadrecintos.composeapp.generated.resources.logo_muni_arica
import org.example.project.domain.usecase.event.EnrollUserUseCase
import org.example.project.domain.usecase.event.GetEventDetailUseCase
import org.example.project.domain.repository.EventRepository
import org.example.project.getPlatform
import org.example.project.presentation.components.MuniTopBarLogo
import org.example.project.presentation.theme.MuniColors
import org.example.project.presentation.theme.MuniGradients
import org.example.project.presentation.theme.MuniSpacing
import org.jetbrains.compose.resources.painterResource

data class EventDetailScreen(val eventId: String) : Screen {

        @Composable
        override fun Content() {

                val navigator = LocalNavigator.currentOrThrow
                val getEventDetailUseCase: GetEventDetailUseCase = koinInject()
                val enrollUserUseCase: EnrollUserUseCase = koinInject()
                val eventRepository: EventRepository = koinInject()
                val viewModel = remember {
                    EventDetailViewModel(
                        getEventDetailUseCase = getEventDetailUseCase,
                        enrollUserUseCase = enrollUserUseCase,
                        eventRepository = eventRepository
                    )
                }
                val state by viewModel.state.collectAsState()

                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(eventId) {
                        viewModel.loadEvent(eventId)
                        viewModel.checkTutorialStatus()
                }

                LaunchedEffect(state.feedbackMessage) {
                        state.feedbackMessage?.let {
                                snackbarHostState.showSnackbar(it)
                                viewModel.onEvent(EventDetailEvent.ClearFeedback)
                        }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                        EventDetailScreenContent(
                                state = state,
                                onBackClick = { navigator.pop() },
                                onEvent = viewModel::onEvent,
                                onDismissTooltip = { viewModel.dismissTooltip() }
                        )

                        SnackbarHost(
                                hostState = snackbarHostState,
                                modifier = Modifier.align(Alignment.BottomCenter)
                        )
        }
}
}

@Composable
fun EventDetailScreenContent(
        state: EventDetailState,
        onBackClick: () -> Unit,
        onEvent: (EventDetailEvent) -> Unit,
        onDismissTooltip: () -> Unit
) {
        Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize().background(MuniColors.surfaceCard)) {

                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .background(MuniGradients.header)
                                                .statusBarsPadding()
                                                .padding(horizontal = MuniSpacing.lg, vertical = MuniSpacing.sm),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Volver",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp).clickable { onBackClick() }
                                )

                                Spacer(modifier = Modifier.width(MuniSpacing.lg))

                                MuniTopBarLogo()

                                Spacer(modifier = Modifier.width(MuniSpacing.lg))

                                Column {
                                        Text(
                                                text = "Detalle evento",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.titleLarge
                                        )
                                        Text(
                                                text = "Información completa",
                                                color = Color.White.copy(alpha = 0.9f),
                                                style = MaterialTheme.typography.bodySmall
                                        )
                                }
                        }

                        when {
                                state.isLoading -> {

                                        Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                Column(
                                                        horizontalAlignment =
                                                                Alignment.CenterHorizontally
                                                ) {
                                                        CircularProgressIndicator(
                                                                color = MuniColors.primaryBlue
                                                        )
                                                        Spacer(modifier = Modifier.height(MuniSpacing.lg))
                                                        Text(
                                                                "Cargando evento...",
                                                                color = MuniColors.mediumGray,
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyMedium
                                                        )
                                                }
                                        }
                                }
                                state.event == null -> {

                                        Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                Column(
                                                        horizontalAlignment =
                                                                Alignment.CenterHorizontally,
                                                        modifier = Modifier.padding(16.dp)
                                                ) {
                                                        Text(
                                                                "No se pudo cargar el evento",
                                                                color = Color(0xFF043CC7),
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .titleMedium,
                                                                fontWeight = FontWeight.Bold
                                                        )
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        Text(
                                                                "Por favor, intenta nuevamente.",
                                                                color = Color.Gray,
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyMedium
                                                        )
                                                }
                                        }
                                }
                                else -> {

                                        val event = state.event

                                        Column(modifier = Modifier.weight(1f)) {

                                                Column(
                                                        modifier =
                                                                Modifier.weight(1f)
                                                                        .verticalScroll(
                                                                                rememberScrollState()
                                                                        )
                                                ) {

                                                        if (!event.imagenUrl.isNullOrBlank()) {
                                                                coil3.compose.AsyncImage(
                                                                        model = event.imagenUrl,
                                                                        contentDescription =
                                                                                "Imagen Evento",
                                                                        modifier =
                                                                                Modifier.height(
                                                                                                160.dp
                                                                                        )
                                                                                        .fillMaxWidth(),
                                                                        contentScale =
                                                                                ContentScale.Crop
                                                                )
                                                        } else {
                                                                Box(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                                160.dp
                                                                                        )
                                                                                        .fillMaxWidth()
                                                                                        .background(
                                                                                                Color.Gray
                                                                                        ),
                                                                        contentAlignment =
                                                                                Alignment.Center
                                                                ) {
                                                                        Icon(
                                                                                imageVector =
                                                                                        Icons.Default
                                                                                                .Image,
                                                                                contentDescription =
                                                                                        "Sin imagen",
                                                                                tint = Color.White,
                                                                                modifier =
                                                                                        Modifier.size(
                                                                                                48.dp
                                                                                        )
                                                                        )
                                                                }
                                                        }

                                                        Column(
                                                                modifier =
                                                                        Modifier.padding(
                                                                                horizontal = 16.dp,
                                                                                vertical = 12.dp
                                                                        )
                                                        ) {

                                                                Text(
                                                                        text = event.title,
                                                                        fontSize = 24.sp,
                                                                        color = Color(0xFF0D47A1),
                                                                        fontWeight =
                                                                                FontWeight.Black,
                                                                        lineHeight = 28.sp
                                                                )

                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        8.dp
                                                                                )
                                                                )

                                                                Text(
                                                                        text = event.description,
                                                                        fontSize = 14.sp,
                                                                        color = Color.Gray,
                                                                        lineHeight = 20.sp,
                                                                        modifier =
                                                                                Modifier.padding(
                                                                                        bottom =
                                                                                                8.dp
                                                                                )
                                                                )

                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        8.dp
                                                                                )
                                                                )

                                                                Column(
                                                                        verticalArrangement =
                                                                                Arrangement
                                                                                        .spacedBy(
                                                                                                12.dp
                                                                                        ),
                                                                        modifier =
                                                                                Modifier.fillMaxWidth()
                                                                ) {

                                                                        event.categoria?.let { cat
                                                                                ->
                                                                                CategoryCard(
                                                                                        categoryName =
                                                                                                cat.nombre
                                                                                )
                                                                        }

                                                                        val encargadoName =
                                                                                if (event.encargado !=
                                                                                                null
                                                                                ) {
                                                                                        "${event.encargado.nombre} ${event.encargado.apellido}"
                                                                                } else {
                                                                                        event.organizerName
                                                                                                .replace(
                                                                                                        "Encargado ",
                                                                                                        ""
                                                                                                )
                                                                                }
                                                                        EncargadoCard(
                                                                                organizerName =
                                                                                        encargadoName
                                                                        )

                                                                        event.cupoMaximo?.let { max
                                                                                ->
                                                                                CapacityCard(
                                                                                        maxCapacity =
                                                                                                max
                                                                                )
                                                                        }
                                                                }

                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        12.dp
                                                                                )
                                                                )

                                                                Row(
                                                                        horizontalArrangement =
                                                                                Arrangement
                                                                                        .spacedBy(
                                                                                                8.dp
                                                                                        ),
                                                                        modifier =
                                                                                Modifier.fillMaxWidth()
                                                                ) {

                                                                        Row(
                                                                                verticalAlignment =
                                                                                        Alignment
                                                                                                .CenterVertically,
                                                                                modifier =
                                                                                        Modifier.clip(
                                                                                                        RoundedCornerShape(
                                                                                                                20.dp
                                                                                                        )
                                                                                                )
                                                                                                .background(
                                                                                                        Color(
                                                                                                                0xFFE3F2FD
                                                                                                        )
                                                                                                )
                                                                                                .padding(
                                                                                                        horizontal =
                                                                                                                12.dp,
                                                                                                        vertical =
                                                                                                                8.dp
                                                                                                )
                                                                        ) {
                                                                                Icon(
                                                                                        Icons.Default
                                                                                                .DateRange,
                                                                                        contentDescription =
                                                                                                null,
                                                                                        tint =
                                                                                                Color(
                                                                                                        0xFF1976D2
                                                                                                ),
                                                                                        modifier =
                                                                                                Modifier.size(
                                                                                                        20.dp
                                                                                                )
                                                                                )
                                                                                Spacer(
                                                                                        modifier =
                                                                                                Modifier.width(
                                                                                                        6.dp
                                                                                                )
                                                                                )
                                                                                Text(
                                                                                        text =
                                                                                                event.date,
                                                                                        fontSize =
                                                                                                14.sp,
                                                                                        fontWeight =
                                                                                                FontWeight
                                                                                                        .Medium,
                                                                                        color =
                                                                                                Color(
                                                                                                        0xFF1976D2
                                                                                                )
                                                                                )
                                                                        }

                                                                        Row(
                                                                                verticalAlignment =
                                                                                        Alignment
                                                                                                .CenterVertically,
                                                                                modifier =
                                                                                        Modifier.clip(
                                                                                                        RoundedCornerShape(
                                                                                                                20.dp
                                                                                                        )
                                                                                                )
                                                                                                .background(
                                                                                                        Color(
                                                                                                                0xFFE0F7FA
                                                                                                        )
                                                                                                )
                                                                                                .padding(
                                                                                                        horizontal =
                                                                                                                12.dp,
                                                                                                        vertical =
                                                                                                                8.dp
                                                                                                )
                                                                                                .weight(
                                                                                                        1f,
                                                                                                        fill =
                                                                                                                false
                                                                                                )
                                                                        ) {
                                                                                Icon(
                                                                                        Icons.Default
                                                                                                .LocationOn,
                                                                                        contentDescription =
                                                                                                null,
                                                                                        tint =
                                                                                                Color(
                                                                                                        0xFF00BCD4
                                                                                                ),
                                                                                        modifier =
                                                                                                Modifier.size(
                                                                                                        20.dp
                                                                                                )
                                                                                )
                                                                                Spacer(
                                                                                        modifier =
                                                                                                Modifier.width(
                                                                                                        6.dp
                                                                                                )
                                                                                )
                                                                                Text(
                                                                                        text =
                                                                                                event.location,
                                                                                        fontSize =
                                                                                                14.sp,
                                                                                        fontWeight =
                                                                                                FontWeight
                                                                                                        .Medium,
                                                                                        color =
                                                                                                Color(
                                                                                                        0xFF00BCD4
                                                                                                ),
                                                                                        maxLines = 1
                                                                                )
                                                                        }
                                                                }

                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        12.dp
                                                                                )
                                                                )

                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        12.dp
                                                                                )
                                                                )

                                                                Card(
                                                                        modifier =
                                                                                Modifier.fillMaxWidth(),
                                                                        shape =
                                                                                RoundedCornerShape(
                                                                                        12.dp
                                                                                ),
                                                                        colors =
                                                                                CardDefaults
                                                                                        .cardColors(
                                                                                                containerColor =
                                                                                                        Color(
                                                                                                                0xFFF0F4FF
                                                                                                        )
                                                                                        ),
                                                                        elevation =
                                                                                CardDefaults
                                                                                        .cardElevation(
                                                                                                0.dp
                                                                                        )
                                                                ) {
                                                                        Row(
                                                                                modifier =
                                                                                        Modifier.fillMaxWidth()
                                                                                                .padding(
                                                                                                        12.dp
                                                                                                ),
                                                                                verticalAlignment =
                                                                                        Alignment
                                                                                                .CenterVertically
                                                                        ) {

                                                                                Box(
                                                                                        modifier =
                                                                                                Modifier.size(
                                                                                                                44.dp
                                                                                                        )
                                                                                                        .clip(
                                                                                                                RoundedCornerShape(
                                                                                                                        10.dp
                                                                                                                )
                                                                                                        )
                                                                                                        .background(
                                                                                                                Color(
                                                                                                                        0xFF043CC7
                                                                                                                )
                                                                                                        ),
                                                                                        contentAlignment =
                                                                                                Alignment
                                                                                                        .Center
                                                                                ) {
                                                                                        Icon(
                                                                                                Icons.Default
                                                                                                        .LocationOn,
                                                                                                contentDescription =
                                                                                                        null,
                                                                                                tint =
                                                                                                        Color.White,
                                                                                                modifier =
                                                                                                        Modifier.size(
                                                                                                                24.dp
                                                                                                        )
                                                                                        )
                                                                                }

                                                                                Spacer(
                                                                                        modifier =
                                                                                                Modifier.width(
                                                                                                        12.dp
                                                                                                )
                                                                                )

                                                                                Column(
                                                                                        modifier =
                                                                                                Modifier.weight(
                                                                                                        1f
                                                                                                )
                                                                                ) {
                                                                                        Text(
                                                                                                text =
                                                                                                        "Ubicación del evento",
                                                                                                style =
                                                                                                        MaterialTheme
                                                                                                                .typography
                                                                                                                .labelSmall,
                                                                                                color =
                                                                                                        Color.Gray
                                                                                        )
                                                                                        Text(
                                                                                                text =
                                                                                                        event.location,
                                                                                                style =
                                                                                                        MaterialTheme
                                                                                                                .typography
                                                                                                                .bodyMedium,
                                                                                                fontWeight =
                                                                                                        FontWeight
                                                                                                                .Medium,
                                                                                                color =
                                                                                                        Color(
                                                                                                                0xFF333333
                                                                                                        )
                                                                                        )
                                                                                }

                                                                                Button(
                                                                                        onClick = {
                                                                                                val coords =
                                                                                                        event.recinto
                                                                                                                ?.coordenadasGPS
                                                                                                                ?: ""
                                                                                                if (coords.isNotBlank()
                                                                                                ) {
                                                                                                        try {
                                                                                                                val parts =
                                                                                                                        coords.split(
                                                                                                                                ","
                                                                                                                        )
                                                                                                                if (parts.size >=
                                                                                                                                2
                                                                                                                ) {
                                                                                                                        val lat =
                                                                                                                                parts[
                                                                                                                                                0]
                                                                                                                                        .trim()
                                                                                                                                        .toDoubleOrNull()
                                                                                                                                        ?: 0.0
                                                                                                                        val lng =
                                                                                                                                parts[
                                                                                                                                                1]
                                                                                                                                        .trim()
                                                                                                                                        .toDoubleOrNull()
                                                                                                                                        ?: 0.0
                                                                                                                        if (lat !=
                                                                                                                                        0.0 &&
                                                                                                                                        lng !=
                                                                                                                                                0.0
                                                                                                                        ) {
                                                                                                                                org.example
                                                                                                                                        .project
                                                                                                                                        .getPlatform()
                                                                                                                                        .openMaps(
                                                                                                                                                lat,
                                                                                                                                                lng,
                                                                                                                                                event.location
                                                                                                                                        )
                                                                                                                        }
                                                                                                                }
                                                                                                        } catch (
                                                                                                                e:
                                                                                                                        Exception) {}
                                                                                                }
                                                                                        },
                                                                                        colors =
                                                                                                ButtonDefaults
                                                                                                        .buttonColors(
                                                                                                                containerColor =
                                                                                                                        Color(
                                                                                                                                0xFF043CC7
                                                                                                                        )
                                                                                                        ),
                                                                                        shape =
                                                                                                RoundedCornerShape(
                                                                                                        8.dp
                                                                                                ),
                                                                                        contentPadding =
                                                                                                PaddingValues(
                                                                                                        horizontal =
                                                                                                                12.dp,
                                                                                                        vertical =
                                                                                                                8.dp
                                                                                                )
                                                                                ) {
                                                                                        Text(
                                                                                                "Ver mapa",
                                                                                                fontSize =
                                                                                                        12.sp
                                                                                        )
                                                                                }
                                                                        }
                                                                }
                                                        }
                                                }

                                                        if (event.canEnroll) {
                                                                Text("Participantes", fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                                                                Spacer(Modifier.height(8.dp))

                                                                state.participantes.forEachIndexed { index, p ->
                                                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                                                                Column(modifier = Modifier.weight(1f)) {
                                                                                        OutlinedTextField(
                                                                                                value = p.nombre,
                                                                                                onValueChange = { onEvent(EventDetailEvent.UpdateParticipanteNombre(index, it)) },
                                                                                                label = { Text("Nombre") },
                                                                                                modifier = Modifier.fillMaxWidth(),
                                                                                                singleLine = true
                                                                                        )
                                                                                        Spacer(Modifier.height(4.dp))
                                                                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                                                                OutlinedTextField(
                                                                                                        value = p.apellido,
                                                                                                        onValueChange = { onEvent(EventDetailEvent.UpdateParticipanteApellido(index, it)) },
                                                                                                        label = { Text("Apellido") },
                                                                                                        modifier = Modifier.weight(1f),
                                                                                                        singleLine = true
                                                                                                )
                                                                                                OutlinedTextField(
                                                                                                        value = p.edad,
                                                                                                        onValueChange = { onEvent(EventDetailEvent.UpdateParticipanteEdad(index, it)) },
                                                                                                        label = { Text("Edad") },
                                                                                                        modifier = Modifier.width(80.dp),
                                                                                                        singleLine = true
                                                                                                )
                                                                                        }
                                                                                }
                                                                                if (state.participantes.size > 1) {
                                                                                        IconButton(onClick = { onEvent(EventDetailEvent.RemoveParticipante(index)) }) {
                                                                                                Icon(Icons.Default.RemoveCircle, contentDescription = "Eliminar", tint = Color.Red)
                                                                                        }
                                                                                }
                                                                        }
                                                                        Spacer(Modifier.height(8.dp))
                                                                }

                                                                TextButton(onClick = { onEvent(EventDetailEvent.AddParticipante(state.participantes.size)) }) {
                                                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                                                        Spacer(Modifier.width(4.dp))
                                                                        Text("Agregar participante")
                                                                }

                                                                Spacer(Modifier.height(12.dp))

                                                                Button(
                                                                        onClick = { onEvent(EventDetailEvent.Enroll) },
                                                                        enabled = !state.isEnrolling && state.participantes.all { it.nombre.isNotBlank() && it.apellido.isNotBlank() },
                                                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF02BB94))
                                                                ) {
                                                                        if (state.isEnrolling) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                                                        else Text("Inscribirse al evento", color = Color.White)
                                                                }
                                                        } else {
                                                                Spacer(Modifier.height(16.dp))
                                                                Card(
                                                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                                                        shape = RoundedCornerShape(12.dp),
                                                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0)),
                                                                        elevation = CardDefaults.cardElevation(0.dp)
                                                                ) {
                                                                        Row(
                                                                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                                                                verticalAlignment = Alignment.CenterVertically,
                                                                                horizontalArrangement = Arrangement.Center
                                                                        ) {
                                                                                val icon = if (event.enrolledStatus.contains("cupo", ignoreCase = true) ||
                                                                                        event.enrolledStatus.contains("lleno", ignoreCase = true)) Icons.Default.Warning else Icons.Default.CheckCircle
                                                                                Icon(icon, null, modifier = Modifier.size(20.dp), tint = Color(0xFF333333))
                                                                                Spacer(Modifier.width(8.dp))
                                                                                Text(
                                                                                        text = event.enrolledStatus.ifBlank { "Ya estás inscrito" },
                                                                                        fontSize = 16.sp,
                                                                                        fontWeight = FontWeight.Bold,
                                                                                        color = Color(0xFF333333)
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
