package space.taran.arkrate.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.rememberNavHostEngine
import space.taran.arkrate.presentation.destinations.AddCurrencyScreenDestination
import space.taran.arkrate.presentation.ui.AnimatedRateBottomNavigation
import space.taran.arkrate.presentation.ui.RateScaffold


@Composable
fun MainScreen() {
    val engine = rememberNavHostEngine()
    val navController = engine.rememberNavController()

    val bottomBarVisible = rememberSaveable { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    when (navBackStackEntry?.destination?.route) {
        AddCurrencyScreenDestination.route -> {
            bottomBarVisible.value = false
        }
        else -> {
            bottomBarVisible.value = true
        }
    }

    RateScaffold(
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
            modifier = Modifier.padding(it)
        )
    }
}

enum class Screen(val route: String) {
    Assets("assets"),
    AddCurrency("addCurrency/{from}"),
    Summary("summary"),
    PairAlert("pairAlert")
}

