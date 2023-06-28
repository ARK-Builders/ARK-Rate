package space.taran.arkrate.presentation.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.spec.Route
import space.taran.arkrate.presentation.NavGraphs
import space.taran.arkrate.presentation.appCurrentDestinationAsState
import space.taran.arkrate.presentation.destinations.Destination
import space.taran.arkrate.presentation.startAppDestination

@SuppressLint("RestrictedApi")
@Composable
fun RateScaffold(
    navController: NavHostController,
    bottomBar: @Composable (Destination) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    val destination = navController.appCurrentDestinationAsState().value
        ?: NavGraphs.root.startAppDestination

    Scaffold(
        bottomBar = { bottomBar(destination) },
        content = content
    )
}
