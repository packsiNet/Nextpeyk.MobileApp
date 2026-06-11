package com.nextpeyk.mobileapp.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextpeyk.mobileapp.ui.screens.home.model.Shipment
import com.nextpeyk.mobileapp.ui.screens.home.model.style
import com.nextpeyk.mobileapp.ui.theme.Accent
import com.nextpeyk.mobileapp.ui.theme.AccentSoft
import com.nextpeyk.mobileapp.ui.theme.Ink
import com.nextpeyk.mobileapp.ui.theme.Line
import com.nextpeyk.mobileapp.ui.theme.Muted

@Composable
fun DetailCard(shipment: Shipment, number: Int, onDetails: () -> Unit = {}, modifier: Modifier = Modifier) {
    val toneStyle = shipment.tone.style()
    val headerGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF1E272F), Color(0xFF14181B)),
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
    ) {
        // Dark header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(headerGradient)
                .padding(18.dp),
        ) {
            // Number badge top-left (RTL: top-end)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(number.toString(), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color.White.copy(alpha = 0.7f))
            }

            Column(modifier = Modifier.padding(end = 38.dp)) {
                // Status
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(toneStyle.dot),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(shipment.statusLabel, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha = 0.55f))
                }
                Spacer(Modifier.height(6.dp))
                // Recipient name
                Text(shipment.recipientName, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, lineHeight = 25.sp)
                Spacer(Modifier.height(4.dp))
                // Tracking (LTR)
                Text(
                    shipment.tracking,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.3f),
                    letterSpacing = 0.8.sp,
                )
            }
        }

        // White body
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 18.dp, vertical = 14.dp),
        ) {
            // Destination row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AccentSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Accent, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("مقصد", fontSize = 10.5.sp, fontWeight = FontWeight.Medium, color = Muted)
                    Text(
                        shipment.destination,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Ink,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Spacer(Modifier.height(13.dp))
            // Bottom row: meta + actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Icon(Icons.Filled.MonitorWeight, contentDescription = null, tint = Color(0xFF9AA4A9), modifier = Modifier.size(14.dp))
                        Text(shipment.weight, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Muted)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Icon(Icons.Filled.Schedule, contentDescription = null, tint = Color(0xFF9AA4A9), modifier = Modifier.size(14.dp))
                        Text(shipment.eta, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Muted)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, Line, RoundedCornerShape(10.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.Phone, contentDescription = "تماس", tint = Accent, modifier = Modifier.size(15.dp))
                    }
                    Row(
                        modifier = Modifier.clickable(onClick = onDetails),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text("جزئیات", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Accent)
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Accent, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}
