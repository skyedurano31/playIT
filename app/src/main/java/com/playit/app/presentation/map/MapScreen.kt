package com.playit.app.presentation.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.playit.app.domain.model.MapNode

@Composable
fun MapScreen(
    onLetterSelected: (Int) -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val mapNodes by viewModel.mapNodes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadMapNodes()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // top bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp)
        ) {
            Text(
                text = "playIT",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mapNodes) { node ->
                    when (node) {
                        is MapNode.LetterNode -> {
                            LetterNodeItem(
                                node = node,
                                onClick = {
                                    if (node.isUnlocked) {
                                        onLetterSelected(node.phonemeId)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LetterNodeItem(
    node: MapNode.LetterNode,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        !node.isUnlocked -> Color.Gray
        node.starsEarned == 3 -> Color(0xFF4CAF50)
        node.starsEarned > 0 -> Color(0xFF2196F3)
        else -> Color(0xFFFFA726)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = node.isUnlocked) { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // letter circle
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (node.isUnlocked) node.letter else "🔒",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column {
            Text(
                text = if (node.isUnlocked) node.letter else "Locked",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            // stars row
            if (node.starsEarned > 0) {
                Row {
                    repeat(3) { index ->
                        Text(
                            text = if (index < node.starsEarned) "⭐" else "☆",
                            fontSize = 16.sp
                        )
                    }
                }
            } else {
                Text(
                    text = if (node.isUnlocked) "Not started" else "",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}