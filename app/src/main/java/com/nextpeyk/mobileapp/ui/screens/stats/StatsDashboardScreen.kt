package ir.nextpeyk.android.ui.screens.stats

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import kotlinx.coroutines.delay

// ── Design tokens ────────────────────────────────────────────────
private val S_INK      = Color(0xFF14181B)
private val S_MUTED    = Color(0xFF8A9499)
private val S_FAINT    = Color(0xFFB4BBBF)
private val S_LINE     = Color(0xFFEEF1F1)
private val S_PAGE     = Color(0xFFEEF0F0)
private val S_ACCENT   = Color(0xFF246FA3)
private val S_ACSOFT   = Color(0xFFE9F1F7)
private val S_GREEN    = Color(0xFF1A7F4B)
private val S_AMBER    = Color(0xFFC98A16)
private val S_RED      = Color(0xFFB83030)
private val S_GUIDE    = Color(0xFFEEF1F1)
private val S_LABEL    = Color(0xFF9AA4A9)

// ── Data ─────────────────────────────────────────────────────────
private enum class StatsPeriod(val label: String, val dayDivisor: Int) {
    Daily("روزانه", 1), Weekly("هفتگی", 7), Monthly("ماهانه", 30)
}

private data class PeriodData(
    val labels: List<String>,
    val delivered: List<Int>,
    val returned: List<Int>,
    val total: Int, val done: Int, val ret: Int, val pending: Int,
    val earnings: String,
)

private val allData = mapOf(
    StatsPeriod.Daily to PeriodData(
        labels = listOf("۸","۹","۱۰","۱۱","۱۲","۱۳","۱۴","۱۵","۱۶","۱۷","۱۸","۱۹"),
        delivered = listOf(2,4,5,7,3,6,8,5,4,7,9,6),
        returned  = listOf(0,1,0,1,1,0,1,0,1,0,2,1),
        total=62, done=47, ret=8, pending=7, earnings="۳٬۴۵۰٬۰۰۰",
    ),
    StatsPeriod.Weekly to PeriodData(
        labels = listOf("شن","یک","دو","سه","چه","پنج","جم"),
        delivered = listOf(18,24,19,31,27,22,35),
        returned  = listOf(3,2,4,3,2,5,3),
        total=176, done=154, ret=22, pending=0, earnings="۱۱٬۲۰۰٬۰۰۰",
    ),
    StatsPeriod.Monthly to PeriodData(
        labels = listOf("۱","۵","۱۰","۱۵","۲۰","۲۵","۳۰"),
        delivered = listOf(42,58,63,71,55,78,90),
        returned  = listOf(6,8,7,9,5,10,8),
        total=712, done=621, ret=64, pending=27, earnings="۴۸٬۸۰۰٬۰۰۰",
    ),
)

// ── Main Screen ──────────────────────────────────────────────────
@Composable
fun StatsDashboardScreen(onBack: () -> Unit) {
    var period by remember { mutableStateOf(StatsPeriod.Daily) }
    var animKey by remember { mutableIntStateOf(0) }
    val data = allData[period]!!

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(S_PAGE)
                .windowInsetsPadding(WindowInsets.systemBars),
        ) {
            StatsTopBar(onBack = onBack)
            PeriodTabBar(selected = period, onSelect = { period = it; animKey++ })
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp)
                    .padding(bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                SummaryRow(data = data)
                DonutLegendCard(data = data, animKey = animKey)
                BarChartCard(data = data, animKey = animKey)
                LineChartCard(data = data, animKey = animKey)
                QuickStatsCard(data = data, period = period)
            }
        }
    }
}

// ── TopBar ───────────────────────────────────────────────────────
@Composable
private fun StatsTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .border(1.dp, S_LINE, RoundedCornerShape(14.dp))
                .clickable { onBack() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = S_INK, modifier = Modifier.size(19.dp))
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("داشبورد آمار", fontSize = 15.5.sp, fontWeight = FontWeight.Bold, color = S_INK)
            Text("نوید رضایی · پیک فعال", fontSize = 11.sp, color = S_MUTED, fontWeight = FontWeight.Medium)
        }
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(S_ACCENT),
            contentAlignment = Alignment.Center,
        ) {
            Text("ن", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

// ── Period Tabs ──────────────────────────────────────────────────
@Composable
private fun PeriodTabBar(selected: StatsPeriod, onSelect: (StatsPeriod) -> Unit) {
    Box(modifier = Modifier.padding(horizontal = 18.dp, vertical = 0.dp).padding(bottom = 12.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            StatsPeriod.entries.forEach { period ->
                val active = period == selected
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (active) S_INK else Color.Transparent)
                        .clickable { onSelect(period) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        period.label,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (active) Color.White else S_MUTED,
                    )
                }
            }
        }
    }
}

