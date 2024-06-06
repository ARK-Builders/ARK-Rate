package dev.arkbuilders.rate.data.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.domain.model.stats.CodeUseStat
import dev.arkbuilders.rate.domain.repo.CodeUseStatRepo
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Entity
data class RoomCodeUseStat(
    @PrimaryKey
    val code: CurrencyCode,
    val count: Long,
    val lastUsedDate: String
)

@Dao
interface CodeUseStatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(state: RoomCodeUseStat)

    @Query("SELECT * FROM RoomCodeUseStat WHERE code = :code")
    suspend fun getByCode(code: CurrencyCode): RoomCodeUseStat?

    @Query("SELECT * FROM RoomCodeUseStat")
    suspend fun getAll(): List<RoomCodeUseStat>
}

@Singleton
class CodeUseStatRepoImpl @Inject constructor(private val dao: CodeUseStatDao) : CodeUseStatRepo {

    override suspend fun codesUsed(vararg codes: CurrencyCode) {
        codes.forEach { code ->
            val old = dao.getByCode(code)?.toCodeUseStat()
            val new =
                old?.copy(count = old.count.inc(), lastUsedDate = OffsetDateTime.now())
                    ?: CodeUseStat(code, 1, OffsetDateTime.now())

            dao.insert(new.toRoom())
        }
    }

    override suspend fun getAll(): Map<CurrencyCode, CodeUseStat> =
        dao.getAll().map { it.toCodeUseStat() }.associateBy { it.code }

}

private fun RoomCodeUseStat.toCodeUseStat() =
    CodeUseStat(code, count, OffsetDateTime.parse(lastUsedDate))

private fun CodeUseStat.toRoom() =
    RoomCodeUseStat(code, count, lastUsedDate.toString())