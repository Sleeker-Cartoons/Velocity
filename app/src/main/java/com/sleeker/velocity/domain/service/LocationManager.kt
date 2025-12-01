package com.sleeker.velocity.domain.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Build
import androidx.core.content.ContextCompat
import com.sleeker.velocity.service.TrackingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class LocationManager(private val context: Context) {

    // Use applicationContext to avoid leaking an Activity
    private val appContext = context.applicationContext

    // Create a coroutine scope for emitting location updates
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _locationUpdates = MutableSharedFlow<Location>(replay = 0)
    val locationUpdates: SharedFlow<Location> = _locationUpdates.asSharedFlow()

    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == TrackingService.ACTION_LOCATION_UPDATE) {
                val lat = intent.getDoubleExtra(EXTRA_LATITUDE, 0.0)
                val lon = intent.getDoubleExtra(EXTRA_LONGITUDE, 0.0)
                val accuracy = intent.getFloatExtra(EXTRA_ACCURACY, 0f)

                val location = Location("").apply {
                    latitude = lat
                    longitude = lon
                    this.accuracy = accuracy
                }

                Timber.d("LocationManager received: $lat, $lon")

                // Emit through coroutine scope
                scope.launch {
                    _locationUpdates.emit(location)
                }
            }
        }
    }

    fun startTracking() {
        Timber.d("Starting location tracking")
        val intent = Intent(appContext, TrackingService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appContext.startForegroundService(intent)
        } else {
            appContext.startService(intent)
        }

        val filter = IntentFilter(TrackingService.ACTION_LOCATION_UPDATE)

        // Use flags overload ONLY on API 33+ (TIRAMISU). For older APIs use the two-arg overload.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Flag choice: exported vs not exported — use RECEIVER_EXPORTED when you expect broadcasts from outside the app.
            // For internal app-only broadcasts, RECEIVER_NOT_EXPORTED is safer. Here we register as NOT_EXPORTED.
            appContext.registerReceiver(locationReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            ContextCompat.registerReceiver(
                appContext,
                locationReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }
    }

    fun stopTracking() {
        Timber.d("Stopping location tracking")
        val intent = Intent(appContext, TrackingService::class.java)
        appContext.stopService(intent)

        try {
            appContext.unregisterReceiver(locationReceiver)
        } catch (e: IllegalArgumentException) {
            // Receiver already unregistered
            Timber.d(e, "Receiver already unregistered")
        } catch (e: Exception) {
            Timber.w(e, "Error unregistering receiver")
        }
    }

    companion object {
        // Keep keys centralized and avoid magic strings
        const val EXTRA_LATITUDE = "latitude"
        const val EXTRA_LONGITUDE = "longitude"
        const val EXTRA_ACCURACY = "accuracy"
    }
}




//package com.sleeker.velocity.domain.service
//
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import android.location.Location
//import android.os.Build
//import com.sleeker.velocity.service.TrackingService
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.SupervisorJob
//import kotlinx.coroutines.flow.MutableSharedFlow
//import kotlinx.coroutines.flow.SharedFlow
//import kotlinx.coroutines.flow.asSharedFlow
//import kotlinx.coroutines.launch
//import timber.log.Timber
//
//class LocationManager(private val context: Context) {
//
//    // Create a coroutine scope for emitting location updates
//    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
//
//    private val _locationUpdates = MutableSharedFlow<Location>(replay = 0)
//    val locationUpdates: SharedFlow<Location> = _locationUpdates.asSharedFlow()
//
//    private val locationReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            if (intent?.action == TrackingService.ACTION_LOCATION_UPDATE) {
//                val lat = intent.getDoubleExtra("latitude", 0.0)
//                val lon = intent.getDoubleExtra("longitude", 0.0)
//                val accuracy = intent.getFloatExtra("accuracy", 0f)
//
//                val location = Location("").apply {
//                    latitude = lat
//                    longitude = lon
//                    this.accuracy = accuracy
//                }
//
//                Timber.d("LocationManager received: $lat, $lon")
//
//                // Emit through coroutine scope
//                scope.launch {
//                    _locationUpdates.emit(location)
//                }
//            }
//        }
//    }
//
//    fun startTracking() {
//        Timber.d("Starting location tracking")
//        val intent = Intent(context, TrackingService::class.java)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(intent)
//        } else {
//            context.startService(intent)
//        }
//
//        val filter = IntentFilter(TrackingService.ACTION_LOCATION_UPDATE)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            context.registerReceiver(locationReceiver, filter, Context.RECEIVER_EXPORTED)
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.registerReceiver(locationReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
//        } else {
//            context.registerReceiver(locationReceiver, filter)
//        }
//    }
//
//    fun stopTracking() {
//        Timber.d("Stopping location tracking")
//        val intent = Intent(context, TrackingService::class.java)
//        context.stopService(intent)
//
//        try {
//            context.unregisterReceiver(locationReceiver)
//        } catch (e: Exception) {
//            Timber.d(e, "Receiver already unregistered")
//        }
//    }
//}
