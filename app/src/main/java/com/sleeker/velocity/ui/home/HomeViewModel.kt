package com.sleeker.velocity.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeker.velocity.data.model.RunEntity
import com.sleeker.velocity.domain.repository.RunRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val runRepository: RunRepository
) : ViewModel() {

    val latestRun: StateFlow<RunEntity?> = runRepository.getLatestRun()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun formatDistance(km: Double, unit: String): String {
        return if (unit == "km") {
            String.format("%.2f km", km)
        } else {
            String.format("%.2f mi", km * 0.621371)
        }
    }

    fun formatDuration(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, secs)
        } else {
            String.format("%02d:%02d", minutes, secs)
        }
    }

    fun formatPace(pacePerKm: Double, unit: String): String {
        val pace = if (unit == "km") pacePerKm else pacePerKm / 0.621371
        val minutes = pace.toLong()
        val seconds = ((pace - minutes) * 60).toLong()
        val unitStr = if (unit == "km") "/km" else "/mi"
        return String.format("%d'%02d\"%s", minutes, seconds, unitStr)
    }
}