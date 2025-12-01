package com.sleeker.velocity.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val PREFERENCES_NAME = "velocity_prefs"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

class PreferencesManager(private val context: Context) {
    companion object {
        private val UNIT_TYPE = stringPreferencesKey("unit_type") // "km" or "miles"
        private val DARK_MODE = booleanPreferencesKey("dark_mode")
        private val BATTERY_OPTIMIZATION_ASKED = booleanPreferencesKey("battery_optimization_asked")
    }

    val unitType: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[UNIT_TYPE] ?: "km"
    }

    val darkMode: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[DARK_MODE] ?: false
    }

    val batteryOptimizationAsked: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[BATTERY_OPTIMIZATION_ASKED] ?: false
    }

    suspend fun setUnitType(unit: String) {
        context.dataStore.edit { prefs -> prefs[UNIT_TYPE] = unit }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[DARK_MODE] = enabled }
    }

    suspend fun setBatteryOptimizationAsked(asked: Boolean) {
        context.dataStore.edit { prefs -> prefs[BATTERY_OPTIMIZATION_ASKED] = asked }
    }
}