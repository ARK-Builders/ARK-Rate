package dev.arkbuilders.rate.watchapp.presentation.quickpairs

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.arkbuilders.rate.core.domain.model.Amount
import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.feature.quick.domain.model.QuickPair
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.math.BigDecimal
import java.time.OffsetDateTime

@HiltViewModel
class QuickPairsViewModel @Inject constructor(
    private val currencyRepo: CurrencyRepo,
) : ViewModel() {


    private val _quickPairs: MutableStateFlow<List<QuickPair>> = MutableStateFlow(listOf())
    val quickPairs: StateFlow<List<QuickPair>> = _quickPairs

    init {
        val a = listOf(
            QuickPair(
                id = 1,
                from = "BTC",
                amount = BigDecimal.valueOf(1.2),
                to = listOf(
                    Amount("USD", BigDecimal.valueOf(12.0)),
                    Amount("EUR", BigDecimal.valueOf(12.0))
                ),
                calculatedDate = OffsetDateTime.now(),
                pinnedDate = null,
                group = Group.empty()
            ),
            QuickPair(
                id = 1,
                from = "BTC",
                amount = BigDecimal.valueOf(1.2),
                to = listOf(
                    Amount("USD", BigDecimal.valueOf(12.0)),
                    Amount("EUR", BigDecimal.valueOf(12.0))
                ),
                calculatedDate = OffsetDateTime.now(),
                pinnedDate = null,
                group = Group.empty()
            ),

            QuickPair(
                id = 1,
                from = "BTC",
                amount = BigDecimal.valueOf(1.2),
                to = listOf(
                    Amount("USD", BigDecimal.valueOf(12.0)),
                    Amount("EUR", BigDecimal.valueOf(12.0))
                ),
                calculatedDate = OffsetDateTime.now(),
                pinnedDate = null,
                group = Group.empty()
            ),

            QuickPair(
                id = 1,
                from = "BTC",
                amount = BigDecimal.valueOf(1.2),
                to = listOf(
                    Amount("USD", BigDecimal.valueOf(12.0)),
                    Amount("EUR", BigDecimal.valueOf(12.0))
                ),
                calculatedDate = OffsetDateTime.now(),
                pinnedDate = null,
                group = Group.empty()
            ),
            QuickPair(
                id = 1,
                from = "BTC",
                amount = BigDecimal.valueOf(1.2),
                to = listOf(
                    Amount("USD", BigDecimal.valueOf(12.0)),
                    Amount("EUR", BigDecimal.valueOf(12.0))
                ),
                calculatedDate = OffsetDateTime.now(),
                pinnedDate = null,
                group = Group.empty()
            ),

            QuickPair(
                id = 1,
                from = "BTC",
                amount = BigDecimal.valueOf(1.2),
                to = listOf(
                    Amount("USD", BigDecimal.valueOf(12.0)),
                    Amount("EUR", BigDecimal.valueOf(12.0))
                ),
                calculatedDate = OffsetDateTime.now(),
                pinnedDate = null,
                group = Group.empty()
            )
        )
        _quickPairs.value = a
    }

}
