@file:OptIn(
    ExperimentalAnimationApi::class,
)

package dev.arkbuilders.rate.presentation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.WindowInsets
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
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.SplashScreenDestination
import com.ramcosta.composedestinations.generated.onboarding.destinations.OnboardingQuickPairScreenDestination
import com.ramcosta.composedestinations.generated.onboarding.destinations.OnboardingQuickScreenDestination
import com.ramcosta.composedestinations.generated.onboarding.destinations.OnboardingScreenDestination
import com.ramcosta.composedestinations.generated.portfolio.destinations.PortfolioScreenDestination
import com.ramcosta.composedestinations.generated.quick.destinations.AddQuickScreenDestination
import com.ramcosta.composedestinations.generated.quick.destinations.QuickScreenDestination
import com.ramcosta.composedestinations.generated.settings.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.navargs.primitives.longNavType
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.ramcosta.composedestinations.scope.resultRecipient
import com.ramcosta.composedestinations.utils.startDestination
import dev.arkbuilders.rate.core.presentation.ui.ConnectivityOfflineSnackbar
import dev.arkbuilders.rate.core.presentation.ui.ConnectivityOfflineSnackbarVisuals
import dev.arkbuilders.rate.core.presentation.ui.ConnectivityOnlineSnackbar
import dev.arkbuilders.rate.core.presentation.ui.ConnectivityOnlineSnackbarVisuals
import dev.arkbuilders.rate.core.presentation.utils.findActivity
import dev.arkbuilders.rate.core.presentation.utils.keyboardAsState
import dev.arkbuilders.rate.feature.onboarding.OnboardingExternalNavigator
import dev.arkbuilders.rate.feature.onboarding.quick.OnboardingQuickScreen
import dev.arkbuilders.rate.feature.quick.presentation.QuickExternalNavigator
import dev.arkbuilders.rate.feature.quick.presentation.main.QuickScreen
import dev.arkbuilders.rate.feature.quickwidget.presentation.action.AddNewPairAction.Companion.ADD_NEW_PAIR
import dev.arkbuilders.rate.feature.quickwidget.presentation.action.AddNewPairAction.Companion.ADD_NEW_PAIR_GROUP_KEY
import dev.arkbuilders.rate.presentation.navigation.AnimatedRateBottomNavigation
import kotlinx.coroutines.flow.drop

private val dontApplySafeDrawingPaddingRoutes =
    listOf(
        OnboardingScreenDestination.route,
        OnboardingQuickScreenDestination.route,
        OnboardingQuickPairScreenDestination.route,
    )

private val showBottomBarRoutes =
    listOf(
        QuickScreenDestination.route,
        PortfolioScreenDestination.route,
        SettingsScreenDestination.route,
    )

@Composable
fun MainScreen() {
    val engine = rememberNavHostEngine()
    val navController = engine.rememberNavController()
    val snackState = remember { SnackbarHostState() }
    val ctx = LocalContext.current

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

    val isKeyboardOpen by keyboardAsState()
    val bottomBarVisible = rememberSaveable { mutableStateOf(false) }
    val applySafeDrawingPadding = remember { mutableStateOf(true) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavGraphs.main.startRoute.route

    bottomBarVisible.value =
        showBottomBarRoutes.any {
            currentRoute.startsWith(it)
        }

    applySafeDrawingPadding.value =
        !dontApplySafeDrawingPaddingRoutes.any {
            currentRoute.startsWith(it)
        }

    if (isKeyboardOpen)
        bottomBarVisible.value = false

    Scaffold(
        modifier =
            Modifier
                .let {
                    if (applySafeDrawingPadding.value) it.safeDrawingPadding() else it
                },
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
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) {
        DestinationsNavHost(
            engine = engine,
            navController = navController,
            start = SplashScreenDestination,
            navGraph = NavGraphs.main,
            modifier = Modifier.padding(it),
        ) {
            composable(OnboardingQuickScreenDestination) {
                val externalNavigator =
                    remember {
                        object : OnboardingExternalNavigator {
                            override fun navigateOnFinish() {
                                destinationsNavigator.navigate(QuickScreenDestination) {
                                    popUpTo(NavGraphs.main.startDestination) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }
                OnboardingQuickScreen(
                    navigator = destinationsNavigator,
                    externalNavigator = externalNavigator,
                )
            }

            composable(QuickScreenDestination) {
                val externalNavigator =
                    remember {
                        object : QuickExternalNavigator {
                            override fun navigateToPairOnboarding() {
                                destinationsNavigator.navigate(OnboardingQuickPairScreenDestination)
                            }
                        }
                    }
                QuickScreen(
                    navigator = destinationsNavigator,
                    resultRecipient = resultRecipient(longNavType),
                    externalNavigator = externalNavigator,
                )
            }
        }
    }
}
