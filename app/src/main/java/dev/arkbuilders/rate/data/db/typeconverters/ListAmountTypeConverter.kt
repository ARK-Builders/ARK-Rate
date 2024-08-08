package dev.arkbuilders.rate.data.db.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.arkbuilders.rate.domain.model.Amount

class ListAmountTypeConverter {
    @TypeConverter
    fun fromListAmount(list: List<Amount>): String {
        val type = object : TypeToken<List<Amount>>() {}.type
        return Gson().toJson(list, type)
    }

    @TypeConverter
    fun toListAmount(list: String): List<Amount> {
        val type = object : TypeToken<List<Amount>>() {}.type
        return Gson().fromJson(list, type)
    }
}
