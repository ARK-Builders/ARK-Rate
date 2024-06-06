package dev.arkbuilders.rate.domain.usecase

import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.domain.model.stats.CodeUseStat
import dev.arkbuilders.rate.domain.repo.CodeUseStatRepo
import java.time.Duration
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

private const val FREQUENT_LIMIT = 5

@Singleton
class CalcFrequentCurrUseCase @Inject constructor(
    private val codeUseStatRepo: CodeUseStatRepo
) {
    suspend operator fun invoke(): List<CurrencyCode> {
        val codeUseStats = codeUseStatRepo.getAll()
        return mapToSortRating(codeUseStats)
            .sortedBy { (_, sortRating) -> sortRating }
            .take(FREQUENT_LIMIT)
            .map { (code, _) -> code }
    }

    private fun mapToSortRating(map: Map<CurrencyCode, CodeUseStat>): List<Pair<CurrencyCode, Double>> {
        val now = OffsetDateTime.now()
        return map.mapValues { (_, stat) ->
            val daysPassed = Duration.between(now, stat.lastUsedDate).toDays()
            val timeFactor = daysPassed * 0.5
            stat.count.toDouble() - timeFactor
        }.toList()
    }
}