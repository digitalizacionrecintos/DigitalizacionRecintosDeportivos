package org.example.project.presentation.features.estadisticas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import org.example.project.domain.model.EstadisticaItem
import org.example.project.domain.usecase.estadisticas.GetEstadisticasUseCase
import org.example.project.presentation.components.MuniTopBarLogo
import org.example.project.presentation.theme.MuniColors
import org.example.project.presentation.theme.MuniGradients
import org.example.project.presentation.theme.MuniSpacing
import org.koin.compose.koinInject

class EstadisticasScreen : Screen {
    @Composable
    override fun Content() {
        val useCase: GetEstadisticasUseCase = koinInject()
        val viewModel = remember { EstadisticasViewModel(useCase) }
        val state by viewModel.state.collectAsState()

        EstadisticasContent(state = state, onEvent = viewModel::onEvent)
    }
}

@Composable
fun EstadisticasContent(state: EstadisticasState, onEvent: (EstadisticasEvent) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().background(MuniGradients.header)
                .statusBarsPadding().padding(horizontal = MuniSpacing.lg, vertical = MuniSpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MuniTopBarLogo()
            Spacer(Modifier.width(MuniSpacing.lg))
            Column {
                Text("Estadísticas", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                Text("Resumen general", color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodySmall)
            }
        }

        Column(Modifier.background(MuniColors.offWhite).fillMaxSize().weight(1f)) {
            when {
                state.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MuniColors.primaryBlue)
                    }
                }
                state.error != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.error, color = Color.Red)
                    }
                }
                state.generales != null -> {
                    val datos = state.generales
                    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Resumen", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Color(0xFF0D47A1))
                                Spacer(Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                    StatCard("Eventos", datos.totalEventos.toString(), Color(0xFF1976D2), Modifier.weight(1f))
                                    StatCard("% Asistencia", "${datos.porcentajeAsistencia}%", Color(0xFF02BB94), Modifier.weight(1f))
                                }
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                    StatCard("Prom. Mensual", "${datos.promedioEventosMensual}", Color(0xFF00BCD4), Modifier.weight(1f))
                                    StatCard("Ausentismo", "${datos.tasaAusentismo}%", Color(0xFFFFCB4A), Modifier.weight(1f))
                                }
                            }
                        }

                        if (datos.eventosPorCategoria.isNotEmpty()) {
                            Spacer(Modifier.height(12.dp))
                            val maxCat = datos.eventosPorCategoria.maxOf { it.cantidad }.toFloat()
                            ChartCard("Eventos por Categoría") {
                                datos.eventosPorCategoria.forEach { item -> BarItem(item, Color(0xFF1976D2), maxCat) }
                            }
                        }

                        if (datos.eventosPorRecinto.isNotEmpty()) {
                            Spacer(Modifier.height(12.dp))
                            val maxRec = datos.eventosPorRecinto.maxOf { it.cantidad }.toFloat()
                            ChartCard("Eventos por Recinto") {
                                datos.eventosPorRecinto.forEach { item -> BarItem(item, Color(0xFF00BCD4), maxRec) }
                            }
                        }

                        val eventosPorMes = datos.eventosPorMes.map { EstadisticaItem(it.key, it.value.toInt()) }
                        if (eventosPorMes.isNotEmpty()) {
                            Spacer(Modifier.height(12.dp))
                            val maxMes = eventosPorMes.maxOf { it.cantidad }.toFloat()
                            ChartCard("Eventos por Mes") {
                                eventosPorMes.forEach { item -> BarItem(item, Color(0xFF02BB94), maxMes) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}

@Composable
private fun ChartCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Color(0xFF0D47A1))
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun BarItem(item: EstadisticaItem, color: Color, maxVal: Float = 100f) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(item.label, style = MaterialTheme.typography.bodySmall, color = Color(0xFF333333), modifier = Modifier.weight(1f))
            Text("${item.cantidad}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(Modifier.height(4.dp))
        val fraction = if (maxVal > 0f) (item.cantidad.toFloat() / maxVal).coerceIn(0f, 1f) else 0f
        Surface(modifier = Modifier.fillMaxWidth().height(8.dp), shape = RoundedCornerShape(4.dp), color = color.copy(alpha = 0.15f)) {
            Box(modifier = Modifier.fillMaxWidth(fraction).fillMaxHeight().background(color, RoundedCornerShape(4.dp)))
        }
    }
}
