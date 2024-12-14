package dev.arkbuilders.rate.core.db.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.arkbuilders.rate.core.domain.model.Amount
import dev.arkbuilders.rate.core.domain.model.AmountStr
import dev.arkbuilders.rate.core.domain.model.toAmount
import dev.arkbuilders.rate.core.domain.model.toStrAmount

class ListAmountTypeConverter {
    @TypeConverter
    fun fromListAmount(list: List<Amount>): String {
        val type = object : TypeToken<List<AmountStr>>() {}.type
        val listStr = list.map { it.toStrAmount() }
        return Gson().toJson(listStr, type)
    }

    @TypeConverter
    fun toListAmount(list: String): List<Amount> {
        val type = object : TypeToken<List<AmountStr>>() {}.type
        return Gson().fromJson<List<AmountStr>>(list, type).map { it.toAmount() }
    }
}
