package space.taran.arkrate.presentation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import space.taran.arkrate.presentation.addcurrency.AddCurrencyScreen
import space.taran.arkrate.presentation.assets.AssetsScreen
import space.taran.arkrate.presentation.summary.SummaryScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen() {
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(navController, startDestination = Screen.Assets.name) {
        composable(Screen.Assets.name) {
            AssetsScreen(navController)
        }
        composable(
            Screen.AddCurrency.name,
            enterTransition = {
                fadeIn()
            },
            exitTransition = {
                fadeOut()
            }
        ) {
            AddCurrencyScreen(navController)
        }
        composable(Screen.Summary.name,
            enterTransition = {
                fadeIn()
            },
            exitTransition = {
                fadeOut()
            }
        ) {
            SummaryScreen()
        }
    }
}

enum class Screen {
    Assets, AddCurrency, Summary
}