package com.sleeker.velocity.ui.tracking

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeker.velocity.data.model.RunEntity
import com.sleeker.velocity.domain.repository.RunRepository
import com.sleeker.velocity.domain.usecase.TrackingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val runRepository: RunRepository,
    private val trackingUseCase: TrackingUseCase
) : ViewModel() {

    enum class TrackingState { IDLE, TRACKING, PAUSED }

    private val _trackingState = MutableStateFlow(TrackingState.IDLE)
    val trackingState: StateFlow<TrackingState> = _trackingState.asStateFlow()

    private val _distance = MutableStateFlow(0.0)
    val distance: StateFlow<Double> = _distance.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _currentPace = MutableStateFlow(0.0)
    val currentPace: StateFlow<Double> = _currentPace.asStateFlow()

    private val _avgPace = MutableStateFlow(0.0)
    val avgPace: StateFlow<Double> = _avgPace.asStateFlow()

    private val _calories = MutableStateFlow(0)
    val calories: StateFlow<Int> = _calories.asStateFlow()

    private val _polylinePoints = MutableStateFlow<List<Pair<Double, Double>>>(emptyList())
    val polylinePoints: StateFlow<List<Pair<Double, Double>>> = _polylinePoints.asStateFlow()

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private var startTime: LocalDateTime? = null
    private var pausedTime: Long = 0L
    private var runId: Long = -1L

    fun startTracking() {
        viewModelScope.launch {
            _trackingState.value = TrackingState.TRACKING
            startTime = LocalDateTime.now()
            _distance.value = 0.0
            _duration.value = 0L
            _calories.value = 0
            _polylinePoints.value = emptyList()

            while (_trackingState.value == TrackingState.TRACKING) {
                val elapsed = System.currentTimeMillis() - (startTime?.let {
                    java.time.ZonedDateTime.of(it, java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                } ?: System.currentTimeMillis())
                _duration.value = elapsed / 1000

                if (_distance.value > 0 && _duration.value > 0) {
                    _avgPace.value = (_duration.value.toDouble() / _distance.value) / 60.0
                    _calories.value = trackingUseCase.calculateCalories(_distance.value, _avgPace.value)
                }

                delay(1000)
            }
        }
    }

    fun pauseTracking() {
        _trackingState.value = TrackingState.PAUSED
        pausedTime = System.currentTimeMillis()
    }

    fun resumeTracking() {
        _trackingState.value = TrackingState.TRACKING
    }

    fun stopTracking() {
        viewModelScope.launch {
            _trackingState.value = TrackingState.IDLE

            if (_distance.value > 0 && startTime != null) {
                val run = RunEntity(
                    startTime = startTime!!,
                    endTime = LocalDateTime.now(),
                    distanceKm = _distance.value,
                    durationSeconds = _duration.value,
                    avgPacePerKm = _avgPace.value,
                    caloriesBurned = _calories.value,
                    maxPacePerKm = 0.0,
                    minPacePerKm = 0.0,
                    polylinePoints = _polylinePoints.value.toString(),
                    isCompleted = true
                )
                runId = runRepository.createRun(run)
            }

            resetTracking()
        }
    }

    fun updateLocationAndDistance(location: Location) {
        _currentLocation.value = location
        val currentPoint = Pair(location.latitude, location.longitude)
        val points = _polylinePoints.value.toMutableList()

        if (points.isNotEmpty()) {
            val lastPoint = points.last()
            val newDistance = trackingUseCase.calculateDistance(
                lastPoint.first, lastPoint.second,
                location.latitude, location.longitude
            )
            _distance.value += newDistance

            if (_duration.value > 0 && newDistance > 0) {
                _currentPace.value = (_duration.value.toDouble() / _distance.value) / 60.0
            }
        }

        points.add(currentPoint)
        _polylinePoints.value = points
    }

    private fun resetTracking() {
        startTime = null
        pausedTime = 0L
        _distance.value = 0.0
        _duration.value = 0L
        _currentPace.value = 0.0
        _avgPace.value = 0.0
        _calories.value = 0
        _polylinePoints.value = emptyList()
        _currentLocation.value = null
    }

    fun formatPace(paceSeconds: Double): String =
        trackingUseCase.paceSecondsToString(paceSeconds)

    fun formatDuration(seconds: Long): String =
        trackingUseCase.durationToString(seconds)
}