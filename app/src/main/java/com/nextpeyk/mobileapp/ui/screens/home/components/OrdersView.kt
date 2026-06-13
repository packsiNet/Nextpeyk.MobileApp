package ir.nextpeyk.android.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.nextpeyk.android.ui.screens.home.model.Shipment
import ir.nextpeyk.android.ui.theme.Ink
import ir.nextpeyk.android.ui.theme.Line
import ir.nextpeyk.android.ui.theme.Muted

@Composable
fun OrdersView(shipments: List<Shipment>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 100.dp),
    ) {
        Spacer(Modifier.height(6.dp))
        Text("سفارش‌ها", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = Ink)
        Text("${shipments.size} ارسال در این هفته", fontSize = 13.5.sp, color = Muted, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(18.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Color.White)
                .padding(horizontal = 18.dp, vertical = 4.dp),
        ) {
            shipments.forEachIndexed { i, s ->
                ShipmentRow(shipment = s, number = i + 1)
                if (i < shipments.size - 1) {
                    HorizontalDivider(color = Line, thickness = 1.dp)
                }
            }
        }
    }
}
