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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import kotlinx.coroutines.isActive
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextpeyk.mobileapp.ui.screens.home.model.sampleShipments
import kotlinx.coroutines.delay

private val C_ACCENT = Color(0xFF246FA3)
private val C_ACCENT_SOFT = Color(0xFFE9F1F7)
private val C_INK = Color(0xFF14181B)
private val C_INK2 = Color(0xFF2C343A)
private val C_MUTED = Color(0xFF8A9499)
private val C_LINE = Color(0xFFEEF1F1)
private val C_PAGE = Color(0xFFEEF0F0)
private val C_GREEN = Color(0xFF1A7F4B)
private val C_GREEN_SOFT = Color(0xFFE3F5EC)
private val C_RED = Color(0xFFB83030)
private val C_RED_SOFT = Color(0xFFFAEAEA)

@Composable
fun DeliveryConfirmScreen(
    shipmentIndex: Int,
    onBack: () -> Unit,
    onComplete: () -> Unit,
) {
    val shipment = sampleShipments.getOrElse(shipmentIndex) { sampleShipments.first() }
    var submitted by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(C_PAGE)
                .windowInsetsPadding(WindowInsets.systemBars),
        ) {
            if (submitted) {
                DeliverySuccessScreen(onComplete = onComplete)
            } else {
                ConfirmTopBar(onBack = onBack)
                DeliveryFormContent(
                    shipment = shipment,
                    onSubmit = { submitted = true },
                )
            }
        }
    }
}

@Composable
private fun ConfirmTopBar(onBack: () -> Unit) {
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
                .border(1.dp, C_LINE, RoundedCornerShape(14.dp))
                .clickable { onBack() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = C_INK, modifier = Modifier.size(19.dp))
        }
        Text(
            "ثبت تحویل",
            modifier = Modifier.weight(1f),
            fontSize = 15.5.sp, fontWeight = FontWeight.Bold, color = C_INK,
        )
        Spacer(Modifier.size(42.dp))
    }
}

@Composable
private fun DeliveryFormContent(
    shipment: com.nextpeyk.mobileapp.ui.screens.home.model.Shipment,
    onSubmit: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var idType by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var errors by remember { mutableStateOf(setOf<String>()) }

    val canSubmit = name.trim().isNotEmpty() && idType.isNotEmpty() && code.length == 5

    Column(modifier = Modifier.fillMaxSize().imePadding()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 2.dp)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Parcel summary card
            ParcelSummaryCard(shipment = shipment)

            // Form card: name + ID type
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White)
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // Name field
                FieldLabel("نام و نام خانوادگی گیرنده", hasError = "name" in errors)
                BasicTextField(
                    value = name,
                    onValueChange = { name = it; if (it.isNotEmpty()) errors = errors - "name" },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(if (name.isNotEmpty()) C_ACCENT_SOFT else Color(0xFFFAFBFB))
                        .border(1.5.dp, if ("name" in errors) C_RED else if (name.isNotEmpty()) C_ACCENT else C_LINE, RoundedCornerShape(13.dp))
                        .padding(horizontal = 14.dp),
                    textStyle = TextStyle(fontSize = 14.sp, color = C_INK),
                    singleLine = true,
                    decorationBox = { inner ->
                        Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.fillMaxSize()) {
                            if (name.isEmpty()) Text("نام کامل گیرنده را وارد کنید", color = C_MUTED, fontSize = 14.sp)
                            inner()
                        }
                    },
                )

                // ID type dropdown
                FieldLabel("احراز هویت با", hasError = "idType" in errors)
                IdTypeDropdown(
                    value = idType,
                    onValueChange = { idType = it; errors = errors - "idType" },
                    isError = "idType" in errors,
                )
            }

            // 5-digit code card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White)
                    .padding(18.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("کد تحویل ۵ رقمی", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = C_INK)
                    if ("code" in errors) {
                        Text(
                            "کد ناقص است",
                            fontSize = 11.sp, fontWeight = FontWeight.Bold, color = C_RED,
                            modifier = Modifier.clip(CircleShape).background(C_RED_SOFT).padding(horizontal = 8.dp, vertical = 2.dp),
                        )
                    }
                }
                OtpInput(
                    value = code,
                    onValueChange = { code = it; if (it.length == 5) errors = errors - "code" },
                    isError = "code" in errors,
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 11.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "کد را از پیامک گیرنده دریافت کنید",
                        fontSize = 11.5.sp, color = C_MUTED, fontWeight = FontWeight.Medium,
                    )
                    if (code.isNotEmpty()) {
                        Text(
                            "پاک کردن",
                            fontSize = 11.5.sp,
                            color = C_RED,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(C_RED_SOFT)
                                .clickable { code = ""; errors = errors - "code" }
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                        )
                    }
                }
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
                    .background(if (canSubmit) C_GREEN else Color(0xFFE8EAEB))
                    .clickable {
                        val e = mutableSetOf<String>()
                        if (name.trim().isEmpty()) e.add("name")
                        if (idType.isEmpty()) e.add("idType")
                        if (code.length != 5) e.add("code")
                        errors = e
                        if (e.isEmpty()) onSubmit()
                    },
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

