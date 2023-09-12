package dev.arkbuilders.rate.data.db

import androidx.room.AutoMigration
import androidx.room.RoomDatabase

@androidx.room.Database(
    entities = [
        RoomCurrencyAmount::class,
        RoomCurrencyRate::class,
        RoomFetchTimestamp::class,
        RoomPairAlertCondition::class,
        RoomQuickCurrency::class,
        RoomQuickBaseCurrency::class
    ],
    version = 8,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 3, to = 8)
    ]
)
abstract class Database : RoomDatabase() {
    abstract fun assetsDao(): AssetsDao
    abstract fun rateDao(): CurrencyRateDao
    abstract fun fetchTimestampDao(): FetchTimestampDao
    abstract fun pairAlertDao(): PairAlertConditionDao
    abstract fun quickDao(): QuickCurrencyDao
    abstract fun quickBaseCurrencyDao(): QuickBaseCurrencyDao

    companion object {
        const val DB_NAME = "arkrate.db"
    }
}