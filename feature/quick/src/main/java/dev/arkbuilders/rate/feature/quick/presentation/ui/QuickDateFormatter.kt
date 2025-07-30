package dev.arkbuilders.rate.feature.quick.presentation.ui

import android.content.Context
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.utils.DateFormatUtils
import java.time.Duration
import java.time.OffsetDateTime

object QuickDateFormatter {
    fun pairCalculatedTime(
        ctx: Context,
        date: OffsetDateTime,
    ): String {
        val now = OffsetDateTime.now()
        val dur = Duration.between(date, now)

        val days = dur.toDays()
        return if (days >= 7) {
            ctx.getString(CoreRString.quick_calculated_on, DateFormatUtils.formatDateOnly(date))
        } else {
            ctx.getString(
                CoreRString.quick_calculated_ago,
                DateFormatUtils.formatElapsedTime(ctx, now, date),
            )
        }
    }

    fun pairRefreshedTime(
        ctx: Context,
        date: OffsetDateTime,
    ): String {
        val now = OffsetDateTime.now()
        return ctx.getString(
            CoreRString.quick_last_refreshed_ago,
            DateFormatUtils.formatElapsedTime(ctx, now, date),
        )
    }
}
