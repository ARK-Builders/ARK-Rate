package com.someone.exchange.storage

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import java.io.File

@JsonClass(generateAdapter = true)
data class total(
    val total: MutableList<Exchange>
)

@JsonClass(generateAdapter = true)
data class Exchange(
    val name: String,
    var number: Double
)


class AppDatabase(val filePath: String) {

    fun getAllExchange(): List<Exchange> {
        if (!File(filePath).canWrite()) {
            File(filePath).writeText("""{"total":[]}""")
        }
        val origin = File(filePath).readText()
        val moshi: Moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(total::class.java)
        val json = jsonAdapter.fromJson(origin)
        return json?.total ?: listOf()
    }

    fun setExchange(name: String, value: Double) {

        if (!File(filePath).canWrite()) {
            File(filePath).writeText("""{"total":[]}""")
        }
        val origin = File(filePath).readText()
        val moshi: Moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(total::class.java)
        val json = jsonAdapter.fromJson(origin) ?: total(mutableListOf())
        for (i in (0 until if (json.total.size == 0) {
            1
        } else {
            json.total.size
        })) {
            if (json.total.size > 0 && json.total[i].name == name) {
                json.total[i].number = value
                break
            }
            if (json.total.size == 0 || i == json.total.size - 1) {
                json.total.add(Exchange(name, value))
            }
        }
        File(filePath).writeText(jsonAdapter.toJson(json))
    }

    fun remove(name: String) {
        if (!File(filePath).canWrite()) {
            File(filePath).writeText("""{"total":[]}""")
        }
        val origin = File(filePath).readText()
        val moshi: Moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(total::class.java)
        val json = jsonAdapter.fromJson(origin) ?: total(mutableListOf())

        for (i in (0 until json.total.size)) {
            if (json.total[i].name == name) {
                json.total.removeAt(i)
                break
            }
        }
        File(filePath).writeText(jsonAdapter.toJson(json))
    }
}