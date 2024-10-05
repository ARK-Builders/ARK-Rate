@file:OptIn(
    ExperimentalMaterialNavigationApi::class,
    ExperimentalAnimationApi::class,
)

package dev.arkbuilders.rate.presentation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.utils.startDestination
import dev.arkbuilders.rate.di.DIManager
import dev.arkbuilders.rate.presentation.destinations.AddQuickScreenDestination
import dev.arkbuilders.rate.presentation.destinations.PairAlertConditionScreenDestination
import dev.arkbuilders.rate.presentation.destinations.PortfolioScreenDestination
import dev.arkbuilders.rate.presentation.destinations.QuickScreenDestination
import dev.arkbuilders.rate.presentation.destinations.SettingsScreenDestination
import dev.arkbuilders.rate.presentation.quick.glancewidget.action.AddNewPairAction.Companion.ADD_NEW_PAIR
import dev.arkbuilders.rate.presentation.quick.glancewidget.action.AddNewPairAction.Companion.ADD_NEW_PAIR_GROUP_KEY
import dev.arkbuilders.rate.presentation.ui.AnimatedRateBottomNavigation
import dev.arkbuilders.rate.presentation.ui.ConnectivityOfflineSnackbar
import dev.arkbuilders.rate.presentation.ui.ConnectivityOfflineSnackbarVisuals
import dev.arkbuilders.rate.presentation.ui.ConnectivityOnlineSnackbar
import dev.arkbuilders.rate.presentation.ui.ConnectivityOnlineSnackbarVisuals
import dev.arkbuilders.rate.presentation.utils.findActivity
import dev.arkbuilders.rate.presentation.utils.keyboardAsState
import kotlinx.coroutines.flow.drop

@Composable
fun MainScreen() {
    val engine =
        rememberAnimatedNavHostEngine(
            rootDefaultAnimations = RootNavGraphDefaultAnimations.ACCOMPANIST_FADING,
        )
    val navController = engine.rememberNavController()
    val snackState = remember { SnackbarHostState() }
    val ctx = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        val activity = ctx.findActivity()
        val intent = activity?.intent
        val createNewPair = intent?.getStringExtra(ADD_NEW_PAIR) ?: ""
        if (createNewPair.isNotEmpty()) {
            val group = intent?.getStringExtra(ADD_NEW_PAIR_GROUP_KEY)
            navController.navigate(AddQuickScreenDestination(group = group))
            intent?.removeExtra(ADD_NEW_PAIR_GROUP_KEY)
            intent?.removeExtra(ADD_NEW_PAIR)
        }
    }
    LaunchedEffect(key1 = Unit) {
        DIManager.component.networkStatus().onlineStatus
            .drop(1)
            .collect { online ->
                val visuals =
                    if (online)
                        ConnectivityOnlineSnackbarVisuals
                    else
                        ConnectivityOfflineSnackbarVisuals
                snackState.showSnackbar(visuals)
            }
    }

    val isKeyboardOpen by keyboardAsState()
    val bottomBarVisible = rememberSaveable { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val destination =
        navController.appCurrentDestinationAsState().value
            ?: NavGraphs.root.startAppDestination

    bottomBarVisible.value =
        when (navBackStackEntry?.destination?.route) {
            QuickScreenDestination.route -> true
            PortfolioScreenDestination.route -> true
            PairAlertConditionScreenDestination.route -> true
            SettingsScreenDestination.route -> true
            else -> false
        }

    if (isKeyboardOpen)
        bottomBarVisible.value = false

    Scaffold(
        modifier =
            Modifier
                .systemBarsPadding()
                .imePadding(),
        snackbarHost = {
            SnackbarHost(
                hostState = snackState,
            ) { data ->
                val visuals = data.visuals
                when (visuals) {
                    is ConnectivityOnlineSnackbarVisuals ->
                        ConnectivityOnlineSnackbar()
                    is ConnectivityOfflineSnackbarVisuals ->
                        ConnectivityOfflineSnackbar()
                }
            }
        },
        bottomBar = {
            AnimatedRateBottomNavigation(
                currentDestination = destination,
                onBottomBarItemClick = {
                    navController.navigate(it) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                bottomBarVisible = bottomBarVisible,
            )
        },
    ) {
        DestinationsNavHost(
            engine = engine,
            navController = navController,
            navGraph = NavGraphs.root,
            modifier = Modifier.padding(it),
            startRoute = QuickScreenDestination.startDestination,
        )
    }
}
