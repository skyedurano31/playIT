package com.playit.app.presentation.map

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.playit.app.domain.model.MapNode
import androidx.compose.ui.unit.times

val colorLocked = Color(0xFFBDBDBD)
val colorUnstarred = Color(0xFFFFA726)
val colorOneStar = Color(0xFF42A5F5)
val colorTwoStar = Color(0xFF66BB6A)
val colorThreeStar = Color(0xFF4CAF50)

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

    Column(modifier = Modifier.fillMaxSize()) {

        // top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "playIT",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onDashboardClicked) {
                Text(
                    text = "Dashboard",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
                    .verticalScroll(scrollState)
            ) {
                WindingPathMap(
                    nodes = mapNodes,
                    onNodeClick = { node ->
                        if (node.isUnlocked) {
                            onLetterSelected(node.phonemeId)
                        }
                    },
                    onBlendItClick = { node ->
                        if (node.isUnlocked) {
                            onBlendItSelected(node.groupId)
                        }
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
    val nodeSize = 72.dp
    val blendItWidth = 110.dp
    val blendItHeight = 44.dp
    val rowHeight = 120.dp
    val screenWidth = 360.dp
    val leftX = 80.dp
    val rightX = screenWidth - 80.dp

    val density = LocalDensity.current

    // Convert to pixels for Canvas
    val nodeSizePx = with(density) { nodeSize.toPx() }
    val blendItHeightPx = with(density) { blendItHeight.toPx() }
    val rowHeightPx = with(density) { rowHeight.toPx() }
    val leftXPx = with(density) { leftX.toPx() }
    val rightXPx = with(density) { rightX.toPx() }

    // Calculate total height in Dp for the Box
    val totalHeight = with(density) {
        ((nodes.size + 1) * rowHeightPx + 80.dp.toPx()).toDp()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(totalHeight)
    ) {
        // Draw connecting path (Canvas uses pixels)
        Canvas(modifier = Modifier.fillMaxSize()) {
            nodes.forEachIndexed { index, node ->
                if (index < nodes.size - 1) {
                    val isLeft = index % 2 == 0
                    val nextIsLeft = (index + 1) % 2 == 0

                    val currentX = if (isLeft) leftXPx else rightXPx
                    val nextX = if (nextIsLeft) leftXPx else rightXPx

                    val currentCenterY = when (node) {
                        is MapNode.LetterNode -> (index + 1) * rowHeightPx + nodeSizePx / 2
                        is MapNode.BlendItNode -> (index + 1) * rowHeightPx + blendItHeightPx / 2
                    }
                    val nextNode = nodes[index + 1]
                    val nextCenterY = when (nextNode) {
                        is MapNode.LetterNode -> (index + 2) * rowHeightPx + nodeSizePx / 2
                        is MapNode.BlendItNode -> (index + 2) * rowHeightPx + blendItHeightPx / 2
                    }

                    val isUnlocked = when (node) {
                        is MapNode.LetterNode -> node.isUnlocked
                        is MapNode.BlendItNode -> node.isUnlocked
                    }
                    val pathColor = if (isUnlocked) Color(0xFF90CAF9) else Color(0xFFE0E0E0)

                    val path = Path().apply {
                        moveTo(currentX, currentCenterY)
                        cubicTo(
                            currentX, currentCenterY + rowHeightPx * 0.5f,
                            nextX, nextCenterY - rowHeightPx * 0.5f,
                            nextX, nextCenterY
                        )
                    }
                    drawPath(
                        path = path,
                        color = pathColor,
                        style = Stroke(width = 8f, cap = StrokeCap.Round)
                    )
                }
            }
        }

        // Draw nodes (using Dp values for offsets)
        nodes.forEachIndexed { index, node ->
            val isLeft = index % 2 == 0
            val yOffset = (index + 1) * rowHeight

            when (node) {
                is MapNode.LetterNode -> {
                    val xOffset = if (isLeft) leftX - nodeSize / 2
                    else rightX - nodeSize / 2
                    LetterNodeCircle(
                        node = node,
                        size = nodeSize,
                        modifier = Modifier
                            .offset(x = xOffset, y = yOffset)
                            .clickable(enabled = node.isUnlocked) {
                                onNodeClick(node)
                            }
                    )
                }
                is MapNode.BlendItNode -> {
                    val xOffset = if (isLeft) leftX - blendItWidth / 2
                    else rightX - blendItWidth / 2
                    BlendItNodeBanner(
                        node = node,
                        modifier = Modifier
                            .offset(x = xOffset, y = yOffset)
                            .clickable(enabled = node.isUnlocked) {
                                onBlendItClick(node)
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun LetterNodeCircle(
    node: MapNode.LetterNode,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val nodeColor = when {
        !node.isUnlocked -> colorLocked
        node.starsEarned == 3 -> colorThreeStar
        node.starsEarned == 2 -> colorTwoStar
        node.starsEarned == 1 -> colorOneStar
        else -> colorUnstarred
    }

    val scale by animateFloatAsState(
        targetValue = if (node.isUnlocked) 1f else 0.85f,
        animationSpec = tween(300),
        label = "scale"
    )

    Column(
        modifier = modifier.scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(nodeColor),
            contentAlignment = Alignment.Center
        ) {
            if (!node.isUnlocked) {
                Text(text = "🔒", fontSize = 24.sp)
            } else {
                Text(
                    text = node.letter,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (node.starsEarned > 0) {
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(3) { index ->
                    Text(
                        text = if (index < node.starsEarned) "⭐" else "☆",
                        fontSize = 12.sp
                    )
                }
            }
        } else if (node.isUnlocked) {
            Text(
                text = node.letter,
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun BlendItNodeBanner(
    node: MapNode.BlendItNode,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        !node.isUnlocked -> colorLocked
        node.starsEarned > 0 -> Color(0xFF7B1FA2)
        else -> Color(0xFF9C27B0)
    }

    val scale by animateFloatAsState(
        targetValue = if (node.isUnlocked) 1f else 0.85f,
        animationSpec = tween(300),
        label = "blendScale"
    )

    Column(
        modifier = modifier.scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(110.dp)
                .height(44.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (node.isUnlocked) "🔤" else "🔒",
                    fontSize = 16.sp
                )
                Text(
                    text = "Blend It",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (node.starsEarned > 0) {
            Spacer(modifier = Modifier.height(2.dp))
            Row {
                repeat(3) { index ->
                    Text(
                        text = if (index < node.starsEarned) "⭐" else "☆",
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}