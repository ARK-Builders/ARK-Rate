package dev.arkbuilders.rate.feature.portfolio.domain.model

import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.Group
import java.math.BigDecimal

data class Asset(
    val id: Long = 0,
    val code: CurrencyCode,
    var value: BigDecimal,
    val group: Group,
) {
    companion object {
        val EMPTY = Asset(0, "", BigDecimal.ZERO, Group.empty())
    }
}
