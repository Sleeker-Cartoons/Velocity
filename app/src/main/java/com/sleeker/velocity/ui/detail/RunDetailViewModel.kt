package com.sleeker.velocity.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeker.velocity.data.model.RunEntity
import com.sleeker.velocity.domain.repository.RunRepository
import com.sleeker.velocity.domain.usecase.TrackingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RunDetailViewModel @Inject constructor(
    private val runRepository: RunRepository,
    private val trackingUseCase: TrackingUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val runId: Long = savedStateHandle.get<Long>("runId") ?: -1L

    private val _run = MutableStateFlow<RunEntity?>(null)
    val run: StateFlow<RunEntity?> = _run.asStateFlow()

    init {
        if (runId > 0) {
            viewModelScope.launch {
                val fetchedRun = runRepository.getRun(runId)
                _run.value = fetchedRun
            }
        }
    }

    fun deleteRun(run: RunEntity) {
        viewModelScope.launch {
            runRepository.deleteRun(run)
        }
    }

    fun formatDistance(km: Double): String = String.format("%.2f km", km)

    fun formatDuration(seconds: Long): String = trackingUseCase.durationToString(seconds)

    fun formatPace(pacePerKm: Double): String = trackingUseCase.paceSecondsToString(pacePerKm * 60)

    fun formatCalories(calories: Int): String = "$calories kcal"

    fun formatDate(run: RunEntity): String {
        val formatter = java.time.format.DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")
        return run.startTime.format(formatter)
    }

    fun getSplits(run: RunEntity) = trackingUseCase.generateSplits(
        distance = run.distanceKm,
        duration = run.durationSeconds,
        avgPace = run.avgPacePerKm,
        splitUnit = 1.0
    )
}
