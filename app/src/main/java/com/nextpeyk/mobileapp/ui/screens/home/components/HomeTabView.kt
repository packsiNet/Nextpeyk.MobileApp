package com.nextpeyk.mobileapp.ui.screens.home.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextpeyk.mobileapp.ui.screens.home.model.Shipment
import com.nextpeyk.mobileapp.ui.theme.Accent
import com.nextpeyk.mobileapp.ui.theme.AccentSoft
import com.nextpeyk.mobileapp.ui.theme.Ink
import com.nextpeyk.mobileapp.ui.theme.Line
import com.nextpeyk.mobileapp.ui.theme.Muted
import com.nextpeyk.mobileapp.ui.theme.Page
import androidx.compose.foundation.interaction.MutableInteractionSource

@Composable
fun HomeTabView(
    shipments: List<Shipment>,
    openIndex: Int,
    showStats: Boolean,
    onStatsToggle: () -> Unit,
    onShipmentSelect: (Int) -> Unit,
    onDetails: (Int) -> Unit = {},
    onProfile: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val others = shipments.mapIndexed { i, s -> Pair(i, s) }.filter { it.first != openIndex }

    Column(modifier = modifier.fillMaxSize()) {
        // Header
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 22.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text("۱۴۰۳/۰۱/۱۰", fontSize = 13.5.sp, color = Muted, fontWeight = FontWeight.Medium)
                    Text("خانه", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = Ink)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Stats toggle
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (showStats) AccentSoft else Color.White)
                            .let {
                                if (showStats) it.then(Modifier) else it
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        IconButton(onClick = onStatsToggle) {
                            Icon(
                                Icons.Filled.BarChart,
                                contentDescription = "آمار",
                                tint = if (showStats) Accent else Ink,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                    // User avatar
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Accent)
                            .clickable { onProfile() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("ن", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            // Featured card
            AnimatedContent(
                targetState = openIndex,
                transitionSpec = { (fadeIn() + slideInVertically { -it / 3 }) togetherWith fadeOut() },
                label = "detail_card",
            ) { idx ->
                DetailCard(
                    shipment = shipments[idx],
                    number = idx + 1,
                    onDetails = { onDetails(idx) },
                    modifier = Modifier
                        .shadow(18.dp, RoundedCornerShape(26.dp))
                        .clip(RoundedCornerShape(26.dp)),
                )
            }

            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("سایر مرسوله‌ها", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Ink)
                Text("برای باز شدن لمس کنید", fontSize = 12.5.sp, color = Muted, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(6.dp))
        }

        // Scrollable list
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 100.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White)
                    .padding(horizontal = 18.dp, vertical = 4.dp),
            ) {
                others.forEachIndexed { idx, (i, s) ->
                    ShipmentRow(shipment = s, number = idx + 1, onClick = { onShipmentSelect(i) })
                    if (idx < others.size - 1) {
                        HorizontalDivider(color = Line, thickness = 1.dp)
                    }
                }
            }
        }
    }
}
