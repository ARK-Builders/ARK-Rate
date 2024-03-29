package dev.arkbuilders.rate.presentation

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.ramcosta.composedestinations.utils.startDestination
import dev.arkbuilders.rate.presentation.destinations.AddCurrencyScreenDestination
import dev.arkbuilders.rate.presentation.destinations.AssetsScreenDestination
import dev.arkbuilders.rate.presentation.destinations.QuickScreenDestination
import dev.arkbuilders.rate.presentation.destinations.SummaryScreenDestination
import dev.arkbuilders.rate.presentation.shared.SharedViewModel
import dev.arkbuilders.rate.presentation.ui.AnimatedRateBottomNavigation
import dev.arkbuilders.rate.presentation.ui.RateScaffold
import dev.arkbuilders.rate.presentation.utils.activityViewModel
import dev.arkbuilders.rate.presentation.utils.keyboardAsState


@Composable
fun MainScreen() {
    val engine = rememberNavHostEngine()
    val navController = engine.rememberNavController()

    val isKeyboardOpen by keyboardAsState()
    val bottomBarVisible = rememberSaveable { mutableStateOf(false) }


    val navBackStackEntry by navController.currentBackStackEntryAsState()

    when (navBackStackEntry?.destination?.route) {
        AddCurrencyScreenDestination.route -> {
            bottomBarVisible.value = false
        }

        SummaryScreenDestination.route -> {
            bottomBarVisible.value = false
        }

        else -> {
            bottomBarVisible.value = true
        }
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

