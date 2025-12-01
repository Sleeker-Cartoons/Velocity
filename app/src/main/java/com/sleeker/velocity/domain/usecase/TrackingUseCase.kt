package com.sleeker.velocity.domain.usecase

import com.sleeker.velocity.data.model.RunEntity
import com.sleeker.velocity.data.model.SplitData
import com.sleeker.velocity.domain.repository.RunRepository
import javax.inject.Inject
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class TrackingUseCase @Inject constructor(
    private val runRepository: RunRepository
) {
    suspend fun saveRun(run: RunEntity) = runRepository.createRun(run)

    suspend fun updateRun(run: RunEntity) = runRepository.updateRun(run)

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0 // Radius of Earth in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * kotlin.math.atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    fun generateSplits(
        distance: Double,
        duration: Long,
        avgPace: Double,
        splitUnit: Double = 1.0 // 1.0 = 1km splits
    ): List<SplitData> {
        val splits = mutableListOf<SplitData>()
        val numSplits = (distance / splitUnit).toInt()

        repeat(numSplits) { index ->
            val splitDistance = splitUnit
            val splitDuration = (splitDistance / (distance / duration)) * 1000 // in seconds as Long
            val splitPace = splitDuration / splitDistance / 60.0
            val caloriesPerKm = 60.0 // Rough estimate
            val splitCalories = (caloriesPerKm * splitDistance).toInt()

            splits.add(
                SplitData(
                    splitNumber = index + 1,
                    distance = splitDistance,
                    duration = splitDuration.toLong(),
                    pace = splitPace,
                    calories = splitCalories
                )
            )
        }
        return splits
    }

    fun calculateCalories(distance: Double, pace: Double): Int {
        // Rough formula: ~60 calories per km for average person
        return (distance * 60).toInt()
    }

    fun paceSecondsToString(paceSeconds: Double): String {
        val minutes = paceSeconds.toLong() / 60
        val seconds = paceSeconds.toLong() % 60
        return String.format("%d'%02d\"", minutes, seconds)
    }

    fun durationToString(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, secs)
        } else {
            String.format("%02d:%02d", minutes, secs)
        }
    }
}