package com.playit.app.presentation.map

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.playit.app.domain.model.MapNode
import kotlin.math.cos
import kotlin.math.sin

// 🎨 Kid-Friendly Colors
val colorLocked = Color(0xFF9E9E9E)
val colorUnstarred = Color(0xFFFFB74D)  // Warm orange
val colorOneStar = Color(0xFF64B5F6)    // Light blue
val colorTwoStar = Color(0xFF81C784)    // Soft green
val colorThreeStar = Color(0xFFFFD54F)  // Golden yellow

// 🌟 Glow colors for unlocked nodes
val colorGlow = Color(0x33FFD54F)

// 🎪 Playful backgrounds
val colorPathUnlocked = Color(0xFFFFCC80)  // Warm path
val colorPathLocked = Color(0xFFE0E0E0)     // Gray path
val colorBackground = Color(0xFFFDF5E6)     // Cream background

@Composable
fun MapScreen(
    onLetterSelected: (Int) -> Unit,
    onBlendItSelected: (Int) -> Unit,
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

    // Auto-scroll to bottom (M) on load
    LaunchedEffect(mapNodes) {
        if (mapNodes.isNotEmpty()) {
            scrollState.scrollTo(scrollState.maxValue)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 🎨 Playful Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFF6B6B), Color(0xFFFF8E53))
                    )
                )
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "",
                    fontSize = 28.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "playIT",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Card(
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))
            ) {
                TextButton(onClick = onDashboardClicked) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📊", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Parent Zone",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFFF6B6B))
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorBackground)
                    .verticalScroll(scrollState)
            ) {
                WindingPathMap(
                    nodes = mapNodes,
                    onNodeClick = { node ->
                        if (node.isUnlocked) onLetterSelected(node.phonemeId)
                    },
                    onBlendItClick = { node ->
                        if (node.isUnlocked) onBlendItSelected(node.groupId)
                    }
                )
            }
        }
    }
}

@Composable
fun WindingPathMap(
    nodes: List<MapNode>,
    onNodeClick: (MapNode.LetterNode) -> Unit,
    onBlendItClick: (MapNode.BlendItNode) -> Unit
) {
    val nodeSize = 80.dp
    val blendItWidth = 120.dp
    val blendItHeight = 50.dp
    val rowHeight = 130.dp
    val screenWidth = 360.dp
    val leftX = 70.dp
    val rightX = screenWidth - 70.dp

    val totalHeight = (nodes.size + 1) * rowHeight + 100.dp
    val density = LocalDensity.current

    // Display nodes as-is (already bottom-to-top from ViewModel)
    val displayNodes = nodes

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(totalHeight)
    ) {
        val nodeSizePx = with(density) { nodeSize.toPx() }
        val blendItHeightPx = with(density) { blendItHeight.toPx() }
        val rowHeightPx = with(density) { rowHeight.toPx() }
        val leftXPx = with(density) { leftX.toPx() }
        val rightXPx = with(density) { rightX.toPx() }
        val totalHeightPx = with(density) { totalHeight.toPx() }

        // 🎨 Draw glowing path with gradient
        Canvas(modifier = Modifier.fillMaxSize()) {
            displayNodes.forEachIndexed { index, node ->
                if (index < displayNodes.size - 1) {
                    val isLeft = index % 2 == 0
                    val nextIsLeft = (index + 1) % 2 == 0

                    val currentX = if (isLeft) leftXPx else rightXPx
                    val nextX = if (nextIsLeft) leftXPx else rightXPx

                    val currentCenterY = when (node) {
                        is MapNode.LetterNode ->
                            totalHeightPx - (index + 1) * rowHeightPx - nodeSizePx / 2
                        is MapNode.BlendItNode ->
                            totalHeightPx - (index + 1) * rowHeightPx - blendItHeightPx / 2
                    }

                    val nextNode = displayNodes[index + 1]
                    val nextCenterY = when (nextNode) {
                        is MapNode.LetterNode ->
                            totalHeightPx - (index + 2) * rowHeightPx - nodeSizePx / 2
                        is MapNode.BlendItNode ->
                            totalHeightPx - (index + 2) * rowHeightPx - blendItHeightPx / 2
                    }

                    val isUnlocked = when (node) {
                        is MapNode.LetterNode -> node.isUnlocked
                        is MapNode.BlendItNode -> node.isUnlocked
                    }

                    // 🎨 Gradient path colors
                    val pathGradient = Brush.linearGradient(
                        colors = if (isUnlocked)
                            listOf(Color(0xFFFFB74D), Color(0xFFFFCC80))
                        else
                            listOf(Color(0xFFBDBDBD), Color(0xFFE0E0E0))
                    )

                    val path = Path().apply {
                        moveTo(currentX, currentCenterY)
                        cubicTo(
                            currentX, currentCenterY - rowHeightPx * 0.5f,
                            nextX, nextCenterY + rowHeightPx * 0.5f,
                            nextX, nextCenterY
                        )
                    }

                    // Draw glow effect
                    drawPath(
                        path = path,
                        color = if (isUnlocked) Color(0x33FFB74D) else Color.Transparent,
                        style = Stroke(width = 16f, cap = StrokeCap.Round)
                    )

                    drawPath(
                        path = path,
                        brush = pathGradient,
                        style = Stroke(width = 8f, cap = StrokeCap.Round)
                    )

                    // ✨ Sparkles on unlocked paths
                    if (isUnlocked && index % 2 == 0) {
                        val t = 0.3f
                        val midX = (currentX + nextX) / 2
                        val midY = (currentCenterY + nextCenterY) / 2
                        drawCircle(
                            color = Color(0xFFFFD54F),
                            radius = 4f,
                            center = Offset(midX, midY)
                        )
                    }
                }
            }
        }

        // 🎨 Draw nodes with bounce animation
        displayNodes.forEachIndexed { index, node ->
            val isLeft = index % 2 == 0

            when (node) {
                is MapNode.LetterNode -> {
                    val xOffset = if (isLeft) leftX - nodeSize / 2
                    else rightX - nodeSize / 2
                    val yOffset = totalHeight - (index + 1) * rowHeight - nodeSize

                    LetterNodeCircle(
                        node = node,
                        size = nodeSize,
                        modifier = Modifier
                            .offset(x = xOffset, y = yOffset)
                    ) {
                        if (node.isUnlocked) onNodeClick(node)
                    }
                }
                is MapNode.BlendItNode -> {
                    val xOffset = if (isLeft) leftX - blendItWidth / 2
                    else rightX - blendItWidth / 2
                    val yOffset = totalHeight - (index + 1) * rowHeight - blendItHeight

                    BlendItNodeBanner(
                        node = node,
                        modifier = Modifier
                            .offset(x = xOffset, y = yOffset)
                    ) {
                        if (node.isUnlocked) onBlendItClick(node)
                    }
                }
            }
        }
    }
}

