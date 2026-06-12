package com.nextpeyk.mobileapp.ui.screens.home.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.view.MotionEvent
import android.view.ViewTreeObserver
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
import com.nextpeyk.mobileapp.core.map.SnappTileSource
import com.nextpeyk.mobileapp.ui.theme.Ink
import com.nextpeyk.mobileapp.ui.theme.Line
import com.nextpeyk.mobileapp.ui.theme.Muted
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay

// ─── Data ──────────────────────────────────────────────────────────────────────

private data class PinData(
    val lat: Double,
    val lon: Double,
    val label: Int,
    val name: String,
    val isPaid: Boolean,
)

private val BLUE_HEX = "#246FA3"
private val RED_HEX = "#C43D3D"

// Route: Vanak Square → Tajrish Square (north along Valiasr / Chamran)
private val tehranPins = listOf(
    PinData(35.7565, 51.4099, 1, "میدان ونک",       true),
    PinData(35.7650, 51.4118, 2, "میرداماد",         true),
    PinData(35.7725, 51.4148, 3, "خیابان جردن",      false),
    PinData(35.7800, 51.4195, 4, "فرمانیه",          true),
    PinData(35.7870, 51.4240, 5, "الهیه",            false),
    PinData(35.7940, 51.4282, 6, "نیاوران",          true),
    PinData(35.7985, 51.4310, 7, "دربند",            false),
    PinData(35.8017, 51.4330, 8, "میدان تجریش",     true),
)

// ─── Route overlay ─────────────────────────────────────────────────────────────

private class RouteOverlay(private val geoPoints: List<GeoPoint>) : Overlay() {

    private val screenPts = Array(geoPoints.size) { Point() }

    // White halo — gives the "raised" look
    private val haloPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 22f
        color = Color.WHITE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    // Subtle shadow under halo
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 24f
        color = Color.argb(40, 0, 0, 0)
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    // Main route line
    private val routePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 9f
        color = Color.parseColor("#246FA3")
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    // Dashed direction indicator on top
    private val dashPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
        color = Color.WHITE
        strokeCap = Paint.Cap.ROUND
        pathEffect = DashPathEffect(floatArrayOf(14f, 18f), 0f)
    }

    override fun draw(canvas: Canvas, mapView: MapView, shadow: Boolean) {
        if (shadow || geoPoints.size < 2) return
        val pj = mapView.projection
        geoPoints.forEachIndexed { i, gp -> pj.toPixels(gp, screenPts[i]) }

        val path = buildCurvedPath(screenPts)

        canvas.drawPath(path, shadowPaint)
        canvas.drawPath(path, haloPaint)
        canvas.drawPath(path, routePaint)
        canvas.drawPath(path, dashPaint)
    }

    // Centripetal Catmull-Rom → cubic bezier (no loops for unequal spacing)
    private fun buildCurvedPath(pts: Array<Point>): Path {
        val path = Path()
        val n = pts.size
        val px = FloatArray(n) { pts[it].x.toFloat() }
        val py = FloatArray(n) { pts[it].y.toFloat() }
        path.moveTo(px[0], py[0])

        for (i in 0 until n - 1) {
            val i0 = (i - 1).coerceAtLeast(0)
            val i3 = (i + 2).coerceAtMost(n - 1)

            // t_ij = euclidean^0.5  (centripetal parameterization, alpha=0.5)
            val t01 = tParam(px[i0], py[i0], px[i], py[i])
            val t12 = tParam(px[i], py[i], px[i + 1], py[i + 1])
            val t23 = tParam(px[i + 1], py[i + 1], px[i3], py[i3])

            val m1x = (px[i + 1] - px[i]) / t12 - (px[i + 1] - px[i0]) / (t01 + t12) + (px[i] - px[i0]) / t01
            val m1y = (py[i + 1] - py[i]) / t12 - (py[i + 1] - py[i0]) / (t01 + t12) + (py[i] - py[i0]) / t01
            val m2x = (px[i3] - px[i + 1]) / t23 - (px[i3] - px[i]) / (t12 + t23) + (px[i + 1] - px[i]) / t12
            val m2y = (py[i3] - py[i + 1]) / t23 - (py[i3] - py[i]) / (t12 + t23) + (py[i + 1] - py[i]) / t12

            val tension = 0.45f  // <1 = softer arc
            val c1x = px[i] + m1x * t12 * tension / 3f
            val c1y = py[i] + m1y * t12 * tension / 3f
            val c2x = px[i + 1] - m2x * t12 * tension / 3f
            val c2y = py[i + 1] - m2y * t12 * tension / 3f

            path.cubicTo(c1x, c1y, c2x, c2y, px[i + 1], py[i + 1])
        }
        return path
    }

    private fun tParam(ax: Float, ay: Float, bx: Float, by: Float): Float {
        val dx = bx - ax; val dy = by - ay
        return kotlin.math.sqrt(kotlin.math.sqrt(dx * dx + dy * dy)).coerceAtLeast(0.001f)
    }
}

