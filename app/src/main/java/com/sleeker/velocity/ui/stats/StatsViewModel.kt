package com.sleeker.velocity.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeker.velocity.data.model.RunEntity
import com.sleeker.velocity.domain.repository.RunRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val runRepository: RunRepository
) : ViewModel() {

    val allRuns: StateFlow<List<RunEntity>> = runRepository.getAllRuns()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val totalDistance: StateFlow<Double> = allRuns.map { runs ->
        runs.sumOf { it.distanceKm }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val thisMonthDistance: StateFlow<Double> = allRuns.map { runs ->
        val now = LocalDateTime.now()
        val startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth())
        runs.filter { it.startTime.isAfter(startOfMonth) }
            .sumOf { it.distanceKm }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val totalRuns: StateFlow<Int> = allRuns.map { it.size }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val currentStreak: StateFlow<Int> = allRuns.map { runs ->
        if (runs.isEmpty()) 0 else 1 // Simplified: count consecutive days with a run
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val weeklyData: StateFlow<List<Pair<String, Double>>> = allRuns.map { runs ->
        val last12Weeks = (11 downTo 0).map { week ->
            val date = LocalDateTime.now().minusWeeks(week.toLong())
            val weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val weekEnd = weekStart.plusDays(7)
            val distance = runs.filter {
                it.startTime.isAfter(weekStart) && it.startTime.isBefore(weekEnd)
            }.sumOf { it.distanceKm }
            "W${12 - week}" to distance
        }
        last12Weeks
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    //major overlook happenibg here. should be death with later
    fun getPersonalRecords(runs: List<RunEntity>): Map<String, String> = mapOf(
        "Fastest 5K" to (runs
            .filter { it.distanceKm >= 5 }
            .minByOrNull { it.avgPacePerKm }
            ?.let { formatPace(it.avgPacePerKm) } ?: "--:--"),

        "Fastest 10K" to (runs
            .filter { it.distanceKm >= 10 }
            .minByOrNull { it.avgPacePerKm }
            ?.let { formatPace(it.avgPacePerKm) } ?: "--:--"),

        "Longest Run" to (runs.maxByOrNull { it.distanceKm }
            ?.let { formatDistance(it.distanceKm) } ?: "--"),

        "Most Calories" to (runs.maxByOrNull { it.caloriesBurned }
            ?.let { "${it.caloriesBurned} kcal" } ?: "--")
    )

    fun formatDistance(km: Double): String = String.format("%.2f km", km)

    fun formatPace(pacePerKm: Double): String {
        val minutes = pacePerKm.toLong()
        val seconds = ((pacePerKm - minutes) * 60).toLong()
        return String.format("%d'%02d\"", minutes, seconds)
    }
}