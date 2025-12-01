package com.sleeker.velocity.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sleeker.velocity.data.model.RunEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface RunDao {
    @Insert
    suspend fun insertRun(run: RunEntity): Long

    @Update
    suspend fun updateRun(run: RunEntity)

    @Delete
    suspend fun deleteRun(run: RunEntity)

    @Query("SELECT * FROM runs WHERE id = :runId")
    suspend fun getRunById(runId: Long): RunEntity?

    @Query("SELECT * FROM runs ORDER BY startTime DESC")
    fun getAllRunsFlow(): Flow<List<RunEntity>>

    @Query("SELECT * FROM runs ORDER BY startTime DESC LIMIT 1")
    fun getLatestRunFlow(): Flow<RunEntity?>

    @Query("SELECT * FROM runs WHERE startTime >= :startDate AND startTime < :endDate ORDER BY startTime DESC")
    fun getRunsInDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<RunEntity>>

    @Query("DELETE FROM runs")
    suspend fun deleteAllRuns()

    @Query("SELECT COUNT(*) FROM runs")
    suspend fun getTotalRunCount(): Int

    @Query("SELECT SUM(distanceKm) FROM runs WHERE isCompleted = 1")
    suspend fun getTotalDistance(): Double?
}