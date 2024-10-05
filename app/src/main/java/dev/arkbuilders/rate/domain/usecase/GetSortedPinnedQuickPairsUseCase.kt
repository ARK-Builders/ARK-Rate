package dev.arkbuilders.rate.domain.usecase

import dev.arkbuilders.rate.domain.model.PinnedQuickPair
import dev.arkbuilders.rate.domain.model.QuickPair
import dev.arkbuilders.rate.domain.repo.QuickRepo
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSortedPinnedQuickPairsUseCase @Inject constructor(
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
        return PinnedQuickPair(pair, actualTo, OffsetDateTime.now())
    }
}
