package com.playit.app.presentation.findit

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

    LaunchedEffect(phonemeId) {
        viewModel.loadGrid(phonemeId)
    }

    LaunchedEffect(isComplete) {
        if (isComplete) {
            kotlinx.coroutines.delay(1000)
            onComplete(phonemeId, viewModel.getStarsEarned())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar (simple, no TopAppBar)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("← Back", fontSize = 16.sp)
            }
            Text(
                text = "🔍 Find It",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        // Progress indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
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

        // Hearts row
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            repeat(5) { index ->
                Text(
                    text = if (index < hearts) "❤️" else "🖤",
                    fontSize = 22.sp
                )
            }
        }

        // Score indicator
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ) {
            Text(
                text = "$score / 3 found",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        // Mascot instruction
        Text(
            text = when {
                isComplete -> "Amazing! You found them all! 🎉"
                score > 0 -> "Great! Keep finding!"
                else -> "🐻 Tap all the pictures that start with the sound!"
            },
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Picture grid - bigger images
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)  // Square card
            .clickable(enabled = !isTapped) { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Big image - fills most of the card
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/${item.imagePath}")
                    .error(android.R.drawable.ic_menu_gallery)
                    .build(),
                contentDescription = item.word,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Fit
            )

            // Word label
            Text(
                text = item.word.uppercase(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isTapped) Color.White else Color(0xFF333333),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}