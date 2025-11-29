package dev.arkbuilders.rate.watchapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.arkbuilders.rate.watchapp.presentation.addquickpairs.AddQuickPairsScreen
import dev.arkbuilders.rate.watchapp.presentation.quickpairs.QuickPairsScreen
import dev.arkbuilders.rate.watchapp.presentation.theme.ArkrateTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            ArkrateTheme {
                val navController = rememberSwipeDismissableNavController()
                Scaffold(
                    vignette = {
                        Vignette(vignettePosition = VignettePosition.TopAndBottom)
                    }
                ) {
                    SwipeDismissableNavHost(
                        navController = navController,
                        startDestination = "list"
                    ) {
                        composable("list") {
//                            OptionsScreen()
//                            SearchScreen()
                            QuickPairsScreen(
                                onNavigateToAdd = {
                                    navController.navigate("addquickpairs")
                                }
                            )
                        }
                        composable("addquickpairs") {
                            AddQuickPairsScreen(
                            )
                        }
                    }
                }
            }

        }
    }
}
