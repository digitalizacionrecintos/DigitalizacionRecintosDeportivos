package org.example.project.presentation.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
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
import org.example.project.presentation.features.events.detail.EventDetailScreen
import org.example.project.presentation.features.events.list.EventListEvent
import org.example.project.presentation.features.events.list.EventListScreenContent
import org.example.project.presentation.features.events.list.EventListViewModel

object HomeTab : Tab {

    override val key: ScreenKey = uniqueScreenKey

    override val options: TabOptions
        @Composable
        get() {
            val title = "Inicio"
            val icon = rememberVectorPainter(Icons.Default.Home)

            return remember { TabOptions(index = 0u, title = title, icon = icon) }
        }

    @Composable
    override fun Content() {

        val parentNavigator = LocalNavigator.currentOrThrow.parent ?: LocalNavigator.currentOrThrow

        val viewModel = remember { EventListViewModel() }
        val state by viewModel.state.collectAsState()

        EventListScreenContent(
                state,
                { event ->
                    if (event is EventListEvent.OnEventClick) {

                        parentNavigator.push(EventDetailScreen(event.eventId))
                    } else {
                        viewModel.onEvent(event)
                    }
                },
                onRefresh = { viewModel.refresh() }
        )
    }
}
