package com.nextpeyk.mobileapp.ui.screens.home.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextpeyk.mobileapp.R
import com.nextpeyk.mobileapp.ui.theme.Accent
import com.nextpeyk.mobileapp.ui.theme.Ink
import com.nextpeyk.mobileapp.ui.theme.Line
import com.nextpeyk.mobileapp.ui.theme.Muted
import kotlinx.coroutines.launch
import kotlin.math.*

private const val LW = 402f
private const val LH = 800f
private const val CLUSTER_DIST = 46f
private const val SPIDER_RADIUS = 42f

private data class MapPin(
    val lx: Float, val ly: Float,
    val color: Color, val label: Int,
    val name: String, val isPaid: Boolean,
)

private val BLUE = Color(0xFF246FA3)
private val RED = Color(0xFFC43D3D)

private val mapPins = listOf(
    MapPin(250f, 205f, BLUE, 1, "بهار محمدی", true),
    MapPin(120f, 312f, BLUE, 2, "مهسا توکلی", true),
    MapPin(131f, 328f, BLUE, 3, "کیان داوری", true),
    MapPin(112f, 333f, RED, 4, "سارا مرادی", false),
    MapPin(300f, 470f, RED, 5, "آرمان رستمی", false),
)

private data class Cluster(val indices: List<Int>, val cx: Float, val cy: Float) {
    val isMulti get() = indices.size > 1
}

private fun buildClusters(): List<Cluster> {
    val assigned = BooleanArray(mapPins.size)
    val result = mutableListOf<Cluster>()
    for (i in mapPins.indices) {
        if (assigned[i]) continue
        val group = mutableListOf(i)
        assigned[i] = true
        for (j in i + 1 until mapPins.size) {
            if (!assigned[j]) {
                val dx = mapPins[i].lx - mapPins[j].lx
                val dy = mapPins[i].ly - mapPins[j].ly
                if (sqrt(dx * dx + dy * dy) < CLUSTER_DIST) {
                    group.add(j)
                    assigned[j] = true
                }
            }
        }
        val cx = group.map { mapPins[it].lx }.average().toFloat()
        val cy = group.map { mapPins[it].ly }.average().toFloat()
        result.add(Cluster(group, cx, cy))
    }
    return result
}

private val clusters = buildClusters()

private fun pinCluster(pinIdx: Int): Cluster = clusters.first { pinIdx in it.indices }

private fun displayPos(pinIdx: Int, expandedCluster: Cluster?, progress: Float): Pair<Float, Float> {
    val pin = mapPins[pinIdx]
    val cluster = pinCluster(pinIdx)
    if (!cluster.isMulti || cluster !== expandedCluster) return pin.lx to pin.ly
    val posInCluster = cluster.indices.indexOf(pinIdx)
    val n = cluster.indices.size
    val angle = (-PI / 2.0 + posInCluster * (2.0 * PI / n)).toFloat()
    val fx = cluster.cx + cos(angle) * SPIDER_RADIUS
    val fy = cluster.cy + sin(angle) * SPIDER_RADIUS
    return (pin.lx + (fx - pin.lx) * progress) to (pin.ly + (fy - pin.ly) * progress)
}

private fun logicalToScreen(lx: Float, ly: Float, canvasW: Float, canvasH: Float): Pair<Float, Float> {
    val scale = maxOf(canvasW / LW, canvasH / LH)
    val dx = (canvasW - LW * scale) / 2f
    val dy = (canvasH - LH * scale) / 2f
    return (lx * scale + dx) to (ly * scale + dy)
}

