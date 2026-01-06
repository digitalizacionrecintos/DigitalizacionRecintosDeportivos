package org.example.project.presentation.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.example.project.presentation.features.history.HistoryScreenContent
import org.example.project.presentation.features.history.HistoryViewModel

object HistoryTab : Tab {
    override val key: ScreenKey = uniqueScreenKey

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.DateRange)
            return remember { TabOptions(index = 1u, title = "Historial", icon = icon) }
        }

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
