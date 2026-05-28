package com.playit.app.presentation.hearit

import androidx.compose.foundation.background
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
            // Progress indicator (fixed at top)
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
                // ✅ Scrollable content area (takes remaining space)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Compact Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Letter
                            Text(
                                text = phoneme!!.letter.uppercase(),
                                fontSize = 64.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 4.sp
                            )

                            // Image
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("file:///android_asset/${phoneme!!.imagePath}")
                                    .error(android.R.drawable.ic_menu_gallery)
                                    .build(),
                                contentDescription = phoneme!!.exampleWord,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Fit
                            )

                            // Word pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(32.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(horizontal = 20.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = phoneme!!.exampleWord.uppercase(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }

                    // Mascot instruction
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFE8F4FD))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (replayCount == 0) {
                                "🐻 Tap the button to hear the sound!"
                            } else {
                                "🎉 Great! Listen again or tap Next."
                            },
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF333333)
                        )
                    }

                    // Play button
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = { viewModel.playAudio() },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    // Replay counter
                    if (replayCount > 0) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            repeat(minOf(replayCount, 5)) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    }
                }

                // ✅ NEXT BUTTON - Fixed at bottom, NOT cropped
                Button(
                    onClick = {
                        viewModel.saveProgress(phonemeId) {
                            onNext(phonemeId)
                        }
                    },
                    enabled = isNextEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Next →", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}