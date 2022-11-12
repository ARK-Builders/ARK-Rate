package space.taran.arkrate.data.db

import androidx.room.RoomDatabase

@androidx.room.Database(
    entities = [
        RoomCurrencyAmount::class,
        RoomCurrencyRate::class,
        RoomFetchTimestamp::class
    ],
    version = 1,
    exportSchema = false
)
abstract class Database : RoomDatabase() {
    abstract fun assetsDao(): AssetsDao
    abstract fun rateDao(): CurrencyRateDao
    abstract fun fetchTimestampDao(): FetchTimestampDao

    companion object {
        const val DB_NAME = "arkrate.db"
    }
}