@Composable
private fun FieldLabel(label: String, hasError: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = C_INK)
        if (hasError) {
            Text(
                "الزامی",
                fontSize = 11.sp, fontWeight = FontWeight.Bold, color = C_RED,
                modifier = Modifier.clip(CircleShape).background(C_RED_SOFT).padding(horizontal = 8.dp, vertical = 2.dp),
            )
        }
    }
}

@Composable
private fun IdTypeDropdown(value: String, onValueChange: (String) -> Unit, isError: Boolean) {
    val options = listOf("national" to "کارت ملی", "birth" to "شناسنامه", "license" to "گواهینامه")
    val selectedLabel = options.find { it.first == value }?.second
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(if (value.isNotEmpty()) C_ACCENT_SOFT else Color(0xFFFAFBFB))
                .border(1.5.dp, if (isError) C_RED else if (value.isNotEmpty()) C_ACCENT else C_LINE, RoundedCornerShape(13.dp))
                .clickable { expanded = true }
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                selectedLabel ?: "نوع مدرک شناسایی را انتخاب کنید",
                fontSize = 14.sp,
                color = if (value.isNotEmpty()) C_INK else C_MUTED,
                fontWeight = if (value.isNotEmpty()) FontWeight.Medium else FontWeight.Normal,
            )
            Icon(
                Icons.Filled.KeyboardArrowDown, null,
                tint = if (value.isNotEmpty()) C_ACCENT else C_MUTED,
                modifier = Modifier.size(16.dp),
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(16.dp))
                .fillMaxWidth(0.88f),
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                options.forEach { (id, label) ->
                    val isSelected = id == value
                    DropdownMenuItem(
                        text = {
                            Text(
                                label,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) C_ACCENT else C_INK,
                            )
                        },
                        onClick = { onValueChange(id); expanded = false },
                        trailingIcon = if (isSelected) {
                            {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = C_ACCENT,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        } else null,
                        colors = MenuDefaults.itemColors(
                            textColor = C_INK,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun OtpInput(value: String, onValueChange: (String) -> Unit, isError: Boolean) {
    val focusRequester = remember { FocusRequester() }
    var cursorVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        while (isActive) {
            delay(530)
            cursorVisible = !cursorVisible
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        // Hidden text field captures input
        BasicTextField(
            value = value,
            onValueChange = { new ->
                val filtered = new.filter { it.isDigit() }.take(5)
                onValueChange(filtered)
            },
            modifier = Modifier
                .size(width = 1.dp, height = 1.dp)
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            textStyle = TextStyle(color = Color.Transparent),
        )
        // 5 visible boxes
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { focusRequester.requestFocus() },
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            (0 until 5).forEach { i ->
                val digit = value.getOrNull(i)
                val filled = digit != null
                val isActiveSlot = i == value.length && value.length < 5
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(58.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (filled) C_ACCENT_SOFT else Color(0xFFFAFBFB))
                        .border(
                            1.8.dp,
                            when {
                                isError && !filled -> C_RED
                                isActiveSlot -> C_ACCENT
                                filled -> C_ACCENT
                                else -> C_LINE
                            },
                            RoundedCornerShape(14.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    if (digit != null) {
                        Text(digit.toString(), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = C_INK)
                    } else if (isActiveSlot && cursorVisible) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(26.dp)
                                .background(C_ACCENT),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ParcelSummaryCard(shipment: com.nextpeyk.mobileapp.ui.screens.home.model.Shipment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF1E272F), C_INK)))
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
            Icon(Icons.Filled.Inventory2, null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Text(
                    shipment.tracking,
                    fontSize = 13.sp, color = Color.White.copy(0.5f),
                    letterSpacing = 0.7.sp, fontWeight = FontWeight.Medium,
                )
            }
            Text(shipment.recipientName, fontSize = 15.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("مبلغ", fontSize = 11.sp, color = Color.White.copy(0.45f))
            Text("۱۸۵٬۰۰۰", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text("تومان", fontSize = 10.sp, color = Color.White.copy(0.4f))
        }
    }
}

@Composable
private fun DeliverySuccessScreen(onComplete: () -> Unit) {
    val circleScale1 = remember { Animatable(0.5f) }
    val circleScale2 = remember { Animatable(0.5f) }
    val circleScale3 = remember { Animatable(0.5f) }
    val checkAlpha = remember { Animatable(0f) }
    val contentAlpha = remember { Animatable(0f) }
    val buttonAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        circleScale3.animateTo(1f, spring(dampingRatio = 0.6f, stiffness = 400f))
        circleScale2.animateTo(1f, spring(dampingRatio = 0.6f, stiffness = 400f))
        circleScale1.animateTo(1f, spring(dampingRatio = 0.6f, stiffness = 400f))
        checkAlpha.animateTo(1f, tween(300))
        delay(100)
        contentAlpha.animateTo(1f, tween(350))
        delay(100)
        buttonAlpha.animateTo(1f, tween(350))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Concentric rings + checkmark
        Box(
            modifier = Modifier.size(148.dp),
            contentAlignment = Alignment.Center,
        ) {
            // Outer ring
            Box(
                modifier = Modifier
                    .size(148.dp)
                    .graphicsLayer { scaleX = circleScale1.value; scaleY = circleScale1.value }
                    .clip(CircleShape)
                    .background(C_GREEN.copy(alpha = 0.07f)),
            )
            // Middle ring
            Box(
                modifier = Modifier
                    .size(116.dp)
                    .graphicsLayer { scaleX = circleScale2.value; scaleY = circleScale2.value }
                    .clip(CircleShape)
                    .background(C_GREEN.copy(alpha = 0.11f)),
            )
            // Inner circle
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .graphicsLayer { scaleX = circleScale3.value; scaleY = circleScale3.value }
                    .clip(CircleShape)
                    .background(C_GREEN),
                contentAlignment = Alignment.Center,
            ) {
                // Checkmark (animated draw)
                androidx.compose.foundation.Canvas(
                    modifier = Modifier
                        .size(42.dp)
                        .graphicsLayer { alpha = checkAlpha.value },
                ) {
                    val path = Path().apply {
                        val sx = size.width / 24f
                        val sy = size.height / 24f
                        moveTo(5 * sx, 12 * sy)
                        lineTo(9.5f * sx, 16.5f * sy)
                        lineTo(19 * sx, 7 * sy)
                    }
                    drawPath(
                        path, Color.White,
                        style = Stroke(
                            width = 2.6.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                        ),
                    )
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        Column(
            modifier = Modifier.graphicsLayer { alpha = contentAlpha.value },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("تحویل ثبت شد", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = C_INK)
            Spacer(Modifier.height(8.dp))
            Text("مرسوله با موفقیت تحویل داده شد", fontSize = 14.sp, color = C_MUTED, fontWeight = FontWeight.Medium)
        }

        Spacer(Modifier.height(22.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .graphicsLayer { alpha = buttonAlpha.value }
                .clip(RoundedCornerShape(18.dp))
                .background(C_GREEN)
                .clickable { onComplete() },
            contentAlignment = Alignment.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(20.dp))
                Text("بازگشت به لیست", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
