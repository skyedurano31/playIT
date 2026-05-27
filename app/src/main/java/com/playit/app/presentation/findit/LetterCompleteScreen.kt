package com.playit.app.presentation.findit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LetterCompleteScreen(
    phonemeId: Int,
    starsEarned: Int,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Letter Complete!",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // stars
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(3) { index ->
                Text(
                    text = if (index < starsEarned) "⭐" else "☆",
                    fontSize = 48.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when (starsEarned) {
                3 -> "Perfect! Amazing job! 🎉"
                2 -> "Great work! Keep it up! 👏"
                else -> "Good job! Practice makes perfect! 💪"
            },
            fontSize = 18.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Continue →",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}