package com.freshcart.shop.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.freshcart.shop.data.AppState
import com.freshcart.shop.data.Route
import com.freshcart.shop.data.Store
import kotlinx.coroutines.delay

private data class StoreTab(val store: Store, val label: String, val icon: ImageVector, val weight: Float)

private val STORE_TABS = listOf(
    StoreTab(Store.Hypermarket, "Hypermarket", Icons.Outlined.Storefront, 133.5f),
    StoreTab(Store.Minutes, "Minutes", Icons.Outlined.Bolt, 102.2f),
    StoreTab(Store.Money, "Send Money", Icons.Outlined.Payments, 127.3f),
)

/** Store switcher: translucent track with a white selected pill that springs between tabs. 40 dp tabs, 4 dp inset. */
@Composable
fun StoreTabs(state: AppState, accent: Color, modifier: Modifier = Modifier) {
    BoxWithConstraints(
        modifier.fillMaxWidth().padding(horizontal = 10.9.dp).height(48.dp).background(C.TabRow, RoundedCornerShape(14.dp)).padding(4.dp),
    ) {
        val total = STORE_TABS.sumOf { it.weight.toDouble() }.toFloat()
        val w = maxWidth
        val starts = STORE_TABS.runningFold(0f) { acc, t -> acc + t.weight }.map { w * (it / total) }
        val idx = STORE_TABS.indexOfFirst { it.store == state.store }
        val x by animateDpAsState(starts[idx], spring(dampingRatio = 0.8f, stiffness = 500f), label = "pillX")
        val pw by animateDpAsState(w * (STORE_TABS[idx].weight / total), spring(dampingRatio = 0.8f, stiffness = 500f), label = "pillW")
        Box(Modifier.offset(x = x).width(pw).height(40.dp).background(Color.White, RoundedCornerShape(11.dp)))
        Row(Modifier.fillMaxSize()) {
            STORE_TABS.forEach { t ->
                val sel = t.store == state.store
                val tint by animateColorAsState(if (sel) accent else C.Slate, label = "tabTint")
                // The id sits on a plain wrapper so it reports the drawn 40 dp, not the 48 dp touch area.
                Box(Modifier.weight(t.weight).height(40.dp).testTag("store_${t.store.name.lowercase()}")) {
                    Row(
                        Modifier.fillMaxSize().clickable(remember { MutableInteractionSource() }, null) { state.store = t.store },
                        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(t.icon, null, tint = tint, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(t.label, style = T.tab, color = if (sel) accent else C.Slate, maxLines = 1)
                    }
                }
            }
        }
    }
}

/** "Today, 8:00 pm – 10:00 pm" on the left, the delivery area with a pin on the right. 28.4 dp tall. */
@Composable
fun DeliveryRow(state: AppState, slot: String, ink: Color = Color.White, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().height(28.4.dp).padding(start = 9.8.dp, end = 17.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(slot, style = T.delivery, color = ink, modifier = Modifier.weight(1f), maxLines = 1)
        Row(Modifier.clickable { }, verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.LocationOn, null, tint = C.Accent, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(2.dp))
            Text(state.address.line.substringAfter(", ").substringBefore(",") + ", Dubai", style = T.delivery.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
                color = ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Icon(Icons.Rounded.KeyboardArrowDown, null, tint = ink, modifier = Modifier.size(18.dp))
        }
    }
}

/** Collapsible top of the store header: 10.9 spacer + 48 tab track + 3 gap + 28.4 delivery row. */
val HEADER_TOP = 90.3.dp
/** Full store header below the status bar: top part + 2.1 + 36 search + 4 + 68 chips. */
val HEADER_FULL = 200.4.dp
/** How far the header collapses: the top part plus the chips' icon row. */
val HEADER_COLLAPSE = 124.3.dp

private val HINTS = listOf("groceries", "fruits", "vegetables", "milk", "headphones", "laundry")

/** Search field (36 dp) with a hint that types itself word by word. Opens the search screen. */
@Composable
fun SearchField(state: AppState, modifier: Modifier = Modifier, bg: Color = C.Surface) {
    var typed by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        var k = 0
        while (true) {
            val word = HINTS[k % HINTS.size]
            for (n in 1..word.length) { typed = word.take(n); delay(110) }
            delay(1600)
            for (n in word.length downTo 0) { typed = word.take(n); delay(45) }
            delay(250); k++
        }
    }
    // The id sits on a plain wrapper so it reports the drawn 36 dp, not the 48 dp touch area.
    Box(modifier.fillMaxWidth().padding(start = 10.2.dp, end = 13.8.dp).height(36.dp).testTag("search_field")) {
        Row(
            Modifier.fillMaxSize().background(bg, RoundedCornerShape(9.9.dp)).clip(RoundedCornerShape(9.9.dp))
                .clickable { state.go(Route.Search()) }.padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Rounded.Search, "Search", tint = C.Slate, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(7.dp))
            Text("Search ", style = T.search, color = C.Hint)
            Text("\"$typed|\"", style = T.search, color = C.Hint, maxLines = 1)
        }
    }
}

