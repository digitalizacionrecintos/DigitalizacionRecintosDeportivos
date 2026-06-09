package org.example.project

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import org.example.project.domain.manager.ServerConfigManager
import org.example.project.domain.manager.ThemeManager
import org.example.project.presentation.di.dataModule
import org.example.project.presentation.di.domainModule
import org.example.project.presentation.di.presentationModule
import org.example.project.presentation.features.auth.login.LoginScreen
import org.example.project.presentation.theme.DarkColorScheme
import org.example.project.presentation.theme.LightColorScheme
import org.example.project.presentation.theme.getAppTypography
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(dataModule, domainModule, presentationModule)
    }) {
        setSingletonImageLoaderFactory { context ->
            ImageLoader.Builder(context)
                .components { add(KtorNetworkFetcherFactory()) }
                .crossfade(true)
                .build()
        }

        val isDarkMode by ThemeManager.isDarkMode.collectAsState()
        val fontScale by ThemeManager.fontScale.collectAsState()

        val typography = getAppTypography()
        val colorScheme = if (isDarkMode) DarkColorScheme else LightColorScheme

        val density = LocalDensity.current
        val customDensity =
            remember(density, fontScale) { Density(density.density, fontScale = fontScale) }

        CompositionLocalProvider(LocalDensity provides customDensity) {
            MaterialTheme(colorScheme = colorScheme, typography = typography) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val initialScreen = LoginScreen()
                    Navigator(screen = initialScreen) { navigator -> SlideTransition(navigator) }
                }
            }
        }
    }
}
