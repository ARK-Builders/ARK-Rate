package dev.arkbuilders.rate.data.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import dev.arkbuilders.rate.domain.model.CurrencyType
import java.time.OffsetDateTime
import javax.inject.Inject

enum class TimestampType {
    FetchCrypto, FetchFiat, CheckPairAlerts
}

@Entity
data class RoomFetchTimestamp(
    @PrimaryKey
    val currencyType: String,
    val timestamp: String
)

@Dao
interface TimestampDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fetchTimestamp: RoomFetchTimestamp)

    @Query("SELECT * FROM RoomFetchTimestamp where currencyType = :currencyType")
    suspend fun getTimestamp(currencyType: String): RoomFetchTimestamp?
}

class TimestampRepo @Inject constructor(private val dao: TimestampDao) {
    suspend fun rememberTimestamp(type: TimestampType) =
        dao.insert(RoomFetchTimestamp(type.name, OffsetDateTime.now().toString()))

    suspend fun getTimestamp(type: TimestampType) =
        dao.getTimestamp(type.name)?.timestamp?.let { OffsetDateTime.parse(it) }
}

