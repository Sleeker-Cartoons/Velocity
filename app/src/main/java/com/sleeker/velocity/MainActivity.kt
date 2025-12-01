package com.sleeker.velocity



import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sleeker.velocity.data.preferences.PreferencesManager
import com.sleeker.velocity.ui.detail.RunDetailScreen
import com.sleeker.velocity.ui.home.HomeScreen
import com.sleeker.velocity.ui.history.HistoryScreen
import com.sleeker.velocity.ui.navigation.VelocityNavigationBar
import com.sleeker.velocity.ui.settings.SettingsScreen
import com.sleeker.velocity.ui.stats.StatsScreen
import com.sleeker.velocity.ui.theme.VelocityTheme
import com.sleeker.velocity.ui.tracking.TrackingScreen
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("MainActivity created")

        setContent {
            VelocityApp(preferencesManager)
        }
    }
}

@Composable
fun VelocityApp(preferencesManager: PreferencesManager) {
    val darkMode by preferencesManager.darkMode.collectAsState(initial = false)
    val navController = rememberNavController()

    VelocityTheme(darkTheme = darkMode) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                // Get current route
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: "home"

                // Hide bottom navigation on these screens
                val hideBottomBar = currentRoute in listOf(
                    "tracking",
                    "detail/{runId}",
                    "settings"
                )

                if (!hideBottomBar) {
                    VelocityNavigationBar(
                        currentRoute = currentRoute.split("/")[0], // Remove parameters
                        onNavigate = { route ->
                            navController.navigate(route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Home Screen
                composable("home") {
                    HomeScreen(
                        onStartTrackingClick = {
                            navController.navigate("tracking")
                        },
                        onRunClick = { runId ->
                            navController.navigate("detail/$runId")
                        },
                        onSettingsClick = {
                            navController.navigate("settings")
                        }
                    )
                }

                // Tracking Screen
                composable("tracking") {
                    TrackingScreen(
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }

                // History Screen
                composable("history") {
                    HistoryScreen(
                        onRunClick = { runId ->
                            navController.navigate("detail/$runId")
                        }
                    )
                }

                // Run Detail Screen (with argument)
                composable(
                    route = "detail/{runId}",
                    arguments = listOf(
                        navArgument("runId") {
                            type = NavType.LongType
                        }
                    )
                ) {
                    RunDetailScreen(
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onShareClick = { run ->
                            Timber.d("Share clicked for run: ${run.id}")
                            // TODO: Implement share functionality
                        }
                    )
                }

                // Stats Screen
                composable("stats") {
                    StatsScreen()
                }

                // Settings Screen
                composable("settings") {
                    SettingsScreen(
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}

// Preview for Android Studio
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun VelocityAppPreview() {
    // Note: Preview won't work with Hilt injection
    // Use it only for UI structure verification
}

@Composable
fun androidx.navigation.NavBackStackEntry?.currentBackStackEntryAsState():
        androidx.compose.runtime.State<androidx.navigation.NavBackStackEntry?> {
    val navController = rememberNavController()
    return navController.currentBackStackEntryAsState()
}