@Composable
fun LetterNodeCircle(
    node: MapNode.LetterNode,
    size: Dp,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // 🎨 Playful color scheme
    val nodeColor = when {
        !node.isUnlocked -> colorLocked
        node.starsEarned == 3 -> colorThreeStar
        node.starsEarned == 2 -> colorTwoStar
        node.starsEarned == 1 -> colorOneStar
        else -> colorUnstarred
    }

    // ✨ Glow animation for unlocked
    val glowAlpha by animateFloatAsState(
        targetValue = if (node.isUnlocked && node.starsEarned > 0) 0.3f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // 🎯 Bounce animation for unlocked
    val scale by animateFloatAsState(
        targetValue = if (node.isUnlocked) 1f else 0.85f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Column(
        modifier = modifier
            .scale(scale)
            .clickable(enabled = node.isUnlocked) { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        // 🌟 Glow background
//        if (node.isUnlocked && node.starsEarned > 0) {
//            Box(
//                modifier = Modifier
//                    .size(size + 12.dp)
//                    .clip(CircleShape)
//                    .background(colorGlow.copy(alpha = glowAlpha))
//            )
//        }

        // 📦 Main letter circle
        Box(
            modifier = Modifier
                .size(size)
                .shadow(
                    elevation = if (node.isUnlocked) 8.dp else 4.dp,
                    shape = CircleShape,
                    clip = false
                )
                .clip(CircleShape)
                .background(
                    brush = if (node.isUnlocked && node.starsEarned > 0)
                        Brush.radialGradient(
                            colors = listOf(nodeColor, nodeColor.copy(alpha = 0.8f))
                        )
                    else Brush.verticalGradient(
                        colors = listOf(nodeColor, nodeColor.copy(alpha = 0.7f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (!node.isUnlocked) {
                Text(text = "🔒", fontSize = 28.sp)
            } else {
                Text(
                    text = node.letter,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // ⭐ Star display with animation
        if (node.starsEarned > 0) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.shadow(2.dp, CircleShape)
            ) {
                repeat(3) { index ->
                    val starScale by animateFloatAsState(
                        targetValue = if (index < node.starsEarned) 1.2f else 1f,
                        animationSpec = repeatable(
                            iterations = 3,
                            animation = tween(200)
                        ),
                        label = "star$index"
                    )
                    Text(
                        text = if (index < node.starsEarned) "⭐" else "☆",
                        fontSize = 14.sp,
                        modifier = Modifier.scale(starScale)
                    )
                }
            }
        } else if (node.isUnlocked) {
            Text(
                text = "✨ tap to play ✨",
                fontSize = 10.sp,
                color = Color(0xFFFF8E53),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun BlendItNodeBanner(
    node: MapNode.BlendItNode,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bgColor = when {
        !node.isUnlocked -> colorLocked
        node.starsEarned > 0 -> Color(0xFF9C27B0)  // Purple for completed
        else -> Color(0xFF7B1FA2)  // Darker purple for available
    }

    // 🎯 Scale animation for unlocked
    val scale by animateFloatAsState(
        targetValue = if (node.isUnlocked) 1f else 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    // ✨ Pulsing effect for available
    val pulse by animateFloatAsState(
        targetValue = if (node.isUnlocked && node.starsEarned == 0) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = modifier
            .scale(scale * pulse)
            .clickable(enabled = node.isUnlocked) { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(50.dp)
                .shadow(
                    elevation = if (node.isUnlocked) 6.dp else 3.dp,
                    shape = RoundedCornerShape(25.dp),
                    clip = false
                )
                .clip(RoundedCornerShape(25.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(bgColor, bgColor.copy(alpha = 0.85f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (node.isUnlocked) "🎮" else "🔒",
                    fontSize = 20.sp
                )
                Text(
                    text = "Blend It",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                if (node.isUnlocked && node.starsEarned == 0) {
                    Text(
                        text = "✨",
                        fontSize = 14.sp
                    )
                }
            }
        }

        if (node.starsEarned > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(3) { index ->
                    Text(
                        text = if (index < node.starsEarned) "⭐" else "☆",
                        fontSize = 11.sp
                    )
                }
            }
        } else if (node.isUnlocked) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "📝 word game",
                fontSize = 9.sp,
                color = Color(0xFF9C27B0),
                fontWeight = FontWeight.Medium
            )
        }
    }
}