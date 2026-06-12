package com.nextpeyk.mobileapp.ui.screens.delivery

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
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

// ── Design tokens ──────────────────────────────────────────────
private val ACCENT = Color(0xFF246FA3)
private val ACCENT_SOFT = Color(0xFFE9F1F7)
private val INK = Color(0xFF14181B)
private val INK2 = Color(0xFF2C343A)
private val MUTED = Color(0xFF8A9499)
private val LINE = Color(0xFFEEF1F1)
private val PAGE = Color(0xFFEEF0F0)
private val GREEN = Color(0xFF1A7F4B)
private val RED = Color(0xFFB83030)
private val RED_SOFT = Color(0xFFFAEAEA)

@Composable
fun DeliveryDetailScreen(
    shipmentIndex: Int,
    onBack: () -> Unit,
    onDeliver: () -> Unit = {},
    onReturn: () -> Unit = {},
) {
    val shipment = sampleShipments.getOrElse(shipmentIndex) { sampleShipments.first() }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PAGE)
                .windowInsetsPadding(WindowInsets.systemBars),
        ) {
            DeliveryTopBar(onBack = onBack)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 2.dp)
                    .padding(bottom = 36.dp),
            ) {
                DeliveryTicketCard(shipment = shipment, onDeliver = onDeliver, onReturn = onReturn)
            }
        }
    }
}

@Composable
private fun DeliveryTopBar(onBack: () -> Unit) {
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
                .border(1.dp, LINE, RoundedCornerShape(14.dp))
                .clickable { onBack() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = INK, modifier = Modifier.size(19.dp))
        }
        Text(
            "تحویل مرسوله",
            modifier = Modifier.weight(1f),
            fontSize = 15.5.sp,
            fontWeight = FontWeight.Bold,
            color = INK,
        )
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .border(1.dp, LINE, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Phone, contentDescription = "تماس", tint = ACCENT, modifier = Modifier.size(19.dp))
        }
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(ACCENT),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.LocationOn, contentDescription = "ردیابی", tint = Color.White, modifier = Modifier.size(19.dp))
        }
    }
}

@Composable
private fun DeliveryTicketCard(
    shipment: com.nextpeyk.mobileapp.ui.screens.home.model.Shipment,
    onDeliver: () -> Unit,
    onReturn: () -> Unit,
) {
    val clipboard = LocalClipboardManager.current
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
                        .background(Brush.linearGradient(listOf(INK2, INK))),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        shipment.recipientName.firstOrNull()?.toString() ?: "?",
                        fontSize = 21.sp, fontWeight = FontWeight.Bold, color = Color.White,
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("گیرندهٔ مرسوله", fontSize = 11.5.sp, color = MUTED, fontWeight = FontWeight.Medium)
                    Text(
                        shipment.recipientName,
                        fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = INK, letterSpacing = (-0.2).sp,
                    )
                }
                DeliveryStatusBadge(label = shipment.statusLabel)
            }

            // Copy tracking button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFAFBFB))
                    .drawBehind {
                        drawRoundRect(
                            color = Color(0xFFD6DCDF),
                            style = Stroke(
                                width = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 4f)),
                            ),
                            cornerRadius = CornerRadius(14.dp.toPx()),
                        )
                    }
                    .clickable {
                        clipboard.setText(AnnotatedString(shipment.tracking))
                        copied = true
                    }
                    .padding(horizontal = 14.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(Color.White)
                            .border(1.dp, LINE, RoundedCornerShape(9.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.Inventory2, null, tint = ACCENT, modifier = Modifier.size(16.dp))
                    }
                    Column {
                        Text("بارکد", fontSize = 10.5.sp, color = MUTED, fontWeight = FontWeight.Medium)
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Text(
                                shipment.tracking,
                                fontSize = 14.sp, fontWeight = FontWeight.Bold, color = INK, letterSpacing = 1.sp,
                            )
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    if (copied) {
                        Icon(Icons.Filled.Check, null, tint = GREEN, modifier = Modifier.size(16.dp))
                        Text("کپی شد", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GREEN)
                    } else {
                        Icon(Icons.Filled.ContentCopy, null, tint = ACCENT, modifier = Modifier.size(15.dp))
                        Text("کپی", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ACCENT)
                    }
                }
            }

            // Address + phone
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DeliveryInfoRow(
                    icon = { Icon(Icons.Filled.LocationOn, null, tint = ACCENT, modifier = Modifier.size(18.dp)) },
                    label = "آدرس تحویل",
                    value = shipment.destination,
                )
                HorizontalDivider(color = LINE, thickness = 1.dp)
                DeliveryInfoRow(
                    icon = { Icon(Icons.Filled.Phone, null, tint = ACCENT, modifier = Modifier.size(18.dp)) },
                    label = "شمارهٔ تماس",
                    value = "۰۹۱۲۱۲۳۴۵۶۷",
                    isLtr = true,
                )
            }

            // Payment card
            DeliveryPaymentCard()

            // Section divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = LINE, thickness = 1.dp)
                Text("اقدامات", fontSize = 11.sp, color = MUTED, fontWeight = FontWeight.SemiBold)
                HorizontalDivider(modifier = Modifier.weight(1f), color = LINE, thickness = 1.dp)
            }

            // Action buttons
            ActionGrid(onDeliver = onDeliver, onReturn = onReturn)
        }
    }
}

