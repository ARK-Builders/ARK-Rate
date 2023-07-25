package dev.arkbuilders.rate.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import dev.arkbuilders.rate.di.NavDepContainer
import dev.arkbuilders.rate.presentation.theme.ARKRateTheme

val LocalDependencyContainer = staticCompositionLocalOf<NavDepContainer> {
    error("No dependency container provided!")
}

class MainActivity : ComponentActivity() {

    private val dependencyContainer by lazy { NavDepContainer(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ARKRateTheme {
                CompositionLocalProvider(LocalDependencyContainer provides dependencyContainer) {
                    MainScreen()
                }
            }
        }
    }
}