/** Measured widths of the source's first five chips; later chips use its rule (label + 20 dp, at least 64 dp). */
val CHIP_WIDTHS = listOf(83.6.dp, 89.5.dp, 64.dp, 64.dp, 73.5.dp)

data class Chip(val label: String, val icon: ImageVector)

/** Tab-shaped chip: rounded top corners and outward flares at the bottom so it joins the page below. */
private fun chipShape(r: Float, flare: Float) = GenericShape { size: Size, _ ->
    moveTo(-flare, size.height)
    quadraticTo(0f, size.height, 0f, size.height - flare)
    lineTo(0f, r)
    quadraticTo(0f, 0f, r, 0f)
    lineTo(size.width - r, 0f)
    quadraticTo(size.width, 0f, size.width, r)
    lineTo(size.width, size.height - flare)
    quadraticTo(size.width, size.height, size.width + flare, size.height)
    close()
}

/**
 * Icon-over-label category chips (68 dp). [collapse] 0..1 shrinks them to a text-only row (34 dp) as the page scrolls.
 */
@Composable
fun ChipTabs(chips: List<Chip>, selected: Int, onSelect: (Int) -> Unit, collapse: Float, tint: Color, selBg: Color, modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val h: Dp = 68.dp - 34.dp * collapse
    LazyRow(modifier.fillMaxWidth().height(h), contentPadding = PaddingValues(horizontal = 10.2.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        itemsIndexed(chips) { i, c ->
            val sel = i == selected
            val shape = remember(density) { with(density) { chipShape(14.dp.toPx(), 9.dp.toPx()) } }
            val w = CHIP_WIDTHS.getOrNull(i)
            Box(
                Modifier.height(h).then(if (w != null) Modifier.width(w) else Modifier.widthIn(min = 64.dp)).testTag("chip_$i")
                    .then(if (sel) Modifier.background(lerp(selBg, Color(0xFFE4E4E6), collapse), if (collapse > 0.5f) RoundedCornerShape(10.dp) else shape) else Modifier)
                    .clickable(remember { MutableInteractionSource() }, null) { onSelect(i) }
                    .padding(horizontal = if (w != null) 0.dp else 10.dp),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (collapse < 0.99f) {
                        Icon(c.icon, null, tint = lerp(tint, C.Ink, collapse), modifier = Modifier.size(30.dp * (1 - collapse)).alpha(1 - collapse))
                        Spacer(Modifier.height(4.dp * (1 - collapse)))
                    }
                    Text(c.label, style = T.chip, color = C.Ink, maxLines = 1, modifier = Modifier.padding(bottom = 10.dp - 1.dp * collapse))
                }
            }
        }
    }
}

/**
 * The collapsing store header shared by Hypermarket and Minutes. [collapsePx] is how far the page has scrolled;
 * the store tabs and delivery row (84 dp) slide away first, then the chips lose their icons (34 dp).
 */
@Composable
fun StoreHeader(
    state: AppState, collapsePx: Float, bg: Color, accent: Color, slot: String,
    chips: List<Chip>, chip: Int, onChip: (Int) -> Unit, chipTint: Color = Color.White, chipSelBg: Color = C.CampaignSoft,
) {
    val density = LocalDensity.current
    // Measured from the status bar: tab track 10.9 dp down (tabs at +15), delivery row +62, search +92.4, chips +132.4.
    val topPx = with(density) { HEADER_TOP.toPx() }
    val chipPx = with(density) { 34.dp.toPx() }
    val t1 = (collapsePx / topPx).coerceIn(0f, 1f)
    val t2 = ((collapsePx - topPx) / chipPx).coerceIn(0f, 1f)
    val frosted = lerp(bg, Color(0xF2FFFFFF), t2)
    Column(Modifier.fillMaxWidth().background(if (t2 > 0f) frosted else bg).statusBarsPadding().testTag("store_header")) {
        Column(
            Modifier.fillMaxWidth().height(HEADER_TOP * (1 - t1)).graphicsLayer { alpha = 1 - t1; translationY = -collapsePx.coerceAtMost(topPx) * 0.3f },
        ) {
            Spacer(Modifier.height(10.9.dp))
            StoreTabs(state, accent)
            Spacer(Modifier.height(3.dp))
            DeliveryRow(state, slot)
        }
        Spacer(Modifier.height(2.1.dp))
        SearchField(state, bg = if (t2 > 0.5f) Color.White else C.Surface)
        Spacer(Modifier.height(4.dp))
        ChipTabs(chips, chip, onChip, t2, chipTint, chipSelBg)
    }
}
