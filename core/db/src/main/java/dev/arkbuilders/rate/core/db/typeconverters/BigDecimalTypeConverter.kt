package dev.arkbuilders.rate.core.db.typeconverters

import androidx.room.TypeConverter
import java.math.BigDecimal

object BigDecimalTypeConverter {
    @TypeConverter
    fun fromBigDecimal(bigDecimal: BigDecimal): String = bigDecimal.toPlainString()

    @TypeConverter
    fun toBigDecimal(bigDecimal: String): BigDecimal = BigDecimal(bigDecimal)
}
