package com.playit.app.presentation.sayit

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SayItScreen(
    phonemeId: Int,
    onNext: (Int) -> Unit,
    onBack: () -> Unit,
    viewModel: SayItViewModel = hiltViewModel()
) {
    // ✅ PERMISSION CODE - Now properly inside @Composable
    var hasAudioPermission by rememberSaveable {
        mutableStateOf(false)
    }

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

    // reset feedback after delay
    LaunchedEffect(feedback) {
        if (feedback is FeedbackState.Correct || feedback is FeedbackState.Incorrect) {
            kotlinx.coroutines.delay(1500)
            viewModel.resetFeedback()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
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
                text = "Say It",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        // sublevel progress bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Hear It", "Say It", "Find It").forEachIndexed { index, label ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
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

        // hearts display
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            repeat(5) { index ->
                Text(
                    text = if (index < hearts) "❤️" else "🖤",
                    fontSize = 24.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // phoneme card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = when (feedback) {
                    is FeedbackState.Correct -> Color(0xFF4CAF50)
                    is FeedbackState.Incorrect -> Color(0xFFF44336)
                    else -> MaterialTheme.colorScheme.surface
                }
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = phoneme?.letter ?: "",
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (feedback) {
                        is FeedbackState.Correct,
                        is FeedbackState.Incorrect -> Color.White
                        else -> MaterialTheme.colorScheme.primary
                    }
                )
                Text(
                    text = when (feedback) {
                        is FeedbackState.Correct -> "Great job! ✓"
                        is FeedbackState.Incorrect -> "Try again!"
                        is FeedbackState.Listening -> "Listening..."
                        else -> "Say the sound!"
                    },
                    fontSize = 16.sp,
                    color = when (feedback) {
                        is FeedbackState.Correct,
                        is FeedbackState.Incorrect -> Color.White
                        else -> Color.Gray
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // vosk status
        if (!isVoskReady) {
            Text(
                text = "Loading speech recognition...",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        Spacer(modifier = Modifier.height(24.dp))

        // attempt tracker
        if (attempts.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                attempts.takeLast(5).forEach { isCorrect ->
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(
                                if (isCorrect) Color(0xFF4CAF50)
                                else Color(0xFFF44336)
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        // mic button
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    when {
                        !hasAudioPermission -> Color.Gray
                        !isVoskReady -> Color.Gray
                        isListening -> Color(0xFFF44336)
                        else -> MaterialTheme.colorScheme.primary
                    }
                )
                .pointerInput(isVoskReady && hasAudioPermission) {
                    if (isVoskReady && hasAudioPermission) {
                        detectTapGestures(
                            onPress = {
                                viewModel.startRecording()
                                tryAwaitRelease()
                                viewModel.stopRecording()
                            }
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when {
                    !hasAudioPermission -> "🚫"
                    isListening -> "🎙️"
                    else -> "🎤"
                },
                fontSize = 40.sp,
                textAlign = TextAlign.Center
            )
        }

        // instruction text
        Text(
            text = when {
                !hasAudioPermission -> "Microphone permission needed"
                isListening -> "Release to stop"
                isVoskReady -> "Hold to speak"
                else -> "Loading..."
            },
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // next button — only shows after passing
        if (isSessionPassed) {
            Button(
                onClick = {
                    viewModel.saveSayItProgress {
                        onNext(phonemeId)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Text(
                    text = "Next →",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}