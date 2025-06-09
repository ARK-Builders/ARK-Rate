package dev.arkbuilders.rate.feature.onboarding.quick

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor

internal sealed class BottomNavItem(
    @StringRes val title: Int,
    @DrawableRes val iconDisabled: Int,
    @DrawableRes val iconEnabled: Int,
) {
    data object Assets : BottomNavItem(
        CoreRString.bottom_nav_portfolio,
        CoreRDrawable.ic_nav_portfolio_disabled,
        CoreRDrawable.ic_nav_portfolio_enabled,
    )

    data object PairAlert : BottomNavItem(
        CoreRString.bottom_nav_alerts,
        CoreRDrawable.ic_nav_alerts_disabled,
        CoreRDrawable.ic_nav_alerts_enabled,
    )

    data object Quick : BottomNavItem(
        CoreRString.bottom_nav_quick,
        CoreRDrawable.ic_nav_quick_disabled,
        CoreRDrawable.ic_nav_quick_enabled,
    )

    data object Settings : BottomNavItem(
        CoreRString.bottom_nav_settings,
        CoreRDrawable.ic_nav_settings_disabled,
        CoreRDrawable.ic_nav_settings_enabled,
    )
}

@Composable
fun MockBottomNavigation(
    portfolioModifier: Modifier,
    pairAlertModifier: Modifier,
) {
    val items =
        listOf(
            BottomNavItem.Quick,
            BottomNavItem.Assets,
            BottomNavItem.Settings,
        )
    Column {
        HorizontalDivider(thickness = 1.dp, color = ArkColor.BorderSecondary)
        BottomAppBar(
            containerColor = Color.White,
        ) {
            items.forEach { item ->
                val selected = item == BottomNavItem.Quick
                NavigationBarItem(
                    selected = selected,
                    onClick = { },
                    icon = {
                        val modifier =
                            when (item) {
                                BottomNavItem.Assets -> portfolioModifier
                                BottomNavItem.PairAlert -> pairAlertModifier
                                BottomNavItem.Quick -> Modifier
                                BottomNavItem.Settings -> Modifier
                            }
                        AnimatedContent(
                            modifier = modifier,
                            targetState = selected,
                        ) { innerSelected ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    painter =
                                        painterResource(
                                            if (innerSelected)
                                                item.iconEnabled
                                            else
                                                item.iconDisabled,
                                        ),
                                    contentDescription = stringResource(item.title),
                                )
                                Text(
                                    text = stringResource(item.title),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                    },
                    colors =
                        NavigationBarItemDefaults.colors(
                            selectedIconColor = ArkColor.BrandUtility,
                            selectedTextColor = ArkColor.BrandUtility,
                            indicatorColor = Color.Transparent,
                            unselectedTextColor = ArkColor.BrandSecondary,
                            unselectedIconColor = ArkColor.BrandSecondary,
                        ),
                )
            }
        }
    }
}
