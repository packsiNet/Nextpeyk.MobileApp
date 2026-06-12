package com.nextpeyk.mobileapp.ui.screens.parcel

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.nextpeyk.mobileapp.core.map.SnappTileSource
import com.nextpeyk.mobileapp.ui.screens.home.model.sampleShipments
import kotlinx.coroutines.delay
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

private val ACCENT_COLOR = Color(0xFF246FA3)
private val ACCENT_SOFT = Color(0xFFE9F1F7)
private val INK_COLOR = Color(0xFF14181B)
private val INK2_COLOR = Color(0xFF2C343A)
private val MUTED_COLOR = Color(0xFF8A9499)
private val LINE_COLOR = Color(0xFFEEF1F1)
private val PAGE_COLOR = Color(0xFFEEF0F0)
private val GREEN_COLOR = Color(0xFF1F8A52)

@Composable
fun ParcelDetailsScreen(shipmentIndex: Int, onBack: () -> Unit) {
    val shipment = sampleShipments.getOrElse(shipmentIndex) { sampleShipments.first() }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PAGE_COLOR)
                .windowInsetsPadding(WindowInsets.systemBars),
        ) {
            ParcelTopBar(onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 2.dp)
                    .padding(bottom = 36.dp),
            ) {
                TicketCard(shipment = shipment)
            }
        }
    }
}

@Composable
private fun ParcelTopBar(onBack: () -> Unit) {
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
                .border(1.dp, LINE_COLOR, RoundedCornerShape(14.dp))
                .clickable { onBack() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = INK_COLOR, modifier = Modifier.size(19.dp))
        }

        Text("جزئیات مرسوله", modifier = Modifier.weight(1f), fontSize = 15.5.sp, fontWeight = FontWeight.Bold, color = INK_COLOR)

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .border(1.dp, LINE_COLOR, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Phone, contentDescription = "تماس", tint = ACCENT_COLOR, modifier = Modifier.size(19.dp))
        }

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(ACCENT_COLOR),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.LocationOn, contentDescription = "ردیابی", tint = Color.White, modifier = Modifier.size(19.dp))
        }
    }
}

@Composable
private fun TicketCard(shipment: com.nextpeyk.mobileapp.ui.screens.home.model.Shipment) {
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }
    LaunchedEffect(copied) { if (copied) { delay(1400); copied = false } }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(13.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.linearGradient(listOf(INK2_COLOR, INK_COLOR))),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(shipment.recipientName.firstOrNull()?.toString() ?: "?", fontSize = 21.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("گیرندهٔ مرسوله", fontSize = 11.5.sp, color = MUTED_COLOR, fontWeight = FontWeight.Medium)
                    Text(shipment.recipientName, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = INK_COLOR, letterSpacing = (-0.2).sp)
                }
                StatusBadge(label = shipment.statusLabel)
            }

            // Tracking code
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFAFBFB))
                    .drawBehind {
                        drawRoundRect(
                            color = Color(0xFFD6DCDF),
                            style = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 4f))),
                            cornerRadius = CornerRadius(14.dp.toPx()),
                        )
                    }
                    .clickable { clipboardManager.setText(AnnotatedString(shipment.tracking)); copied = true }
                    .padding(horizontal = 14.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier.size(30.dp).clip(RoundedCornerShape(9.dp)).background(Color.White).border(1.dp, LINE_COLOR, RoundedCornerShape(9.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.Inventory2, null, tint = ACCENT_COLOR, modifier = Modifier.size(16.dp))
                    }
                    Column {
                        Text("بارکد", fontSize = 10.5.sp, color = MUTED_COLOR, fontWeight = FontWeight.Medium)
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Text(shipment.tracking, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = INK_COLOR, letterSpacing = 1.sp)
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    if (copied) {
                        Icon(Icons.Filled.Check, null, tint = GREEN_COLOR, modifier = Modifier.size(16.dp))
                        Text("کپی شد", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GREEN_COLOR)
                    } else {
                        Icon(Icons.Filled.ContentCopy, null, tint = ACCENT_COLOR, modifier = Modifier.size(15.dp))
                        Text("کپی", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ACCENT_COLOR)
                    }
                }
            }

            // Real tile map thumbnail
            MapThumb()

            // Address + phone
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoRow(icon = { Icon(Icons.Filled.LocationOn, null, tint = ACCENT_COLOR, modifier = Modifier.size(18.dp)) }, label = "آدرس تحویل", value = shipment.destination)
                HorizontalDivider(color = LINE_COLOR, thickness = 1.dp)
                InfoRow(icon = { Icon(Icons.Filled.Phone, null, tint = ACCENT_COLOR, modifier = Modifier.size(18.dp)) }, label = "شمارهٔ تماس", value = "۰۹۱۲۱۲۳۴۵۶۷", isLtr = true)
            }

            PaymentCard()
        }
    }
}