// ── Summary Cards ────────────────────────────────────────────────
@Composable
private fun SummaryRow(data: PeriodData) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SummaryCard(
            modifier = Modifier.weight(1f),
            label = "کل مرسوله",
            value = data.total.toString(),
            sub = "+${(data.total * 0.08).roundToInt()} نسبت به قبل",
            color = S_ACCENT,
            iconColor = S_ACCENT,
            iconContent = {
                // Package icon
                androidx.compose.foundation.Canvas(modifier = Modifier.size(18.dp)) {
                    val s = size.width / 24f
                    val stroke = Stroke(1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    drawPath(Path().apply {
                        moveTo(21*s,8*s); lineTo(12*s,3*s); lineTo(3*s,8*s); lineTo(12*s,13*s); close()
                    }, S_ACCENT, style = stroke)
                    drawPath(Path().apply {
                        moveTo(3*s,8*s); lineTo(3*s,16*s); lineTo(12*s,21*s); lineTo(21*s,16*s); lineTo(21*s,8*s)
                    }, S_ACCENT, style = stroke)
                }
            },
        )
        SummaryCard(
            modifier = Modifier.weight(1f),
            label = "درآمد (تومان)",
            value = data.earnings,
            sub = null,
            color = S_GREEN,
            iconColor = S_GREEN,
            iconContent = {
                // Money/payment icon
                androidx.compose.foundation.Canvas(modifier = Modifier.size(18.dp)) {
                    val s = size.width / 24f
                    val stroke = Stroke(1.8.dp.toPx(), cap = StrokeCap.Round)
                    drawRoundRect(S_GREEN, Offset(2.5f*s, 6*s), Size(19*s, 12*s), CornerRadius(2.5f*s), style = stroke)
                    drawCircle(S_GREEN, 2.6f*s, Offset(12*s, 12*s), style = stroke)
                }
            },
        )
    }
}

