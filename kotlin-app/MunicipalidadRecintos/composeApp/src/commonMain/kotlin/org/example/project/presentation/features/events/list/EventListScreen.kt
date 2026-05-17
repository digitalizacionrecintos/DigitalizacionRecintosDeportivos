package org.example.project.presentation.features.events.list

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import municipalidadrecintos.composeapp.generated.resources.Res
import municipalidadrecintos.composeapp.generated.resources.logo_muni_arica
import municipalidadrecintos.composeapp.generated.resources.logo_sol_municipalidad
import org.example.project.domain.model.Event
import org.example.project.presentation.components.AnimatedEventCard
import org.example.project.presentation.components.EventTag
import org.example.project.presentation.components.ShimmerEventCard
import org.example.project.presentation.features.events.detail.EventDetailScreen
import org.jetbrains.compose.resources.painterResource

class EventListScreen : Screen {
        @Composable
        override fun Content() {
                val navigator = LocalNavigator.currentOrThrow
                val viewModel = remember { EventListViewModel() }
                val state by viewModel.state.collectAsState()
                EventListScreenContent(
                        state = state,
                        onEvent = { event ->
                                when (event) {
                                        is EventListEvent.OnEventClick -> {
                                                navigator.push(
                                                        EventDetailScreen(eventId = event.eventId)
                                                )
                                        }
                                        else -> viewModel.onEvent(event)
                                }
                        },
                        onRefresh = { viewModel.refresh() }
                )
        }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun EventListScreenContent(
        state: EventListState,
        onEvent: (EventListEvent) -> Unit,
        onRefresh: () -> Unit
) {
        val gradientBrush =
                Brush.verticalGradient(
                        0.0f to Color(0xFF043CC7),
                        0.25f to Color(0xFF3A83DF),
                        0.55f to Color(0xFF4BAAEA),
                        0.92f to Color(0xFF3DBAD7)
                )
        Column(modifier = Modifier.fillMaxSize()) {
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
                                        text = "Eventos disponibles",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontSize = 22.sp
                                )
                                Text(
                                        text = "Reserva tu espacio",
                                        color = Color.White.copy(alpha = 0.9f),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 12.sp
                                )
                        }
                }
                Column(Modifier.background(brush = gradientBrush).fillMaxSize().weight(1f)) {

                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {

                                TextField(
                                        value = state.searchQuery,
                                        onValueChange = {
                                                onEvent(EventListEvent.OnSearchQueryChange(it))
                                        },
                                        placeholder = {
                                                Text("Buscar eventos...", color = Color(0xFF6B7280))
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
                                                        label = "Ordenar",
                                                        options =
                                                                listOf(
                                                                        "Reciente",
                                                                        "Antiguo",
                                                                        "Sin ordenar"
                                                                ),
                                                        selectedOption =
                                                                when (state.selectedSortOrder) {
                                                                        DateSortOrder.DESCENDING ->
                                                                                "Reciente"
                                                                        DateSortOrder.ASCENDING ->
                                                                                "Antiguo"
                                                                        DateSortOrder.NONE ->
                                                                                "Sin ordenar"
                                                                },
                                                        onOptionSelected = { selected ->
                                                                val sortOrder =
                                                                        when (selected) {
                                                                                "Reciente" ->
                                                                                        DateSortOrder
                                                                                                .DESCENDING
                                                                                "Antiguo" ->
                                                                                        DateSortOrder
                                                                                                .ASCENDING
                                                                                else ->
                                                                                        DateSortOrder
                                                                                                .NONE
                                                                        }
                                                                onEvent(
                                                                        EventListEvent
                                                                                .OnSortOrderChange(
                                                                                        sortOrder
                                                                                )
                                                                )
                                                        },
                                                        modifier = Modifier.weight(1f)
                                                )

                                        org.example.project.presentation.features.manager.events
                                                .CompactDropdown(
                                                        label = "Año",
                                                        options =
                                                                listOf("Todos") +
                                                                        (2020..2030).map {
                                                                                it.toString()
                                                                        },
                                                        selectedOption = state.selectedDate
                                                                        ?: "Todos",
                                                        onOptionSelected = { selected ->
                                                                onEvent(
                                                                        EventListEvent.OnDateSelect(
                                                                                if (selected ==
                                                                                                "Todos"
                                                                                )
                                                                                        null
                                                                                else selected
                                                                        )
                                                                )
                                                        },
                                                        modifier = Modifier.weight(1f)
                                                )

                                        org.example.project.presentation.features.manager.events
                                                .CompactDropdown(
                                                        label = "Categoría",
                                                        options =
                                                                listOf("Todas") +
                                                                        state.categories.map {
                                                                                it.nombre
                                                                        },
                                                        selectedOption =
                                                                state.categories
                                                                        .find {
                                                                                it.id ==
                                                                                        state.selectedCategoryId
                                                                        }
                                                                        ?.nombre
                                                                        ?: "Todas",
                                                        onOptionSelected = { selected ->
                                                                onEvent(
                                                                        EventListEvent
                                                                                .OnCategorySelect(
                                                                                        if (selected ==
                                                                                                        "Todas"
                                                                                        )
                                                                                                null
                                                                                        else
                                                                                                state.categories
                                                                                                        .find {
                                                                                                                it.nombre ==
                                                                                                                        selected
                                                                                                        }
                                                                                                        ?.id
                                                                                )
                                                                )
                                                        },
                                                        modifier = Modifier.weight(1f)
                                                )
                                }
                        }

                        val pullRefreshState =
                                rememberPullRefreshState(
                                        refreshing = state.isRefreshing,
                                        onRefresh = onRefresh
                                )

                        Box(Modifier.pullRefresh(pullRefreshState).fillMaxSize().weight(1f)) {
                                LazyColumn(
                                        contentPadding =
                                                PaddingValues(
                                                        top = 16.dp,
                                                        start = 16.dp,
                                                        end = 16.dp,
                                                        bottom =
                                                                10.dp +
                                                                        WindowInsets.safeDrawing
                                                                                .asPaddingValues()
                                                                                .calculateBottomPadding()
                                                ),
                                        verticalArrangement = Arrangement.spacedBy(16.dp),
                                        modifier = Modifier.fillMaxSize()
                                ) {
                                        if (state.isLoading && !state.isRefreshing) {
                                                items(3) { _ -> ShimmerEventCard() }
                                        } else if (state.events.isEmpty() && !state.isLoading) {
                                                item {
                                                        Text(
                                                                "No hay eventos disponibles.",
                                                                modifier = Modifier.padding(16.dp),
                                                                color = Color.White,
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyLarge
                                                        )
                                                }
                                        } else {
                                                itemsIndexed(state.events) { index, event ->
                                                        EventCard(
                                                                event = event,
                                                                index = index,
                                                                onClick = {
                                                                        onEvent(
                                                                                EventListEvent
                                                                                        .OnEventClick(
                                                                                                event.id
                                                                                        )
                                                                        )
                                                                }
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
}

@Composable
fun EventCard(event: Event, index: Int, onClick: () -> Unit) {

        val accentColor = Color(0xFF02BB94)

        AnimatedEventCard(index = index, onClick = onClick) {
                Box {

                        Column(modifier = Modifier.fillMaxWidth()) {

                                Box(modifier = Modifier.height(160.dp).fillMaxWidth()) {
                                        if (event.imagenUrl.isNotBlank()) {
                                                coil3.compose.AsyncImage(
                                                        model = event.imagenUrl,
                                                        contentDescription = "Imagen Evento",
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentScale = ContentScale.Crop
                                                )
                                        } else {
                                                Box(
                                                        modifier =
                                                                Modifier.fillMaxSize()
                                                                        .background(Color.Gray),
                                                        contentAlignment = Alignment.Center
                                                ) {
                                                        Icon(
                                                                imageVector =
                                                                        Icons.Default
                                                                                .Image,

                                                                contentDescription = "Sin imagen",
                                                                tint = Color.White,
                                                                modifier = Modifier.size(48.dp)
                                                        )
                                                }
                                        }

                                        Box(
                                                modifier =
                                                        Modifier.fillMaxSize()
                                                                .background(
                                                                        Brush.verticalGradient(
                                                                                colors =
                                                                                        listOf(
                                                                                                Color.Transparent,
                                                                                                Color.Black
                                                                                                        .copy(
                                                                                                                alpha =
                                                                                                                        0.3f
                                                                                                        )
                                                                                        )
                                                                        )
                                                                )
                                        )
                                }

                                Column(modifier = Modifier.padding(16.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                        text = event.title,
                                                        style = MaterialTheme.typography.titleLarge,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF0D47A1),
                                                        modifier = Modifier.weight(1f),
                                                        maxLines = 2,
                                                        overflow =
                                                                androidx.compose.ui.text.style
                                                                        .TextOverflow.Ellipsis
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Image(
                                                        painter =
                                                                painterResource(
                                                                        Res.drawable
                                                                                .logo_sol_municipalidad
                                                                ),
                                                        contentDescription =
                                                                "Logo Sol Municipalidad",
                                                        modifier = Modifier.width(28.dp),
                                                        contentScale = ContentScale.FillWidth
                                                )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                        ) {
                                                Box(modifier = Modifier.weight(1f, fill = false)) {
                                                        EventTag(
                                                                text = event.location,
                                                                icon = Icons.Default.LocationOn,
                                                                backgroundColor = Color(0xFF00BCD4),
                                                                delayMillis = 0L
                                                        )
                                                }

                                                event.categoria?.let { cat ->
                                                        EventTag(
                                                                text =
                                                                        cat.nombre
                                                                                .uppercase(),
                                                                icon = Icons.Default.Label,
                                                                backgroundColor =
                                                                        Color(
                                                                                0xFFFFCB4A
                                                                        ),
                                                                textColor =
                                                                        Color(
                                                                                0xFF85690F
                                                                        ),
                                                                delayMillis = 0L
                                                        )
                                                }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        EventTag(
                                                text = event.date,
                                                icon = Icons.Default.DateRange,
                                                backgroundColor = Color(0xFF1976D2),
                                                delayMillis = 0L
                                        )
                                }
                        }

                        Box(
                                modifier =
                                        Modifier.width(6.dp)
                                                .fillMaxHeight()
                                                .background(
                                                        Brush.verticalGradient(
                                                                colors =
                                                                        listOf(
                                                                                accentColor,
                                                                                accentColor.copy(
                                                                                        alpha = 0.7f
                                                                                )
                                                                        )
                                                        )
                                                )
                        )
                }
        }
}
