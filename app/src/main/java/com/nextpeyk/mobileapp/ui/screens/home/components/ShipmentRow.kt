package com.nextpeyk.mobileapp.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextpeyk.mobileapp.ui.screens.home.model.Shipment
import com.nextpeyk.mobileapp.ui.screens.home.model.ShipmentTone
import com.nextpeyk.mobileapp.ui.theme.Accent
import com.nextpeyk.mobileapp.ui.theme.AccentSoft
import com.nextpeyk.mobileapp.ui.theme.Ink
import com.nextpeyk.mobileapp.ui.theme.Muted
import com.nextpeyk.mobileapp.ui.theme.RedColor
import com.nextpeyk.mobileapp.ui.theme.RedSoft

@Composable
fun ShipmentRow(
    shipment: Shipment,
    number: Int? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val dotColor = if (shipment.tone == ShipmentTone.Muted) RedColor else Accent
    val ringColor = if (shipment.tone == ShipmentTone.Muted) RedSoft else AccentSoft

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(vertical = 13.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF3F5F6)),
            contentAlignment = Alignment.Center,
        ) {
            if (number != null) {
                Text(number.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5A6469))
            } else {
                Icon(Icons.Filled.Inventory2, contentDescription = null, tint = Color(0xFF5A6469), modifier = Modifier.size(21.dp))
            }
        }
        Spacer(Modifier.width(13.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(shipment.tracking, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Ink)
            Text(
                shipment.recipientName, fontSize = 12.5.sp, fontWeight = FontWeight.Medium,
                color = Muted, maxLines = 1, overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(15.dp)
                .clip(CircleShape)
                .background(ringColor),
            contentAlignment = Alignment.Center,
        ) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(dotColor))
        }
        if (onClick != null) {
            Spacer(Modifier.width(4.dp))
            Icon(
                Icons.Filled.ChevronLeft,
                contentDescription = null,
                tint = Muted,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
