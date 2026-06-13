package ir.nextpeyk.android.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.nextpeyk.android.ui.theme.Accent
import ir.nextpeyk.android.ui.theme.AccentSoft
import ir.nextpeyk.android.ui.theme.Ink
import ir.nextpeyk.android.ui.theme.Line
import ir.nextpeyk.android.ui.theme.Muted

private data class AddOption(val icon: ImageVector, val label: String, val sub: String)

private val addOptions = listOf(
    AddOption(Icons.Filled.Inventory2, "ارسال بسته", "تحویل درب به درب"),
    AddOption(Icons.Filled.CalendarMonth, "زمان‌بندی تحویل‌گیری", "انتخاب تاریخ و ساعت"),
    AddOption(Icons.Filled.LocalShipping, "ارسال عمده", "چند بسته به‌صورت هم‌زمان"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBottomSheet(onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 22.dp).padding(bottom = 40.dp)) {
            Text("ارسال جدید", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Ink)
            Spacer(Modifier.height(16.dp))
            addOptions.forEachIndexed { i, opt ->
                if (i > 0) HorizontalDivider(color = Line, thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDismiss() }
                        .padding(vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(AccentSoft),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(opt.icon, contentDescription = null, tint = Accent, modifier = Modifier.size(23.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(opt.label, fontSize = 15.5.sp, fontWeight = FontWeight.Bold, color = Ink)
                        Text(opt.sub, fontSize = 13.sp, color = Muted)
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFFC4CACD), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
