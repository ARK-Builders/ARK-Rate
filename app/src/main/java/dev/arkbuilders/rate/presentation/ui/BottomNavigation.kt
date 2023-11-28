@file:OptIn(ExperimentalAnimationApi::class)

package dev.arkbuilders.rate.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.presentation.destinations.AssetsScreenDestination
import dev.arkbuilders.rate.presentation.destinations.Destination
import dev.arkbuilders.rate.presentation.destinations.PairAlertConditionScreenDestination
import dev.arkbuilders.rate.presentation.destinations.QuickScreenDestination
import dev.arkbuilders.rate.presentation.destinations.SettingsScreenDestination

sealed class BottomNavItem(
        var title: String,
        var icon: Int,
        var route: String,
                          ) {
    object Assets : BottomNavItem("Assets", R.drawable.ic_list, AssetsScreenDestination.route)

    object PairAlert : BottomNavItem("Alerts",
                                     R.drawable.ic_notifications,
                                     PairAlertConditionScreenDestination.route)

    object Quick :
        BottomNavItem("Quick", R.drawable.currency_exchange, QuickScreenDestination.route)

    object Settings :
        BottomNavItem("Settings", R.drawable.ic_settings, SettingsScreenDestination.route)

}

@Composable
fun AnimatedRateBottomNavigation(currentDestination: Destination,
        onBottomBarItemClick: (String) -> Unit,
        bottomBarVisible: State<Boolean>) {
    AnimatedContent(targetState = bottomBarVisible.value, transitionSpec = {
        slideInVertically { height -> height } togetherWith slideOutVertically { height -> height }
    }) { expanded ->
        if (expanded) RateBottomNavigation(currentDestination, onBottomBarItemClick)
    }
}

@Composable
fun RateBottomNavigation(currentDestination: Destination, onBottomBarItemClick: (String) -> Unit) {
    val items = listOf(BottomNavItem.Quick,
                       BottomNavItem.Assets,
                       BottomNavItem.PairAlert,
                       BottomNavItem.Settings)

    BottomNavigation(backgroundColor = colorResource(id = R.color.teal_200),
                     contentColor = Color.Black) {
        items.forEach { item ->
            BottomNavigationItem(icon = {
                Icon(painterResource(id = item.icon), contentDescription = item.title)
            },
                                 label = {
                                     Text(text = item.title, fontSize = 9.sp)
                                 },
                                 selectedContentColor = Color.Black,
                                 unselectedContentColor = Color.Black.copy(0.4f),
                                 alwaysShowLabel = true,
                                 selected = item.route.contains(currentDestination.baseRoute),
                                 onClick = { onBottomBarItemClick(item.route) })
        }
    }
}