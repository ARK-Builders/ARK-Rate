package dev.arkbuilders.rate.core.db.typeconverters

import androidx.room.TypeConverter
import java.time.OffsetDateTime

object OffsetDateTimeTypeConverter {
    @TypeConverter
    fun fromOffsetDateTime(date: OffsetDateTime): String = date.toString()

    @TypeConverter
    fun toOffsetDateTime(date: String): OffsetDateTime = OffsetDateTime.parse(date)
}
