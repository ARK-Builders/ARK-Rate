package dev.arkbuilders.rate.data

import android.icu.util.Currency
import dev.arkbuilders.rate.domain.model.CurrencyCode
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object CurrUtils {
    fun validateInput(
        oldInput: String,
        newInput: String
    ): String {
        val containsDigitsAndDot = Regex("[0-9]*\\.?[0-9]*")
        if (!containsDigitsAndDot.matches(newInput)) {
            return oldInput
        }

        val leadingZeros = "^0+(?=\\d)".toRegex()

        return newInput.replace(leadingZeros, "")
    }

    fun validateInputWithMinusChar(
        oldInput: String,
        newInput: String
    ): String {
        val containsDigitsAndDot = Regex("-?[0-9]*\\.?[0-9]*")
        if (!containsDigitsAndDot.matches(newInput)) {
            return oldInput
        }

        val leadingZeros = "^0+(?=\\d)".toRegex()

        return newInput.replace(leadingZeros, "")
    }

    fun prepareToDisplay(value: Double): String {
        var fractionSize = if (value > 10) 2 else 8

        val fractionalPart = value.toLong() - value
        if (fractionalPart == 0.0) {
            fractionSize = 0
        }
        val fractionPattern = if (fractionSize == 0) {
            ""
        } else {
            "." + "#".repeat(fractionSize)
        }

        val formatSymbols = DecimalFormatSymbols(Locale.ENGLISH)
        formatSymbols.decimalSeparator = '.'
        formatSymbols.groupingSeparator = ','
        val numberFormatter = DecimalFormat("###,###$fractionPattern", formatSymbols)
        return numberFormatter.format(value)
    }

    fun roundOff(number: Double): String {
        val fractionSize =
            if (number > 10) 2 else 8

        val df = DecimalFormat(
            "#." + "#".repeat(fractionSize),
            DecimalFormatSymbols(Locale.ENGLISH)
        )
        df.roundingMode = RoundingMode.CEILING
        return df.format(number)
    }

    fun getSymbolOrCode(code: CurrencyCode): String {
        return try {
            Currency.getInstance(code).symbol
        } catch (e: Throwable) {
            code
        }
    }
}

fun String.toDoubleSafe() = when {
    this == "" -> 0.0
    this == "-" -> 0.0
    else -> this.toDouble()
}
