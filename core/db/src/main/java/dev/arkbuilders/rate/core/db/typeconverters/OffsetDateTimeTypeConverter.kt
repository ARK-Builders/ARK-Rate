package dev.arkbuilders.rate.core.db.typeconverters

import androidx.room.TypeConverter
import java.time.OffsetDateTime

object OffsetDateTimeTypeConverter {
    @TypeConverter
    fun fromOffsetDateTime(date: OffsetDateTime): String = date.toString()

    @TypeConverter
    fun toOffsetDateTime(dateString: String): OffsetDateTime {
        val date = OffsetDateTime.parse(dateString)
        val offset = OffsetDateTime.now().offset
        return date.withOffsetSameInstant(offset)
    }
}
