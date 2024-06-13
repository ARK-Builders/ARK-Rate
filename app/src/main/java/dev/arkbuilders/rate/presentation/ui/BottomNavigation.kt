@file:OptIn(ExperimentalAnimationApi::class)

package dev.arkbuilders.rate.presentation.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
    @StringRes val title: Int,
    @DrawableRes val iconDisabled: Int,
    @DrawableRes val iconEnabled: Int,
    val route: String,
) {
    data object Assets : BottomNavItem(
        R.string.bottom_nav_portfolio,
        R.drawable.ic_nav_portfolio_disabled,
        R.drawable.ic_nav_portfolio_enabled,
        PortfolioScreenDestination.route
    )

    data object PairAlert : BottomNavItem(
        R.string.bottom_nav_alerts,
        R.drawable.ic_nav_alerts_disabled,
        R.drawable.ic_nav_alerts_enabled,
        PairAlertConditionScreenDestination.route
    )

    data object Quick : BottomNavItem(
        R.string.bottom_nav_quick,
        R.drawable.ic_nav_quick_disabled,
        R.drawable.ic_nav_quick_enabled,
        QuickScreenDestination.route
    )

    data object Settings : BottomNavItem(
        R.string.bottom_nav_settings,
        R.drawable.ic_nav_settings_disabled,
        R.drawable.ic_nav_settings_enabled,
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
                val selected = item.route.contains(currentDestination.baseRoute)
                NavigationBarItem(
                    selected = selected,
                    onClick = { onBottomBarItemClick(item.route) },
                    icon = {
                        AnimatedContent(targetState = selected) { innerSelected ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    painter = painterResource(
                                        if (innerSelected) item.iconEnabled
                                        else item.iconDisabled
                                    ),
                                    contentDescription = stringResource(item.title)
                                )
                                Text(
                                    text = stringResource(item.title),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
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