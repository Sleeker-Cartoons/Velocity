# Velocity 🏃‍♂️📍  
*A precise running tracker with Google Maps integration*

Velocity is a native Android application focused on **accurate GPS-based run tracking** and a clean, distraction-free experience.  
It’s built with **Kotlin**, modern Android components, and **Google Maps** for real-time route visualization.

---

## Features

- **Live run tracking**
  - Continuous GPS tracking using Play Services Location
  - Real-time distance, duration, and pace
- **Google Maps integration**
  - Draws your route as a polyline while you move
  - Automatically follows your current position
- **Foreground tracking service**
  - Runs reliably in the background via a foreground service + notification
  - Survives screen off / app in background (within Android limits)
- **GPS noise handling**
  - Filters out unrealistic jumps and spikes in speed
  - Smoother and more realistic pace & distance tracking
- **Run session controls**
  - Start / pause / resume / stop tracking
  - Auto-stops and cleans up resources when a session ends
- **Modern Android stack**
  - Kotlin + Coroutines + Flows
  - Gradle Kotlin DSL (`build.gradle.kts`, `settings.gradle.kts`)
  - Clear separation between UI and tracking logic

> This repository is currently focused on **core tracking and Maps integration** rather than full UI polish or analytics.

---

## Architecture Overview

Velocity is structured around a **service-driven tracking flow**:

- **TrackingService**
  - Foreground service that subscribes to location updates
  - Applies basic filtering/validation on raw GPS locations
  - Emits updates (location, distance, pace, duration) to the UI
- **Location layer**
  - Uses Google Play Services fused location provider
  - Handles request intervals, priority, and lifecycle
- **UI layer**
  - Displays live stats and your route on Google Maps
  - Subscribes to tracking state from the service
  - Provides controls: start / pause / resume / stop

This setup is intentionally close to **production patterns** you’d use in a fitness / tracking app:
- Service for long-running work  
- Maps SDK for visualization  
- Reactive stream of tracking data (Flows / callbacks) to the UI

---

## Tech Stack

- **Language:** Kotlin
- **Build:** Gradle (Kotlin DSL)
- **Platform:** Native Android
- **Location & Maps:**
  - Google Play Services Location
  - Google Maps SDK for Android
- **Other:**
  - Foreground service + notification channel
  - Coroutines / Flows for async and streaming updates

---

## Setup & Installation

- **Clone the repository:** git clone [https://github.com/yourusername/velocity.git](https://github.com/yourusername/velocity.git)
- **Google Maps API Key:** Obtain an API key from the Google Cloud Console with "Maps SDK for Android" enabled.
                        -  Add the key to your local.properties or AndroidManifest.xml (depending on your setup)

 -   <meta-data
 -   android:name="com.google.android.geo.API_KEY"
 -   android:value="YOUR_API_KEY_HERE" />

   


