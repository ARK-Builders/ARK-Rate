package dev.arkbuilders.rate.presentation.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.spec.Route
import dev.arkbuilders.rate.presentation.NavGraphs
import dev.arkbuilders.rate.presentation.appCurrentDestinationAsState
import dev.arkbuilders.rate.presentation.destinations.Destination
import dev.arkbuilders.rate.presentation.startAppDestination

@SuppressLint("RestrictedApi")
@Composable
fun RateScaffold(
    modifier: Modifier,
    navController: NavHostController,
    bottomBar: @Composable (Destination) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    val destination = navController.appCurrentDestinationAsState().value
        ?: NavGraphs.root.startAppDestination

    Scaffold(
        modifier = modifier,
        bottomBar = { bottomBar(destination) },
        content = content
    )
}
