package com.sleeker.velocity.ui.settings


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeker.velocity.data.preferences.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.first

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val unitType = preferencesManager.unitType
    val darkMode = preferencesManager.darkMode

    fun toggleUnits() {
        viewModelScope.launch {
            unitType.first().let { current ->
                val newUnit = if (current == "km") "miles" else "km"
                preferencesManager.setUnitType(newUnit)
            }
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setDarkMode(enabled)
        }
    }

    fun exportAllRuns() {
        // TODO: Implement GPX export logic
    }
}