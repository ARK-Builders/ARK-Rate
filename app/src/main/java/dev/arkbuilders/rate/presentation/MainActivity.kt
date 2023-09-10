package dev.arkbuilders.rate.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import dev.arkbuilders.rate.di.DIManager
import dev.arkbuilders.rate.di.NavDepContainer
import dev.arkbuilders.rate.presentation.theme.ARKRateTheme
import kotlinx.coroutines.launch

val LocalDependencyContainer = staticCompositionLocalOf<NavDepContainer> {
    error("No dependency container provided!")
}

class MainActivity : ComponentActivity() {

    private val dependencyContainer by lazy { NavDepContainer(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        lifecycleScope.launch {
            val assetsRepo = DIManager.component.assetsRepo()
            val startFromQuickScreen = assetsRepo.allCurrencyAmount().isNotEmpty()
            setContent {
                ARKRateTheme {
                    CompositionLocalProvider(LocalDependencyContainer provides dependencyContainer) {
                        MainScreen(startFromQuickScreen)
                    }
                }
            }
        }
    }
}