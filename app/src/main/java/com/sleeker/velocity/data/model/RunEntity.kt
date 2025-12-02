package com.sleeker.velocity.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "runs")
data class RunEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime?,
    val distanceKm: Double,
    val durationSeconds: Long,
    val avgPacePerKm: Double,
    val caloriesBurned: Int,
    val maxPacePerKm: Double,
    val minPacePerKm: Double,
    val maxSpeedKmph: Double = 0.0,
    val polylinePoints: String, // Encoded polyline or JSON list of LatLng
    val isCompleted: Boolean = false,
    val gpxData: String? = null, // GPX export data
)
