package com.playit.app.presentation.blendit

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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

// 🎨 Kid-Friendly Colors
val colorSuccess = Color(0xFF4CAF50)
val colorError = Color(0xFFF44336)
val colorHint = Color(0xFFFF9800)
val colorTileAvailable = Color(0xFFFF6B6B)
val colorTilePlaced = Color(0xFFE0E0E0)
val colorSlotEmpty = Color(0xFFF5F5F5)
val colorSlotFilled = Color(0xFFB3E5FC)
val colorSlotLocked = Color(0xFFA5D6A7)

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

    // 🎯 Bounce animation for correct answer
    val correctBounce by animateFloatAsState(
        targetValue = if (feedback is BlendItFeedback.Correct) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "correctBounce"
    )

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
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFDF5E6), Color(0xFFFFF3E0))
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🎪 Playful Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFF6B6B).copy(alpha = 0.2f))
            ) {
                TextButton(onClick = onBack) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⬅️", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Back", color = Color(0xFFFF6B6B), fontSize = 14.sp)
                    }
                }
            }

            Text(
                text = "Blend It",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7B1FA2)
            )

            Spacer(modifier = Modifier.width(60.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ❤️ Playful Hearts with animation
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            repeat(5) { index ->
                val heartScale by animateFloatAsState(
                    targetValue = if (index < hearts) 1.2f else 1f,
                    animationSpec = repeatable(
                        iterations = 3,
                        animation = tween(200)
                    ),
                    label = "heart$index"
                )
                Text(
                    text = if (index < hearts) "❤️" else "🖤",
                    fontSize = 24.sp,
                    modifier = Modifier.scale(heartScale)
                )
            }
        }

        // 📊 Word Progress with animated bar
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Word $wordProgress of 5",
                fontSize = 14.sp,
                color = Color(0xFF7B1FA2),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = wordProgress / 5f,
                modifier = Modifier
                    .width(150.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFFFF6B6B),
                trackColor = Color(0xFFE0E0E0)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading || currentWord == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFFF6B6B))
            }
        } else {
            // 🖼️ Word Image with bounce effect
            Card(
                modifier = Modifier
                    .size(180.dp)
                    .scale(correctBounce)
                    .clickable { viewModel.replayAudio() }
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(24.dp),
                        clip = false
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
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
                        modifier = Modifier.size(140.dp)
                    )

                    // 🔊 Audio replay button with pulse
                    val pulseScale by animateFloatAsState(
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "pulse"
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .size(36.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFFFF6B6B), Color(0xFFFF8E53))
                                )
                            )
                            .shadow(4.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔊", fontSize = 18.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 💬 Feedback Text with animation
            val feedbackColor = when (feedback) {
                is BlendItFeedback.Correct -> colorSuccess
                is BlendItFeedback.Incorrect -> colorError
                else -> Color.Gray
            }
            val feedbackScale by animateFloatAsState(
                targetValue = when (feedback) {
                    is BlendItFeedback.Correct, is BlendItFeedback.Incorrect -> 1.1f
                    else -> 1f
                },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy
                ),
                label = "feedbackScale"
            )

            Text(
                text = when (feedback) {
                    is BlendItFeedback.Correct -> "CORRECT! "
                    is BlendItFeedback.Incorrect -> if (wrongAttempts >= 2) "HINT UNLOCKED!" else "😅 TRY AGAIN! 😅"
                    else -> "BUILD THE WORD!"
                },
                fontSize = 18.sp,
                color = feedbackColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.scale(feedbackScale)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 📝 Letter Slots
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                slots.forEach { slot ->
                    LetterSlotBox(
                        slot = slot,
                        feedback = feedback,
                        onClick = { viewModel.onSlotTapped(slot) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 🚀 Submit Button
            val allFilled = slots.isNotEmpty() && slots.all { it.isFilled }
            val buttonScale by animateFloatAsState(
                targetValue = if (allFilled && feedback is BlendItFeedback.Idle) 1.02f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy
                ),
                label = "buttonScale"
            )

            Button(
                onClick = { viewModel.onSubmit() },
                enabled = allFilled && feedback is BlendItFeedback.Idle,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .scale(buttonScale),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (allFilled) Color(0xFF4CAF50) else Color(0xFFBDBDBD)
                )
            ) {
                Text(
                    text = if (allFilled) "SUBMIT" else "FILL ALL SLOTS",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🎨 Tile Bank
            Text(
                text = "Tap a letter to place it",
                fontSize = 12.sp,
                color = Color(0xFF7B1FA2),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
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
    feedback: BlendItFeedback,
    onClick: () -> Unit
) {
    val isSlotCorrect = feedback is BlendItFeedback.Correct

    val slotScale by animateFloatAsState(
        targetValue = if (isSlotCorrect) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy
        ),
        label = "slotScale"
    )

    val bgColor = when {
        slot.isLocked -> colorSlotLocked
        slot.isFilled -> colorSlotFilled
        else -> colorSlotEmpty
    }

    val borderColor = when {
        slot.isLocked -> Color(0xFF4CAF50)
        slot.isFilled -> Color(0xFF2196F3)
        else -> Color(0xFFE0E0E0)
    }

    Box(
        modifier = Modifier
            .size(60.dp)
            .scale(slotScale)
            .shadow(
                elevation = if (slot.isFilled) 6.dp else 2.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false
            )
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(
                width = 3.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = slot.isFilled && !slot.isLocked) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (slot.isFilled) {
            Text(
                text = slot.letter,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = if (slot.isLocked) Color(0xFF2E7D32) else Color(0xFF1565C0),
                textAlign = TextAlign.Center
            )
        } else if (slot.isLocked) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50).copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🔒", fontSize = 20.sp)
            }
        } else {
            Text(
                text = "?",
                fontSize = 28.sp,
                color = Color(0xFFBDBDBD),
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
fun LetterTileBox(
    tile: TileItem,
    onClick: () -> Unit
) {
    val tileScale by animateFloatAsState(
        targetValue = if (!tile.isPlaced) 1f else 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "tileScale"
    )

    Box(
        modifier = Modifier
            .size(65.dp)
            .scale(tileScale)
            .shadow(
                elevation = if (!tile.isPlaced) 8.dp else 2.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = if (!tile.isPlaced)
                    Brush.radialGradient(
                        colors = listOf(Color(0xFFFF8E53), Color(0xFFFF6B6B))
                    )
                else Brush.linearGradient(
                    colors = listOf(Color(0xFFE0E0E0), Color(0xFFBDBDBD))
                )
            )
            .border(
                width = if (!tile.isPlaced) 2.dp else 1.dp,
                color = Color.White.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(enabled = !tile.isPlaced) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (!tile.isPlaced) {
            Text(
                text = tile.letter,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text = "✓",
                fontSize = 32.sp,
                color = Color(0xFF757575),
                fontWeight = FontWeight.Bold
            )
        }
    }
}