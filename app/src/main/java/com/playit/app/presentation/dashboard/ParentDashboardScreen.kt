package com.playit.app.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.playit.app.domain.usecase.LetterPerformance
import com.playit.app.domain.usecase.LetterStatus

@Composable
fun ParentDashboardScreen(
    onBack: () -> Unit,
    viewModel: ParentDashboardViewModel = hiltViewModel()
) {
    val letterPerformances by viewModel.letterPerformances.collectAsState()
    val completedCount by viewModel.completedCount.collectAsState()
    val totalStars by viewModel.totalStars.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        // top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("Back", color = Color.White)
            }
            Text(
                text = "Parent Dashboard",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(64.dp))
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // overall stats card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem(
                                value = "$completedCount / 28",
                                label = "Letters done"
                            )
                            StatItem(
                                value = "$totalStars ⭐",
                                label = "Total stars"
                            )
                            StatItem(
                                value = "${((completedCount / 28f) * 100).toInt()}%",
                                label = "Progress"
                            )
                        }
                    }
                }

                // legend
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        LegendItem(color = Color(0xFF4CAF50), label = "Mastered ≥80%")
                        LegendItem(color = Color(0xFFFFA726), label = "Developing 50-79%")
                        LegendItem(color = Color(0xFFF44336), label = "At risk <50%")
                    }
                }

                // letter performance table header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Letter", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Accuracy", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Stars", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Status", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                // letter rows
                items(letterPerformances) { performance ->
                    LetterPerformanceRow(performance = performance)
                }
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Text(text = label, fontSize = 11.sp, color = Color.Gray)
    }
}

@Composable
fun LetterPerformanceRow(performance: LetterPerformance) {
    val statusColor = when (performance.status) {
        LetterStatus.GREEN -> Color(0xFF4CAF50)
        LetterStatus.YELLOW -> Color(0xFFFFA726)
        LetterStatus.RED -> Color(0xFFF44336)
        LetterStatus.NOT_STARTED -> Color.LightGray
    }

    val statusLabel = when (performance.status) {
        LetterStatus.GREEN -> "Mastered"
        LetterStatus.YELLOW -> "Developing"
        LetterStatus.RED -> "At risk"
        LetterStatus.NOT_STARTED -> "Not started"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // letter
        Text(
            text = performance.letter,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(48.dp)
        )

        // accuracy
        Text(
            text = if (performance.status == LetterStatus.NOT_STARTED) "—"
            else "${(performance.accuracy * 100).toInt()}%",
            fontSize = 14.sp,
            modifier = Modifier.width(72.dp)
        )

        // stars
        Text(
            text = if (performance.starsEarned > 0)
                "⭐".repeat(performance.starsEarned)
            else "—",
            fontSize = 14.sp,
            modifier = Modifier.width(64.dp)
        )

        // status badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(statusColor.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = statusLabel,
                fontSize = 11.sp,
                color = statusColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
}