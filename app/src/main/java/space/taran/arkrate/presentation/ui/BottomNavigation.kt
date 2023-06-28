package space.taran.arkrate.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ramcosta.composedestinations.spec.Direction
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import space.taran.arkrate.R
import space.taran.arkrate.presentation.destinations.AssetsScreenDestination
import space.taran.arkrate.presentation.destinations.Destination
import space.taran.arkrate.presentation.destinations.PairAlertConditionScreenDestination
import space.taran.arkrate.presentation.destinations.SummaryScreenDestination

sealed class BottomNavItem(
    var title: String,
    var icon: Int,
    var direction: DirectionDestinationSpec
) {
    object Assets : BottomNavItem(
        "Assets",
        R.drawable.ic_list,
        AssetsScreenDestination
    )

    object Summary : BottomNavItem(
        "Summary",
        R.drawable.ic_list_alt,
        SummaryScreenDestination
    )

    object PairAlert : BottomNavItem(
        "Notifications",
        R.drawable.ic_notifications,
        PairAlertConditionScreenDestination
    )
}

@Composable
fun AnimatedRateBottomNavigation(
    currentDestination: Destination,
    onBottomBarItemClick: (Direction) -> Unit,
    bottomBarVisible: State<Boolean>
) {
    AnimatedVisibility(
        visible = bottomBarVisible.value,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        content = { RateBottomNavigation(currentDestination, onBottomBarItemClick) }
    )
}

@Composable
fun RateBottomNavigation(
    currentDestination: Destination,
    onBottomBarItemClick: (Direction) -> Unit
) {
    val items = listOf(
        BottomNavItem.Assets,
        BottomNavItem.Summary,
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
                selected = currentDestination == item.direction,
                onClick = { onBottomBarItemClick(item.direction) }
            )
        }
    }
}