package org.example.project.presentation.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.example.project.domain.usecase.estadisticas.GetEstadisticasUseCase
import org.example.project.presentation.features.estadisticas.EstadisticasContent
import org.example.project.presentation.features.estadisticas.EstadisticasViewModel
import org.koin.compose.koinInject

object EstadisticasTab : Tab {
    override val key: ScreenKey = uniqueScreenKey

    override val options: TabOptions
        @Composable
        get() {
            val title = "Estadísticas"
            val icon = rememberVectorPainter(Icons.Default.Leaderboard)
            return remember { TabOptions(index = 5u, title = title, icon = icon) }
        }

    @Composable
    override fun Content() {
        val useCase: GetEstadisticasUseCase = koinInject()
        val viewModel = remember { EstadisticasViewModel(useCase) }
        val state by viewModel.state.collectAsState()

        EstadisticasContent(state = state, onEvent = viewModel::onEvent)
    }
}