@Composable
private fun DeliveryStatusBadge(label: String) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(ACCENT_SOFT)
            .padding(horizontal = 11.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(ACCENT))
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ACCENT)
    }
}

@Composable
private fun DeliveryInfoRow(
    icon: @Composable () -> Unit,
    label: String,
    value: String,
    isLtr: Boolean = false,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ACCENT_SOFT),
            contentAlignment = Alignment.Center,
        ) { icon() }
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 11.5.sp, color = MUTED, fontWeight = FontWeight.Medium)
            if (isLtr) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = INK, lineHeight = 20.sp)
                }
            } else {
                Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = INK, lineHeight = 20.sp)
            }
        }
    }
}

@Composable
private fun DeliveryMapThumb() {
    val infinite = rememberInfiniteTransition(label = "map")
    val pulseAlpha by infinite.animateFloat(
        initialValue = 0.45f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Restart),
        label = "pulse",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(22.dp))
            .border(1.dp, LINE, RoundedCornerShape(22.dp)),
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
                    setOnTouchListener { _, _ -> true }  // block all touch → outer scroll works
                }
            },
            modifier = Modifier.fillMaxSize(),
        )

        // Pin
        Icon(
            Icons.Filled.LocationOn,
            contentDescription = null,
            tint = ACCENT,
            modifier = Modifier.align(Alignment.Center).size(38.dp),
        )

        // Distance chip
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(Color.White.copy(alpha = 0.95f))
                .padding(horizontal = 11.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Box(modifier = Modifier.size(8.dp)) {
                Box(Modifier.fillMaxSize().clip(CircleShape).background(ACCENT.copy(alpha = pulseAlpha)))
                Box(Modifier.size(6.dp).align(Alignment.Center).clip(CircleShape).background(ACCENT))
            }
            Text("۱۲ دقیقه", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = INK)
            Box(Modifier.width(1.dp).height(13.dp).background(LINE))
            Text("۲٫۴ کیلومتر", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = MUTED)
        }
    }
}


@Composable
private fun DeliveryPaymentCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF222A30), INK)))
            .padding(horizontal = 20.dp, vertical = 16.dp),
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
                    Box(Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF5FB98A)))
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
                    .size(50.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.White.copy(0.08f))
                    .border(1.dp, Color.White.copy(0.12f), RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Refresh, null, tint = Color.White, modifier = Modifier.size(25.dp))
            }
        }
    }
}

@Composable
private fun ActionGrid(onDeliver: () -> Unit, onReturn: () -> Unit) {
    val deliverSource = remember { MutableInteractionSource() }
    val returnSource = remember { MutableInteractionSource() }
    val deliverPressed by deliverSource.collectIsPressedAsState()
    val returnPressed by returnSource.collectIsPressedAsState()
    val deliverScale by animateFloatAsState(if (deliverPressed) 0.96f else 1f, label = "d_scale")
    val returnScale by animateFloatAsState(if (returnPressed) 0.96f else 1f, label = "r_scale")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(Color(0xFFEEF0F1))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        // تحویل — dark filled
        Box(
            modifier = Modifier
                .weight(1f)
                .height(46.dp)
                .graphicsLayer { scaleX = deliverScale; scaleY = deliverScale }
                .clip(CircleShape)
                .background(INK)
                .clickable(interactionSource = deliverSource, indication = null, onClick = onDeliver),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                // Up arrow icon
                DeliverArrowIcon(up = true)
                Text("تحویل", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        // مرجوع — transparent
        Box(
            modifier = Modifier
                .weight(1f)
                .height(46.dp)
                .graphicsLayer { scaleX = returnScale; scaleY = returnScale }
                .clip(CircleShape)
                .background(Color.Transparent)
                .clickable(interactionSource = returnSource, indication = null, onClick = onReturn),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                // Down arrow icon
                DeliverArrowIcon(up = false)
                Text("مرجوع", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = INK)
            }
        }
    }
}

@Composable
private fun DeliverArrowIcon(up: Boolean) {
    androidx.compose.foundation.Canvas(modifier = Modifier.size(15.dp)) {
        val c = center
        val color = if (up) Color.White else INK
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        if (up) {
            // Up arrow: M12 19V5M6 11l6-6 6 6 (scaled to 15x15)
            val sx = size.width / 24f
            val sy = size.height / 24f
            val shaft = Path().apply { moveTo(12 * sx, 19 * sy); lineTo(12 * sx, 5 * sy) }
            val head = Path().apply { moveTo(6 * sx, 11 * sy); lineTo(12 * sx, 5 * sy); lineTo(18 * sx, 11 * sy) }
            drawPath(shaft, color, style = stroke)
            drawPath(head, color, style = stroke)
        } else {
            // Down arrow: M12 5v14M18 13l-6 6-6-6
            val sx = size.width / 24f
            val sy = size.height / 24f
            val shaft = Path().apply { moveTo(12 * sx, 5 * sy); lineTo(12 * sx, 19 * sy) }
            val head = Path().apply { moveTo(18 * sx, 13 * sy); lineTo(12 * sx, 19 * sy); lineTo(6 * sx, 13 * sy) }
            drawPath(shaft, color, style = stroke)
            drawPath(head, color, style = stroke)
        }
    }
}
