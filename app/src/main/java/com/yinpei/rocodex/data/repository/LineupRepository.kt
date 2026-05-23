package com.yinpei.rocodex.data.repository

import com.yinpei.rocodex.data.local.LineupDao
import com.yinpei.rocodex.data.model.Lineup
import kotlinx.coroutines.flow.Flow

class LineupRepository(private val lineupDao: LineupDao) {

    fun getAllLineups(): Flow<List<Lineup>> {
        return lineupDao.getAllLineups()
    }

    suspend fun getLineupById(id: Int): Lineup? {
        return lineupDao.getLineupById(id)
    }

    suspend fun insertLineup(lineup: Lineup): Long {
        return lineupDao.insertLineup(lineup)
    }

    suspend fun updateLineup(lineup: Lineup) {
        lineupDao.updateLineup(lineup)
    }

    suspend fun deleteLineup(lineup: Lineup) {
        lineupDao.deleteLineup(lineup)
    }
}
