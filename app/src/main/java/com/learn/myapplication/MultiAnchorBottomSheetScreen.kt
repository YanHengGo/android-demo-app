package com.learn.myapplication

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learn.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class SheetAnchor { Hidden, Peek, HalfExpanded, Expanded }

private data class TabSpec(val label: String, val icon: ImageVector)

private val tabs = listOf(
    TabSpec("検索", Icons.Filled.Search),
    TabSpec("近く", Icons.Filled.Place),
    TabSpec("保存", Icons.Filled.Star),
    TabSpec("お気に入り", Icons.Filled.Favorite),
    TabSpec("設定", Icons.Filled.Settings),
)

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MultiAnchorBottomSheetScreen() {
    val density = LocalDensity.current
    val expandedTopInset = 56.dp
    val peekHeight = 116.dp

    val draggableState = remember {
        AnchoredDraggableState(initialValue = SheetAnchor.Peek)
    }

    val scope = rememberCoroutineScope()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val screenHeightPx = with(density) { maxHeight.toPx() }
        val peekPx = with(density) { peekHeight.toPx() }
        val halfPx = screenHeightPx * 0.5f
        val expandedTopPx = with(density) { expandedTopInset.toPx() }
        val sheetMaxHeight = maxHeight - expandedTopInset

        LaunchedEffect(screenHeightPx) {
            draggableState.updateAnchors(
                DraggableAnchors {
                    SheetAnchor.Hidden at screenHeightPx
                    SheetAnchor.Peek at (screenHeightPx - peekPx)
                    SheetAnchor.HalfExpanded at (screenHeightPx - halfPx)
                    SheetAnchor.Expanded at expandedTopPx
                }
            )
        }

        MapPlaceholder(Modifier.fillMaxSize())

        AnchorControls(
            current = draggableState.currentValue,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 24.dp, end = 16.dp),
            onSelect = { target -> scope.launch { draggableState.animateTo(target) } },
        )

        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .height(sheetMaxHeight)
                .offset {
                    val y = draggableState.offset
                    val safeY = if (y.isNaN()) screenHeightPx else y
                    IntOffset(0, safeY.roundToInt())
                },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            shadowElevation = 12.dp,
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(Modifier.fillMaxSize()) {
                Column(
                    Modifier.anchoredDraggable(draggableState, Orientation.Vertical),
                ) {
                    DragHandle()
                    SheetTabRow(selected = selectedTab, onSelect = { selectedTab = it })
                }
                HorizontalDivider()
                TabContent(selected = selectedTab, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun DragHandle() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(width = 36.dp, height = 4.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)),
        )
    }
}

@Composable
private fun SheetTabRow(selected: Int, onSelect: (Int) -> Unit) {
    TabRow(
        selectedTabIndex = selected,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                selected = selected == index,
                onClick = { onSelect(index) },
                text = { Text(tab.label, fontSize = 12.sp) },
                icon = { Icon(tab.icon, contentDescription = tab.label) },
            )
        }
    }
}

@Composable
private fun TabContent(selected: Int, modifier: Modifier = Modifier) {
    when (selected) {
        0 -> SimpleListTab(
            modifier = modifier,
            icon = Icons.Filled.Search,
            entries = listOf(
                "カフェ" to "近くの 24 件",
                "レストラン" to "近くの 51 件",
                "ガソリンスタンド" to "近くの 8 件",
                "ホテル" to "近くの 12 件",
                "コンビニ" to "近くの 33 件",
                "公園" to "近くの 6 件",
            ),
        )
        1 -> SimpleListTab(
            modifier = modifier,
            icon = Icons.Filled.Place,
            entries = listOf(
                "渋谷スカイ" to "0.4 km · 展望スポット",
                "代々木公園" to "1.2 km · 公園",
                "Blue Bottle Coffee" to "0.8 km · カフェ",
                "明治神宮" to "1.6 km · 神社",
                "新宿御苑" to "2.5 km · 庭園",
            ),
        )
        2 -> SimpleListTab(
            modifier = modifier,
            icon = Icons.Filled.Star,
            entries = listOf(
                "自宅" to "保存済み",
                "会社" to "保存済み",
                "実家" to "保存済み",
                "よく行く図書館" to "保存済み",
            ),
        )
        3 -> SimpleListTab(
            modifier = modifier,
            icon = Icons.Filled.Favorite,
            entries = listOf(
                "お気に入りラーメン店" to "★ 4.6",
                "週末のドライブコース" to "★ 4.8",
                "夜景スポット" to "★ 4.9",
            ),
        )
        else -> SettingsTab(modifier)
    }
}

@Composable
private fun SimpleListTab(
    icon: ImageVector,
    entries: List<Pair<String, String>>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        items(entries) { (title, subtitle) ->
            ListItem(
                headlineContent = { Text(title) },
                supportingContent = { Text(subtitle) },
                leadingContent = { Icon(icon, contentDescription = null) },
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun SettingsTab(modifier: Modifier = Modifier) {
    var traffic by remember { mutableStateOf(true) }
    var satellite by remember { mutableStateOf(false) }
    var nightMode by remember { mutableStateOf(false) }
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        item {
            ListItem(
                headlineContent = { Text("交通情報") },
                supportingContent = { Text("リアルタイムの混雑を表示") },
                trailingContent = { Switch(checked = traffic, onCheckedChange = { traffic = it }) },
            )
            HorizontalDivider()
        }
        item {
            ListItem(
                headlineContent = { Text("航空写真") },
                supportingContent = { Text("地図を衛星画像に切り替え") },
                trailingContent = { Switch(checked = satellite, onCheckedChange = { satellite = it }) },
            )
            HorizontalDivider()
        }
        item {
            ListItem(
                headlineContent = { Text("ナイトモード") },
                supportingContent = { Text("暗いテーマを使用") },
                trailingContent = { Switch(checked = nightMode, onCheckedChange = { nightMode = it }) },
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun AnchorControls(
    current: SheetAnchor,
    onSelect: (SheetAnchor) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        shadowElevation = 4.dp,
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            SheetAnchor.entries.forEach { anchor ->
                val label = when (anchor) {
                    SheetAnchor.Hidden -> "隠す"
                    SheetAnchor.Peek -> "Peek"
                    SheetAnchor.HalfExpanded -> "半開"
                    SheetAnchor.Expanded -> "全開"
                }
                FilledTonalButton(
                    onClick = { onSelect(anchor) },
                    enabled = current != anchor,
                ) { Text(label, fontSize = 12.sp) }
            }
        }
    }
}

@Composable
private fun MapPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier.background(
            Brush.verticalGradient(
                listOf(Color(0xFFE8F0E5), Color(0xFFD7E3D0)),
            ),
        ),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            
            // Draw a simple grid
            val step = 80.dp.toPx()
            for (x in 0..(canvasWidth / step).toInt()) {
                drawLine(
                    color = Color.White.copy(alpha = 0.5f),
                    start = Offset(x * step, 0f),
                    end = Offset(x * step, canvasHeight),
                    strokeWidth = 1f
                )
            }
            for (y in 0..(canvasHeight / step).toInt()) {
                drawLine(
                    color = Color.White.copy(alpha = 0.5f),
                    start = Offset(0f, y * step),
                    end = Offset(canvasWidth, y * step),
                    strokeWidth = 1f
                )
            }
        }
    }
}
