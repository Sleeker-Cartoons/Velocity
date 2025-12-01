package com.sleeker.velocity.domain.repository

import com.sleeker.velocity.data.model.RunEntity
import kotlinx.coroutines.flow.Flow

interface RunRepository {
    suspend fun createRun(run: RunEntity): Long
    suspend fun updateRun(run: RunEntity)
    suspend fun deleteRun(run: RunEntity)
    suspend fun getRun(id: Long): RunEntity?
    fun getAllRuns(): Flow<List<RunEntity>>
    fun getLatestRun(): Flow<RunEntity?>
    suspend fun getTotalDistance(): Double
    suspend fun getTotalRunCount(): Int
}
