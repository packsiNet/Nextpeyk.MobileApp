package com.nextpeyk.mobileapp.ui.screens.home.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.nextpeyk.mobileapp.ui.theme.Ink
import com.nextpeyk.mobileapp.ui.theme.Line
import com.nextpeyk.mobileapp.ui.theme.Muted
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

private object SnappTileSource : OnlineTileSourceBase(
    "SnappMaps", 0, 18, 256, ".png",
    arrayOf("https://raster.snappmaps.ir"),
) {
    override fun getTileURLString(pMapTileIndex: Long): String {
        val z = MapTileIndex.getZoom(pMapTileIndex)
        val x = MapTileIndex.getX(pMapTileIndex)
        val y = MapTileIndex.getY(pMapTileIndex)
        return "https://raster.snappmaps.ir/styles/snapp-style/$z/$x/$y.png"
    }
}

private data class PinData(
    val lat: Double,
    val lon: Double,
    val label: Int,
    val name: String,
    val isPaid: Boolean,
)

private val BLUE_HEX = "#246FA3"
private val RED_HEX = "#C43D3D"

private val tehranPins = listOf(
    PinData(35.7025, 51.4030, 1, "بهار محمدی", true),
    PinData(35.6952, 51.3780, 2, "مهسا توکلی", true),
    PinData(35.6940, 51.3760, 3, "کیان داوری", true),
    PinData(35.6928, 51.3748, 4, "سارا مرادی", false),
    PinData(35.6810, 51.4120, 5, "آرمان رستمی", false),
)

private fun makePinBitmap(label: Int, isPaid: Boolean): Bitmap {
    val size = 80
    val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val cv = Canvas(bmp)
    val colorHex = if (isPaid) BLUE_HEX else RED_HEX
    val baseColor = Color.parseColor(colorHex)
    val r = Color.red(baseColor)
    val g = Color.green(baseColor)
    val b = Color.blue(baseColor)
    val cx = size / 2f
    val cy = size / 2f

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Outer pulse ring
    paint.color = Color.argb(50, r, g, b)
    cv.drawCircle(cx, cy, cx - 1f, paint)

    // Colored ring border
    paint.color = Color.argb(160, r, g, b)
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 3f
    cv.drawCircle(cx, cy, cx - 6f, paint)
    paint.style = Paint.Style.FILL

    // Filled circle
    paint.color = baseColor
    cv.drawCircle(cx, cy, cx - 10f, paint)

    // White inner
    paint.color = Color.WHITE
    cv.drawCircle(cx, cy, cx - 24f, paint)

    // Number label
    paint.color = Color.WHITE
    paint.textSize = 24f
    paint.typeface = Typeface.DEFAULT_BOLD
    paint.textAlign = Paint.Align.CENTER
    val textY = cy - (paint.descent() + paint.ascent()) / 2f
    paint.color = baseColor
    cv.drawText(label.toString(), cx, textY, paint)

    return bmp
}

@Composable
fun MapTabView(modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    var selectedPin by remember { mutableStateOf<PinData?>(null) }

    DisposableEffect(Unit) {
        Configuration.getInstance().userAgentValue = ctx.packageName
        onDispose { }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    setTileSource(SnappTileSource)
                    setMultiTouchControls(true)
                    isHorizontalMapRepetitionEnabled = false
                    isVerticalMapRepetitionEnabled = false
                    controller.setZoom(13.0)
                    controller.setCenter(GeoPoint(35.6892, 51.3890))

                    tehranPins.forEach { pin ->
                        val marker = Marker(this)
                        marker.position = GeoPoint(pin.lat, pin.lon)
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        marker.icon = BitmapDrawable(
                            context.resources,
                            makePinBitmap(pin.label, pin.isPaid),
                        )
                        marker.title = pin.name
                        marker.infoWindow = null
                        marker.setOnMarkerClickListener { _, _ ->
                            selectedPin = if (selectedPin == pin) null else pin
                            true
                        }
                        overlays.add(marker)
                    }

                    setOnTouchListener { v, event ->
                        if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                            selectedPin = null
                        }
                        v.onTouchEvent(event)
                    }
                }
            },
            modifier = Modifier.fillMaxSize(),
        )

        // Tooltip for selected pin
        selectedPin?.let { pin ->
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
                    val pinColor = if (pin.isPaid)
                        androidx.compose.ui.graphics.Color(0xFF246FA3)
                    else
                        androidx.compose.ui.graphics.Color(0xFFC43D3D)

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(pinColor),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            pin.label.toString(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = androidx.compose.ui.graphics.Color.White,
                        )
                    }
                    Column {
                        Text(pin.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Ink)
                        Text(
                            if (pin.isPaid) "پرداخت شده" else "پرداخت در محل",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = pinColor,
                        )
                    }
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(col),
                            )
                            Text(lbl, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Ink)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Line))
                Spacer(Modifier.height(8.dp))
                Text(
                    "روی مارکر راننده بزنید تا اطلاعات نمایش داده شود",
                    fontSize = 11.sp,
                    color = Muted,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
