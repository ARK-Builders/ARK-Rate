@file:OptIn(ExperimentalAnimationApi::class)

package dev.arkbuilders.rate.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.presentation.destinations.Destination
import dev.arkbuilders.rate.presentation.destinations.PairAlertConditionScreenDestination
import dev.arkbuilders.rate.presentation.destinations.PortfolioScreenDestination
import dev.arkbuilders.rate.presentation.destinations.QuickScreenDestination
import dev.arkbuilders.rate.presentation.destinations.SettingsScreenDestination
import dev.arkbuilders.rate.presentation.theme.ArkColor

sealed class BottomNavItem(
    var title: String,
    var icon: Int,
    var route: String,
) {
    object Assets : BottomNavItem(
        "Portfolio",
        R.drawable.ic_nav_portfolio,
        PortfolioScreenDestination.route
    )

    object PairAlert : BottomNavItem(
        "Alerts",
        R.drawable.ic_nav_alerts,
        PairAlertConditionScreenDestination.route
    )

    object Quick : BottomNavItem(
        "Quick",
        R.drawable.ic_nav_quick,
        QuickScreenDestination.route
    )

    object Settings : BottomNavItem(
        "Settings",
        R.drawable.ic_nav_settings,
        SettingsScreenDestination.route
    )

}

@Composable
fun AnimatedRateBottomNavigation(
    currentDestination: Destination,
    onBottomBarItemClick: (String) -> Unit,
    bottomBarVisible: State<Boolean>
) {
    AnimatedContent(
        targetState = bottomBarVisible.value,
        transitionSpec = {
            slideInVertically { height -> height } with
                    slideOutVertically { height -> height }
        }
    ) { expanded ->
        if (expanded)
            RateBottomNavigation(currentDestination, onBottomBarItemClick)
    }
}

@Composable
fun RateBottomNavigation(
    currentDestination: Destination,
    onBottomBarItemClick: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem.Quick,
        BottomNavItem.Assets,
        BottomNavItem.PairAlert,
        BottomNavItem.Settings
    )
    Column {
        HorizontalDivider(thickness = 1.dp, color = ArkColor.BorderSecondary)
        BottomAppBar(
            containerColor = Color.White,
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    selected = item.route.contains(currentDestination.baseRoute),
                    onClick = { onBottomBarItemClick(item.route) },
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painterResource(id = item.icon),
                                contentDescription = item.title
                            )
                            Text(
                                text = item.title,
                                fontSize = 9.sp
                            )
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ArkColor.BrandUtility,
                        selectedTextColor = ArkColor.BrandUtility,
                        indicatorColor = Color.Transparent,
                        unselectedTextColor = ArkColor.BrandSecondary,
                        unselectedIconColor = ArkColor.BrandSecondary
                    )
                )
            }
        }
    }
}