// ─── Pin bitmap ────────────────────────────────────────────────────────────────

private fun makePinBitmap(label: Int, isPaid: Boolean): Bitmap {
    val size = 80
    val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val cv = Canvas(bmp)
    val baseColor = Color.parseColor(if (isPaid) BLUE_HEX else RED_HEX)
    val r = Color.red(baseColor); val g = Color.green(baseColor); val b = Color.blue(baseColor)
    val cx = size / 2f; val cy = size / 2f
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    paint.color = Color.argb(50, r, g, b)
    cv.drawCircle(cx, cy, cx - 1f, paint)

    paint.color = Color.argb(160, r, g, b)
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 3f
    cv.drawCircle(cx, cy, cx - 6f, paint)
    paint.style = Paint.Style.FILL

    paint.color = baseColor
    cv.drawCircle(cx, cy, cx - 10f, paint)

    paint.color = Color.WHITE
    cv.drawCircle(cx, cy, cx - 24f, paint)

    paint.color = baseColor
    paint.textSize = 24f
    paint.typeface = Typeface.DEFAULT_BOLD
    paint.textAlign = Paint.Align.CENTER
    cv.drawText(label.toString(), cx, cy - (paint.descent() + paint.ascent()) / 2f, paint)

    return bmp
}

// ─── Composable ────────────────────────────────────────────────────────────────

@Composable
fun MapTabView(modifier: Modifier = Modifier) {
    var selectedPin by remember { mutableStateOf<PinData?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    setTileSource(SnappTileSource)
                    setMultiTouchControls(true)
                    isHorizontalMapRepetitionEnabled = false
                    isVerticalMapRepetitionEnabled = false

                    // Fallback center before layout resolves
                    controller.setZoom(13.0)
                    controller.setCenter(GeoPoint(35.7790, 51.4215))

                    // Route drawn first → below markers
                    overlays.add(RouteOverlay(tehranPins.map { GeoPoint(it.lat, it.lon) }))

                    tehranPins.forEach { pin ->
                        val marker = Marker(this)
                        marker.position = GeoPoint(pin.lat, pin.lon)
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        marker.icon = BitmapDrawable(context.resources, makePinBitmap(pin.label, pin.isPaid))
                        marker.title = pin.name
                        marker.infoWindow = null
                        marker.setOnMarkerClickListener { _, _ ->
                            selectedPin = if (selectedPin == pin) null else pin
                            true
                        }
                        overlays.add(marker)
                    }

                    setOnTouchListener { v, event ->
                        if (event.action == MotionEvent.ACTION_DOWN) selectedPin = null
                        v.onTouchEvent(event)
                    }

                    // Auto-zoom to fit all pins after first layout
                    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            viewTreeObserver.removeOnGlobalLayoutListener(this)
                            val bbox = BoundingBox(
                                tehranPins.maxOf { it.lat },
                                tehranPins.maxOf { it.lon },
                                tehranPins.minOf { it.lat },
                                tehranPins.minOf { it.lon },
                            )
                            zoomToBoundingBox(bbox, false, 120)
                        }
                    })
                }
            },
            modifier = Modifier.fillMaxSize(),
        )

        // Tooltip
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

        // Legend
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
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Line))
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
