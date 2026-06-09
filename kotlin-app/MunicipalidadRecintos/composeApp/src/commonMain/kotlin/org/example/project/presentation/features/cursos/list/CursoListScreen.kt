package org.example.project.presentation.features.cursos.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.domain.model.Curso
import org.example.project.domain.usecase.curso.GetAvailableCursosUseCase
import org.example.project.presentation.components.MuniTopBarLogo
import org.example.project.presentation.features.cursos.detail.CursoDetailScreen
import org.example.project.presentation.theme.MuniColors
import org.example.project.presentation.theme.MuniGradients
import org.example.project.presentation.theme.MuniSpacing
import org.koin.compose.koinInject

class CursoListScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val useCase: GetAvailableCursosUseCase = koinInject()
        val viewModel = remember { CursoListViewModel(useCase) }
        val state by viewModel.state.collectAsState()

        CursoListContent(
            state = state,
            onEvent = { event ->
                when (event) {
                    is CursoListEvent.OnCursoClick -> {
                        navigator.push(CursoDetailScreen(event.cursoId))
                    }
                    else -> viewModel.onEvent(event)
                }
            },
            onRefresh = { viewModel.refresh() }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CursoListContent(
    state: CursoListState,
    onEvent: (CursoListEvent) -> Unit,
    onRefresh: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
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
                Text("Cursos disponibles", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                Text("Inscríbete en nuestros cursos", color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodySmall)
            }
        }

        Column(Modifier.background(MuniGradients.contentBackground).fillMaxSize().weight(1f)) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                TextField(
                    value = state.searchQuery,
                    onValueChange = { onEvent(CursoListEvent.OnSearchQueryChange(it)) },
                    placeholder = { Text("Buscar cursos...", color = MuniColors.mediumGray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MuniColors.mediumGray, modifier = Modifier.size(20.dp)) },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
                        unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            val pullRefreshState = rememberPullRefreshState(refreshing = state.isRefreshing, onRefresh = onRefresh)
            Box(Modifier.pullRefresh(pullRefreshState).fillMaxSize()) {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (state.isLoading && !state.isRefreshing) {
                        items(3) {
                            ShimmerCursoCard()
                        }
                    } else if (state.cursos.isEmpty() && !state.isLoading) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("No hay cursos disponibles.", color = MuniColors.mediumGray, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    } else {
                        items(state.cursos) { curso ->
                            CursoCard(curso = curso, onClick = { onEvent(CursoListEvent.OnCursoClick(curso.idCurso)) })
                        }
                    }
                }
                PullRefreshIndicator(refreshing = state.isRefreshing, state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter), backgroundColor = Color.White, contentColor = Color(0xFF043CC7))
            }
        }
    }
}

@Composable
fun CursoCard(curso: Curso, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFF043CC7), modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text(curso.nombre, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Color(0xFF0D47A1), modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Text(curso.descripcion.take(100) + if (curso.descripcion.length > 100) "..." else "",
                style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 2)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF1976D2))
                    Spacer(Modifier.width(4.dp))
                    Text("${curso.fechaInicio} - ${curso.fechaFin}", style = MaterialTheme.typography.labelSmall, color = Color(0xFF1976D2))
                }
                curso.cupo?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF02BB94))
                        Spacer(Modifier.width(4.dp))
                        Text("Cupo: $it", style = MaterialTheme.typography.labelSmall, color = Color(0xFF02BB94))
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                val estadoColor = when (curso.estado.uppercase()) {
                    "PUBLICADO" -> Color(0xFF02BB94)
                    "EN_PROGRESO" -> Color(0xFF1976D2)
                    "FINALIZADO" -> Color.Gray
                    "CANCELADO" -> Color.Red
                    else -> Color(0xFFFFCB4A)
                }
                Surface(shape = RoundedCornerShape(4.dp), color = estadoColor.copy(alpha = 0.15f)) {
                    Text(curso.estado, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall, color = estadoColor, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun ShimmerCursoCard() {
    Card(
        modifier = Modifier.fillMaxWidth().height(140.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Column {
                Box(Modifier.fillMaxWidth(0.6f).height(20.dp).background(Color.LightGray, RoundedCornerShape(4.dp)))
                Spacer(Modifier.height(12.dp))
                Box(Modifier.fillMaxWidth(0.9f).height(14.dp).background(Color.LightGray, RoundedCornerShape(4.dp)))
                Spacer(Modifier.height(12.dp))
                Box(Modifier.fillMaxWidth(0.4f).height(14.dp).background(Color.LightGray, RoundedCornerShape(4.dp)))
            }
        }
    }
}