@Composable
private fun SummaryCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    sub: String?,
    color: Color,
    iconColor: Color,
    iconContent: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .padding(horizontal = 14.dp, vertical = 13.dp),
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(color.copy(alpha = 0.09f)),
            contentAlignment = Alignment.Center,
        ) {
            iconContent()
        }
        Spacer(Modifier.height(9.dp))
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = S_INK, lineHeight = 22.sp, letterSpacing = (-0.3).sp)
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 11.sp, color = S_MUTED, fontWeight = FontWeight.Medium)
        if (sub != null) {
            Spacer(Modifier.height(3.dp))
            Text(sub, fontSize = 10.5.sp, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

// ── Donut + Legend ───────────────────────────────────────────────
@Composable
private fun DonutLegendCard(data: PeriodData, animKey: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        DonutRing(done = data.done, ret = data.ret, total = data.total)

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            listOf(
                Triple(S_GREEN, "تحویل موفق", data.done),
                Triple(S_RED, "مرجوع", data.ret),
                Triple(S_AMBER, "در انتظار", data.pending),
            ).forEach { (color, label, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
                        Text(label, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = S_INK)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        val pct = if (data.total > 0) value.toFloat() / data.total else 0f
                        Box(
                            modifier = Modifier
                                .width(70.dp)
                                .height(5.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF0F3F4)),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(pct)
                                    .fillMaxHeight()
                                    .clip(CircleShape)
                                    .background(color),
                            )
                        }
                        Text(
                            value.toString(),
                            fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = S_INK,
                            modifier = Modifier.widthIn(min = 24.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DonutRing(done: Int, ret: Int, total: Int) {
    Box(
        modifier = Modifier.size(104.dp),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val r = 42.dp.toPx()
            val sw = 10.dp.toPx()
            val center = Offset(size.width / 2f, size.height / 2f)
            val box = androidx.compose.ui.geometry.Rect(center.x - r, center.y - r, center.x + r, center.y + r)
            val t = total.coerceAtLeast(1).toFloat()
            val doneP = done / t
            val retP = ret / t
            val pendP = (1f - doneP - retP).coerceAtLeast(0f)

            // Background ring
            drawCircle(Color(0xFFF0F3F4), r, center, style = Stroke(sw))

            // Pending (amber)
            var start = -90f
            val pendSweep = pendP * 360f
            if (pendSweep > 0f) {
                drawArc(S_AMBER, start, pendSweep, false, box.topLeft, Size(box.width, box.height), style = Stroke(sw, cap = StrokeCap.Round))
                start += pendSweep
            }
            // Returned (red)
            val retSweep = retP * 360f
            if (retSweep > 0f) {
                drawArc(S_RED.copy(alpha = 0.8f), start, retSweep, false, box.topLeft, Size(box.width, box.height), style = Stroke(sw, cap = StrokeCap.Round))
                start += retSweep
            }
            // Done (green)
            val doneSweep = doneP * 360f
            if (doneSweep > 0f) {
                drawArc(S_GREEN, start, doneSweep, false, box.topLeft, Size(box.width, box.height), style = Stroke(sw, cap = StrokeCap.Round))
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(done.toString(), fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = S_INK)
            Text("تحویل", fontSize = 9.sp, color = S_MUTED)
        }
    }
}

// ── Bar Chart Card ────────────────────────────────────────────────
@Composable
private fun BarChartCard(data: PeriodData, animKey: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .padding(bottom = 6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("تحویل در برابر مرجوع", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = S_INK)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LegendDot(color = S_GREEN, label = "تحویل")
                LegendDot(color = S_RED.copy(alpha = 0.7f), label = "مرجوع")
            }
        }
        BarChart(labels = data.labels, delivered = data.delivered, returned = data.returned, animKey = animKey)
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(color))
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = S_MUTED)
    }
}

@Composable
private fun BarChart(labels: List<String>, delivered: List<Int>, returned: List<Int>, animKey: Int) {
    val textMeasurer = rememberTextMeasurer()
    val progress = remember(animKey) { Animatable(0f) }
    LaunchedEffect(animKey) {
        delay(60)
        progress.animateTo(1f, tween(500, easing = CubicBezierEasing(0.2f, 0.8f, 0.25f, 1f)))
    }

    androidx.compose.foundation.Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(134.dp),
    ) {
        val n = labels.size
        val labelAreaH = 20.dp.toPx()
        val chartH = size.height - labelAreaH
        val pad = 6.dp.toPx()
        val maxVal = (delivered.indices.maxOfOrNull { i -> delivered[i] + returned[i] } ?: 1).toFloat()
        val gap = (size.width - pad * 2) / n
        val bw = gap * 0.55f

        // Guide lines
        listOf(0.25f, 0.5f, 0.75f, 1f).forEach { ratio ->
            val y = chartH - chartH * ratio
            drawLine(S_GUIDE, Offset(pad, y), Offset(size.width - pad, y), 1f)
        }

        // Bars
        labels.indices.forEach { i ->
            val cx = pad + gap * i + gap / 2f
            val totalRatio = (delivered[i] + returned[i]) / maxVal
            val delRatio = delivered[i] / maxVal
            val totalH = (totalRatio * chartH * progress.value).coerceAtLeast(3f)
            val delH = (delRatio * chartH * progress.value).coerceAtLeast(3f)
            val retH = (totalH - delH).coerceAtLeast(0f)

            // Gray total background
            drawRoundRect(
                Color(0xFFEEF1F1),
                topLeft = Offset(cx - bw / 2, chartH - totalH),
                size = Size(bw, totalH),
                cornerRadius = CornerRadius(bw / 4),
            )
            // Green delivered
            drawRoundRect(
                S_GREEN,
                topLeft = Offset(cx - bw / 2, chartH - delH),
                size = Size(bw, delH),
                cornerRadius = CornerRadius(bw / 4),
            )
            // Red returned (on top)
            if (retH > 0f) {
                drawRoundRect(
                    S_RED.copy(alpha = 0.7f),
                    topLeft = Offset(cx - bw / 2, chartH - totalH),
                    size = Size(bw, retH),
                    cornerRadius = CornerRadius(minOf(retH / 2, bw / 4)),
                )
            }

            // Label
            val measured = textMeasurer.measure(
                labels[i],
                style = TextStyle(fontSize = 9.5.sp, color = S_LABEL, fontWeight = FontWeight.SemiBold),
            )
            drawText(
                measured,
                topLeft = Offset(cx - measured.size.width / 2f, chartH + 6.dp.toPx()),
            )
        }
    }
}

// ── Line Chart Card ───────────────────────────────────────────────
@Composable
private fun LineChartCard(data: PeriodData, animKey: Int) {
    val growthPct = (data.done * 0.08).roundToInt()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .padding(bottom = 6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("روند تحویل موفق", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = S_INK)
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(S_ACSOFT)
                    .padding(horizontal = 9.dp, vertical = 3.dp),
            ) {
                Text("+$growthPct%", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = S_ACCENT)
            }
        }
        LineChart(labels = data.labels, delivered = data.delivered, animKey = animKey)
    }
}

