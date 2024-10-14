package dev.arkbuilders.ratewatch.data.db

import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.arkbuilders.ratewatch.data.db.dao.CodeUseStatDao
import dev.arkbuilders.ratewatch.data.db.dao.CurrencyRateDao
import dev.arkbuilders.ratewatch.data.db.dao.PairAlertDao
import dev.arkbuilders.ratewatch.data.db.dao.PortfolioDao
import dev.arkbuilders.ratewatch.data.db.dao.QuickPairDao
import dev.arkbuilders.ratewatch.data.db.dao.TimestampDao
import dev.arkbuilders.ratewatch.data.db.entity.RoomAsset
import dev.arkbuilders.ratewatch.data.db.entity.RoomCodeUseStat
import dev.arkbuilders.ratewatch.data.db.entity.RoomCurrencyRate
import dev.arkbuilders.ratewatch.data.db.entity.RoomFetchTimestamp
import dev.arkbuilders.ratewatch.data.db.entity.RoomPairAlert
import dev.arkbuilders.ratewatch.data.db.entity.RoomQuickPair
import dev.arkbuilders.ratewatch.data.db.typeconverters.ListAmountTypeConverter
import dev.arkbuilders.ratewatch.data.db.typeconverters.OffsetDateTimeTypeConverter

@androidx.room.Database(
    entities = [
        RoomAsset::class,
        RoomCurrencyRate::class,
        RoomFetchTimestamp::class,
        RoomPairAlert::class,
        RoomQuickPair::class,
        RoomCodeUseStat::class,
    ],
    version = 13,
    exportSchema = false,
)
@TypeConverters(ListAmountTypeConverter::class, OffsetDateTimeTypeConverter::class)
abstract class Database : RoomDatabase() {
    abstract fun assetsDao(): PortfolioDao

    abstract fun rateDao(): CurrencyRateDao

    abstract fun fetchTimestampDao(): TimestampDao

    abstract fun pairAlertDao(): PairAlertDao

    abstract fun quickDao(): QuickPairDao

    abstract fun codeUseStatDao(): CodeUseStatDao

    companion object {
        const val DB_NAME = "arkrate.db"
    }
}
