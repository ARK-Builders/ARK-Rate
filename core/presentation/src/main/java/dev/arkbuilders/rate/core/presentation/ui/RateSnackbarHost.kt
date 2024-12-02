package dev.arkbuilders.rate.core.presentation.ui

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun RateSnackbarHost(snackState: SnackbarHostState) {
    SnackbarHost(modifier = Modifier, hostState = snackState) { data ->
        val visuals = data.visuals
        when (visuals) {
            is NotifyAddedSnackbarVisuals ->
                NotifyAddedSnackbarContent(
                    visuals,
                    onDismiss = { data.dismiss() },
                )

            is NotifyRemovedSnackbarVisuals ->
                NotifyRemovedSnackbarContent(
                    visuals,
                    onDismiss = { data.dismiss() },
                )
        }
    }
}
