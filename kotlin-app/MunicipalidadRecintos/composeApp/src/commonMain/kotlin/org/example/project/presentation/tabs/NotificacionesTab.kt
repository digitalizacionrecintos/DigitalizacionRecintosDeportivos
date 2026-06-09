package org.example.project.presentation.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.example.project.domain.usecase.notificacion.GetNotificacionesUseCase
import org.example.project.domain.usecase.notificacion.MarkNotificacionReadUseCase
import org.example.project.presentation.features.notificaciones.NotificacionesContent
import org.example.project.presentation.features.notificaciones.NotificacionesViewModel
import org.koin.compose.koinInject

object NotificacionesTab : Tab {
    override val key: ScreenKey = uniqueScreenKey

    override val options: TabOptions
        @Composable
        get() {
            val title = "Notificaciones"
            val icon = rememberVectorPainter(Icons.Default.Notifications)
            return remember { TabOptions(index = 4u, title = title, icon = icon) }
        }

    @Composable
    override fun Content() {
        val getNotificacionesUseCase: GetNotificacionesUseCase = koinInject()
        val markNotificacionReadUseCase: MarkNotificacionReadUseCase = koinInject()
        val viewModel = remember { NotificacionesViewModel(getNotificacionesUseCase, markNotificacionReadUseCase) }
        val state by viewModel.state.collectAsState()

        NotificacionesContent(state = state, onEvent = viewModel::onEvent)
    }
}
