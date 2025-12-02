package com.sleeker.velocity.ui.tracking

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeker.velocity.data.model.RunEntity
import com.sleeker.velocity.domain.repository.RunRepository
import com.sleeker.velocity.domain.service.LocationManager
import com.sleeker.velocity.domain.usecase.TrackingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val runRepository: RunRepository,
    private val trackingUseCase: TrackingUseCase,
    private val locationManager: LocationManager // <--- 1. Inject LocationManager
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
    private var locationJob: Job? = null

    init {
        // <--- 2. Listen for location updates permanently
        viewModelScope.launch {
            locationManager.locationUpdates.collect { location ->
                if (_trackingState.value == TrackingState.TRACKING) {
                    updateLocationAndDistance(location)
                }
            }
        }
    }

    fun startTracking() {
        viewModelScope.launch {
            // <--- 3. Tell the Service to start collecting GPS data
            locationManager.startTracking()

            _trackingState.value = TrackingState.TRACKING
            startTime = LocalDateTime.now()

            // Reset data if starting fresh
            if (_duration.value == 0L) {
                _distance.value = 0.0
                _calories.value = 0
                _polylinePoints.value = emptyList()
            }

            // Timer Loop
            while (_trackingState.value == TrackingState.TRACKING) {
                val elapsed = System.currentTimeMillis() - (startTime?.let {
                    java.time.ZonedDateTime.of(it, ZoneId.systemDefault()).toInstant().toEpochMilli()
                } ?: System.currentTimeMillis())

                // Only update duration if we are not paused (simple logic for now)
                _duration.value = elapsed / 1000

                // Recalculate Averages
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
        // Note: We keep the service running so we don't lose GPS lock,
        // but we ignore updates in the collector above.
    }

    fun resumeTracking() {
        _trackingState.value = TrackingState.TRACKING
        // You would need more complex logic here to handle "pause duration" subtraction
        // For now, we just resume the state.
    }

    fun stopTracking() {
        viewModelScope.launch {
            _trackingState.value = TrackingState.IDLE

            // <--- 4. Stop the GPS Service
            locationManager.stopTracking()

            // Save Run Logic (Fixed for treadmill/stationary runs)
            if ((_distance.value > 0 || _duration.value > 0) && startTime != null) {
                val run = RunEntity(
                    startTime = startTime!!,
                    endTime = LocalDateTime.now(),
                    distanceKm = _distance.value,
                    durationSeconds = _duration.value,
                    avgPacePerKm = _avgPace.value,
                    caloriesBurned = _calories.value,
                    maxPacePerKm = 0.0,
                    minPacePerKm = 0.0,
                    maxSpeedKmph = currentMaxSpeedKmph,
                    polylinePoints = _polylinePoints.value.toString(), // Simplified serialization
                    isCompleted = true
                )
                runRepository.createRun(run)
            }

            resetTracking()
        }
    }

    private var lastLocationTimestamp: Long = 0L

    private var currentMaxSpeedKmph: Double = 0.0

    private fun updateLocationAndDistance(location: Location) {
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

            // --- SPEED CALCULATION LOGIC ---
            val currentTime = System.currentTimeMillis()
            var instantSpeedKmph = 0.0

            // 1. Get speed from GPS (Best)
            if (location.hasSpeed() && location.speed > 0) {
                instantSpeedKmph = (location.speed * 3.6)
            }
            // 2. Fallback: Calculate manually
            else if (newDistance > 0 && lastLocationTimestamp > 0) {
                val timeDeltaSeconds = (currentTime - lastLocationTimestamp) / 1000.0
                if (timeDeltaSeconds > 0) {
                    instantSpeedKmph = (newDistance / (timeDeltaSeconds / 3600.0))
                }
            }

            // 3. Update Max Speed if this is the fastest we've gone
            if (instantSpeedKmph > currentMaxSpeedKmph) {
                currentMaxSpeedKmph = instantSpeedKmph
            }

            // 4. Update Pace UI (existing logic)
            if(instantSpeedKmph > 0) {
                _currentPace.value = 60.0 / instantSpeedKmph
            }

            lastLocationTimestamp = currentTime
        } else {
            lastLocationTimestamp = System.currentTimeMillis()
        }

        points.add(currentPoint)
        _polylinePoints.value = points
    }


//    private fun updateLocationAndDistance(location: Location) {
//        _currentLocation.value = location
//        val currentPoint = Pair(location.latitude, location.longitude)
//        val points = _polylinePoints.value.toMutableList()
//
//        if (points.isNotEmpty()) {
//            val lastPoint = points.last()
//
//            // 1. Calculate the distance moved
//            val newDistance = trackingUseCase.calculateDistance(
//                lastPoint.first, lastPoint.second,
//                location.latitude, location.longitude
//            )
//            _distance.value += newDistance
//
//            // 2. FIX: Improved Pace Calculation
//            val currentTime = System.currentTimeMillis()
//
//            // If GPS provides speed, use it (Most accurate)
//            if (location.hasSpeed() && location.speed > 0) {
//                val speedKmph = (location.speed * 3.6)
//                if(speedKmph > 0) {
//                    _currentPace.value = 60.0 / speedKmph
//                }
//            }
//            // Fallback: Calculate manually if we have moved and time has passed
//            else if (newDistance > 0 && lastLocationTimestamp > 0) {
//                val timeDeltaSeconds = (currentTime - lastLocationTimestamp) / 1000.0
//                if (timeDeltaSeconds > 0) {
//                    val speedKmph = (newDistance / (timeDeltaSeconds / 3600.0)) // km / hours
//                    if (speedKmph > 0) {
//                        _currentPace.value = 60.0 / speedKmph
//                    }
//                }
//            }
//
//            lastLocationTimestamp = currentTime
//        } else {
//            // First point
//            lastLocationTimestamp = System.currentTimeMillis()
//        }
//
//        points.add(currentPoint)
//        _polylinePoints.value = points
//    }


//    private fun updateLocationAndDistance(location: Location) {
//        _currentLocation.value = location
//        val currentPoint = Pair(location.latitude, location.longitude)
//        val points = _polylinePoints.value.toMutableList()
//
//        if (points.isNotEmpty()) {
//            val lastPoint = points.last()
//
//            val newDistance = trackingUseCase.calculateDistance(
//                lastPoint.first, lastPoint.second,
//                location.latitude, location.longitude
//            )
//            // Accumulate distance
//            _distance.value += newDistance
//
//            val currentTime = System.currentTimeMillis()
//
//            // Calculate Instant Pace (min/km)
//            // Speed is m/s. 1 m/s = 16.66 min/km.
//            // Better: use location.speed if available, else calculate manually
//            if (location.hasSpeed() && location.speed > 0) {
//                // speed is m/s. pace = 1000 / (speed * 60)
//                val speedKmph = (location.speed * 3.6)
//                if(speedKmph > 0) {
//                    _currentPace.value = 60.0 / speedKmph
//                }
//            }
//        }
//
//        points.add(currentPoint)
//        _polylinePoints.value = points
//    }

    private fun resetTracking() {
        startTime = null
        currentMaxSpeedKmph = 0.0
        lastLocationTimestamp = 0L
        _distance.value = 0.0
        _duration.value = 0L
        _currentPace.value = 0.0
        _avgPace.value = 0.0
        _calories.value = 0
        _polylinePoints.value = emptyList()
        _currentLocation.value = null
    }

    fun formatPace(paceSeconds: Double): String =
        trackingUseCase.paceSecondsToString(paceSeconds * 60) // Convert min/km to seconds for formatter

    fun formatDuration(seconds: Long): String =
        trackingUseCase.durationToString(seconds)
}