package ir.nextpeyk.android.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ir.nextpeyk.android.ui.screens.home.model.HomeTab
import ir.nextpeyk.android.ui.theme.Accent
import ir.nextpeyk.android.ui.theme.Muted
import ir.nextpeyk.android.ui.theme.PillActive
import ir.nextpeyk.android.ui.theme.PillBg

@Composable
fun FloatingTabBar(
    activeTab: HomeTab,
    onTabSelect: (HomeTab) -> Unit,
    onScan: () -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
        Box(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .clip(CircleShape)
                .background(PillBg)
                .border(1.dp, Color.Black.copy(alpha = 0.05f), CircleShape)
                .padding(4.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                TabNavBtn(icon = Icons.Filled.Home, active = activeTab == HomeTab.Home) { onTabSelect(HomeTab.Home) }
                TabNavBtn(icon = Icons.Filled.Map, active = activeTab == HomeTab.Map) { onTabSelect(HomeTab.Map) }

                // Separator
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(20.dp)
                        .background(Muted.copy(alpha = 0.3f))
                        .padding(horizontal = 2.dp),
                )

                TabActionBtn(icon = Icons.Filled.Search, onClick = onSearch)
                TabActionBtn(icon = Icons.Filled.QrCodeScanner, onClick = onScan)
            }
        }
    }
}

@Composable
private fun TabNavBtn(icon: ImageVector, active: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 44.dp, height = 34.dp)
            .clip(CircleShape)
            .background(if (active) PillActive else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = if (active) Accent else Muted, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun TabActionBtn(icon: ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 44.dp, height = 34.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = Muted, modifier = Modifier.size(22.dp))
    }
}
