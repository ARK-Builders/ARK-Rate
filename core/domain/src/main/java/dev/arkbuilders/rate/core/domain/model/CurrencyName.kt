package dev.arkbuilders.rate.core.domain.model

data class CurrencyName(
    val code: CurrencyCode,
    val name: String,
) {
    companion object {
        val EMPTY = CurrencyName("", "")
    }
}
