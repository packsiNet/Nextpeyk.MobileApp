package ir.nextpeyk.android.ui.screens.home.components

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.carto.core.BinaryData
import com.carto.graphics.Bitmap as CartoBitmap
import com.carto.graphics.Color as CartoColor
import com.carto.styles.LineJoinType
import com.carto.styles.LineStyleBuilder
import com.carto.styles.MarkerStyleBuilder
import ir.nextpeyk.android.ui.theme.Ink
import ir.nextpeyk.android.ui.theme.Muted
import org.neshan.common.model.LatLng
import org.neshan.mapsdk.MapView
import org.neshan.mapsdk.model.Marker
import org.neshan.mapsdk.model.Polyline
import java.io.ByteArrayOutputStream

private data class PinData(
    val lat: Double,
    val lon: Double,
    val label: Int,
    val name: String,
    val isPaid: Boolean,
)

private val tehranPins = listOf(
    PinData(35.7565, 51.4099, 1, "میدان ونک",    true),
    PinData(35.7650, 51.4118, 2, "میرداماد",      true),
    PinData(35.7725, 51.4148, 3, "خیابان جردن",   false),
    PinData(35.7800, 51.4195, 4, "فرمانیه",       true),
    PinData(35.7870, 51.4240, 5, "الهیه",         false),
    PinData(35.7940, 51.4282, 6, "نیاوران",       true),
    PinData(35.7985, 51.4310, 7, "دربند",         false),
    PinData(35.8017, 51.4330, 8, "میدان تجریش",  true),
)

private fun androidBitmapToCartoBitmap(bitmap: android.graphics.Bitmap): CartoBitmap {
    val bos = ByteArrayOutputStream()
    bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, bos)
    return CartoBitmap.createFromCompressed(BinaryData(bos.toByteArray()))
}

private fun makePinBitmap(label: Int, isPaid: Boolean): android.graphics.Bitmap {
    val BLUE_HEX = "#246FA3"
    val RED_HEX  = "#C43D3D"
    val size = 80
    val bmp = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888)
    val cv = Canvas(bmp)
    val baseColor = Color.parseColor(if (isPaid) BLUE_HEX else RED_HEX)
    val r = Color.red(baseColor); val g = Color.green(baseColor); val b = Color.blue(baseColor)
    val cx = size / 2f; val cy = size / 2f
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.color = Color.argb(50, r, g, b); cv.drawCircle(cx, cy, cx - 1f, paint)
    paint.color = Color.argb(160, r, g, b); paint.style = Paint.Style.STROKE; paint.strokeWidth = 3f
    cv.drawCircle(cx, cy, cx - 6f, paint); paint.style = Paint.Style.FILL
    paint.color = baseColor; cv.drawCircle(cx, cy, cx - 10f, paint)
    paint.color = Color.WHITE; cv.drawCircle(cx, cy, cx - 24f, paint)
    paint.color = baseColor; paint.textSize = 24f; paint.typeface = Typeface.DEFAULT_BOLD
    paint.textAlign = Paint.Align.CENTER
    cv.drawText(label.toString(), cx, cy - (paint.descent() + paint.ascent()) / 2f, paint)
    return bmp
}

private fun makeMarkerStyle(label: Int, isPaid: Boolean): com.carto.styles.MarkerStyle {
    val cartoBitmap = androidBitmapToCartoBitmap(makePinBitmap(label, isPaid))
    return MarkerStyleBuilder().apply {
        setBitmap(cartoBitmap)
        setSize(30f)
    }.buildStyle()
}

private fun makeRoutePolyline(points: List<LatLng>): Polyline {
    val lineStyle = LineStyleBuilder().apply {
        setColor(CartoColor(0x24.toShort(), 0x6F.toShort(), 0xA3.toShort(), 0xFF.toShort()))
        setWidth(5f)
        setLineJoinType(LineJoinType.LINE_JOIN_TYPE_ROUND)
    }.buildStyle()
    return Polyline(ArrayList(points), lineStyle)
}

@Composable
fun MapTabView(modifier: Modifier = Modifier) {
    var selectedPin by remember { mutableStateOf<PinData?>(null) }
    val markers = remember { mutableMapOf<Marker, PinData>() }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    moveCamera(LatLng(tehranPins.first().lat, tehranPins.first().lon), 13f)

                    addPolyline(makeRoutePolyline(tehranPins.map { LatLng(it.lat, it.lon) }))

                    tehranPins.forEach { pin ->
                        val marker = Marker(LatLng(pin.lat, pin.lon), makeMarkerStyle(pin.label, pin.isPaid))
                        marker.setTitle(pin.name)
                        markers[marker] = pin
                        addMarker(marker)
                    }

                    setOnMarkerClickListener { clickedMarker ->
                        val pin = markers[clickedMarker]
                        selectedPin = if (selectedPin == pin) null else pin
                    }
                }
            },
            modifier = Modifier.fillMaxSize(),
        )

        selectedPin?.let { pin ->
            val pinColor = if (pin.isPaid)
                androidx.compose.ui.graphics.Color(0xFF246FA3)
            else
                androidx.compose.ui.graphics.Color(0xFFC43D3D)

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(androidx.compose.ui.graphics.Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier.size(24.dp).clip(CircleShape).background(pinColor),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(pin.label.toString(), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold,
                            color = androidx.compose.ui.graphics.Color.White)
                    }
                    Column {
                        Text(pin.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Ink)
                        Text(
                            if (pin.isPaid) "پرداخت شده" else "پرداخت در محل",
                            fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = pinColor,
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.94f))
                .padding(horizontal = 18.dp, vertical = 12.dp),
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    listOf(
                        androidx.compose.ui.graphics.Color(0xFF246FA3) to "پرداخت شده",
                        androidx.compose.ui.graphics.Color(0xFFC43D3D) to "پرداخت در محل",
                    ).forEach { (col, lbl) ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(col))
                            Text(lbl, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Ink)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp)
                    .background(androidx.compose.ui.graphics.Color(0xFFEEF1F1)))
                Spacer(Modifier.height(8.dp))
                Text(
                    "روی مارکر راننده بزنید تا اطلاعات نمایش داده شود",
                    fontSize = 11.sp, color = Muted, fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
