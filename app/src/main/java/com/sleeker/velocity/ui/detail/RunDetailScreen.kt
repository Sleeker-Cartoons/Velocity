package com.sleeker.velocity.ui.detail


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sleeker.velocity.data.model.RunEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunDetailScreen(
    viewModel: RunDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onShareClick: (RunEntity) -> Unit
) {
    val run by viewModel.run.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (run != null) {
                CenterAlignedTopAppBar(
                    title = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                viewModel.formatDate(run!!),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                viewModel.formatDistance(run!!.distanceKm),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.deleteRun(run!!); onBackClick() }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFFFF6B6B)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        // Map Placeholder
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            color = Color(0xFFF5F5F5)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("📍", fontSize = 48.sp)
                                    Text(
                                        "Route map",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }

//                    item {
//                        // Stats Row (4 cards)
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            StatCard(
//                                label = "Distance",
//                                value = viewModel.formatDistance(run!!.distanceKm),
//                                modifier = Modifier.weight(1f)
//                            )
//                            StatCard(
//                                label = "Time",
//                                value = viewModel.formatDuration(run!!.durationSeconds),
//                                modifier = Modifier.weight(1f)
//                            )
//                            StatCard(
//                                label = "Avg Pace",
//                                value = viewModel.formatPace(run!!.avgPacePerKm),
//                                modifier = Modifier.weight(1f)
//                            )
//                            StatCard(
//                                label = "Calories",
//                                value = viewModel.formatCalories(run!!.caloriesBurned),
//                                modifier = Modifier.weight(1f)
//                            )
//                        }
//                    }

                    // velocity/ui/detail/RunDetailScreen.kt

// ... inside the LazyColumn item with the StatCards
                    item {
                        // Update the Row to include Max Speed.
                        // Since 5 cards won't fit well in one row, let's make a second row or adjust the layout.
                        // Here is a layout with 2 rows of metrics:

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Row 1
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                StatCard(
                                    label = "Distance",
                                    value = viewModel.formatDistance(run!!.distanceKm),
                                    modifier = Modifier.weight(1f)
                                )
                                StatCard(
                                    label = "Time",
                                    value = viewModel.formatDuration(run!!.durationSeconds),
                                    modifier = Modifier.weight(1f)
                                )
                                StatCard(
                                    label = "Calories",
                                    value = viewModel.formatCalories(run!!.caloriesBurned),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Row 2
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                StatCard(
                                    label = "Avg Pace",
                                    value = viewModel.formatPace(run!!.avgPacePerKm),
                                    modifier = Modifier.weight(1f)
                                )
                                // <--- NEW MAX SPEED CARD
                                StatCard(
                                    label = "Max Speed",
                                    value = viewModel.formatSpeed(run!!.maxSpeedKmph),
                                    modifier = Modifier.weight(1f)
                                )
                                // Spacer to balance the row visually
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    item {
                        // Pace Chart Placeholder
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 2.dp
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "📈 Pace over distance\n(Chart component)",
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }

                    item {
                        // Splits Table
                        Column {
                            Text(
                                "Splits",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            val splits = viewModel.getSplits(run!!)
                            splits.forEach { split ->
                                SplitRow(
                                    split = split,
                                    isBestSplit = split.pace == splits.minOf { it.pace }
                                )
                            }
                        }
                    }

                    item {
                        // Action Buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onShareClick(run!!) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.padding(4.dp))
                                Text("Share", fontSize = 12.sp)
                            }

                            Button(
                                onClick = { /* Export GPX */ },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                            ) {
                                Text("Export GPX", fontSize = 12.sp)
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Run not found")
                }
            }
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp)),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SplitRow(
    split: com.sleeker.velocity.data.model.SplitData,
    isBestSplit: Boolean
) {
    val backgroundColor = if (isBestSplit) {
        MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp)),
        color = backgroundColor,
        shadowElevation = if (isBestSplit) 1.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Split ${split.splitNumber}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                "%.2f km".format(split.distance),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Text(
                "%d'%02d\"".format(
                    split.pace.toLong() / 60,
                    (split.pace.toLong() % 60)
                ),
                fontSize = 11.sp,
                color = if (isBestSplit) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
            )

            if (isBestSplit) {
                Text(
                    "✓ Best",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}