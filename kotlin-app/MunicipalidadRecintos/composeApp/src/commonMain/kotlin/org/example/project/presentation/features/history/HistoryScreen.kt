package org.example.project.presentation.features.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.School
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import org.example.project.domain.model.CursoHistorial
import org.example.project.domain.model.Event
import org.example.project.domain.model.ParticipanteHistorial
import org.example.project.domain.repository.UserRepository
import org.example.project.presentation.components.EmptyStateView
import org.example.project.presentation.components.MuniTopBarLogo
import org.example.project.presentation.theme.MuniColors
import org.example.project.presentation.theme.MuniGradients
import org.example.project.presentation.theme.MuniSpacing
import org.koin.compose.koinInject

class HistoryScreen : Screen {
    @Composable
    override fun Content() {
        val userRepository: UserRepository = koinInject()
        val viewModel = remember { HistoryViewModel(userRepository) }
        val state by viewModel.state.collectAsState()

        HistoryScreenContent(
            state = state,
            onRefresh = { viewModel.refresh() },
            onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
            onYearSelected = { viewModel.onYearSelected(it) },
            onMonthSelected = { viewModel.onMonthSelected(it) },
            onTabSelected = { viewModel.onTabSelected(it) },
            onEventSubTabSelected = { viewModel.onEventSubTabSelected(it) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HistoryScreenContent(
    state: HistoryState,
    onRefresh: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onYearSelected: (String?) -> Unit,
    onMonthSelected: (String?) -> Unit,
    onTabSelected: (Int) -> Unit,
    onEventSubTabSelected: (Int) -> Unit
) {
    val outerTabs = listOf("Eventos", "Cursos")
    val eventSubTabs = listOf("PrÃ³ximos", "Finalizados")

    Column(modifier = Modifier.fillMaxSize().background(MuniColors.offWhite)) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .background(MuniGradients.header)
                .statusBarsPadding()
                .padding(horizontal = MuniSpacing.lg, vertical = MuniSpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MuniTopBarLogo()
            Spacer(modifier = Modifier.width(MuniSpacing.lg))
            Column {
                Text("Mi historial", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                Text("Inscripciones", color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodySmall)
            }
        }

        
        TabRow(
            selectedTabIndex = state.selectedTab,
            containerColor = MuniColors.surfaceCard,
            contentColor = MuniColors.primaryBlue,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedTab]),
                    height = 3.dp, color = MuniColors.primaryBlue
                )
            }
        ) {
            outerTabs.forEachIndexed { index, title ->
                Tab(
                    selected = state.selectedTab == index,
                    onClick = { onTabSelected(index) },
                    text = { Text(title, fontWeight = FontWeight.SemiBold) }
                )
            }
        }

        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            TextField(
                value = state.searchQuery,
                onValueChange = { onSearchQueryChange(it) },
                placeholder = { Text("Buscar...", color = Color(0xFF6B7280)) },
                leadingIcon = {
                    Icon(Icons.Default.SearchOff, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(20.dp))
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            if (state.selectedTab == 0) {
                
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    eventSubTabs.forEachIndexed { index, title ->
                        FilterChip(
                            selected = state.selectedEventSubTab == index,
                            onClick = { onEventSubTabSelected(index) },
                            label = { Text(title, fontSize = 13.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MuniColors.primaryBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CompactDropdown(
                        label = "AÃ±o",
                        options = listOf("Todos") + state.availableYears,
                        selectedOption = state.selectedYear ?: "Todos",
                        onOptionSelected = { selected ->
                            onYearSelected(if (selected == "Todos") null else selected)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    CompactDropdown(
                        label = "Mes",
                        options = state.availableMonths.map { it.first },
                        selectedOption = state.availableMonths
                            .find { it.second?.toString()?.padStart(2, '0') == state.selectedMonth }
                            ?.first ?: "Todos",
                        onOptionSelected = { selected ->
                            val monthNumber = state.availableMonths.find { it.first == selected }?.second
                            onMonthSelected(monthNumber?.toString()?.padStart(2, '0'))
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        val eventsToShow = when (state.selectedEventSubTab) {
            0 -> state.upcomingEvents
            1 -> state.pastEvents
            else -> emptyList()
        }
        val cursosToShow = if (state.selectedTab == 1) state.cursos else emptyList()

        val pullRefreshState = rememberPullRefreshState(refreshing = state.isRefreshing, onRefresh = onRefresh)

        Box(Modifier.weight(1f).fillMaxWidth().pullRefresh(pullRefreshState)) {
            if (state.selectedTab == 1) {
                if (cursosToShow.isEmpty()) {
                    Box(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                        EmptyStateView(message = if (state.error != null) state.error else "No tienes cursos inscritos.", icon = Icons.Default.SearchOff)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(cursosToShow) { curso ->
                            CursoHistoryCard(curso = curso)
                        }
                    }
                }
            } else if (eventsToShow.isEmpty()) {
                Box(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                    EmptyStateView(message = if (state.error != null) state.error else "No hay eventos en esta categorÃ­a.", icon = Icons.Default.SearchOff)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(eventsToShow) { event ->
                        HistoryItemCard(event = event, isPast = state.selectedEventSubTab == 1)
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = state.isRefreshing, state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter), backgroundColor = Color.White, contentColor = Color(0xFF043CC7)
            )
        }
    }
}

@Composable
fun HistoryItemCard(event: Event, isPast: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(if (isPast) Color(0xFFEEEEEE) else Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPast) Icons.Default.CheckCircle else Icons.Default.Schedule,
                    contentDescription = null,
                    tint = if (isPast) Color.Gray else Color(0xFF043CC7),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = event.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (isPast) Color.Gray else Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = event.date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            if (!isPast) {
                Surface(color = Color(0xFFE0F7FA), shape = RoundedCornerShape(4.dp)) {
                    Text(text = "Inscrito", color = Color(0xFF006064), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
        }
    }
}

@Composable
fun CursoHistoryCard(curso: CursoHistorial) {
    var selectedParticipante by remember { mutableStateOf<ParticipanteHistorial?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFFFF3E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = curso.nombreCurso, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.People, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${curso.participantes.size} participante(s)", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
            if (curso.participantes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Participantes:", fontWeight = FontWeight.Medium, fontSize = 13.sp, color = Color(0xFF333333))
                Spacer(modifier = Modifier.height(4.dp))
                curso.participantes.forEach { participante ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedParticipante = participante }
                            .padding(vertical = 4.dp, horizontal = 4.dp)
                    ) {
                        Icon(Icons.Default.People, null, modifier = Modifier.size(14.dp), tint = Color(0xFF757575))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${participante.nombre} ${participante.apellido}".trim(),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF333333)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${participante.presentes}/${participante.totalSesiones}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (participante.presentes == participante.totalSesiones) Color(0xFF2E7D32) else Color(0xFF85690F)
                        )
                    }
                }
            }
        }
    }

    selectedParticipante?.let { p ->
        AsistenciaDialog(
            participante = p,
            onDismiss = { selectedParticipante = null }
        )
    }
}

@Composable
private fun AsistenciaDialog(
    participante: ParticipanteHistorial,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${participante.nombre} ${participante.apellido}".trim(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF0D47A1)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Asistencia: ${participante.presentes}/${participante.totalSesiones}",
                        fontSize = 13.sp,
                        color = if (participante.presentes == participante.totalSesiones) Color(0xFF2E7D32) else Color(0xFF85690F)
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.Gray)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                participante.asistencias.forEachIndexed { idx, estado ->
                    val (iconTint, bg, label) = when (estado) {
                        "PRESENTE" -> Triple(Color(0xFF2E7D32), Color(0xFFE8F5E9), "Presente")
                        "AUSENTE" -> Triple(Color(0xFFC62828), Color(0xFFFFEBEE), "Ausente")
                        else -> Triple(Color(0xFF85690F), Color(0xFFFFF8E1), "Pendiente")
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = bg,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SesiÃ³n ${idx + 1}",
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = Color(0xFF333333),
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = iconTint.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = iconTint,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = MuniColors.primaryBlue, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompactDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, fontSize = 12.sp) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodySmall,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedBorderColor = MuniColors.primaryBlue
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.bodySmall) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
