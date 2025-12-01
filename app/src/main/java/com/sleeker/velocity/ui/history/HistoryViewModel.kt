package com.sleeker.velocity.ui.history


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeker.velocity.data.model.RunEntity
import com.sleeker.velocity.domain.repository.RunRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val runRepository: RunRepository
) : ViewModel() {

    val allRuns: StateFlow<List<RunEntity>> = runRepository.getAllRuns()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _deletedRunId = MutableStateFlow<Long?>(null)
    val deletedRunId: StateFlow<Long?> = _deletedRunId.asStateFlow()

    fun deleteRun(run: RunEntity) {
        viewModelScope.launch {
            _deletedRunId.value = run.id
            runRepository.deleteRun(run)
        }
    }

    fun clearDeleteNotification() {
        _deletedRunId.value = null
    }

    fun formatDistance(km: Double): String = String.format("%.2f km", km)

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

    fun formatPace(pacePerKm: Double): String {
        val minutes = pacePerKm.toLong()
        val seconds = ((pacePerKm - minutes) * 60).toLong()
        return String.format("%d'%02d\"/km", minutes, seconds)
    }

    fun formatDate(run: RunEntity): String {
        val formatter = java.time.format.DateTimeFormatter.ofPattern("EEE, d MMM")
        return run.startTime.format(formatter)
    }
}