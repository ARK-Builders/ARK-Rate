package dev.arkbuilders.rate.presentation.utils

import android.content.Context
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.domain.model.CurrencyCode

object IconUtils {
    fun iconForCurrCode(ctx: Context, code: CurrencyCode): Int {
        var lowercaseCode = code.lowercase()
        // try is reserved work, but we have Turkish lira
        if (lowercaseCode == "try") {
            lowercaseCode = "curr_try"
        }

        val drawableID = ctx.resources
            .getIdentifier(
                lowercaseCode,
                "drawable",
                ctx.packageName
            )

        return if (drawableID > 0) {
            drawableID
        } else {
            R.drawable.ic_earth
        }
    }
}
