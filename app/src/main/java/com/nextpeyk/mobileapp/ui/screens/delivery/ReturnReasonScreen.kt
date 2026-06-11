package com.nextpeyk.mobileapp.ui.screens.delivery

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextpeyk.mobileapp.ui.screens.home.model.sampleShipments
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val R_ACCENT = Color(0xFF246FA3)
private val R_ACCENT_SOFT = Color(0xFFE9F1F7)
private val R_INK = Color(0xFF14181B)
private val R_INK2 = Color(0xFF2C343A)
private val R_MUTED = Color(0xFF8A9499)
private val R_LINE = Color(0xFFEEF1F1)
private val R_PAGE = Color(0xFFEEF0F0)
private val R_RED = Color(0xFFB83030)
private val R_RED_SOFT = Color(0xFFFAEAEA)

private data class ReturnReason(val id: String, val label: String, val emoji: String)

private val returnReasons = listOf(
    ReturnReason("wrong_address", "آدرس اشتباه", "📍"),
    ReturnReason("not_present", "عدم حضور گیرنده", "🚪"),
    ReturnReason("long_wait", "انتظار بیش از ۵ دقیقه", "⏱"),
    ReturnReason("damaged", "آسیب بسته", "📦"),
    ReturnReason("refused", "امتناع از دریافت", "✋"),
    ReturnReason("other", "سایر", "💬"),
)

@Composable
fun ReturnReasonScreen(
    shipmentIndex: Int,
    onBack: () -> Unit,
    onComplete: () -> Unit,
) {
    val shipment = sampleShipments.getOrElse(shipmentIndex) { sampleShipments.first() }
    var selectedId by remember { mutableStateOf<String?>(null) }
    var note by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }
    val shakeOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val isOther = selectedId == "other"
    val canSubmit = selectedId != null && (!isOther || note.trim().isNotEmpty())

    fun handleSubmit() {
        if (selectedId == null) {
            scope.launch {
                for (i in 0..3) {
                    shakeOffset.animateTo(if (i % 2 == 0) -6f else 6f, tween(70))
                }
                shakeOffset.animateTo(0f, tween(70))
            }
            return
        }
        submitted = true
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(R_PAGE)
                .windowInsetsPadding(WindowInsets.systemBars),
        ) {
            if (submitted) {
                ReturnSuccessScreen(
                    reasonLabel = returnReasons.find { it.id == selectedId }?.label ?: "",
                    note = note,
                    onComplete = onComplete,
                )
            } else {
                // TopBar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White)
                            .border(1.dp, R_LINE, RoundedCornerShape(14.dp))
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(ArrowBack, contentDescription = "بازگشت", tint = R_INK, modifier = Modifier.size(19.dp))
                    }
                    Text(
                        "دلیل مرجوعی",
                        modifier = Modifier.weight(1f),
                        fontSize = 15.5.sp, fontWeight = FontWeight.Bold, color = R_INK,
                    )
                    Spacer(Modifier.size(42.dp))
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 18.dp, vertical = 2.dp)
                            .padding(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        // Parcel summary
                        ReturnParcelCard(shipment = shipment)

                        // Reason grid
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(22.dp))
                                .background(Color.White)
                                .padding(18.dp),
                        ) {
                            Text(
                                "دلیل مرجوع شدن بسته را انتخاب کنید",
                                fontSize = 13.sp, fontWeight = FontWeight.Bold, color = R_INK,
                                modifier = Modifier.padding(bottom = 14.dp),
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .offset(x = shakeOffset.value.dp),
                                verticalArrangement = Arrangement.spacedBy(9.dp),
                            ) {
                                returnReasons.chunked(2).forEach { row ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(9.dp),
                                    ) {
                                        row.forEach { reason ->
                                            ReasonButton(
                                                reason = reason,
                                                selected = selectedId == reason.id,
                                                onClick = { selectedId = reason.id },
                                                modifier = Modifier.weight(1f),
                                            )
                                        }
                                        if (row.size == 1) Spacer(Modifier.weight(1f))
                                    }
                                }
                            }
                        }

                        // Note textarea
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(22.dp))
                                .background(Color.White.copy(alpha = if (selectedId != null) 1f else 0.45f))
                                .padding(16.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("توضیحات", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = R_INK)
                                if (isOther) {
                                    Text(
                                        "الزامی",
                                        fontSize = 11.sp, fontWeight = FontWeight.Bold, color = R_RED,
                                        modifier = Modifier.clip(CircleShape).background(R_RED_SOFT).padding(horizontal = 8.dp, vertical = 2.dp),
                                    )
                                } else {
                                    Text("اختیاری", fontSize = 11.sp, color = R_MUTED, fontWeight = FontWeight.Medium)
                                }
                            }
                            BasicTextField(
                                value = note,
                                onValueChange = { note = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFAFBFB))
                                    .border(
                                        1.5.dp,
                                        if (isOther && note.trim().isEmpty()) R_RED else R_LINE,
                                        RoundedCornerShape(12.dp),
                                    )
                                    .padding(horizontal = 13.dp, vertical = 11.dp),
                                textStyle = TextStyle(
                                    fontSize = 13.5.sp, color = R_INK, lineHeight = 22.sp,
                                ),
                                enabled = selectedId != null,
                                decorationBox = { inner ->
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        if (note.isEmpty()) {
                                            Text(
                                                if (isOther) "لطفاً دلیل را توضیح دهید…" else "توضیحات اضافی (اختیاری)",
                                                color = R_MUTED, fontSize = 13.5.sp,
                                            )
                                        }
                                        inner()
                                    }
                                },
                            )
                        }
                    }

                    // Submit button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 10.dp)
                            .padding(bottom = 18.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (canSubmit) R_RED else Color(0xFFE8EAEB))
                                .clickable { handleSubmit() },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                "ثبت نهایی",
                                fontSize = 16.sp, fontWeight = FontWeight.Bold,
                                color = if (canSubmit) Color.White else Color(0xFFAAAAAA),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReasonButton(
    reason: ReturnReason,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(15.dp))
            .background(if (selected) R_RED_SOFT else Color.White)
            .border(1.5.dp, if (selected) R_RED else R_LINE, RoundedCornerShape(15.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Text(reason.emoji, fontSize = 14.sp, modifier = Modifier.padding(start = 2.dp))
        Text(
            reason.label,
            fontSize = 12.5.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            color = if (selected) R_RED else R_INK,
            lineHeight = 17.sp,
            modifier = Modifier.weight(1f),
        )
        if (selected) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(R_RED),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(10.dp))
            }
        }
    }
}

