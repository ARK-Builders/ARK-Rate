package dev.arkbuilders.rate.feature.quick.domain.usecase

import dev.arkbuilders.rate.core.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.feature.quick.domain.model.PinnedQuickPair
import dev.arkbuilders.rate.feature.quick.domain.model.QuickPair
import dev.arkbuilders.rate.feature.quick.domain.repo.QuickRepo
import java.time.OffsetDateTime

class GetSortedPinnedQuickPairsUseCase(
    private val quickRepo: QuickRepo,
    private val convertUseCase: ConvertWithRateUseCase,
) {
    suspend operator fun invoke() =
        quickRepo.getAll()
            .filter { it.isPinned() }
            .map {
                mapPairToPinned(it)
            }
            .sortedByDescending { it.pair.pinnedDate }

    private suspend fun mapPairToPinned(pair: QuickPair): PinnedQuickPair {
        val actualTo =
            pair.to.map { to ->
                val (amount, _) = convertUseCase.invoke(pair.from, pair.amount, to.code)
                amount
            }
        return PinnedQuickPair(
            pair,
            actualTo,
            OffsetDateTime.now(),
        )
    }
}
