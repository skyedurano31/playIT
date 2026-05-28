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

// 🌈 Kid-friendly colors
val colorLocked = Color(0xFFB0BEC5)
val colorNext = Color(0xFFFFD54F)
val colorOneStar = Color(0xFF64B5F6)
val colorTwoStar = Color(0xFF81C784)
val colorThreeStar = Color(0xFF4CAF50)
val colorPath = Color(0xFFFFF176)
val colorBlendIt = Color(0xFF9C27B0)
val colorBlendItLocked = Color(0xFFCE93D8)
val colorBlendPath = Color(0xFFFFB74D)

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
                text = "👾 PlayIT",
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

    // Get letter nodes in ORIGINAL order (m, s, a, i, o, b, e...)
    val letterNodes = nodes.filterIsInstance<MapNode.LetterNode>()

    // 🔥 FIX: Reverse for bottom-to-top counting, insert blendIT, then reverse back
    val nodesFromBottom = letterNodes.reversed()

    val combinedFromBottom = mutableListOf<Pair<MapNode, Boolean>>()

    nodesFromBottom.forEachIndexed { index, letterNode ->
        combinedFromBottom.add(Pair(letterNode, false))

        // Add blendIT after every 5th letter from BOTTOM
        // Letters from bottom: index 0=m, 1=s, 2=a, 3=i, 4=o → blendIT after o
        if ((index + 1) % 5 == 0 && index < nodesFromBottom.size - 1) {
            val placeholderNode = MapNode.LetterNode(
                phonemeId = 1000 + ((index + 1) / 5),
                letter = "📖",
                isUnlocked = letterNode.isUnlocked,
                starsEarned = 0
            )
            combinedFromBottom.add(Pair(placeholderNode, true))
        }
    }

    // Reverse back for rendering (bottom to top display)
    val combinedNodes = combinedFromBottom.reversed()

    Box(
        Modifier
            .fillMaxWidth()
            .height(with(density) { ((combinedNodes.size + 1) * rowHeightPx).toDp() })
    ) {

        // ✨ PATH
        Canvas(Modifier.fillMaxSize()) {
            for (i in 0 until combinedNodes.size - 1) {
                val (currentNode, isCurrentBlendIt) = combinedNodes[i]
                val (nextNode, isNextBlendIt) = combinedNodes[i + 1]

                val startX = if (i % 2 == 0) leftXPx else rightXPx
                val endX = if ((i + 1) % 2 == 0) leftXPx else rightXPx

                val startY = (i + 1) * rowHeightPx
                val endY = (i + 2) * rowHeightPx

                val isBlendItPath = isCurrentBlendIt || isNextBlendIt
                val isCompleted = if (currentNode is MapNode.LetterNode && !isCurrentBlendIt) {
                    currentNode.starsEarned > 0
                } else false

                val pathColor = when {
                    isBlendItPath -> colorBlendPath
                    isCompleted -> colorPath
                    else -> Color(0xFFFFE082)
                }

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
                    color = pathColor,
                    style = Stroke(width = 10f, cap = StrokeCap.Round)
                )
            }
        }

        // 🎮 NODES
        combinedNodes.forEachIndexed { index, (node, isBlendIt) ->
            val isLeft = index % 2 == 0
            val xPx = if (isLeft) leftXPx else rightXPx
            val yPx = (index + 1) * rowHeightPx

            val bounce = rememberInfiniteTransition(label = "bounce_$index")
                .animateFloat(
                    0f, 10f,
                    infiniteRepeatable(
                        tween(900),
                        RepeatMode.Reverse
                    ),
                    label = "b"
                ).value

            if (isBlendIt && node is MapNode.LetterNode) {
                val scale by animateFloatAsState(
                    targetValue = if (node.isUnlocked) 1f else 0.85f,
                    animationSpec = spring(),
                    label = "scale"
                )

                BlendItPlaceholder(
                    isUnlocked = node.isUnlocked,
                    size = nodeSize,
                    modifier = Modifier.offset(
                        x = with(density) { xPx.toDp() - nodeSize / 2 },
                        y = with(density) { (yPx.toDp()) + bounce.dp }
                    ),
                    scale = scale
                )
            } else if (node is MapNode.LetterNode && !isBlendIt) {
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

@Composable
fun BlendItPlaceholder(
    isUnlocked: Boolean,
    size: Dp,
    modifier: Modifier,
    scale: Float
) {
    val color = if (isUnlocked) colorBlendIt else colorBlendItLocked

    Column(
        modifier = modifier.scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                text = if (isUnlocked) "📖" else "🔒",
                fontSize = 32.sp
            )
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = "blendIT",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            maxLines = 1
        )

        Text(
            text = "Soon!",
            fontSize = 8.sp,
            color = color.copy(alpha = 0.7f)
        )
    }
}