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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

            Spacer(Modifier.height(20.dp))
            ShipmentFilterTabs()
            Spacer(Modifier.height(10.dp))
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
                shipments.forEachIndexed { i, s ->
                    ShipmentRow(
                        shipment = s,
                        number = i + 1,
                        isActive = i == openIndex,
                        onClick = { onShipmentSelect(i) },
                    )
                    if (i < shipments.size - 1) {
                        HorizontalDivider(color = Line, thickness = 1.dp)
                    }
                }
            }
        }
    }
}

private data class FilterTab(val label: String, val count: Int)

@Composable
private fun ShipmentFilterTabs() {
    val tabs = listOf(FilterTab("درحال انجام", 50), FilterTab("تحویل", 35), FilterTab("مرجوع", 15))
    var selected by remember { mutableIntStateOf(0) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(Color(0xFFEEF0F1))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        tabs.forEachIndexed { idx, tab ->
            val active = selected == idx
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(CircleShape)
                    .background(if (active) Ink else Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { selected = idx },
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Text(
                        tab.label,
                        fontSize = 13.sp,
                        fontWeight = if (active) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (active) Color.White else Muted,
                    )
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (active) Color.White.copy(alpha = 0.18f) else Color(0xFFDEE2E4))
                            .padding(horizontal = 6.dp, vertical = 1.dp),
                    ) {
                        Text(
                            tab.count.toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (active) Color.White else Muted,
                        )
                    }
                }
            }
        }
    }
}
