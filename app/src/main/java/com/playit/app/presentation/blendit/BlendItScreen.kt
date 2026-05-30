package com.playit.app.presentation.blendit

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.playit.app.domain.model.SlotItem
import com.playit.app.domain.model.TileItem

@Composable
fun BlendItScreen(
    groupId: Int,
    onComplete: (Int, Int) -> Unit,
    onBack: () -> Unit,
    viewModel: BlendItViewModel = hiltViewModel()
) {
    val currentWord by viewModel.currentWord.collectAsState()
    val tileBank by viewModel.tileBank.collectAsState()
    val slots by viewModel.slots.collectAsState()
    val hearts by viewModel.hearts.collectAsState()
    val wordProgress by viewModel.wordProgress.collectAsState()
    val feedback by viewModel.feedback.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val wrongAttempts by viewModel.wrongAttempts.collectAsState()

    LaunchedEffect(groupId) {
        viewModel.loadSession(groupId)
    }

    LaunchedEffect(feedback) {
        if (feedback is BlendItFeedback.SessionComplete) {
            kotlinx.coroutines.delay(500)
            onComplete(groupId, viewModel.getStarsEarned())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("Back")
            }
            Text(
                text = "Blend It",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        // hearts
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            repeat(5) { index ->
                Text(
                    text = if (index < hearts) "❤️" else "🖤",
                    fontSize = 20.sp
                )
            }
        }

        // word progress
        Text(
            text = "$wordProgress / 5 words",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading || currentWord == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {

            // word image + audio replay
            Card(
                modifier = Modifier
                    .size(160.dp)
                    .clickable { viewModel.replayAudio() },
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("file:///android_asset/${currentWord!!.imagePath}")
                            .error(android.R.drawable.ic_menu_gallery)
                            .build(),
                        contentDescription = currentWord!!.word,
                        modifier = Modifier.size(120.dp)
                    )
                    // audio replay hint
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔊", fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // feedback text
            val feedbackText = when (feedback) {
                is BlendItFeedback.Correct -> "Correct! ✓"
                is BlendItFeedback.Incorrect -> if (wrongAttempts >= 2) "Here's a hint!" else "Try again!"
                else -> "Build the word!"
            }
            val feedbackColor = when (feedback) {
                is BlendItFeedback.Correct -> Color(0xFF4CAF50)
                is BlendItFeedback.Incorrect -> Color(0xFFF44336)
                else -> Color.Gray
            }
            Text(
                text = feedbackText,
                fontSize = 16.sp,
                color = feedbackColor,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // letter slots
            val slotBgColor by animateColorAsState(
                targetValue = when (feedback) {
                    is BlendItFeedback.Correct -> Color(0xFF4CAF50)
                    is BlendItFeedback.Incorrect -> Color(0xFFF44336)
                    else -> Color.Transparent
                },
                animationSpec = tween(300),
                label = "slotColor"
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                slots.forEach { slot ->
                    LetterSlotBox(
                        slot = slot,
                        feedbackColor = if (feedback !is BlendItFeedback.Idle) slotBgColor
                        else Color.Transparent,
                        onClick = { viewModel.onSlotTapped(slot) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // submit button
            val allFilled = slots.isNotEmpty() && slots.all { it.isFilled }
            Button(
                onClick = { viewModel.onSubmit() },
                enabled = allFilled && feedback is BlendItFeedback.Idle,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Submit",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // tile bank
            Text(
                text = "Tap a letter to place it",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(tileBank) { tile ->
                    LetterTileBox(
                        tile = tile,
                        onClick = {
                            if (!tile.isPlaced) viewModel.onTileTapped(tile)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LetterSlotBox(
    slot: SlotItem,
    feedbackColor: Color,
    onClick: () -> Unit
) {
    val bgColor = when {
        feedbackColor != Color.Transparent -> feedbackColor
        slot.isLocked -> Color(0xFF81C784)
        slot.isFilled -> MaterialTheme.colorScheme.primaryContainer
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(
                width = 2.dp,
                color = if (slot.isFilled) MaterialTheme.colorScheme.primary
                else Color.LightGray,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(enabled = slot.isFilled && !slot.isLocked) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = slot.letter,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = if (slot.isFilled) MaterialTheme.colorScheme.onPrimaryContainer
            else Color.Transparent,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun LetterTileBox(
    tile: TileItem,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (tile.isPlaced) Color.LightGray
                else MaterialTheme.colorScheme.primary
            )
            .clickable(enabled = !tile.isPlaced) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (tile.isPlaced) "" else tile.letter,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}