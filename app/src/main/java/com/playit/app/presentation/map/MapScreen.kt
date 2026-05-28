package com.playit.app.presentation.map

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.playit.app.domain.model.MapNode
import kotlinx.coroutines.delay

// 🌈 Kid-friendly colors (brighter + playful)
val colorLocked = Color(0xFFB0BEC5)
val colorNext = Color(0xFFFFD54F)
val colorOneStar = Color(0xFF64B5F6)
val colorTwoStar = Color(0xFF81C784)
val colorThreeStar = Color(0xFF4CAF50)
val colorPath = Color(0xFFFFF176)
val colorBackground = Color(0xFFFFF8E1)

@Composable
fun MapScreen(
    onLetterSelected: (Int) -> Unit,
    onDashboardClicked: () -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val mapNodes by viewModel.mapNodes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scrollState = rememberScrollState()

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadMapNodes()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(mapNodes, isLoading) {
        if (mapNodes.isNotEmpty() && !isLoading) {
            delay(120)
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Column(Modifier.fillMaxSize()) {

        // 🌟 FUN TOP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFFFFD54F), Color(0xFFFF8A65))
                    )
                )
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🌟 PlayIT Adventure",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            TextButton(onClick = onDashboardClicked) {
                Text("Parent", color = Color.White)
            }
        }

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFFFC107))
            }
        } else {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFFFF8E1), Color(0xFFFFECB3))
                        )
                    )
                    .verticalScroll(scrollState)
            ) {
                WindingPathMap(
                    nodes = mapNodes,
                    onNodeClick = { if (it.isUnlocked) onLetterSelected(it.phonemeId) }
                )
            }
        }
    }
}

@Composable
fun WindingPathMap(
    nodes: List<MapNode>,
    onNodeClick: (MapNode.LetterNode) -> Unit
) {
    val nodeSize = 78.dp
    val rowHeight = 125.dp

    val density = LocalDensity.current
    val leftX = 80.dp
    val rightX = 280.dp

    val leftXPx = with(density) { leftX.toPx() }
    val rightXPx = with(density) { rightX.toPx() }
    val nodeSizePx = with(density) { nodeSize.toPx() }
    val rowHeightPx = with(density) { rowHeight.toPx() }

    Box(
        Modifier
            .fillMaxWidth()
            .height(with(density) { ((nodes.size + 1) * rowHeightPx).toDp() })
    ) {

        // ✨ PATH
        Canvas(Modifier.fillMaxSize()) {
            for (i in 0 until nodes.size - 1) {

                val current = nodes[i] as? MapNode.LetterNode ?: continue
                val next = nodes[i + 1] as? MapNode.LetterNode ?: continue

                val startX = if (i % 2 == 0) leftXPx else rightXPx
                val endX = if ((i + 1) % 2 == 0) leftXPx else rightXPx

                val startY = (i + 1) * rowHeightPx
                val endY = (i + 2) * rowHeightPx

                val isDone = current.starsEarned > 0

                val path = Path().apply {
                    moveTo(startX, startY)
                    cubicTo(
                        startX, startY + 100f,
                        endX, endY - 100f,
                        endX, endY
                    )
                }

                drawPath(
                    path = path,
                    color = if (isDone) colorPath else Color(0xFFFFE082),
                    style = Stroke(width = 10f, cap = StrokeCap.Round)
                )
            }
        }

        // 🎮 NODES
        nodes.filterIsInstance<MapNode.LetterNode>()
            .forEachIndexed { index, node ->

                val isLeft = index % 2 == 0
                val xPx = if (isLeft) leftXPx else rightXPx
                val yPx = (index + 1) * rowHeightPx

                val bounce = rememberInfiniteTransition(label = "bounce")
                    .animateFloat(
                        0f, 10f,
                        infiniteRepeatable(
                            tween(900),
                            RepeatMode.Reverse
                        ),
                        label = "b"
                    ).value

                val scale by animateFloatAsState(
                    targetValue = if (node.isUnlocked) 1f else 0.85f,
                    animationSpec = spring(),
                    label = "scale"
                )

                LetterNode(
                    node = node,
                    size = nodeSize,
                    modifier = Modifier.offset(
                        x = with(density) { xPx.toDp() - nodeSize / 2 },
                        y = with(density) { (yPx.toDp()) + bounce.dp }
                    ),
                    scale = scale,
                    onClick = { onNodeClick(node) }
                )
            }
    }
}

@Composable
fun LetterNode(
    node: MapNode.LetterNode,
    size: Dp,
    modifier: Modifier,
    scale: Float,
    onClick: () -> Unit
) {

    val color = when {
        !node.isUnlocked -> colorLocked
        node.starsEarned == 3 -> colorThreeStar
        node.starsEarned == 2 -> colorTwoStar
        node.starsEarned == 1 -> colorOneStar
        else -> colorNext
    }

    Column(
        modifier = modifier
            .scale(scale)
            .clickable(enabled = node.isUnlocked) { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 🌟 glowing circle
        Box(
            Modifier
                .size(size)
                .shadow(12.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(color, color.copy(alpha = 0.7f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = if (node.isUnlocked) node.letter.uppercase() else "🔒",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(Modifier.height(6.dp))

        // ⭐ stars
        Row {
            repeat(3) {
                Text(
                    text = if (it < node.starsEarned) "⭐" else "☆",
                    fontSize = 14.sp
                )
            }
        }
    }
}