package ir.nextpeyk.android.ui.screens.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.nextpeyk.android.ui.theme.Accent
import ir.nextpeyk.android.ui.theme.Ink
import ir.nextpeyk.android.ui.theme.Line
import ir.nextpeyk.android.ui.theme.Muted
import ir.nextpeyk.android.ui.theme.NextpeykGreen
import ir.nextpeyk.android.ui.theme.RedColor
import kotlin.math.PI

private data class StatItem(val label: String, val value: Int, val color: Color)

private val stats = listOf(
    StatItem("همه", 5, Accent),
    StatItem("پرداخت شده", 3, NextpeykGreen),
    StatItem("عدم پذیرش", 2, RedColor),
)

@Composable
fun StatsPopover(onClose: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .padding(horizontal = 18.dp, vertical = 16.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("آمار مرسوله‌ها", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Ink)
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF3F5F6)),
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "بستن", tint = Muted, modifier = Modifier.size(12.dp))
                }
            }
            Spacer(Modifier.height(14.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                DonutChart(segments = stats)
                Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    stats.forEach { s ->
                        val total = stats.sumOf { it.value }.coerceAtLeast(1)
                        val pct = s.value.toFloat() / total
                        val animPct by animateFloatAsState(
                            targetValue = pct, animationSpec = tween(600), label = "bar_${s.label}",
                        )
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(Modifier.size(8.dp).clip(CircleShape).background(s.color))
                                    Text(s.label, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = Muted)
                                }
                                Text(s.value.toString(), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = s.color)
                            }
                            Spacer(Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF0F2F3)),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(animPct)
                                        .fillMaxHeight()
                                        .clip(CircleShape)
                                        .background(s.color),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DonutChart(segments: List<StatItem>) {
    val total = segments.sumOf { it.value }.coerceAtLeast(1).toFloat()
    val r = 38f
    val strokeWidth = 11f
    val circ = (2 * PI * r).toFloat()

    androidx.compose.foundation.Canvas(modifier = Modifier.size(110.dp)) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val radiusPx = r / 100f * size.width

        drawCircle(color = Color(0xFFF0F2F3), radius = radiusPx, style = Stroke(width = strokeWidth / 100f * size.width))

        var offset = 0f
        segments.forEach { seg ->
            val pct = seg.value / total
            val dash = circ * pct - 2f
            val sweepAngle = pct * 360f - (2f / circ * 360f)
            drawArc(
                color = seg.color,
                startAngle = -90f + offset * 360f / circ,
                sweepAngle = sweepAngle.coerceAtLeast(0f),
                useCenter = false,
                style = Stroke(width = strokeWidth / 100f * size.width, cap = StrokeCap.Round),
                topLeft = androidx.compose.ui.geometry.Offset(cx - radiusPx, cy - radiusPx),
                size = androidx.compose.ui.geometry.Size(radiusPx * 2, radiusPx * 2),
            )
            offset += circ * pct
        }
    }
}
