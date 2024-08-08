package dev.arkbuilders.rate.presentation.utils

import android.content.Context
import dev.arkbuilders.rate.R
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateFormatUtils {
    fun latestCheckElapsedTime(
        ctx: Context,
        now: OffsetDateTime,
        date: OffsetDateTime
    ): String {
        var dur = Duration.between(date, now)

        val days = dur.toDays()
        if (days > 0) {
            return ctx.resources.getQuantityString(
                R.plurals.plurals_day,
                days.toInt(),
                days.toInt()
            )
        }

        val hours = dur.toHours()
        if (hours > 0) {
            return ctx.resources.getQuantityString(
                R.plurals.plurals_hour,
                hours.toInt(),
                hours.toInt()
            )
        }
        dur = dur.minusHours(hours)

        val minutes = dur.toMinutes()
        if (minutes > 0) {
            return ctx.resources.getQuantityString(
                R.plurals.plurals_minute,
                minutes.toInt(),
                minutes.toInt()
            )
        }
        dur = dur.minusMinutes(hours)

        val seconds = dur.seconds
        return ctx.resources.getQuantityString(
            R.plurals.plurals_second,
            seconds.toInt(),
            seconds.toInt()
        )
    }

    fun latestCheckTime(
        date: OffsetDateTime
    ): String {
        val format = DateTimeFormatter
            .ofPattern("hh:mm a, dd MMM yyyy", Locale.ENGLISH)
        return format.format(date)
    }

    fun calculatedOn(
        date: OffsetDateTime
    ): String {
        val format = DateTimeFormatter
            .ofPattern("dd MMM yyyy", Locale.ENGLISH)
        return format.format(date)
    }

    fun notifiedOn(
        date: OffsetDateTime
    ): String {
        val format =
            DateTimeFormatter.ofPattern("MMM dd - hh:mm a", Locale.ENGLISH)
        return format.format(date)
    }
}
