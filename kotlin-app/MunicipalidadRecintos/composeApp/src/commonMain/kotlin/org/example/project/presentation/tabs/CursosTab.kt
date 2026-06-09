package org.example.project.presentation.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.example.project.domain.usecase.curso.GetAvailableCursosUseCase
import org.example.project.presentation.features.cursos.detail.CursoDetailScreen
import org.example.project.presentation.features.cursos.list.CursoListContent
import org.example.project.presentation.features.cursos.list.CursoListEvent
import org.example.project.presentation.features.cursos.list.CursoListViewModel
import org.koin.compose.koinInject

object CursosTab : Tab {
    override val key: ScreenKey = uniqueScreenKey

    override val options: TabOptions
        @Composable
        get() {
            val title = "Cursos"
            val icon = rememberVectorPainter(Icons.Default.School)
            return remember { TabOptions(index = 3u, title = title, icon = icon) }
        }

    @Composable
    override fun Content() {
        val parentNavigator = LocalNavigator.currentOrThrow.parent ?: LocalNavigator.currentOrThrow
        val getAvailableCursosUseCase: GetAvailableCursosUseCase = koinInject()
        val viewModel = remember { CursoListViewModel(getAvailableCursosUseCase) }
        val state by viewModel.state.collectAsState()

        CursoListContent(
            state = state,
            onEvent = { event ->
                if (event is CursoListEvent.OnCursoClick) {
                    parentNavigator.push(CursoDetailScreen(event.cursoId))
                } else {
                    viewModel.onEvent(event)
                }
            },
            onRefresh = { viewModel.refresh() }
        )
    }
}
