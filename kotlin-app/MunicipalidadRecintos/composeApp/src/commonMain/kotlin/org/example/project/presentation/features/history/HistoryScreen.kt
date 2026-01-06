package org.example.project.presentation.features.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import org.example.project.domain.model.Event
import org.example.project.presentation.components.EmptyStateView
import org.jetbrains.compose.resources.painterResource

class HistoryScreen : Screen {
        @Composable
        override fun Content() {
                val viewModel = remember { HistoryViewModel() }
                val state by viewModel.state.collectAsState()

                HistoryScreenContent(
                        state = state,
                        onRefresh = { viewModel.refresh() },
                        onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                        onYearSelected = { viewModel.onYearSelected(it) },
                        onMonthSelected = { viewModel.onMonthSelected(it) }
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
        onMonthSelected: (String?) -> Unit
) {
        var selectedTabIndex by remember { mutableStateOf(0) }

        val gradientBrush =
                Brush.verticalGradient(0.0f to Color(0xFF043CC7), 1.0f to Color(0xFF3DBAD7))

        Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {

                Row(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .background(
                                                Brush.horizontalGradient(
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
                                        text = "Mis eventos",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontSize = 22.sp
                                )
                                Text(
                                        text = "Historial de inscripciones",
                                        color = Color.White.copy(alpha = 0.9f),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 12.sp
                                )
                        }
                }

                TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = Color.White,
                        contentColor = Color(0xFF043CC7),
                        indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                        modifier =
                                                Modifier.tabIndicatorOffset(
                                                        tabPositions[selectedTabIndex]
                                                ),
                                        height = 3.dp,
                                        color = Color(0xFF043CC7)
                                )
                        }
                ) {
                        Tab(
                                selected = selectedTabIndex == 0,
                                onClick = { selectedTabIndex = 0 },
                                text = { Text("Próximos", fontWeight = FontWeight.SemiBold) }
                        )
                        Tab(
                                selected = selectedTabIndex == 1,
                                onClick = { selectedTabIndex = 1 },
                                text = { Text("Finalizados", fontWeight = FontWeight.SemiBold) }
                        )
                }

                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        TextField(
                                value = state.searchQuery,
                                onValueChange = { onSearchQueryChange(it) },
                                placeholder = {
                                        Text("Buscar eventos...", color = Color(0xFF6B7280))
                                },
                                leadingIcon = {
                                        Icon(
                                                Icons.Default.SearchOff,
                                                contentDescription = null,
                                                tint = Color(0xFF6B7280),
                                                modifier = Modifier.size(20.dp)
                                        )
                                },
                                colors =
                                        TextFieldDefaults.colors(
                                                unfocusedContainerColor = Color.White,
                                                focusedContainerColor = Color.White,
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
                                                options = listOf("Todos") + state.availableYears,
                                                selectedOption = state.selectedYear ?: "Todos",
                                                onOptionSelected = { selected ->
                                                        onYearSelected(
                                                                if (selected == "Todos") null
                                                                else selected
                                                        )
                                                },
                                                modifier = Modifier.weight(1f)
                                        )

                                org.example.project.presentation.features.manager.events
                                        .CompactDropdown(
                                                label = "Mes",
                                                options = state.availableMonths.map { it.first },
                                                selectedOption =
                                                        state.availableMonths
                                                                .find {
                                                                        it.second
                                                                                ?.toString()
                                                                                ?.padStart(
                                                                                        2,
                                                                                        '0'
                                                                                ) ==
                                                                                state.selectedMonth
                                                                }
                                                                ?.first
                                                                ?: "Todos",
                                                onOptionSelected = { selected ->
                                                        val monthNumber =
                                                                state.availableMonths
                                                                        .find {
                                                                                it.first == selected
                                                                        }
                                                                        ?.second
                                                        onMonthSelected(
                                                                monthNumber
                                                                        ?.toString()
                                                                        ?.padStart(2, '0')
                                                        )
                                                },
                                                modifier = Modifier.weight(1f)
                                        )
                        }
                }

                val eventsToShow =
                        if (selectedTabIndex == 0) state.upcomingEvents else state.pastEvents

                val pullRefreshState =
                        rememberPullRefreshState(
                                refreshing = state.isRefreshing,
                                onRefresh = onRefresh
                        )

                Box(Modifier.weight(1f).fillMaxWidth().pullRefresh(pullRefreshState)) {
                        if (eventsToShow.isEmpty()) {

                                Box(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                                        EmptyStateView(
                                                message =
                                                        if (state.error != null) state.error
                                                        else "No hay eventos en esta categoría.",
                                                icon = Icons.Default.SearchOff
                                        )
                                }
                        } else {
                                LazyColumn(
                                        contentPadding = PaddingValues(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.fillMaxSize()
                                ) {
                                        items(eventsToShow) { event ->
                                                HistoryItemCard(
                                                        event = event,
                                                        isPast = selectedTabIndex == 1
                                                )
                                        }
                                }
                        }

                        PullRefreshIndicator(
                                refreshing = state.isRefreshing,
                                state = pullRefreshState,
                                modifier = Modifier.align(Alignment.TopCenter),
                                backgroundColor = Color.White,
                                contentColor = Color(0xFF043CC7)
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
                Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                ) {

                        Box(
                                modifier =
                                        Modifier.size(48.dp)
                                                .clip(CircleShape)
                                                .background(
                                                        if (isPast) Color(0xFFEEEEEE)
                                                        else Color(0xFFE3F2FD)
                                                ),
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        imageVector =
                                                if (isPast) Icons.Default.CheckCircle
                                                else Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = if (isPast) Color.Gray else Color(0xFF043CC7),
                                        modifier = Modifier.size(24.dp)
                                )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = event.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = if (isPast) Color.Gray else Color.Black
                                )
                                Spacer(modifier = Modifier.height(4.dp))
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

                        if (!isPast) {
                                Surface(
                                        color = Color(0xFFE0F7FA),
                                        shape = RoundedCornerShape(4.dp)
                                ) {
                                        Text(
                                                text = "Inscrito",
                                                color = Color(0xFF006064),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier =
                                                        Modifier.padding(
                                                                horizontal = 6.dp,
                                                                vertical = 2.dp
                                                        )
                                        )
                                }
                        }
                }
        }
}
