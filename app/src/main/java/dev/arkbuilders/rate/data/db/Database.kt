package dev.arkbuilders.rate.data.db

import androidx.room.RoomDatabase

@androidx.room.Database(
    entities = [
        RoomAsset::class,
        RoomCurrencyRate::class,
        RoomFetchTimestamp::class,
        RoomPairAlert::class,
        RoomQuickPair::class,
        RoomCodeUseStat::class
    ],
    version = 8,
    exportSchema = true,
)
abstract class Database : RoomDatabase() {
    abstract fun assetsDao(): PortfolioDao
    abstract fun rateDao(): CurrencyRateDao
    abstract fun fetchTimestampDao(): FetchTimestampDao
    abstract fun pairAlertDao(): PairAlertDao
    abstract fun quickDao(): QuickPairDao
    abstract fun codeUseStatDao(): CodeUseStatDao

    companion object {
        const val DB_NAME = "arkrate.db"
    }
}