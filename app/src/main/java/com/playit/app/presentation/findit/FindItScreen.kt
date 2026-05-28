package com.playit.app.presentation.findit

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.playit.app.domain.model.GridItem
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindItScreen(
    phonemeId: Int,
    onComplete: (Int, Int) -> Unit,
    onBack: () -> Unit,
    viewModel: FindItViewModel = hiltViewModel()
) {
    val grid by viewModel.grid.collectAsState()
    val hearts by viewModel.hearts.collectAsState()
    val score by viewModel.score.collectAsState()
    val isComplete by viewModel.isComplete.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val tappedItems by viewModel.tappedItems.collectAsState()
    val lastTapCorrect by viewModel.lastTapCorrect.collectAsState()

    // Celebration animation
    var showCelebration by remember { mutableStateOf(false) }

    // Play celebration when all 3 are found
    LaunchedEffect(score) {
        if (score == 3 && !isComplete) {
            showCelebration = true
            delay(800)
            showCelebration = false
        }
    }

    LaunchedEffect(phonemeId) {
        viewModel.loadGrid(phonemeId)
    }

    LaunchedEffect(isComplete) {
        if (isComplete) {
            delay(1000)
            onComplete(phonemeId, viewModel.getStarsEarned())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🔍 Find It", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", fontSize = 22.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Progress indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Hear It", "Say It", "Find It").forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                when {
                                    index < 2 -> Color(0xFF4CAF50)
                                    index == 2 -> MaterialTheme.colorScheme.primary
                                    else -> Color.LightGray
                                }
                            )
                    )
                }
            }

            // Hearts row with animation
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                repeat(5) { index ->
                    val heartScale by animateFloatAsState(
                        targetValue = if (index < hearts) 1f else 0.85f,
                        animationSpec = spring(),
                        label = "heart_$index"
                    )
                    Text(
                        text = if (index < hearts) "❤️" else "🖤",
                        fontSize = 24.sp,
                        modifier = Modifier.scale(heartScale)
                    )
                }
            }

            // Score indicator with animation
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    repeat(3) { starIndex ->
                        val starScale by animateFloatAsState(
                            targetValue = if (starIndex < score) 1.2f else 0.8f,
                            animationSpec = spring(),
                            label = "star_$starIndex"
                        )
                        Text(
                            text = if (starIndex < score) "⭐" else "☆",
                            fontSize = 18.sp,
                            modifier = Modifier.scale(starScale)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$score / 3 found",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Mascot instruction with animation
            val instructionText = when {
                isComplete -> "🎉 Amazing! You found them all! 🎉"
                score > 0 -> "Great! Keep finding! ⭐"
                else -> "🐻 Tap all the pictures that start with the sound!"
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFE8F4FD)
            ) {
                Text(
                    text = instructionText,
                    fontSize = 14.sp,
                    color = Color(0xFF333333),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(12.dp)
                )
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Picture grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    itemsIndexed(grid) { index, item ->
                        PictureCard(
                            item = item,
                            index = index,
                            isTapped = tappedItems.contains(index),
                            lastTapCorrect = if (tappedItems.contains(index)) lastTapCorrect else null,
                            onClick = { viewModel.onCardTapped(item, index) }
                        )
                    }
                }
            }

            // Celebration overlay
            if (showCelebration) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(enabled = false) { },
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(32.dp),
                        color = Color.White,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(
                                text = "🎉",
                                fontSize = 64.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Great Job!",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                            Text(
                                text = "You found all 3 pictures!",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PictureCard(
    item: GridItem,
    index: Int,
    isTapped: Boolean,
    lastTapCorrect: Boolean?,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isTapped && item.isTarget -> Color(0xFF4CAF50)  // Green for correct
        isTapped && !item.isTarget -> Color(0xFFF44336) // Red for wrong
        else -> MaterialTheme.colorScheme.surface
    }

    // Animation for card tap
    val scale by animateFloatAsState(
        targetValue = if (isTapped) 0.95f else 1f,
        animationSpec = spring(),
        label = "scale"
    )

    // Border animation for correct answer
    val borderWidth by animateDpAsState(
        targetValue = if (lastTapCorrect == true && !isTapped) 4.dp else 0.dp,
        animationSpec = tween(300),
        label = "border"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .scale(scale)
            .clickable(enabled = !isTapped) { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isTapped) 2.dp else 6.dp
        ),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Image with border highlight for correct/incorrect feedback
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when {
                            lastTapCorrect == true -> Color(0xFF4CAF50).copy(alpha = 0.3f)
                            lastTapCorrect == false -> Color(0xFFF44336).copy(alpha = 0.3f)
                            else -> Color.Transparent
                        }
                    )
                    .padding(4.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("file:///android_asset/${item.imagePath}")
                        .error(android.R.drawable.ic_menu_gallery)
                        .build(),
                    contentDescription = item.word,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )
            }

            // Word label with animation on correct tap
            val wordScale by animateFloatAsState(
                targetValue = if (lastTapCorrect == true) 1.1f else 1f,
                animationSpec = spring(),
                label = "wordScale"
            )

            Text(
                text = item.word.uppercase(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = when {
                    isTapped && item.isTarget -> Color.White
                    isTapped && !item.isTarget -> Color.White
                    else -> Color(0xFF333333)
                },
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.scale(wordScale)
            )

            // Checkmark for correct answers
            if (isTapped && item.isTarget) {
                Text(
                    text = "✓",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            } else if (isTapped && !item.isTarget) {
                Text(
                    text = "✗",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}