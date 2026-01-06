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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import org.example.project.domain.manager.SessionManager
import org.example.project.domain.model.UserRole
import org.example.project.presentation.tabs.HistoryTab
import org.example.project.presentation.tabs.HomeTab
import org.example.project.presentation.tabs.ManagerEventsTab
import org.example.project.presentation.tabs.ManagerHistoryTab
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
                                containerColor = Color.White,
                                tonalElevation = 4.dp,
                                modifier = Modifier.fillMaxWidth(),
                                windowInsets = WindowInsets(0, 0, 0, 0)
                        ) {
                            if (currentRole == UserRole.MANAGER) {
                                TabNavigationItem(ManagerEventsTab)
                                TabNavigationItem(ManagerHistoryTab)
                            } else {
                                TabNavigationItem(HomeTab)
                                TabNavigationItem(HistoryTab)
                            }
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
                            selectedIconColor = Color(0xFF043CC7),
                            selectedTextColor = Color(0xFF043CC7),
                            indicatorColor =
                                    Color(0xFFE6E6E6),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                    )
    )
}