@Composable
private fun StatusBadge(label: String) {
    Row(
        modifier = Modifier.clip(CircleShape).background(ACCENT_SOFT).padding(horizontal = 11.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(ACCENT_COLOR))
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ACCENT_COLOR)
    }
}

@Composable
private fun InfoRow(icon: @Composable () -> Unit, label: String, value: String, isLtr: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(13.dp)) {
        Box(modifier = Modifier.size(38.dp).clip(RoundedCornerShape(12.dp)).background(ACCENT_SOFT), contentAlignment = Alignment.Center) { icon() }
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 11.5.sp, color = MUTED_COLOR, fontWeight = FontWeight.Medium)
            if (isLtr) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = INK_COLOR, lineHeight = 20.sp)
                }
            } else {
                Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = INK_COLOR, lineHeight = 20.sp)
            }
        }
    }
}

@Composable
private fun MapThumb() {
    val infinite = rememberInfiniteTransition(label = "map")
    val pulseAlpha by infinite.animateFloat(
        initialValue = 0.45f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Restart),
        label = "pulse",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(158.dp)
            .clip(RoundedCornerShape(22.dp))
            .border(1.dp, LINE_COLOR, RoundedCornerShape(22.dp)),
    ) {
        // Real tile map — static thumbnail (touch disabled)
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(SnappTileSource)
                    setMultiTouchControls(false)
                    isFocusable = false
                    isClickable = false
                    controller.setZoom(15.0)
                    controller.setCenter(GeoPoint(35.7025, 51.4030))
                    setOnTouchListener { _, _ -> true }
                }
            },
            modifier = Modifier.fillMaxSize(),
        )

        // Pin centered
        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = ACCENT_COLOR, modifier = Modifier.align(Alignment.Center).size(38.dp))

        // Distance chip
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(Color.White.copy(alpha = 0.95f))
                .padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Box(modifier = Modifier.size(8.dp)) {
                Box(Modifier.fillMaxSize().clip(CircleShape).background(ACCENT_COLOR.copy(alpha = pulseAlpha)))
                Box(Modifier.size(6.dp).align(Alignment.Center).clip(CircleShape).background(ACCENT_COLOR))
            }
            Text("۱۲ دقیقه", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = INK_COLOR)
            Box(Modifier.width(1.dp).height(13.dp).background(LINE_COLOR))
            Text("۲٫۴ کیلومتر", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = MUTED_COLOR)
        }
    }
}

@Composable
private fun PaymentCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF222A30), INK_COLOR)))
            .padding(horizontal = 20.dp, vertical = 17.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    modifier = Modifier.padding(bottom = 7.dp),
                ) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF5FB98A)))
                    Text("پرداخت در محل · نقدی", fontSize = 11.5.sp, color = Color.White.copy(0.62f), fontWeight = FontWeight.Medium)
                }
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        "۲۳٬۷۵۰٬۳۴۰",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = (-0.3).sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    Text("تومان", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White.copy(0.6f), modifier = Modifier.padding(bottom = 2.dp))
                }
            }
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(0.08f))
                    .border(1.dp, Color.White.copy(0.12f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Refresh, null, tint = Color.White, modifier = Modifier.size(26.dp))
            }
        }
    }
}
