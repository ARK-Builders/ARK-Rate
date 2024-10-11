package dev.arkbuilders.rate.data.db.typeconverters

import androidx.room.TypeConverter
import java.math.BigDecimal

class BigDecimalTypeConverter {
    @TypeConverter
    fun fromBigDecimal(bigDecimal: BigDecimal): String = bigDecimal.toPlainString()

    @TypeConverter
    fun toBigDecimal(bigDecimal: String): BigDecimal = BigDecimal(bigDecimal)
}
