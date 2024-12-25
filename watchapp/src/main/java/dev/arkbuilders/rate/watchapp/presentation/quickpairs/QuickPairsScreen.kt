package dev.arkbuilders.rate.watchapp.presentation.quickpairs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.arkbuilders.rate.watchapp.presentation.quickpairs.composables.QuickPairItem
import dev.arkbuilders.rate.watchapp.presentation.quickpairs.composables.QuickPairsEmpty

@Composable
fun QuickPairsScreen(
    modifier: Modifier = Modifier,
    viewModel: QuickPairsViewModel = QuickPairsViewModel()
) {
    val quickPairsList = viewModel.quickPairs.collectAsState().value

    if (quickPairsList.isEmpty()) {
        QuickPairsEmpty(modifier = modifier.fillMaxSize())
    } else {
        LazyColumn(modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(4.dp)
        ) {
            items(quickPairsList) { quickPair ->
                QuickPairItem(quick = quickPair)
            }
        }
    }
}
