package com.freshcart.shop.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.freshcart.shop.data.AppState
import com.freshcart.shop.data.Route
import com.freshcart.shop.data.Store
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class StoreTab(val store: Store, val label: String, val icon: ImageVector)

private val STORE_TABS = listOf(
    StoreTab(Store.Hypermarket, "Hypermarket", Icons.Rounded.Storefront),
    StoreTab(Store.Minutes, "Minutes", Icons.Rounded.Bolt),
    StoreTab(Store.Money, "Send Money", Icons.Rounded.Payments),
)

/** Folder-tab outline: rounded top corners, and bottom corners that flare outwards so the tab grows out of the block below it. */
private fun folderTab(r: Float, flare: Float) = GenericShape { size: Size, _ ->
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
 * Store switcher, the first of the three tab rows: three large tabs with a pictogram over the label (70 dp).
 * A lifted folder tab sits behind the selected store. When the store changes it springs across from where it was,
 * stretching on the way, and the new store's pictogram pops up and settles; the phone ticks once.
 */
@Composable
fun StoreTabs(state: AppState, modifier: Modifier = Modifier) {
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    val idx = STORE_TABS.indexOfFirst { it.store == state.store }
    // Each store page draws its own header, so the tab starts from the store that was showing before.
    val from = remember { STORE_TABS.indexOfFirst { it.store == state.prevStore } }
    val pos = remember { Animatable(from.toFloat()) }
    LaunchedEffect(idx) {
        pos.animateTo(idx.toFloat(), spring(dampingRatio = 0.72f, stiffness = 320f))
        state.prevStore = state.store
    }
    BoxWithConstraints(modifier.fillMaxWidth().padding(horizontal = 8.dp).height(D.BigTabH)) {
        val tabPx = with(density) { (maxWidth / STORE_TABS.size).toPx() }
        val shape = remember(density) { with(density) { folderTab(18.dp.toPx(), 10.dp.toPx()) } }
        Box(
            Modifier.offset { IntOffset((tabPx * pos.value).roundToInt(), 0) }.width(maxWidth / STORE_TABS.size).fillMaxHeight()
                // stretches sideways while it travels and is back to its own width when it lands
                .graphicsLayer { scaleX = 1f + 0.14f * sin(abs(pos.value - idx).coerceAtMost(1f) * PI).toFloat() }
                .background(C.TabLift, shape).border(1.dp, C.TabEdge, shape),
        )
        Row(Modifier.fillMaxSize()) {
            STORE_TABS.forEachIndexed { i, t ->
                val sel = i == idx
                val lift by animateFloatAsState(if (sel) 1f else 0f, M.pop(), label = "storeLift")
                Column(
                    Modifier.weight(1f).fillMaxHeight().testTag("store_${t.store.name.lowercase()}")
                        .clickable(remember { MutableInteractionSource() }, null) {
                            if (!sel) { haptic.performHapticFeedback(HapticFeedbackType.LongPress); state.store = t.store }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        t.icon, null, tint = Color.White,
                        modifier = Modifier.size(30.dp).graphicsLayer {
                            val s = 0.82f + 0.26f * lift
                            scaleX = s; scaleY = s
                            translationY = -3.dp.toPx() * lift
                            rotationZ = -10f * (1f - lift)
                            alpha = 0.72f + 0.28f * lift.coerceIn(0f, 1f)
                        },
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(t.label, style = T.storeTab, color = Color.White, maxLines = 1,
                        modifier = Modifier.graphicsLayer { alpha = 0.78f + 0.22f * lift.coerceIn(0f, 1f) })
                }
            }
        }
    }
}

/** Where the order goes ("Home ›" over the street line) on the left; the delivery promise as a pill on the right. 56 dp. */
@Composable
fun AddressRow(state: AppState, promise: String, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().height(D.AddrH).padding(horizontal = D.Side), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f).clickable(remember { MutableInteractionSource() }, null) { }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.LocationOn, null, tint = C.Accent, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text(state.address.label, style = T.addrLabel, color = Color.White, maxLines = 1)
                Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Text(state.address.line, style = T.small, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(Modifier.width(12.dp))
        Row(
            Modifier.height(32.dp).background(C.TabLift, RoundedCornerShape(D.R16)).border(1.dp, C.TabEdge, RoundedCornerShape(D.R16)).padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.Schedule, null, tint = C.Accent, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text(promise, style = T.smallM, color = Color.White, maxLines = 1)
        }
    }
}

/** The part of a store header that slides away on scroll: address row + store tabs. */
val HEADER_TOP = D.AddrH + D.BigTabH
/** The part that stays: 8 + search field + 4 + second tab row. */
val HEADER_PINNED = 8.dp + D.SearchTall + 4.dp + D.TextTabH
/** Full store header below the status bar. */
val HEADER_FULL = HEADER_TOP + HEADER_PINNED
/** How far the header collapses. */
val HEADER_COLLAPSE = HEADER_TOP

private val HINTS = listOf("milk", "fruits", "vegetables", "headphones", "laundry", "groceries")

/** Search field (56 dp). The hinted word changes every few seconds, sliding up. Opens the search screen. */
@Composable
fun SearchField(state: AppState, modifier: Modifier = Modifier, bg: Color = Color.White) {
    val reduced = reducedMotion()
    var k by remember { mutableIntStateOf(0) }
    LaunchedEffect(reduced) { if (!reduced) while (true) { delay(2600); k = (k + 1) % HINTS.size } }
    Box(modifier.fillMaxWidth().padding(horizontal = D.Side).height(D.SearchTall).testTag("search_field")) {
        Row(
            Modifier.fillMaxSize().background(bg, RoundedCornerShape(D.R16)).clip(RoundedCornerShape(D.R16))
                .clickable { state.go(Route.Search()) }.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Rounded.Search, "Search", tint = C.Slate, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(10.dp))
            Text("Search for ", style = T.search, color = C.Hint, maxLines = 1)
            AnimatedContent(
                k, label = "hint",
                transitionSpec = { (slideInVertically(M.bar()) { it } + fadeIn(M.snap())) togetherWith (slideOutVertically(M.bar()) { -it } + fadeOut(M.snap())) },
            ) { i -> Text("'${HINTS[i]}'", style = T.search, color = C.Hint, maxLines = 1) }
        }
    }
}

