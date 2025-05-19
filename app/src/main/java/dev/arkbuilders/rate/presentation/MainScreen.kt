@file:OptIn(
    ExperimentalAnimationApi::class,
)

package dev.arkbuilders.rate.presentation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.onboarding.destinations.OnboardingQuickScreenDestination
import com.ramcosta.composedestinations.generated.onboarding.destinations.OnboardingScreenDestination
import com.ramcosta.composedestinations.generated.portfolio.destinations.PortfolioScreenDestination
import com.ramcosta.composedestinations.generated.quick.destinations.AddQuickScreenDestination
import com.ramcosta.composedestinations.generated.quick.destinations.QuickScreenDestination
import com.ramcosta.composedestinations.generated.settings.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.ramcosta.composedestinations.spec.Direction
import com.ramcosta.composedestinations.utils.startDestination
import dev.arkbuilders.rate.core.domain.repo.PreferenceKey
import dev.arkbuilders.rate.core.presentation.ui.ConnectivityOfflineSnackbar
import dev.arkbuilders.rate.core.presentation.ui.ConnectivityOfflineSnackbarVisuals
import dev.arkbuilders.rate.core.presentation.ui.ConnectivityOnlineSnackbar
import dev.arkbuilders.rate.core.presentation.ui.ConnectivityOnlineSnackbarVisuals
import dev.arkbuilders.rate.core.presentation.utils.findActivity
import dev.arkbuilders.rate.core.presentation.utils.keyboardAsState
import dev.arkbuilders.rate.feature.onboarding.OnboardingExternalNavigator
import dev.arkbuilders.rate.feature.onboarding.quick.OnboardingQuickScreen
import dev.arkbuilders.rate.feature.quickwidget.presentation.action.AddNewPairAction.Companion.ADD_NEW_PAIR
import dev.arkbuilders.rate.feature.quickwidget.presentation.action.AddNewPairAction.Companion.ADD_NEW_PAIR_GROUP_KEY
import dev.arkbuilders.rate.presentation.navigation.AnimatedRateBottomNavigation
import kotlinx.coroutines.flow.drop

@Composable
fun MainScreen() {
    val engine = rememberNavHostEngine()
    val navController = engine.rememberNavController()
    val snackState = remember { SnackbarHostState() }
    val ctx = LocalContext.current
    var start: Direction? by remember { mutableStateOf(null) }

    LaunchedEffect(key1 = Unit) {
        val activity = ctx.findActivity()
        val intent = activity?.intent
        val createNewPair = intent?.getStringExtra(ADD_NEW_PAIR) ?: ""
        if (createNewPair.isNotEmpty()) {
            val groupId = intent?.getLongExtra(ADD_NEW_PAIR_GROUP_KEY, 0L)
            navController.navigate(AddQuickScreenDestination(groupId = groupId))
            intent?.removeExtra(ADD_NEW_PAIR_GROUP_KEY)
            intent?.removeExtra(ADD_NEW_PAIR)
        }
    }
    LaunchedEffect(key1 = Unit) {
        App.instance.coreComponent.networkStatus().onlineStatus
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
    LaunchedEffect(key1 = Unit) {
        val completed = App.instance.coreComponent.prefs().get(PreferenceKey.IsOnboardingCompleted)
        start =
            if (completed) {
                QuickScreenDestination
            } else {
                OnboardingScreenDestination
            }
    }

    val isKeyboardOpen by keyboardAsState()
    val bottomBarVisible = rememberSaveable { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavGraphs.main.startRoute.route

    bottomBarVisible.value =
        when {
            currentRoute.startsWith(QuickScreenDestination.route) -> true
            currentRoute.startsWith(PortfolioScreenDestination.route) -> true
            currentRoute.startsWith(SettingsScreenDestination.route) -> true
            else -> false
        }

    if (isKeyboardOpen)
        bottomBarVisible.value = false

    if (start == null)
        return

    Scaffold(
        modifier =
            Modifier
                .safeDrawingPadding(),
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
                currentRoute = currentRoute,
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
            start = start!!,
            navGraph = NavGraphs.main,
            modifier = Modifier.padding(it),
        ) {
            composable(OnboardingQuickScreenDestination) {
                OnboardingQuickScreen(
                    externalNavigator =
                        object : OnboardingExternalNavigator {
                            override fun navigateOnFinish() {
                                destinationsNavigator.navigate(QuickScreenDestination) {
                                    popUpTo(NavGraphs.main.startDestination) {
                                        inclusive = true
                                    }
                                }
                            }
                        },
                )
            }
        }
    }
}
