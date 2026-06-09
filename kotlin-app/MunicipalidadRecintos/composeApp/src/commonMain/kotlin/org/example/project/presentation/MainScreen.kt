package org.example.project.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.example.project.presentation.theme.MuniColors
import org.example.project.presentation.theme.MuniElevation
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import org.example.project.domain.manager.SessionManager
import org.example.project.domain.model.UserRole
import org.example.project.presentation.tabs.CursosTab
import org.example.project.presentation.tabs.EstadisticasTab
import org.example.project.presentation.tabs.HistoryTab
import org.example.project.presentation.tabs.HomeTab
import org.example.project.presentation.tabs.ManagerEventsTab
import org.example.project.presentation.tabs.ManagerHistoryTab
import org.example.project.presentation.tabs.NotificacionesTab
import org.example.project.presentation.tabs.ProfileTab

class MainScreen : Screen {
    @Composable
    override fun Content() {
        val currentRole by SessionManager.currentRole.collectAsState()

        val firstTab = if (currentRole == UserRole.MANAGER) ManagerEventsTab else HomeTab

        TabNavigator(firstTab) {
            Scaffold(
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    bottomBar = {
                        NavigationBar(
                                containerColor = MuniColors.surfaceCard,
                                tonalElevation = MuniElevation.raised,
                                modifier = Modifier.fillMaxWidth(),
                                windowInsets = WindowInsets(0, 0, 0, 0)
                        ) {
                            if (currentRole == UserRole.MANAGER) {
                                TabNavigationItem(ManagerEventsTab)
                                TabNavigationItem(ManagerHistoryTab)
                                TabNavigationItem(EstadisticasTab)
                            } else {
                                TabNavigationItem(HomeTab)
                                TabNavigationItem(CursosTab)
                                TabNavigationItem(HistoryTab)
                            }
                            TabNavigationItem(NotificacionesTab)
                            TabNavigationItem(ProfileTab)
                        }
                    }
            ) { innerPadding ->

                Box(modifier = Modifier.padding(innerPadding)) { CurrentTab() }
            }
        }
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current

    NavigationBarItem(
            selected = tabNavigator.current == tab,
            onClick = { tabNavigator.current = tab },
            icon = {
                tab.options.icon?.let { painter ->
                    Icon(painter = painter, contentDescription = tab.options.title)
                }
            },
            label = { Text(tab.options.title) },
            colors =
                    NavigationBarItemDefaults.colors(
                            selectedIconColor = MuniColors.primaryBlue,
                            selectedTextColor = MuniColors.primaryBlue,
                            indicatorColor = MuniColors.offWhite,
                            unselectedIconColor = MuniColors.mediumGray,
                            unselectedTextColor = MuniColors.mediumGray
                    )
    )
}
