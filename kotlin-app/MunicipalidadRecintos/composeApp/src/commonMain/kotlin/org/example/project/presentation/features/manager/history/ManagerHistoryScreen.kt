package org.example.project.presentation.features.manager.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
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
import municipalidadrecintos.composeapp.generated.resources.Res
import municipalidadrecintos.composeapp.generated.resources.logo_muni_arica
import org.example.project.domain.usecase.event.GetManagerEventsUseCase
import org.example.project.presentation.components.MuniTopBarLogo
import org.example.project.presentation.theme.MuniColors
import org.example.project.presentation.theme.MuniGradients
import org.example.project.presentation.theme.MuniShapes
import org.example.project.presentation.theme.MuniSpacing
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

class ManagerHistoryScreen : Screen {
        @Composable
        override fun Content() {
                val getManagerEventsUseCase: GetManagerEventsUseCase = koinInject<GetManagerEventsUseCase>()
                val viewModel = remember { ManagerHistoryViewModel(getManagerEventsUseCase) }
                val state by viewModel.state.collectAsState()
                val years by viewModel.years.collectAsState()
                val categories by viewModel.categories.collectAsState()

                var selectedEvent by remember { mutableStateOf<ManagerHistoryEvent?>(null) }

                val months =
                        listOf(
                                "Todos" to null,
                                "Enero" to 1,
                                "Febrero" to 2,
                                "Marzo" to 3,
                                "Abril" to 4,
                                "Mayo" to 5,
                                "Junio" to 6,
                                "Julio" to 7,
                                "Agosto" to 8,
                                "Septiembre" to 9,
                                "Octubre" to 10,
                                "Noviembre" to 11,
                                "Diciembre" to 12
                        )

                Column(modifier = Modifier.fillMaxSize().background(MuniColors.offWhite)) {

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
                                                text = "Historial",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.titleLarge
                                        )
                                        Text(
                                                text = "Eventos finalizados",
                                                color = Color.White.copy(alpha = 0.9f),
                                                style = MaterialTheme.typography.bodySmall
                                        )
                                }
                        }

                        Column(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .background(Color.White)
                                                .padding(16.dp)
                        ) {

                                TextField(
                                        value = viewModel.searchQuery,
                                        onValueChange = { viewModel.onSearchQueryChange(it) },
                                        placeholder = {
                                                Text("Buscar eventos...", color = Color(0xFF9CA3AF))
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
                                                        unfocusedContainerColor = Color(0xFFF3F4F6),
                                                        focusedContainerColor = Color(0xFFF3F4F6),
                                                        unfocusedIndicatorColor = Color.Transparent,
                                                        focusedIndicatorColor = Color.Transparent
                                                ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {

                                        org.example.project.presentation.features.manager.events
                                                .CompactDropdown(
                                                        label = "Año",
                                                        options = years.map { it.toString() },
                                                        selectedOption =
                                                                viewModel.selectedYear?.toString()
                                                                        ?: years.firstOrNull()
                                                                                ?.toString()
                                                                                ?: "",
                                                        onOptionSelected = {
                                                                it.toIntOrNull()?.let { year ->
                                                                        viewModel.onYearSelected(
                                                                                year
                                                                        )
                                                                }
                                                        },
                                                        modifier = Modifier.weight(1f)
                                                )

                                        org.example.project.presentation.features.manager.events
                                                .CompactDropdown(
                                                        label = "Mes",
                                                        options = months.map { it.first },
                                                        selectedOption =
                                                                months
                                                                        .find {
                                                                                it.second ==
                                                                                        viewModel
                                                                                                .selectedMonth
                                                                        }
                                                                        ?.first
                                                                        ?: "Todos",
                                                        onOptionSelected = { selected ->
                                                                viewModel.onMonthSelected(
                                                                        months
                                                                                .find {
                                                                                        it.first ==
                                                                                                selected
                                                                                }
                                                                                ?.second
                                                                )
                                                        },
                                                        modifier = Modifier.weight(1f)
                                                )

                                        org.example.project.presentation.features.manager.events
                                                .CompactDropdown(
                                                        label = "Categoría",
                                                        options =
                                                                listOf("Todas") +
                                                                        categories.map {
                                                                                it.nombre
                                                                        },
                                                        selectedOption =
                                                                categories
                                                                        .find {
                                                                                it.id ==
                                                                                        viewModel
                                                                                                .selectedCategoryId
                                                                        }
                                                                        ?.nombre
                                                                        ?: "Todas",
                                                        onOptionSelected = { selected ->
                                                                viewModel.onCategorySelected(
                                                                        if (selected == "Todas")
                                                                                null
                                                                        else
                                                                                categories
                                                                                        .find {
                                                                                                it.nombre ==
                                                                                                        selected
                                                                                        }
                                                                                        ?.id
                                                                )
                                                        },
                                                        modifier = Modifier.weight(1f)
                                                )
                                }
                        }

                        when (val currentState = state) {
                                is ManagerHistoryState.Loading -> {
                                        Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                        ) { CircularProgressIndicator(color = Color(0xFF0D47A1)) }
                                }
                                is ManagerHistoryState.Error -> {
                                        Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                Text(
                                                        text = currentState.message,
                                                        color = Color.Red,
                                                        modifier = Modifier.padding(16.dp)
                                                )
                                        }
                                }
                                is ManagerHistoryState.Success -> {
                                        if (currentState.events.isEmpty()) {
                                                Box(
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentAlignment = Alignment.Center
                                                ) {
                                                        Text(
                                                                "No se encontraron eventos finalizados con estos filtros.",
                                                                color = Color.Gray
                                                        )
                                                }
                                        } else {
                                                LazyColumn(
                                                        contentPadding =
                                                                PaddingValues(
                                                                        horizontal = 16.dp,
                                                                        vertical = 8.dp
                                                                ),
                                                        verticalArrangement =
                                                                Arrangement.spacedBy(12.dp),
                                                        modifier = Modifier.fillMaxSize()
                                                ) {
                                                        items(currentState.events) { event ->
                                                                ManagerHistoryCard(
                                                                        event = event,
                                                                        onClick = {
                                                                                selectedEvent =
                                                                                        event
                                                                        }
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }
                }

                selectedEvent?.let { event ->
                        AttendeesModal(event = event, onDismiss = { selectedEvent = null })
                }
        }
}

@Composable
fun ManagerHistoryCard(event: ManagerHistoryEvent, onClick: () -> Unit) {
        Card(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
        ) {
                Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                ) {

                        Box(
                                modifier =
                                        Modifier.size(48.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFEEEEEE)),
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(24.dp)
                                )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = event.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Color(0xFFE3F2FD),
                                                modifier = Modifier.padding(end = 6.dp)
                                        ) {
                                                Text(
                                                        text = event.categoryName,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = Color(0xFF1565C0),
                                                        modifier =
                                                                Modifier.padding(
                                                                        horizontal = 6.dp,
                                                                        vertical = 2.dp
                                                                )
                                                )
                                        }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                                Icons.Default.DateRange,
                                                null,
                                                tint = Color.Gray,
                                                modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                                text = event.date,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray
                                        )
                                }
                        }
                }
        }
}

