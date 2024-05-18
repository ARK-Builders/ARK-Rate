package dev.arkbuilders.rate.data.db

import androidx.room.RoomDatabase

@androidx.room.Database(
    entities = [
        RoomCurrencyAmount::class,
        RoomCurrencyRate::class,
        RoomFetchTimestamp::class,
        RoomPairAlert::class,
        RoomQuickPair::class,
    ],
    version = 2,
    exportSchema = true,
)
abstract class Database : RoomDatabase() {
    abstract fun assetsDao(): AssetsDao
    abstract fun rateDao(): CurrencyRateDao
    abstract fun fetchTimestampDao(): FetchTimestampDao
    abstract fun pairAlertDao(): PairAlertDao
    abstract fun quickDao(): QuickPairDao

    companion object {
        const val DB_NAME = "arkrate.db"
    }
}