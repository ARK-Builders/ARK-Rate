package dev.arkbuilders.rate.core.domain.usecase

import dev.arkbuilders.rate.core.domain.BuildConfigFields
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.CurrencyInfo

class SearchUseCase(
    private val buildConfigFields: BuildConfigFields,
) {
    operator fun invoke(
        all: List<CurrencyInfo>,
        frequent: List<CurrencyCode>,
        query: String,
    ): List<CurrencyInfo> {
        val filtered =
            all
                .filter {
                    it.name.contains(query, ignoreCase = true) ||
                        it.code.contains(query, ignoreCase = true)
                }.sortedBy { it.code }

        val prefixAndIcons =
            filtered.filter {
                it.code.startsWith(
                    query,
                    ignoreCase = true,
                ) && it.code in buildConfigFields.availableIconCodes
            }

        val prefix = filtered.filter { it.code.startsWith(query, ignoreCase = true) }

        val frequent = filtered.filter { it.code in frequent }
        val icons = filtered.filter { it.code in buildConfigFields.availableIconCodes }

        val result = prefixAndIcons + prefix + frequent + icons + filtered
        return result.distinct()
    }
}
