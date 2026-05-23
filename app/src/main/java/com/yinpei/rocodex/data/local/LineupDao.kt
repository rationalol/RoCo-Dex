package com.yinpei.rocodex.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.yinpei.rocodex.data.model.Lineup
import kotlinx.coroutines.flow.Flow

@Dao
interface LineupDao {
    @Query("SELECT * FROM lineups")
    fun getAllLineups(): Flow<List<Lineup>>

    @Query("SELECT * FROM lineups WHERE id = :id")
    suspend fun getLineupById(id: Int): Lineup?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLineup(lineup: Lineup): Long

    @Update
    suspend fun updateLineup(lineup: Lineup)

    @Delete
    suspend fun deleteLineup(lineup: Lineup)
}