@Composable
private fun ReturnParcelCard(shipment: com.nextpeyk.mobileapp.ui.screens.home.model.Shipment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF1E272F), R_INK)))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(Color.White.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center,
        ) {
            // Box/package icon via canvas
            androidx.compose.foundation.Canvas(modifier = Modifier.size(20.dp)) {
                val sx = size.width / 24f; val sy = size.height / 24f
                val stroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                // Top diamond
                drawPath(Path().apply {
                    moveTo(21 * sx, 8 * sy); lineTo(12 * sx, 3 * sy); lineTo(3 * sx, 8 * sy)
                    lineTo(12 * sx, 13 * sy); close()
                }, Color.White, style = stroke)
                // Side lines
                drawPath(Path().apply {
                    moveTo(3 * sx, 8 * sy); lineTo(3 * sx, 16 * sy)
                    lineTo(12 * sx, 21 * sy); lineTo(21 * sx, 16 * sy); lineTo(21 * sx, 8 * sy)
                }, Color.White, style = stroke)
                drawLine(Color.White, Offset(12 * sx, 13 * sy), Offset(12 * sx, 21 * sy), 1.8.dp.toPx())
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Text(
                    shipment.tracking,
                    fontSize = 11.sp, color = Color.White.copy(0.45f),
                    fontWeight = FontWeight.Medium, letterSpacing = 0.6.sp,
                )
            }
            Text(shipment.recipientName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        // "مرجوع" red badge
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color(0xFFB83030).copy(alpha = 0.25f))
                .border(1.dp, Color(0xFFB83030).copy(alpha = 0.4f), CircleShape)
                .padding(horizontal = 10.dp, vertical = 5.dp),
        ) {
            Text("مرجوع", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF08080))
        }
    }
}

@Composable
private fun ReturnSuccessScreen(reasonLabel: String, note: String, onComplete: () -> Unit) {
    val circleScale = remember { Animatable(0.5f) }
    val contentAlpha = remember { Animatable(0f) }
    val buttonAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        circleScale.animateTo(1f, spring(dampingRatio = 0.6f, stiffness = 400f))
        contentAlpha.animateTo(1f, tween(350))
        delay(100)
        buttonAlpha.animateTo(1f, tween(350))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Red circle with return icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .graphicsLayer { scaleX = circleScale.value; scaleY = circleScale.value }
                .clip(CircleShape)
                .background(R_RED_SOFT),
            contentAlignment = Alignment.Center,
        ) {
            // Return arrow icon via Canvas
            androidx.compose.foundation.Canvas(modifier = Modifier.size(38.dp)) {
                val sx = size.width / 24f; val sy = size.height / 24f
                val stroke = Stroke(width = 2.3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                // M9 14l-4-4 4-4
                drawPath(Path().apply {
                    moveTo(9 * sx, 14 * sy); lineTo(5 * sx, 10 * sy); lineTo(9 * sx, 6 * sy)
                }, R_RED, style = stroke)
                // M5 10h8a4 4 0 010 8H12
                val arcPath = Path().apply {
                    moveTo(5 * sx, 10 * sy)
                    lineTo(13 * sx, 10 * sy)
                    arcTo(
                        rect = androidx.compose.ui.geometry.Rect(
                            left = 9 * sx, top = 10 * sy,
                            right = 17 * sx, bottom = 18 * sy,
                        ),
                        startAngleDegrees = -90f,
                        sweepAngleDegrees = 180f,
                        forceMoveTo = false,
                    )
                    lineTo(12 * sx, 18 * sy)
                }
                drawPath(arcPath, R_RED, style = stroke)
            }
        }

        Spacer(Modifier.height(18.dp))

        Column(
            modifier = Modifier.graphicsLayer { alpha = contentAlpha.value },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("مرجوعی ثبت شد", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = R_INK)
            Spacer(Modifier.height(8.dp))
            Text(
                "دلیل: $reasonLabel",
                fontSize = 14.sp, color = R_MUTED, fontWeight = FontWeight.Medium, lineHeight = 22.sp,
            )
            if (note.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Text("«$note»", fontSize = 13.sp, color = R_MUTED)
            }
        }

        Spacer(Modifier.height(18.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .graphicsLayer { alpha = buttonAlpha.value }
                .clip(RoundedCornerShape(17.dp))
                .background(R_RED)
                .clickable { onComplete() },
            contentAlignment = Alignment.Center,
        ) {
            Text("بازگشت به لیست", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
