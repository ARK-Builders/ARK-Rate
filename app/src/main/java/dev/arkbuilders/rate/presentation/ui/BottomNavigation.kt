package dev.arkbuilders.rate.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import dev.arkbuilders.rate.presentation.destinations.SummaryScreenDestination

sealed class BottomNavItem(
    var title: String,
    var icon: Int,
    var route: String,
) {
    object Assets : BottomNavItem(
        "Assets",
        R.drawable.ic_list,
        AssetsScreenDestination.route
    )

    object Summary : BottomNavItem(
        "Summary",
        R.drawable.ic_list_alt,
        SummaryScreenDestination.invoke(amount = null).route
    )

    object PairAlert : BottomNavItem(
        "Notifications",
        R.drawable.ic_notifications,
        PairAlertConditionScreenDestination.route
    )

    object Quick : BottomNavItem(
        "Quick",
        R.drawable.currency_exchange,
        QuickScreenDestination.route
    )
}

@Composable
fun AnimatedRateBottomNavigation(
    currentDestination: Destination,
    onBottomBarItemClick: (String) -> Unit,
    bottomBarVisible: State<Boolean>
) {
    AnimatedVisibility(
        visible = bottomBarVisible.value,
        enter = fadeIn(),
        exit = fadeOut(),
        content = { RateBottomNavigation(currentDestination, onBottomBarItemClick) }
    )
}

@Composable
fun RateBottomNavigation(
    currentDestination: Destination,
    onBottomBarItemClick: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem.Assets,
        BottomNavItem.Summary,
        BottomNavItem.Quick,
        BottomNavItem.PairAlert,
    )

    BottomNavigation(
        backgroundColor = colorResource(id = R.color.teal_200),
        contentColor = Color.Black
    ) {
        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painterResource(id = item.icon),
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 9.sp
                    )
                },
                selectedContentColor = Color.Black,
                unselectedContentColor = Color.Black.copy(0.4f),
                alwaysShowLabel = true,
                selected = item.route.contains(currentDestination.baseRoute),
                onClick = { onBottomBarItemClick(item.route) }
            )
        }
    }
}