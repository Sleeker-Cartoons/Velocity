// File: app/src/main/java/com/velocity/VelocityApp.kt
package com.sleeker.velocity

import android.app.Application
import androidx.viewbinding.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Application class for Velocity running tracker
 *
 * This class is instantiated before any other class when the process for your application/package is created.
 * It's the perfect place to initialize libraries that need context and should be available throughout the app lifecycle.
 */
@HiltAndroidApp
class VelocityApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Timber for logging (only in debug builds)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("Velocity App initialized in DEBUG mode")
        }

        Timber.d("VelocityApp onCreate completed")
    }
}

/**
 * Usage Notes:
 *
 * 1. This class MUST be declared in AndroidManifest.xml:
 *    <application android:name=".VelocityApp" ...>
 *
 * 2. @HiltAndroidApp annotation triggers Hilt's code generation
 *    - Generates a base class for the Application that serves as the application-level DI container
 *    - All components and modules are accessible from this container
 *
 * 3. Timber is used for logging throughout the app:
 *    - Timber.d() for debug logs
 *    - Timber.e() for errors
 *    - Timber.w() for warnings
 *    - Timber.i() for info
 *
 * 4. BuildConfig.DEBUG automatically distinguishes between debug and release builds
 *    - Logs only appear in debug builds
 *    - Production builds have no logging overhead
 */