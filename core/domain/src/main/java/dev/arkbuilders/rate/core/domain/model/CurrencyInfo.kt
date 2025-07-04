package dev.arkbuilders.rate.core.domain.model

data class CurrencyInfo(
    val code: CurrencyCode,
    val name: String,
    val country: List<String>,
) {
    companion object {
        val EMPTY = CurrencyInfo("", "", emptyList())

        fun emptyWithCode(code: CurrencyCode) = EMPTY.copy(code = code)
    }
}
