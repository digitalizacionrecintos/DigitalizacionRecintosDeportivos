package org.example.project.presentation.features.manager.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import org.example.project.domain.model.ManagerEvent
import org.example.project.domain.usecase.event.GetManagerEventsUseCase
import org.example.project.presentation.components.MuniTopBarLogo
import org.example.project.presentation.components.ShimmerEventCard
import org.example.project.presentation.features.manager.attendance.ManagerEventDetailScreen
import org.example.project.presentation.theme.MuniColors
import org.example.project.presentation.theme.MuniElevation
import org.example.project.presentation.theme.MuniGradients
import org.example.project.presentation.theme.MuniShapes
import org.example.project.presentation.theme.MuniSpacing
import org.koin.compose.koinInject

class ManagerEventsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val getManagerEventsUseCase: GetManagerEventsUseCase = koinInject()
        val viewModel = remember { ManagerEventsViewModel(getManagerEventsUseCase) }
        val state by viewModel.state.collectAsState()

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
                    Text("Mis Eventos", color = Color.White, fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge)
                    Text("Gestiona tus eventos", color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodySmall)
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth().weight(1f).background(MuniColors.offWhite)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().background(MuniColors.surfaceCard).padding(MuniSpacing.lg)
                ) {
                    TextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        placeholder = { Text("Buscar eventos...", color = MuniColors.lightGray) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = MuniColors.mediumGray, modifier = Modifier.size(20.dp)) },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MuniColors.ultraLightGray,
                            focusedContainerColor = MuniColors.ultraLightGray,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        ),
                        shape = MuniShapes.textField,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(MuniSpacing.sm))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MuniSpacing.sm)
                    ) {
                        CompactDropdown(
                            label = "Ordenar",
                            options = listOf("Reciente", "Antiguo"),
                            selectedOption = state.sortOrder,
                            onOptionSelected = { viewModel.updateSortOrder(it) },
                            modifier = Modifier.weight(1f)
                        )
                        CompactDropdown(
                            label = "Año",
                            options = listOf("Todos") + viewModel.availableYears,
                            selectedOption = state.selectedYear ?: "Todos",
                            onOptionSelected = { viewModel.updateSelectedYear(if (it == "Todos") null else it) },
                            modifier = Modifier.weight(1f)
                        )
                        CompactDropdown(
                            label = "Categoría",
                            options = listOf("Todas") + viewModel.availableCategories,
                            selectedOption = state.selectedCategory ?: "Todas",
                            onOptionSelected = { viewModel.updateSelectedCategory(if (it == "Todas") null else it) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = MuniSpacing.lg, vertical = MuniSpacing.lg),
                    verticalArrangement = Arrangement.spacedBy(MuniSpacing.lg),
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (state.isLoading) {
                        items(3) { ShimmerEventCard() }
                    } else {
                        val errorMsg = state.errorMessage
                        if (errorMsg != null) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = MuniSpacing.xxl),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(errorMsg,
                                        color = MuniColors.errorRed, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        } else if (state.events.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = MuniSpacing.xxl),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No se encontraron eventos con los filtros aplicados",
                                        color = MuniColors.mediumGray, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        } else {
                            itemsIndexed(state.events) { _, event ->
                                ModernEventCard(
                                    event = event,
                                    onClick = {
                                        navigator.push(
                                            ManagerEventDetailScreen(
                                                eventId = event.id.toInt(),
                                                managerId = state.managerId
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
}

@Composable
fun ModernEventCard(event: ManagerEvent, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MuniShapes.cardLarge,
        colors = CardDefaults.cardColors(containerColor = MuniColors.surfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = MuniElevation.subtle)
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
                        modifier = Modifier.fillMaxSize().background(MuniColors.lightGray.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Sin imagen",
                            tint = MuniColors.lightGray,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                Surface(
                    modifier = Modifier.padding(MuniSpacing.sm).align(Alignment.TopStart),
                    shape = MuniShapes.buttonSmall,
                    color = Color(0xFF040581)
                ) {
                    Text(
                        text = event.categoryName,
                        modifier = Modifier.padding(horizontal = MuniSpacing.sm, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color.White
                    )
                }
            }
            Column(modifier = Modifier.padding(MuniSpacing.lg)) {
                Text(text = event.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium,
                    color = MuniColors.darkGray, lineHeight = 24.sp)
                Spacer(modifier = Modifier.height(MuniSpacing.sm))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.LocationOn, null, tint = MuniColors.mediumGray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = event.location, style = MaterialTheme.typography.bodySmall, color = MuniColors.mediumGray, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(MuniSpacing.sm))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.DateRange, null, tint = MuniColors.mediumGray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = event.date, style = MaterialTheme.typography.bodySmall, color = MuniColors.mediumGray)
                }
            }
        }
    }
}