@Composable
private fun LineChart(labels: List<String>, delivered: List<Int>, animKey: Int) {
    val textMeasurer = rememberTextMeasurer()
    val progress = remember(animKey) { Animatable(0f) }
    LaunchedEffect(animKey) {
        delay(80)
        progress.animateTo(1f, tween(600, easing = CubicBezierEasing(0.2f, 0.8f, 0.25f, 1f)))
    }

    androidx.compose.foundation.Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(122.dp),
    ) {
        val n = labels.size
        if (n < 2) return@Canvas
        val labelAreaH = 22.dp.toPx()
        val chartH = size.height - labelAreaH
        val pad = 16.dp.toPx()
        val maxVal = (delivered.maxOrNull() ?: 1).toFloat() * 1.15f

        // Guide lines
        listOf(0.25f, 0.5f, 0.75f, 1f).forEach { r ->
            val y = chartH - (chartH * r * 0.85f + 5.dp.toPx())
            drawLine(S_GUIDE, Offset(pad, y), Offset(size.width - pad, y), 1f)
        }

        val pts = delivered.mapIndexed { i, v ->
            val x = pad + (size.width - pad * 2) / (n - 1) * i
            val y = chartH - (v / maxVal * (chartH - 10.dp.toPx()) + 5.dp.toPx())
            Offset(x, y)
        }

        // Gradient fill under line
        val fillPath = Path().apply {
            moveTo(pts.first().x, pts.first().y)
            pts.forEach { lineTo(it.x, it.y) }
            lineTo(pts.last().x, chartH)
            lineTo(pts.first().x, chartH)
            close()
        }
        drawPath(
            fillPath,
            Brush.verticalGradient(
                colors = listOf(S_ACCENT.copy(alpha = 0.22f), S_ACCENT.copy(alpha = 0f)),
                startY = 0f, endY = chartH,
            ),
        )

        // Line
        val linePath = Path().apply {
            moveTo(pts.first().x, pts.first().y)
            pts.drop(1).forEach { lineTo(it.x, it.y) }
        }
        drawPath(linePath, S_ACCENT, style = Stroke(2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Dots
        pts.forEach { pt ->
            drawCircle(Color.White, 4.dp.toPx(), pt)
            drawCircle(S_ACCENT, 4.dp.toPx(), pt, style = Stroke(2.dp.toPx()))
        }

        // X-axis labels
        labels.forEachIndexed { i, lbl ->
            val measured = textMeasurer.measure(
                lbl,
                style = TextStyle(fontSize = 9.5.sp, color = S_LABEL, fontWeight = FontWeight.SemiBold),
            )
            drawText(measured, topLeft = Offset(pts[i].x - measured.size.width / 2f, chartH + 6.dp.toPx()))
        }
    }
}

// ── Quick Stats ───────────────────────────────────────────────────
@Composable
private fun QuickStatsCard(data: PeriodData, period: StatsPeriod) {
    val t = data.total.coerceAtLeast(1)
    val deliveryRate = "${(data.done.toFloat() / t * 100).roundToInt()}%"
    val dailyAvg = (data.done.toFloat() / period.dayDivisor).roundToInt().toString()
    val returnRate = "${(data.ret.toFloat() / t * 100).roundToInt()}%"
    val pending = data.pending.toString()

    val items = listOf(
        Triple("نرخ تحویل", deliveryRate, Color(0xFF7AD4A8)),
        Triple("میانگین روزانه", dailyAvg, Color(0xFF7AB8E4)),
        Triple("نرخ مرجوع", returnRate, Color(0xFFF08080)),
        Triple("در انتظار", pending, Color(0xFFFFD080)),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(S_INK)
            .padding(16.dp),
    ) {
        Text(
            "خلاصه‌ی سریع",
            fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(0.9f),
            modifier = Modifier.padding(bottom = 14.dp),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items.take(2).forEach { (label, value, color) -> QuickStatCell(label, value, color) }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items.drop(2).forEach { (label, value, color) -> QuickStatCell(label, value, color) }
            }
        }
    }
}

@Composable
private fun QuickStatCell(label: String, value: String, color: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.07f))
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = color, letterSpacing = (-0.3).sp)
        Spacer(Modifier.height(3.dp))
        Text(label, fontSize = 11.sp, color = Color.White.copy(0.45f), fontWeight = FontWeight.Medium)
    }
}
