@file:OptIn(ExperimentalMaterialNavigationApi::class,
    ExperimentalAnimationApi::class
)

package dev.arkbuilders.rate.presentation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.ramcosta.composedestinations.utils.startDestination
import dev.arkbuilders.rate.presentation.destinations.PairAlertConditionScreenDestination
import dev.arkbuilders.rate.presentation.destinations.PortfolioScreenDestination
import dev.arkbuilders.rate.presentation.destinations.QuickScreenDestination
import dev.arkbuilders.rate.presentation.destinations.SettingsScreenDestination
import dev.arkbuilders.rate.presentation.ui.AnimatedRateBottomNavigation
import dev.arkbuilders.rate.presentation.ui.RateScaffold
import dev.arkbuilders.rate.presentation.utils.keyboardAsState


@Composable
fun MainScreen() {
    val engine = rememberAnimatedNavHostEngine(
        rootDefaultAnimations = RootNavGraphDefaultAnimations.ACCOMPANIST_FADING,
    )
    val navController = engine.rememberNavController()

    val isKeyboardOpen by keyboardAsState()
    val bottomBarVisible = rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    bottomBarVisible.value = when (navBackStackEntry?.destination?.route) {
        QuickScreenDestination.route -> true
        PortfolioScreenDestination.route -> true
        PairAlertConditionScreenDestination.route -> true
        SettingsScreenDestination.route -> true
        else -> false
    }

    if (isKeyboardOpen)
        bottomBarVisible.value = false

    RateScaffold(
        modifier = Modifier
            .systemBarsPadding()
            .imePadding(),
        navController = navController,
        bottomBar = { destination ->
            AnimatedRateBottomNavigation(
                currentDestination = destination,
                onBottomBarItemClick = {
                    navController.navigate(it) {
                        launchSingleTop = true
                    }
                },
                bottomBarVisible = bottomBarVisible
            )
        }
    ) {
        DestinationsNavHost(
            engine = engine,
            navController = navController,
            navGraph = NavGraphs.root,
            modifier = Modifier.padding(it),
            startRoute = QuickScreenDestination.startDestination
        )
    }
}

