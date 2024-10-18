package dev.arkbuilders.ratewatch.presentation.quickpairs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import dev.arkbuilders.ratewatch.domain.model.Amount
import dev.arkbuilders.ratewatch.domain.model.PinnedQuickPair
import dev.arkbuilders.ratewatch.domain.model.QuickPair
import java.time.OffsetDateTime
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun QuickPairsScreen(
    modifier: Modifier = Modifier,
    viewModel: QuickPairsViewModel = viewModel()
) {
    val a = viewModel.status.collectAsState()
    Column(modifier = modifier.fillMaxSize()) {
        QuickPairItem(
            quick = PinnedQuickPair(
                pair = QuickPair(
                    id = 1,
                    from = "BTC",
                    amount = 1.2,
                    to = listOf(Amount("USD", 12.0)),
                    calculatedDate = OffsetDateTime.now(),
                    pinnedDate = null,
                    group = null
                ),
                actualTo = listOf(Amount("USD", 12.0)),
                refreshDate = OffsetDateTime.now(),
            )
        )
    }
}