private fun DrawScope.drawMap() {
    val scale = maxOf(size.width / LW, size.height / LH)
    val dx = (size.width - LW * scale) / 2f
    val dy = (size.height - LH * scale) / 2f

    withTransform({ translate(dx, dy); scale(scale) }) {
        // Background
        drawRect(Color(0xFFDCE8E2), topLeft = Offset.Zero, size = Size(LW, LH))

        // Park areas
        drawRoundRect(Color(0xFFC8DFC5), topLeft = Offset(-10f, 300f), size = Size(200f, 220f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(30f))
        drawRoundRect(Color(0xFFC8DFC5), topLeft = Offset(260f, 50f), size = Size(170f, 200f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(30f))
        drawRoundRect(Color(0xFFC8DFC5), topLeft = Offset(80f, 560f), size = Size(140f, 140f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(22f))

        // Water
        val waterPath = Path().apply {
            moveTo(320f, 560f)
            quadraticBezierTo(390f, 530f, 440f, 580f)
            lineTo(440f, 800f)
            lineTo(300f, 800f)
            close()
        }
        drawPath(waterPath, Color(0xFFB0D4E8))

        // Buildings
        listOf(
            Offset(170f, 80f) to Size(75f, 70f),
            Offset(170f, 170f) to Size(75f, 80f),
            Offset(30f, 100f) to Size(65f, 55f),
            Offset(30f, 175f) to Size(65f, 60f),
        ).forEach { (tl, sz) ->
            drawRoundRect(Color(0xFFE4EBE4), topLeft = tl, size = sz, cornerRadius = androidx.compose.ui.geometry.CornerRadius(9f))
        }

        // Road casings
        val roadCase = Color(0xFFDFE6E6)
        drawLine(roadCase, Offset(-10f, 200f), Offset(420f, 200f), strokeWidth = 20f, cap = StrokeCap.Round)
        drawLine(roadCase, Offset(255f, -10f), Offset(255f, 820f), strokeWidth = 20f, cap = StrokeCap.Round)
        val curve1Case = Path().apply { moveTo(-10f, 440f); quadraticBezierTo(175f, 425f, 260f, 480f); quadraticBezierTo(345f, 535f, 430f, 465f) }
        drawPath(curve1Case, Color.Transparent, style = Stroke(width = 16f, cap = StrokeCap.Round))
        drawPath(curve1Case, roadCase, style = Stroke(width = 16f, cap = StrokeCap.Round))
        drawLine(roadCase, Offset(115f, -10f), Offset(115f, 820f), strokeWidth = 14f, cap = StrokeCap.Round)
        drawLine(roadCase, Offset(-10f, 650f), Offset(420f, 650f), strokeWidth = 12f, cap = StrokeCap.Round)

        // Roads
        val roadWhite = Color.White
        drawLine(roadWhite, Offset(-10f, 200f), Offset(420f, 200f), strokeWidth = 13f, cap = StrokeCap.Round)
        drawLine(roadWhite, Offset(255f, -10f), Offset(255f, 820f), strokeWidth = 13f, cap = StrokeCap.Round)
        val curve1 = Path().apply { moveTo(-10f, 440f); quadraticBezierTo(175f, 425f, 260f, 480f); quadraticBezierTo(345f, 535f, 430f, 465f) }
        drawPath(curve1, roadWhite, style = Stroke(width = 10f, cap = StrokeCap.Round))
        drawLine(roadWhite, Offset(115f, -10f), Offset(115f, 820f), strokeWidth = 8f, cap = StrokeCap.Round)
        drawLine(roadWhite, Offset(-10f, 650f), Offset(420f, 650f), strokeWidth = 8f, cap = StrokeCap.Round)
    }
}

private fun buildRoutePath(positions: List<Pair<Float, Float>>): Path {
    val pp = positions
    val path = Path()
    path.moveTo(pp[0].first, pp[0].second)
    for (i in 0 until pp.size - 1) {
        val p0 = if (i > 0) pp[i - 1] else pp[0]
        val p1 = pp[i]
        val p2 = pp[i + 1]
        val p3 = if (i + 2 < pp.size) pp[i + 2] else pp[pp.size - 1]
        val c1x = p1.first + (p2.first - p0.first) / 6f
        val c1y = p1.second + (p2.second - p0.second) / 6f
        val c2x = p2.first - (p3.first - p1.first) / 6f
        val c2y = p2.second - (p3.second - p1.second) / 6f
        path.cubicTo(c1x, c1y, c2x, c2y, p2.first, p2.second)
    }
    return path
}

@Composable
fun MapTabView(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val spiderfyProgress = remember { Animatable(0f) }
    var expandedCluster by remember { mutableStateOf<Cluster?>(null) }
    var selectedPinIdx by remember { mutableStateOf<Int?>(null) }
    var canvasSize by remember { mutableStateOf(Size(1f, 1f)) }
    val density = LocalDensity.current

    fun collapseSpiderfy() {
        scope.launch {
            spiderfyProgress.animateTo(0f, tween(520, easing = CubicBezierEasing(0f, 0f, 0.2f, 1f)))
            expandedCluster = null
        }
        selectedPinIdx = null
    }

    fun onPinTap(pinIdx: Int) {
        val cluster = pinCluster(pinIdx)
        if (cluster.isMulti) {
            if (expandedCluster !== cluster) {
                expandedCluster = cluster
                selectedPinIdx = pinIdx
                scope.launch {
                    spiderfyProgress.animateTo(1f, tween(520, easing = CubicBezierEasing(0f, 0f, 0.2f, 1f)))
                }
            } else {
                selectedPinIdx = if (selectedPinIdx == pinIdx) null else pinIdx
            }
        } else {
            selectedPinIdx = if (selectedPinIdx == pinIdx) null else pinIdx
        }
    }

    val progress = spiderfyProgress.value

    // Compute current display positions
    val displayPositions = mapPins.indices.map { displayPos(it, expandedCluster, progress) }

    // Route path in logical coords
    val routePath = buildRoutePath(displayPositions)

    // Motorcycle at midpoint of first segment
    val motoLx = (displayPositions[0].first + displayPositions[1].first) / 2f
    val motoLy = (displayPositions[0].second + displayPositions[1].second) / 2f

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFDCE8E2))
            .onSizeChanged { canvasSize = Size(it.width.toFloat(), it.height.toFloat()) }
            .pointerInput(Unit) { detectTapGestures { collapseSpiderfy() } },
    ) {
        // Map canvas (background + roads + route)
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawMap()

            val scale = maxOf(size.width / LW, size.height / LH)
            val dx = (size.width - LW * scale) / 2f
            val dy = (size.height - LH * scale) / 2f

            withTransform({ translate(dx, dy); scale(scale) }) {
                // Cluster rings
                clusters.filter { it.isMulti && it !== expandedCluster }.forEach { c ->
                    drawCircle(c.indices.first().let { mapPins[it].color }.copy(alpha = 0.12f), radius = 22f, center = Offset(c.cx, c.cy))
                    drawCircle(
                        c.indices.first().let { mapPins[it].color }.copy(alpha = 0.5f),
                        radius = 22f,
                        center = Offset(c.cx, c.cy),
                        style = Stroke(width = 1.2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3f, 4f))),
                    )
                }

                // Spider connector lines
                if (expandedCluster != null && progress > 0.01f) {
                    val cl = expandedCluster!!
                    cl.indices.forEach { idx ->
                        val dp = displayPositions[idx]
                        drawLine(
                            mapPins[idx].color.copy(alpha = 0.3f * progress),
                            Offset(cl.cx, cl.cy),
                            Offset(dp.first, dp.second),
                            strokeWidth = 1f,
                        )
                    }
                }

                // Route: white halo
                drawPath(routePath, Color.White.copy(alpha = 0.85f), style = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                // Route: dark center
                drawPath(routePath, Color(0xFF15191C), style = Stroke(width = 1.6f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        }

        // Motorcycle icon
        val (motoSx, motoSy) = logicalToScreen(motoLx, motoLy, canvasSize.width, canvasSize.height)
        val motoPx = with(density) { 32.dp.roundToPx() }
        Box(
            modifier = Modifier
                .offset { IntOffset((motoSx - motoPx / 2).toInt(), (motoSy - motoPx / 2).toInt()) }
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.motorcycle),
                contentDescription = "موتورسیکلت",
                modifier = Modifier.size(22.dp),
                contentScale = ContentScale.Fit,
            )
        }

        // Pin markers
        mapPins.indices.forEach { idx ->
            val (lx, ly) = displayPositions[idx]
            val (sx, sy) = logicalToScreen(lx, ly, canvasSize.width, canvasSize.height)
            val pin = mapPins[idx]
            val isSelected = selectedPinIdx == idx
            val pinSizePx = with(density) { 30.dp.roundToPx() }

            Box(
                modifier = Modifier
                    .offset { IntOffset((sx - pinSizePx / 2).toInt(), (sy - pinSizePx / 2).toInt()) }
                    .size(30.dp)
                    .pointerInput(idx) {
                        detectTapGestures { e ->
                            onPinTap(idx)
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                // Pulse ring
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(2.dp, pin.color.copy(alpha = 0.35f), CircleShape),
                )
                // Pin circle
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 34.dp else 30.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                0f to Color.White,
                                0.46f to pin.color,
                                0.7f to pin.color,
                                1f to Color(0x6B000000),
                            )
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        pin.label.toString(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                    )
                }

                // Tooltip on select
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .offset(y = (-52).dp)
                            .clip(RoundedCornerShape(13.dp))
                            .background(Color.White)
                            .padding(horizontal = 12.dp, vertical = 9.dp)
                            .pointerInput(Unit) { detectTapGestures { } },
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                                Box(
                                    modifier = Modifier.size(19.dp).clip(CircleShape).background(pin.color),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(pin.label.toString(), fontSize = 10.5.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                }
                                Text(pin.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Ink)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                if (pin.isPaid) "پرداخت شده" else "پرداخت در محل",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = pin.color,
                            )
                        }
                    }
                }
            }
        }

        // Zoom-out button
        if (expandedCluster != null && progress > 0.04f) {
            Box(
                modifier = Modifier
                    .padding(14.dp)
                    .align(Alignment.TopStart)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.96f))
                    .clickable { collapseSpiderfy() }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Filled.ZoomOutMap, contentDescription = null, tint = Accent, modifier = Modifier.size(15.dp))
                    Text("نمای کامل", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Ink)
                }
            }
        }

        // Legend card
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White.copy(alpha = 0.94f))
                .padding(horizontal = 18.dp, vertical = 12.dp)
                .pointerInput(Unit) { detectTapGestures { } },
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        listOf(BLUE to "پرداخت شده", RED to "پرداخت در محل").forEach { (col, lbl) ->
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.radialGradient(
                                                0f to Color.White,
                                                0.46f to col,
                                                0.7f to col,
                                                1f to Color(0x59000000),
                                            )
                                        ),
                                )
                                Text(lbl, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Ink)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Line))
                Spacer(Modifier.height(8.dp))
                Text(
                    if (expandedCluster != null) "برای بازگشت، بیرون از مارکرها را لمس کنید"
                    else "روی مرسوله‌های نزدیکِ هم بزنید تا نقشه باز شود",
                    fontSize = 11.sp,
                    color = Muted,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
