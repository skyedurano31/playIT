package com.playit.app.presentation.sayit

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SayItScreen(
    phonemeId: Int,
    onNext: (Int) -> Unit,
    onBack: () -> Unit,
    viewModel: SayItViewModel = hiltViewModel()
) {
    // Permission state
    var hasAudioPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasAudioPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // ViewModel state
    val phoneme by viewModel.phoneme.collectAsState()
    val hearts by viewModel.hearts.collectAsState()
    val feedback by viewModel.feedback.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val attempts by viewModel.attempts.collectAsState()
    val isVoskReady by viewModel.isVoskReady.collectAsState()
    val isSessionPassed by viewModel.isSessionPassed.collectAsState()

    LaunchedEffect(phonemeId) {
        viewModel.loadPhoneme(phonemeId)
    }

    // Pulsing animation for mic button when ready
    val infiniteTransition = rememberInfiniteTransition(label = "mic")
    val micPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Reset feedback after delay
    LaunchedEffect(feedback) {
        if (feedback is FeedbackState.Correct || feedback is FeedbackState.Incorrect) {
            delay(1500)
            viewModel.resetFeedback()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "🎤 Say It",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", fontSize = 24.sp)
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                                    index < 1 -> Color(0xFF4CAF50)
                                    index == 1 -> MaterialTheme.colorScheme.primary
                                    else -> Color.LightGray
                                }
                            )
                    )
                }
            }

            // Hearts - Kid-friendly animated hearts
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
                        fontSize = 28.sp,
                        modifier = Modifier.scale(heartScale)
                    )
                }
            }

            // Main letter card with animated feedback
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(32.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (feedback) {
                        is FeedbackState.Correct -> Color(0xFF4CAF50)
                        is FeedbackState.Incorrect -> Color(0xFFF44336)
                        else -> Color(0xFFFFF3E0)
                    }
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Big letter
                    Text(
                        text = phoneme?.letter?.uppercase() ?: "",
                        fontSize = 100.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (feedback) {
                            is FeedbackState.Correct,
                            is FeedbackState.Incorrect -> Color.White
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Feedback message with icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (feedback) {
                            is FeedbackState.Correct -> {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Correct",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Great job! ✓",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            is FeedbackState.Incorrect -> {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Incorrect",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Try again!",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            is FeedbackState.Listening -> {
                                Text(
                                    text = "🎧 Listening...",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            else -> {
                                Text(
                                    text = "Say the sound! 🗣️",
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // Vosk status
            if (!isVoskReady) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFE8F4FD)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎙️ Loading speech recognition...",
                            color = Color(0xFF333333),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Attempt tracker - Visual history
            if (attempts.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFF5F5F5)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Your tries:", fontSize = 12.sp, color = Color.Gray)
                        attempts.takeLast(5).forEach { isCorrect ->
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isCorrect) Color(0xFF4CAF50)
                                        else Color(0xFFF44336)
                                    )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mascot instruction
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFE8F4FD)
            ) {
                Text(
                    text = when {
                        !hasAudioPermission -> "🐻 Please allow microphone access!"
                        isSessionPassed -> "🎉 Amazing! Tap Next to continue!"
                        isListening -> "🎙️ Say the letter sound..."
                        else -> "🐻 Tap the mic button and say the letter sound!"
                    },
                    fontSize = 14.sp,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF333333)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Big Mic Button
            Box(
                modifier = Modifier
                    .size(if (isListening) 110.dp else 100.dp)
                    .scale(if (isListening) micPulse else 1f)
                    .clip(CircleShape)
                    .background(
                        when {
                            !hasAudioPermission -> Color.Gray
                            !isVoskReady -> Color.Gray
                            isListening -> Color(0xFFF44336)
                            else -> Color(0xFFFFA726)
                        }
                    )
                    .clickable(
                        enabled = isVoskReady && hasAudioPermission && !isListening && !isSessionPassed
                    ) {
                        viewModel.startRecording()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when {
                        !hasAudioPermission -> "🔇"
                        isListening -> "🎙️"
                        else -> "🎤"
                    },
                    fontSize = 48.sp
                )
            }

            // Status text under mic
            Text(
                text = when {
                    !hasAudioPermission -> "🔒 Permission needed"
                    isListening -> "🎙️ Speaking..."
                    isSessionPassed -> "✅ Completed!"
                    isVoskReady -> "🎤 Tap to speak"
                    else -> "⚙️ Loading..."
                },
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ✅ Next button - Always visible area, conditionally enabled
            Button(
                onClick = {
                    if (isSessionPassed) {
                        viewModel.saveSayItProgress {
                            onNext(phonemeId)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSessionPassed) Color(0xFF4CAF50) else Color.Gray
                ),
                enabled = isSessionPassed  // Button is always visible but disabled until passed
            ) {
                if (isSessionPassed) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = if (isSessionPassed) "Next →" else "Complete the exercise first",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}