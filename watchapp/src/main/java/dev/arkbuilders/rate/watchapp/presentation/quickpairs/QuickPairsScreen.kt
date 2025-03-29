package dev.arkbuilders.rate.watchapp.presentation.quickpairs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Text
import dev.arkbuilders.rate.watchapp.presentation.quickpairs.composables.QuickPairItem
import dev.arkbuilders.rate.watchapp.presentation.quickpairs.composables.QuickPairsEmpty

@Composable
fun QuickPairsScreen(
    modifier: Modifier = Modifier,
    viewModel: QuickPairsViewModel = QuickPairsViewModel(),
    onNavigateToAdd: () -> Unit
) {
    val quickPairsList = viewModel.quickPairs.collectAsState().value

    if (quickPairsList.isEmpty()) {
        QuickPairsEmpty(modifier = modifier.fillMaxSize())
    } else {
        ScalingLazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(4.dp)
        ) {
            item {
                Text(
                    modifier = modifier.fillMaxWidth(),
                    text = "Quick",
                    textAlign = TextAlign.Center
                )
            }
            items(quickPairsList.size, key = null) { idx ->
                QuickPairItem(quick = quickPairsList[idx], onClick = onNavigateToAdd)
            }
        }
    }
}
