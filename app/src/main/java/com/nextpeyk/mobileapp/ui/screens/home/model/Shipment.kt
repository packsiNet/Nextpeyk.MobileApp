package com.nextpeyk.mobileapp.ui.screens.home.model

import androidx.compose.ui.graphics.Color
import com.nextpeyk.mobileapp.ui.theme.Accent
import com.nextpeyk.mobileapp.ui.theme.AccentSoft
import com.nextpeyk.mobileapp.ui.theme.AmberColor
import com.nextpeyk.mobileapp.ui.theme.AmberSoft
import com.nextpeyk.mobileapp.ui.theme.GreenSoft
import com.nextpeyk.mobileapp.ui.theme.Muted
import com.nextpeyk.mobileapp.ui.theme.NextpeykGreen
import com.nextpeyk.mobileapp.ui.theme.RedColor
import com.nextpeyk.mobileapp.ui.theme.RedSoft

enum class ShipmentTone { Accent, Amber, Green, Muted }

data class ToneStyle(val bg: Color, val fg: Color, val dot: Color)

fun ShipmentTone.style(): ToneStyle = when (this) {
    ShipmentTone.Accent -> ToneStyle(AccentSoft, Color(0xFF1F6FA3), Accent)
    ShipmentTone.Amber  -> ToneStyle(AmberSoft, Color(0xFF9A6B12), AmberColor)
    ShipmentTone.Green  -> ToneStyle(GreenSoft, Color(0xFF2C7D4A), NextpeykGreen)
    ShipmentTone.Muted  -> ToneStyle(Color(0xFFF0F1F2), Muted, RedColor)
}

data class Shipment(
    val tracking: String,
    val recipientName: String,
    val tone: ShipmentTone,
    val statusLabel: String,
    val step: Int,
    val fromAddress: String,
    val destination: String,
    val weight: String,
    val courier: String,
    val eta: String,
)

val sampleShipments = listOf(
    Shipment("PASR8875676545", "بهار محمدی", ShipmentTone.Accent, "در راه", 1,
        "انبار مرکزی تهران", "خیابان ولیعصر، منطقهٔ ۶", "۲٫۴ کیلوگرم", "رضا کریم", "۱۲ دقیقه"),
    Shipment("PASR4471209983", "آرمان رستمی", ShipmentTone.Amber, "در انتظار", 0,
        "سعادت‌آباد، منطقهٔ ۲", "میدان ونک، منطقهٔ ۳", "۱٫۱ کیلوگرم", "سمیرا اکبری", "۲۵ دقیقه"),
    Shipment("PASR1183440072", "مهسا توکلی", ShipmentTone.Green, "تحویل شد", 2,
        "انبار شرق تهران", "میدان تجریش، منطقهٔ ۱", "۳٫۸ کیلوگرم", "حسین نوری", "تحویل‌شده"),
    Shipment("PASR7765540021", "کیان داوری", ShipmentTone.Green, "تحویل شد", 2,
        "انبار غرب تهران", "ونک، منطقهٔ ۳", "۰٫۹ کیلوگرم", "مریم صادقی", "تحویل‌شده"),
    Shipment("PASR3098871254", "سارا مرادی", ShipmentTone.Muted, "لغو شد", 0,
        "انبار مرکزی تهران", "جردن، منطقهٔ ۳", "۱٫۶ کیلوگرم", "—", "لغو‌شده"),
)
