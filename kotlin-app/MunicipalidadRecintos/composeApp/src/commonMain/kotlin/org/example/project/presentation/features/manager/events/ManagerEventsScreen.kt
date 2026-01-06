package org.example.project.presentation.features.manager.events

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import municipalidadrecintos.composeapp.generated.resources.Res
import municipalidadrecintos.composeapp.generated.resources.logo_muni_arica
import org.example.project.data.remote.ApiService
import org.example.project.domain.manager.SessionManager
import org.example.project.domain.model.ManagerEvent
import org.example.project.domain.util.DateTimeUtils
import org.example.project.presentation.components.ShimmerEventCard
import org.example.project.presentation.features.manager.attendance.ManagerEventDetailScreen
import org.jetbrains.compose.resources.painterResource

class ManagerEventsScreen : Screen {
        @Composable
        override fun Content() {
                val navigator = LocalNavigator.currentOrThrow
                val apiService = remember { ApiService() }
                var allEvents by remember { mutableStateOf<List<ManagerEvent>>(emptyList()) }
                var isLoading by remember { mutableStateOf(true) }
                var managerId by remember { mutableStateOf(0) }

                var searchQuery by remember { mutableStateOf("") }
                var sortOrder by remember { mutableStateOf("Reciente") }
                var selectedYear by remember { mutableStateOf<String?>(null) }
                var selectedCategory by remember { mutableStateOf<String?>(null) }

                val availableYears =
                        remember(allEvents) {
                                allEvents
                                        .mapNotNull { event ->
                                                event.date
                                                        .substringAfterLast("/")
                                                        .take(4)
                                                        .toIntOrNull()
                                        }
                                        .distinct()
                                        .sorted()
                                        .map { it.toString() }
                        }

                val availableCategories =
                        remember(allEvents) {
                                allEvents.map { it.categoryName }.distinct().sorted()
                        }

                val displayedEvents =
                        remember(
                                searchQuery,
                                sortOrder,
                                selectedYear,
                                selectedCategory,
                                allEvents
                        ) {
                                var filtered = allEvents

                                if (searchQuery.isNotBlank()) {
                                        filtered =
                                                filtered.filter { event ->
                                                        event.title.contains(
                                                                searchQuery,
                                                                ignoreCase = true
                                                        ) ||
                                                                event.location.contains(
                                                                        searchQuery,
                                                                        ignoreCase = true
                                                                )
                                                }
                                }

                                if (selectedYear != null) {
                                        filtered =
                                                filtered.filter { event ->
                                                        event.date.contains(selectedYear!!)
                                                }
                                }

                                if (selectedCategory != null) {
                                        filtered =
                                                filtered.filter { event ->
                                                        event.categoryName == selectedCategory
                                                }
                                }

                                when (sortOrder) {
                                        "Reciente" -> filtered.sortedByDescending { it.date }
                                        "Antiguo" -> filtered.sortedBy { it.date }
                                        else -> filtered
                                }
                        }

                LaunchedEffect(Unit) {
                        try {
                                val currentUser = SessionManager.getCurrentUser()
                                val idEncargado =
                                        currentUser?.id
                                                ?: run {
                                                        return@LaunchedEffect
                                                }

                                managerId = idEncargado

                                val managerEventsDto =
                                        apiService.getManagerEventsByEncargado(idEncargado)

                                val mappedEvents =
                                        managerEventsDto
                                                .filter {
                                                        it.estado.uppercase() != "FINALIZADO" &&
                                                                it.estado.uppercase() != "TERMINADO"
                                                }
                                                .map { dto ->
                                                        ManagerEvent(
                                                                id = dto.idEvento.toString(),
                                                                title = dto.titulo,
                                                                date =
                                                                        "${DateTimeUtils.formatEventDate(dto.fechaInicio, dto.horaInicio)} - ${DateTimeUtils.formatTime(dto.horaFin)}",
                                                                location = dto.recinto?.nombre
                                                                                ?: "Sin recinto",
                                                                categoryName = dto.categoria?.nombre
                                                                                ?: "General",
                                                                imagenUrl =
                                                                        dto.imagenUrl?.takeIf {
                                                                                it.isNotBlank()
                                                                        }
                                                                                ?: dto.recinto
                                                                                        ?.imagenUrl
                                                        )
                                                }

                                allEvents = mappedEvents
                        } catch (e: Exception) {
                                e.printStackTrace()
                        } finally {
                                isLoading = false
                        }
                }

                Column(modifier = Modifier.fillMaxSize()) {

                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .background(
                                                        androidx.compose.ui.graphics.Brush
                                                                .horizontalGradient(
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
                                                .padding(horizontal = 20.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Image(
                                        painter = painterResource(Res.drawable.logo_muni_arica),
                                        contentDescription = "Logo",
                                        modifier =
                                                Modifier.size(48.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(Color.White.copy(alpha = 0.1f))
                                                        .padding(4.dp),
                                        contentScale = ContentScale.Fit
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                        Text(
                                                text = "Mis Eventos",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.titleLarge,
                                                fontSize = 22.sp
                                        )
                                        Text(
                                                text = "Gestiona tus eventos",
                                                color = Color.White.copy(alpha = 0.9f),
                                                style = MaterialTheme.typography.bodySmall,
                                                fontSize = 12.sp
                                        )
                                }
                        }

                        Column(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .weight(1f)
                                                .background(Color(0xFFF5F7FA))
                        ) {

                                Column(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .background(Color.White)
                                                        .padding(16.dp)
                                ) {

                                        TextField(
                                                value = searchQuery,
                                                onValueChange = { searchQuery = it },
                                                placeholder = {
                                                        Text(
                                                                "Buscar eventos...",
                                                                color = Color(0xFF9CA3AF)
                                                        )
                                                },
                                                leadingIcon = {
                                                        Icon(
                                                                Icons.Default.Search,
                                                                contentDescription = null,
                                                                tint = Color(0xFF6B7280),
                                                                modifier = Modifier.size(20.dp)
                                                        )
                                                },
                                                colors =
                                                        TextFieldDefaults.colors(
                                                                unfocusedContainerColor =
                                                                        Color(0xFFF3F4F6),
                                                                focusedContainerColor =
                                                                        Color(0xFFF3F4F6),
                                                                unfocusedIndicatorColor =
                                                                        Color.Transparent,
                                                                focusedIndicatorColor =
                                                                        Color.Transparent
                                                        ),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.fillMaxWidth()
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {

                                                CompactDropdown(
                                                        label = "Ordenar",
                                                        options = listOf("Reciente", "Antiguo"),
                                                        selectedOption = sortOrder,
                                                        onOptionSelected = { sortOrder = it },
                                                        modifier = Modifier.weight(1f)
                                                )

                                                CompactDropdown(
                                                        label = "Año",
                                                        options = listOf("Todos") + availableYears,
                                                        selectedOption = selectedYear ?: "Todos",
                                                        onOptionSelected = {
                                                                selectedYear =
                                                                        if (it == "Todos") null
                                                                        else it
                                                        },
                                                        modifier = Modifier.weight(1f)
                                                )

                                                CompactDropdown(
                                                        label = "Categoría",
                                                        options =
                                                                listOf("Todas") +
                                                                        availableCategories,
                                                        selectedOption = selectedCategory
                                                                        ?: "Todas",
                                                        onOptionSelected = {
                                                                selectedCategory =
                                                                        if (it == "Todas") null
                                                                        else it
                                                        },
                                                        modifier = Modifier.weight(1f)
                                                )
                                        }
                                }

                                LazyColumn(
                                        contentPadding =
                                                PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp),
                                        modifier = Modifier.fillMaxSize()
                                ) {
                                        if (isLoading) {
                                                items(3) { ShimmerEventCard() }
                                        } else if (displayedEvents.isEmpty()) {
                                                item {
                                                        Box(
                                                                modifier =
                                                                        Modifier.fillMaxWidth()
                                                                                .padding(
                                                                                        vertical =
                                                                                                32.dp
                                                                                ),
                                                                contentAlignment = Alignment.Center
                                                        ) {
                                                                Text(
                                                                        "No se encontraron eventos con los filtros aplicados",
                                                                        color = Color(0xFF6B7280),
                                                                        fontSize = 14.sp
                                                                )
                                                        }
                                                }
                                        } else {
                                                itemsIndexed(displayedEvents) { _, event ->
                                                        ModernEventCard(
                                                                event = event,
                                                                onClick = {
                                                                        navigator.push(
                                                                                ManagerEventDetailScreen(
                                                                                        eventId =
                                                                                                event.id
                                                                                                        .toInt(),
                                                                                        managerId =
                                                                                                managerId
                                                                                )
                                                                        )
                                                                }
                                                        )
                                                }
                                        }
                                }
                        }
                }
        }
}

@Composable
fun ModernEventCard(event: ManagerEvent, onClick: () -> Unit) {
        Card(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
                Column {

                        Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                                if (!event.imagenUrl.isNullOrBlank()) {
                                        AsyncImage(
                                                model = event.imagenUrl,
                                                contentDescription = "Imagen del evento",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                        )
                                } else {
                                        Box(
                                                modifier =
                                                        Modifier.fillMaxSize()
                                                                .background(Color.LightGray),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                Icon(
                                                        imageVector = Icons.Default.Image,
                                                        contentDescription = "Sin imagen",
                                                        tint = Color.Gray,
                                                        modifier = Modifier.size(48.dp)
                                                )
                                        }
                                }

                                Surface(
                                        modifier =
                                                Modifier.padding(12.dp).align(Alignment.TopStart),
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF040581)
                                ) {
                                        Text(
                                                text = event.categoryName,
                                                modifier =
                                                        Modifier.padding(
                                                                horizontal = 12.dp,
                                                                vertical = 6.dp
                                                        ),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                        )
                                }
                        }

                        Column(modifier = Modifier.padding(16.dp)) {

                                Text(
                                        text = event.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color(0xFF1A1A1A),
                                        lineHeight = 24.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                ) {
                                        Icon(
                                                Icons.Default.LocationOn,
                                                contentDescription = null,
                                                tint = Color(0xFF6B7280),
                                                modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                                text = event.location,
                                                fontSize = 13.sp,
                                                color = Color(0xFF6B7280),
                                                modifier = Modifier.weight(1f)
                                        )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                ) {
                                        Icon(
                                                Icons.Default.DateRange,
                                                contentDescription = null,
                                                tint = Color(0xFF6B7280),
                                                modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                                text = event.date,
                                                fontSize = 13.sp,
                                                color = Color(0xFF6B7280)
                                        )
                                }
                        }
                }
        }
}