data class Chip(val label: String, val icon: ImageVector)

/**
 * Second tab row: a small icon and a capital label per tab with hairlines between, and one underline that springs
 * to the selected tab, its width overshooting a little as it lands. 48 dp tall.
 */
@Composable
fun TextTabs(chips: List<Chip>, selected: Int, onSelect: (Int) -> Unit, ink: Color, accent: Color, modifier: Modifier = Modifier) {
    val haptic = LocalHapticFeedback.current
    val scroll = rememberScrollState()
    val bounds = remember { mutableStateMapOf<Int, Pair<Float, Float>>() }   // tab -> left and width in px, inside the row
    val left = remember { Animatable(0f) }
    val width = remember { Animatable(0f) }
    val target = bounds[selected]
    LaunchedEffect(target) {
        if (target == null) return@LaunchedEffect
        if (width.value == 0f) { left.snapTo(target.first); width.snapTo(target.second) }
        else {
            launch { left.animateTo(target.first, M.bar()) }
            launch { width.animateTo(target.second, M.pop()) }
            launch { scroll.animateScrollTo((target.first - 48f).roundToInt().coerceAtLeast(0), M.bar()) }
        }
    }
    Row(
        modifier.fillMaxWidth().height(D.TextTabH).horizontalScroll(scroll).padding(horizontal = 8.dp)
            .drawBehind {
                val h = 3.dp.toPx()
                drawRoundRect(accent, Offset(left.value, size.height - h), Size(width.value.coerceAtLeast(0f), h), CornerRadius(h, h))
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        chips.forEachIndexed { i, c ->
            val sel = i == selected
            val on by animateFloatAsState(if (sel) 1f else 0f, M.snap(), label = "tabOn")
            if (i > 0) Box(Modifier.width(1.dp).height(16.dp).background(ink.copy(alpha = 0.24f)))
            Row(
                Modifier.fillMaxHeight().testTag("chip_$i")
                    .onPlaced { bounds[i] = it.positionInParent().x to it.size.width.toFloat() }
                    .clickable(remember { MutableInteractionSource() }, null) {
                        if (!sel) { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); onSelect(i) }
                    }
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(c.icon, null, tint = ink, modifier = Modifier.size(16.dp).graphicsLayer { val s = 1f + 0.2f * on; scaleX = s; scaleY = s; alpha = 0.78f + 0.22f * on })
                Spacer(Modifier.width(6.dp))
                Text(c.label.uppercase(), style = T.textTab, color = ink, maxLines = 1, modifier = Modifier.graphicsLayer { alpha = 0.78f + 0.22f * on })
            }
        }
    }
}

/**
 * The store header shared by Hypermarket and Minutes. On the store's colour: the address row and the store tabs;
 * below them, on the lifted ground the selected tab grows out of, the search field and the second tab row.
 * [collapsePx] is how far the page has scrolled: the address and the store tabs slide away under the status bar and
 * the part that stays turns white, its ink dark and its underline the store's accent.
 */
@Composable
fun StoreHeader(
    state: AppState, collapsePx: Float, bg: Color, accent: Color, slot: String,
    chips: List<Chip>, chip: Int, onChip: (Int) -> Unit,
) {
    val density = LocalDensity.current
    val topPx = with(density) { HEADER_TOP.toPx() }
    val t = (collapsePx / topPx).coerceIn(0f, 1f)
    Column(Modifier.fillMaxWidth().testTag("store_header")) {
        Column(Modifier.fillMaxWidth().background(bg).statusBarsPadding()) {
            Box(Modifier.fillMaxWidth().height(HEADER_TOP * (1f - t)).clipToBounds()) {
                Column(Modifier.wrapContentHeight(align = Alignment.Bottom, unbounded = true).graphicsLayer { alpha = 1f - t }) {
                    AddressRow(state, slot)
                    StoreTabs(state)
                }
            }
        }
        Column(Modifier.fillMaxWidth().background(lerp(C.TabLift.compositeOver(bg), Color.White, t))) {
            Spacer(Modifier.height(8.dp))
            SearchField(state, bg = lerp(Color.White, C.Surface, t))
            Spacer(Modifier.height(4.dp))
            TextTabs(chips, chip, onChip, ink = lerp(Color.White, C.Ink, t), accent = lerp(Color.White, accent, t))
            if (t > 0.98f) Box(Modifier.fillMaxWidth().height(1.dp).background(C.Line))
        }
    }
}
