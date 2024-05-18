package dev.arkbuilders.rate.data

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

object CurrUtils {
    fun validateInput(
        oldInput: String,
        newInput: String
    ): String {
        val containsDigitsAndDot = Regex("[0-9]*\\.?[0-9]*")
        if (!containsDigitsAndDot.matches(newInput))
            return oldInput

        val containsDigit = Regex(".*[0-9].*")
        if (!containsDigit.matches(newInput)) {
            return newInput
        }

        val leadingZeros = "^0+(?=\\d)".toRegex()

        return newInput.replace(leadingZeros, "")
    }

    fun prepareToDisplay(value: Double): String {
        val formatSymbols = DecimalFormatSymbols(Locale.ENGLISH)
        formatSymbols.decimalSeparator = '.'
        formatSymbols.groupingSeparator = ','
        val numberFormatter = DecimalFormat("###,###.##", formatSymbols)
        return numberFormatter.format(value)
    }

    fun roundOff(number: Double): Double {
        val fractionSize =
            if (number > 10) 2 else 8

        val df = DecimalFormat(
            "#." + "#".repeat(fractionSize),
            DecimalFormatSymbols(Locale.ENGLISH)
        )
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }
}