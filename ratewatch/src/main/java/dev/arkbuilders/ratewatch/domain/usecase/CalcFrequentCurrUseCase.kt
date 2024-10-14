package dev.arkbuilders.ratewatch.domain.usecase

import dev.arkbuilders.ratewatch.domain.model.CurrencyCode
import dev.arkbuilders.ratewatch.domain.model.stats.CodeUseStat
import dev.arkbuilders.ratewatch.domain.repo.CodeUseStatRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Duration
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

private const val FREQUENT_LIMIT = 5

@Singleton
class CalcFrequentCurrUseCase @Inject constructor(
    private val codeUseStatRepo: CodeUseStatRepo,
) {
    suspend operator fun invoke(stats: List<CodeUseStat>? = null): List<CurrencyCode> {
        val codeUseStats = stats ?: codeUseStatRepo.getAll()
        return mapToSortRating(codeUseStats)
            .sortedByDescending { (_, sortRating) -> sortRating }
            .take(FREQUENT_LIMIT)
            .map { (code, _) -> code }
    }

    fun flow(): Flow<List<CurrencyCode>> =
        codeUseStatRepo.getAllFlow().map { list ->
            invoke(list)
        }

    private fun mapToSortRating(list: List<CodeUseStat>): List<Pair<CurrencyCode, Double>> {
        val now = OffsetDateTime.now()
        return list.map { stat ->
            val daysPassed = Duration.between(stat.lastUsedDate, now).toDays()
            val timeFactor = daysPassed * 0.5
            stat.code to stat.count.toDouble() - timeFactor
        }.toList()
    }
}
