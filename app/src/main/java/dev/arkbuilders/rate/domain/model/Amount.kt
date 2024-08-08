package dev.arkbuilders.rate.domain.model

import dev.arkbuilders.rate.data.toDoubleSafe

data class Amount(val code: CurrencyCode, val value: Double)

data class AmountStr(val code: CurrencyCode, val value: String)

fun Amount.toStrAmount() = AmountStr(code, value.toString())

fun AmountStr.toDAmount() = Amount(code, value.toDoubleSafe())
