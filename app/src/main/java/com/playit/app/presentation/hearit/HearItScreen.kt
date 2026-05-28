package com.playit.app.presentation.hearit

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
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
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HearItScreen(
    phonemeId: Int,
    onNext: (Int) -> Unit,
    onBack: () -> Unit,
    viewModel: HearItViewModel = hiltViewModel()
) {
    val phoneme by viewModel.phoneme.collectAsState()
    val replayCount by viewModel.replayCount.collectAsState()
    val isNextEnabled by viewModel.isNextEnabled.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Animation for play button pulse
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val buttonPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LaunchedEffect(phonemeId) {
        viewModel.loadPhoneme(phonemeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🔊 Hear It", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Hear It", "Say It", "Find It").forEachIndexed { index, label ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                if (index == 0) MaterialTheme.colorScheme.primary
                                else Color.LightGray
                            )
                    )
                }
            }

            if (isLoading || phoneme == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Scrollable content area
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Letter and Image Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        shape = RoundedCornerShape(28.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF3E0)  // Warm, kid-friendly
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Big Letter with soft glow effect
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                                Color.Transparent
                                            ),
                                            radius = 80f
                                        )
                                    )
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = phoneme!!.letter.uppercase(),
                                    fontSize = 72.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 6.sp
                                )
                            }

                            // Image with soft shadow
                            Surface(
                                modifier = Modifier
                                    .size(140.dp)
                                    .clip(RoundedCornerShape(20.dp)),
                                shadowElevation = 4.dp,
                                tonalElevation = 0.dp,
                                color = Color.White
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data("file:///android_asset/${phoneme!!.imagePath}")
                                        .error(android.R.drawable.ic_menu_gallery)
                                        .build(),
                                    contentDescription = phoneme!!.exampleWord,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            // Word Pill - Colorful and visible
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(40.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                shadowElevation = 2.dp
                            ) {
                                Text(
                                    text = phoneme!!.exampleWord.uppercase(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 2.sp,
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                                )
                            }
                        }
                    }

                    // Mascot instruction
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFFE8F4FD)
                    ) {
                        Text(
                            text = if (replayCount == 0) {
                                "🐻 Tap the 🎵 button to hear the sound!"
                            } else {
                                "🎉 Great! Listen again or tap Next."
                            },
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF333333),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    // Play Button with Pulse Animation
                    Box(
                        modifier = Modifier
                            .size(if (replayCount == 0) 80.dp else 72.dp)
                            .scale(if (replayCount == 0) buttonPulse else 1f)
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                    )
                                )
                            )
                            .clickable { viewModel.playAudio() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    // Replay counter with animation
                    if (replayCount > 0) {
                        AnimatedContent(
                            targetState = replayCount,
                            label = "replay"
                        ) { count ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                repeat(minOf(count, 5)) { index ->
                                    val scale by animateFloatAsState(
                                        targetValue = 1f,
                                        animationSpec = spring(),
                                        label = "dot_$index"
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .scale(scale)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                }
                            }
                        }
                    }
                }

                // Next Button
                Button(
                    onClick = {
                        viewModel.saveProgress(phonemeId) {
                            onNext(phonemeId)
                        }
                    },
                    enabled = isNextEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isNextEnabled) Color(0xFF4CAF50) else Color.Gray
                    )
                ) {
                    Text(
                        text = if (isNextEnabled) "Next →" else "Listen first!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}