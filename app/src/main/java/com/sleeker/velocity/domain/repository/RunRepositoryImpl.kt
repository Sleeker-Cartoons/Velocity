package com.sleeker.velocity.domain.repository

import com.sleeker.velocity.data.local.RunDao
import com.sleeker.velocity.data.model.RunEntity
import com.sleeker.velocity.domain.repository.RunRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RunRepositoryImpl @Inject constructor(
    private val runDao: RunDao
) : RunRepository {
    override suspend fun createRun(run: RunEntity): Long = runDao.insertRun(run)

    override suspend fun updateRun(run: RunEntity) = runDao.updateRun(run)

    override suspend fun deleteRun(run: RunEntity) = runDao.deleteRun(run)

    override suspend fun getRun(id: Long): RunEntity? = runDao.getRunById(id)

    override fun getAllRuns(): Flow<List<RunEntity>> = runDao.getAllRunsFlow()

    override fun getLatestRun(): Flow<RunEntity?> = runDao.getLatestRunFlow()

    override suspend fun getTotalDistance(): Double =
        runDao.getTotalDistance() ?: 0.0

    override suspend fun getTotalRunCount(): Int =
        runDao.getTotalRunCount()
}