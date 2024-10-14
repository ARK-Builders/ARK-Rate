package dev.arkbuilders.ratewatch.domain.model

data class CurrencyName(
    val code: CurrencyCode,
    val name: String,
) {
    companion object {
        val EMPTY = CurrencyName("", "")
    }
}
