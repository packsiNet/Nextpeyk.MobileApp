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
    Shipment(
        "PASR8875676545",
        "بهار محمدی",
        ShipmentTone.Accent,
        "در راه",
        1,
        "انبار مرکزی تهران، خیابان آزادی، بعد از میدان انقلاب، پلاک ۱۲۸",
        "تهران، خیابان ولیعصر، بالاتر از پارک ساعی، کوچه نسترن، پلاک ۲۴",
        "۲٫۴ کیلومتر",
        "رضا کریم",
        "۱۲ دقیقه"
    ),
    Shipment(
        "PASR4471209983",
        "آرمان رستمی",
        ShipmentTone.Amber,
        "در انتظار",
        0,
        "تهران، سعادت‌آباد، بلوار دریا، نبش خیابان صراف‌ها، ساختمان سپهر",
        "تهران، میدان ونک، خیابان گاندی جنوبی، کوچه هفتم، پلاک ۵۶",
        "۱٫۱ کیلومتر",
        "سمیرا اکبری",
        "۲۵ دقیقه"
    ),
    Shipment(
        "PASR1183440072",
        "مهسا توکلی",
        ShipmentTone.Green,
        "تحویل شد",
        2,
        "انبار شرق تهران، بزرگراه رسالت، خروجی هنگام، مجتمع لجستیک پارس",
        "تهران، میدان تجریش، خیابان شهرداری، روبه‌روی پاساژ قائم، پلاک ۸",
        "۳٫۸ کیلومتر",
        "حسین نوری",
        "تحویل‌شده"
    ),
    Shipment(
        "PASR7765540021",
        "کیان داوری",
        ShipmentTone.Green,
        "تحویل شد",
        2,
        "انبار غرب تهران، اتوبان حکیم، بعد از خروجی ستاری، بلوک شماره ۳",
        "تهران، ونک، خیابان ملاصدرا، نبش خیابان شیخ بهایی شمالی، پلاک ۴۱",
        "۰٫۹ کیلومتر",
        "مریم صادقی",
        "تحویل‌شده"
    ),
    Shipment(
        "PASR3098871254",
        "سارا مرادی",
        ShipmentTone.Muted,
        "لغو شد",
        0,
        "انبار مرکزی تهران، خیابان کارگر شمالی، کوچه شانزدهم، ساختمان لجستیک",
        "تهران، خیابان جردن، بالاتر از چهارراه جهان کودک، برج نگین، واحد ۱۰",
        "۱٫۶ کیلومتر",
        "—",
        "لغو‌شده"
    ),
    Shipment(
        "PASR6612309871",
        "نیلوفر احمدی",
        ShipmentTone.Accent,
        "در راه",
        1,
        "انبار جنوب تهران، خیابان شوش، نرسیده به میدان شوش، پلاک ۴۵",
        "تهران، نارمک، خیابان هنگام، کوچه دوم، پلاک ۱۱",
        "۴٫۲ کیلومتر",
        "علی رضایی",
        "۱۸ دقیقه"
    ),
    Shipment(
        "PASR2234567890",
        "فریدون کاظمی",
        ShipmentTone.Amber,
        "در انتظار",
        0,
        "انبار شمال تهران، تجریش، کوچه یازدهم، ساختمان الوند",
        "تهران، یوسف‌آباد، خیابان بیست و سوم، پلاک ۶۲",
        "۲٫۷ کیلومتر",
        "زهرا حسینی",
        "۳۰ دقیقه"
    ),
    Shipment(
        "PASR5543219876",
        "شیرین محمودی",
        ShipmentTone.Green,
        "تحویل شد",
        2,
        "انبار مرکزی تهران، خیابان آزادی، پلاک ۲۰۳",
        "تهران، اکباتان، فاز سه، بلوک B، واحد ۷",
        "۵٫۱ کیلومتر",
        "محمد علوی",
        "تحویل‌شده"
    ),
    Shipment(
        "PASR9987654310",
        "داریوش صادقی",
        ShipmentTone.Accent,
        "در راه",
        1,
        "انبار غرب تهران، پونک، بلوار شهید اشرفی اصفهانی، پلاک ۷۸",
        "تهران، شهران، خیابان طاهری، کوچه سوم، پلاک ۱۸",
        "۱٫۸ کیلومتر",
        "نسرین کریمی",
        "۸ دقیقه"
    ),
    Shipment(
        "PASR1122334455",
        "پریسا علیزاده",
        ShipmentTone.Muted,
        "لغو شد",
        0,
        "انبار شرق تهران، بزرگراه رسالت، خروجی هنگام، پلاک ۵۵",
        "تهران، تهران‌پارس، خیابان فرجام، کوچه پنجم، پلاک ۳۳",
        "۲٫۳ کیلومتر",
        "—",
        "لغو‌شده"
    ),
    Shipment(
        "PASR7788990011",
        "رامین قاسمی",
        ShipmentTone.Amber,
        "در انتظار",
        0,
        "انبار مرکزی تهران، خیابان ستارخان، پلاک ۱۶۵",
        "تهران، سیدخندان، خیابان شریعتی، بالاتر از پل سیدخندان، پلاک ۴",
        "۳٫۵ کیلومتر",
        "مینا رحیمی",
        "۴۲ دقیقه"
    ),
    Shipment(
        "PASR3344556677",
        "الناز نوروزی",
        ShipmentTone.Green,
        "تحویل شد",
        2,
        "انبار جنوب تهران، خیابان ری، نزدیک حرم عبدالعظیم، پلاک ۸۹",
        "تهران، ری، خیابان امام خمینی، کوچه هشتم، پلاک ۲۱",
        "۰٫۷ کیلومتر",
        "سعید منصوری",
        "تحویل‌شده"
    ),
    Shipment(
        "PASR8899001122",
        "حامد اسماعیلی",
        ShipmentTone.Accent,
        "در راه",
        1,
        "انبار شمال غرب تهران، صادقیه، بلوار آیت‌الله کاشانی، پلاک ۱۱۲",
        "تهران، جنت‌آباد، خیابان گلستان، کوچه دهم، پلاک ۴۵",
        "۲٫۹ کیلومتر",
        "فاطمه موسوی",
        "۲۲ دقیقه"
    ),
    Shipment(
        "PASR5566778899",
        "مژگان طاهری",
        ShipmentTone.Green,
        "تحویل شد",
        2,
        "انبار مرکزی تهران، خیابان انقلاب، نزدیک دانشگاه تهران، پلاک ۵۱",
        "تهران، بلوار کشاورز، خیابان ۱۶ آذر، کوچه دوم، پلاک ۱۴",
        "۱٫۴ کیلومتر",
        "رضا نجفی",
        "تحویل‌شده"
    ),
    Shipment(
        "PASR2233445566",
        "سهراب کمالی",
        ShipmentTone.Muted,
        "لغو شد",
        0,
        "انبار غرب تهران، جاده مخصوص کرج، کیلومتر ۵، مجتمع صنعتی آرین",
        "تهران، مرزداران، خیابان گوهردشت، کوچه سیزدهم، پلاک ۷۷",
        "۶٫۳ کیلومتر",
        "—",
        "لغو‌شده"
    ),
)
