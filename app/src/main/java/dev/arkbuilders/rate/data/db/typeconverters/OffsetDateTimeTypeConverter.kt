package dev.arkbuilders.rate.data.db.typeconverters

import androidx.room.TypeConverter
import java.time.OffsetDateTime

class OffsetDateTimeTypeConverter {
    @TypeConverter
    fun fromOffsetDateTime(date: OffsetDateTime): String = date.toString()

    @TypeConverter
    fun toOffsetDateTime(date: String): OffsetDateTime = OffsetDateTime.parse(date)
}
