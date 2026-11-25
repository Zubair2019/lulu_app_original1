package com.freshcart.shop.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshcart.shop.data.AppState
import com.freshcart.shop.ui.C
import com.freshcart.shop.ui.D
import com.freshcart.shop.ui.M
import com.freshcart.shop.ui.Plex
import com.freshcart.shop.ui.StoreTabs
import com.freshcart.shop.ui.T
import com.freshcart.shop.ui.appear
import com.freshcart.shop.ui.fmt
import com.freshcart.shop.ui.popIn
import com.freshcart.shop.ui.pressable
import com.freshcart.shop.ui.reducedMotion
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val Indigo = Color(0xFF4E46CD)
private val IndigoPill = Color(0xFF5A52D3)
private val IndigoSoft = Color(0xFFE1E0F1)
private val GlowBlue = Color(0x664FA3FF)
private val GlowRose = Color(0x55C86BFF)
private val Sky = Color(0xFF8FC1FF)

data class Corridor(val flag: String, val country: String, val code: String, val rate: Double)

private val CORRIDORS = listOf(
    Corridor("🇮🇳", "India", "INR", 25.25), Corridor("🇵🇰", "Pakistan", "PKR", 75.27), Corridor("🇵🇭", "Philippines", "PHP", 17.29),
    Corridor("🇧🇩", "Bangladesh", "BDT", 31.30), Corridor("🇱🇰", "Sri Lanka", "LKR", 90.45), Corridor("🇪🇬", "Egypt", "EGP", 14.70),
    Corridor("🇳🇵", "Nepal", "NPR", 40.41),
)

/**
 * Two soft lights drifting across the header. They wake when the page opens and on every [wake], move for about
 * three seconds and glide to rest. Draw-only: nothing on the header changes place, and screen readers don't see it.
 */
@Composable
private fun Modifier.aurora(wake: Int, reduced: Boolean): Modifier {
    val phase = remember { Animatable(0f) }
    LaunchedEffect(wake) { if (!reduced) phase.animateTo(phase.value + 1f, tween(3200, easing = FastOutSlowInEasing)) }
    return drawBehind {
        val a = phase.value * 2f * PI.toFloat()
        val c1 = Offset(size.width * (0.22f + 0.22f * cos(a)), size.height * (0.30f + 0.22f * sin(a)))
        val c2 = Offset(size.width * (0.82f - 0.20f * cos(a * 0.7f)), size.height * (0.78f - 0.18f * sin(a * 0.7f)))
        drawCircle(Brush.radialGradient(listOf(GlowBlue, Color.Transparent), c1, size.width * 0.6f), size.width * 0.6f, c1)
        drawCircle(Brush.radialGradient(listOf(GlowRose, Color.Transparent), c2, size.width * 0.55f), size.width * 0.55f, c2)
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MoneyScreen(state: AppState) {
    var picked by remember { mutableStateOf<Corridor?>(null) }
    var wake by remember { mutableIntStateOf(0) }
    val reduced = reducedMotion()
    val haptic = LocalHapticFeedback.current
    val pick: (Corridor?) -> Unit = { wake++; haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); picked = it }
    Column(Modifier.fillMaxSize().background(C.Surface)) {
        // Header: the same top rows as the other stores, then the headline, the three promises and the flags.
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = D.R24, bottomEnd = D.R24)).background(Indigo).aurora(wake, reduced)) {
            Column(Modifier.fillMaxWidth().statusBarsPadding()) {
                Row(Modifier.fillMaxWidth().height(D.AddrH).padding(horizontal = D.Side), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Public, null, tint = C.Accent, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Demo Cart Money", style = T.addrLabel, color = Color.White, maxLines = 1)
                        Text("Transfers from the UAE", style = T.small, color = Color.White, maxLines = 1)
                    }
                }
                StoreTabs(state)
                Spacer(Modifier.height(D.Side))
                // the headline arrives word by word
                Row(Modifier.padding(horizontal = D.Side)) {
                    val h = T.h1.copy(fontSize = 22.sp, lineHeight = 28.sp)
                    Text("Send money ", style = h, color = Color.White, modifier = Modifier.appear(0))
                    Text("fast", style = h, color = Sky, modifier = Modifier.appear(2))
                    Text(", worldwide", style = h, color = Color.White, modifier = Modifier.appear(4))
                }
                Spacer(Modifier.height(D.GridGap))
                Row(Modifier.fillMaxWidth().padding(horizontal = D.Side), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Pill(Icons.Rounded.Bolt, "Within 30 mins*", Modifier.weight(1f).popIn(2))
                    Pill(Icons.Outlined.CheckCircle, "No fees", Modifier.weight(1f).popIn(3))
                    Pill(Icons.AutoMirrored.Outlined.TrendingUp, "Great rates", Modifier.weight(1f).popIn(4))
                }
                FlagMarquee(reduced) { pick(it) }
                Spacer(Modifier.height(D.GridGap))
            }
        }
        // The rates card opens into the converter: the card changes size in place and the flag travels to its new spot.
        SharedTransitionLayout(Modifier.fillMaxSize()) {
            AnimatedContent(picked, transitionSpec = { fadeIn(M.bar()) togetherWith fadeOut(M.snap()) }, label = "money") { c ->
                if (c == null) RatesCard(this@SharedTransitionLayout, this) { pick(it) } else Converter(this@SharedTransitionLayout, this, c) { pick(null) }
            }
        }
    }
}

@Composable
private fun Pill(icon: ImageVector, text: String, modifier: Modifier) {
    Row(
        modifier.height(32.dp).background(IndigoPill, RoundedCornerShape(D.R16)).border(1.dp, C.TabEdge, RoundedCornerShape(D.R16)),
        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = C.Accent, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(4.dp))
        Text(text, style = T.smallM, color = Color.White, maxLines = 1)
    }
}

/**
 * Country flags drifting sideways (76 dp buttons). They drift for about three seconds, easing in and out, then rest
 * for a moment before the next drift; a finger on the row stops them. A tapped flag dips and springs back.
 */
@Composable
private fun FlagMarquee(reduced: Boolean, onPick: (Corridor) -> Unit) {
    val list = rememberLazyListState()
    val loop = remember { List(12) { CORRIDORS }.flatten() }
    val scope = rememberCoroutineScope()
    val pxPerDp = LocalDensity.current.density
    LaunchedEffect(reduced) {
        if (reduced) return@LaunchedEffect
        while (true) {
            val start = withFrameNanos { it }
            var last = start
            while (true) {
                val now = withFrameNanos { it }
                val t = (now - start) / 1e9f
                if (t > 3.2f) break
                val speed = sin(t / 3.2f * PI).toFloat()                       // glide in, glide out
                if (!list.isScrollInProgress) list.scrollBy(speed * 90f * pxPerDp * ((now - last) / 1e9f))   // up to 90 dp a second
                last = now
            }
            delay(1800)
        }
    }
    LazyRow(state = list, modifier = Modifier.fillMaxWidth().padding(top = D.GridGap).height(76.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        items(loop.size) { k ->
            val c = loop[k]
            val dip = remember { Animatable(1f) }
            Column(
                Modifier.width(76.dp).height(76.dp).graphicsLayer { scaleX = dip.value; scaleY = dip.value }.clip(RoundedCornerShape(D.R12))
                    .clickable(remember { MutableInteractionSource() }, null) {
                        scope.launch { dip.snapTo(0.82f); dip.animateTo(1f, M.pop()) }
                        onPick(c)
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(8.dp))
                Box(Modifier.size(36.dp).background(Color.White, CircleShape), contentAlignment = Alignment.Center) {
                    Text(c.flag, style = TextStyle(fontFamily = Plex, fontSize = 23.sp))
                }
                Spacer(Modifier.height(6.dp))
                Text(c.country, style = T.smallM, color = Color.White, maxLines = 1)
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun RatesCard(shared: SharedTransitionScope, visibility: AnimatedVisibilityScope, onPick: (Corridor) -> Unit) = with(shared) {
    val reduced = reducedMotion()
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(start = D.Side, end = D.Side, top = D.R20, bottom = D.R24)) {
        Column(
            Modifier.fillMaxWidth().appear(0, from = 36.dp)
                .sharedBounds(rememberSharedContentState("money-card"), visibility, boundsTransform = { _, _ -> M.sheet() })
                .clip(RoundedCornerShape(D.R16)).background(Color.White).border(1.dp, C.Line, RoundedCornerShape(D.R16)),
        ) {
            Row(Modifier.fillMaxWidth().background(C.Surface).padding(horizontal = D.Side, vertical = D.GridGap), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Send money abroad", style = T.bodyM.copy(fontWeight = FontWeight.Bold), color = C.Ink)
                    Text("Today's rate for 1 AED", style = T.small, color = C.Ink2)
                }
                Row(Modifier.height(32.dp).background(IndigoSoft, RoundedCornerShape(D.R16)).padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.History, null, tint = Indigo, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("My Transactions", style = T.smallM, color = Indigo)
                }
            }
            CORRIDORS.forEachIndexed { i, c ->
                // each rate counts up to its value as its row arrives
                val shown = remember { Animatable(if (reduced) 1f else 0f) }
                LaunchedEffect(Unit) { if (!reduced) { delay(120L + i * M.Stagger); shown.animateTo(1f, M.sheet()) } }
                Row(
                    Modifier.fillMaxWidth().height(56.dp).appear(i + 1, sideways = true).clickable { onPick(c) }.padding(horizontal = D.R20),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(c.flag, style = TextStyle(fontFamily = Plex, fontSize = 20.sp),
                        modifier = Modifier.sharedElement(rememberSharedContentState("flag-${c.code}"), visibility, boundsTransform = { _, _ -> M.sheet() }))
                    Spacer(Modifier.width(D.Side))
                    Text(c.country, style = T.body, color = C.Ink, modifier = Modifier.weight(1f))
                    Text("%.2f".format(c.rate * shown.value.coerceIn(0f, 1f).let { if (it > 0.999f) 1f else it }), style = T.bodyM.copy(fontWeight = FontWeight.Bold), color = C.Ink)
                    Text(" ${c.code}", style = T.small, color = C.Ink2)
                }
            }
            Box(
                Modifier.padding(horizontal = D.Side, vertical = 10.dp).fillMaxWidth().height(D.Touch).background(Indigo, RoundedCornerShape(D.R12))
                    .pressable { onPick(CORRIDORS.first()) }.testTag("send_money"),
                contentAlignment = Alignment.Center,
            ) { Text("Send Money", style = T.button, color = Color.White) }
            Text("Rates are indicative and confirmed before you pay. *Delivery times may vary.", style = T.small,
                color = C.Ink2, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(horizontal = D.Side, vertical = 6.dp))
            Row(Modifier.fillMaxWidth().padding(top = D.GridGap).background(Color(0xFFFBF2DE)).padding(vertical = 14.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("Powered by ", style = T.small, color = C.Ink2)
                Text("Demo Cart Exchange", style = T.smallM.copy(fontWeight = FontWeight.Bold), color = C.Money)
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Converter(shared: SharedTransitionScope, visibility: AnimatedVisibilityScope, c: Corridor, onBack: () -> Unit) = with(shared) {
    var amount by remember { mutableStateOf("100.00") }
    var notice by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val aed = amount.replace(",", "").toDoubleOrNull() ?: 0.0
    val target = aed * c.rate
    // The amount they get rolls from the old value to the new one; at rest it is exactly the new value.
    var from by remember { mutableDoubleStateOf(target) }
    var to by remember { mutableDoubleStateOf(target) }
    val roll = remember { Animatable(1f) }
    var turns by remember { mutableIntStateOf(0) }
    LaunchedEffect(target) {
        if (target != to) {
            from = from + (to - from) * roll.value
            to = target
            turns++
            roll.snapTo(0f); roll.animateTo(1f, M.bar())
        }
    }
    val shownAmount = if (roll.value >= 0.999f) to else from + (to - from) * roll.value
    val spin by animateFloatAsState(turns * 180f, M.pop(), label = "swap")
    val ring = remember { Animatable(0f) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(D.Side)) {
        Column(
            Modifier.fillMaxWidth()
                .sharedBounds(rememberSharedContentState("money-card"), visibility, boundsTransform = { _, _ -> M.sheet() })
                .clip(RoundedCornerShape(D.R16)).background(Color.White).border(1.dp, C.Line, RoundedCornerShape(D.R16)).padding(D.Side),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(D.Touch).clip(CircleShape).clickable(onClick = onBack), contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back to rates", tint = C.Ink, modifier = Modifier.size(22.dp))
                }
                Text("Send money to ${c.country}", style = T.bodyM.copy(fontWeight = FontWeight.Bold), color = C.Ink, modifier = Modifier.weight(1f))
                Text("AED 1 = ${c.code} %.2f".format(c.rate), style = T.small, color = C.Ink2)
            }
            Spacer(Modifier.height(D.GridGap))
            AmountRow("From", "AED", { Text("🇦🇪", style = TextStyle(fontFamily = Plex, fontSize = 18.sp)) }) {
                BasicTextField(amount, { amount = it.filter { ch -> ch.isDigit() || ch == '.' }.take(9) }, singleLine = true,
                    textStyle = TextStyle(fontFamily = Plex, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = C.Ink, textAlign = TextAlign.End),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth().testTag("money_amount"))
            }
            Box(Modifier.fillMaxWidth().height(32.dp), contentAlignment = Alignment.Center) {
                Box(Modifier.size(32.dp).graphicsLayer { rotationZ = spin }.background(IndigoSoft, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.SwapVert, null, tint = Indigo, modifier = Modifier.size(18.dp))
                }
            }
            AmountRow("To", c.code, {
                Text(c.flag, style = TextStyle(fontFamily = Plex, fontSize = 18.sp),
                    modifier = Modifier.sharedElement(rememberSharedContentState("flag-${c.code}"), visibility, boundsTransform = { _, _ -> M.sheet() }))
            }) {
                Text(fmt(shownAmount), style = TextStyle(fontFamily = Plex, fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End), color = C.Ink,
                    modifier = Modifier.fillMaxWidth().testTag("money_result"))
            }
            Spacer(Modifier.height(D.Side))
            Box(
                Modifier.fillMaxWidth().height(52.dp)
                    // a ring runs out from the button once when it is pressed
                    .drawBehind {
                        if (ring.value > 0f && ring.value < 1f) {
                            val grow = 14.dp.toPx() * ring.value
                            drawRoundRect(Indigo.copy(alpha = (1f - ring.value) * 0.55f), Offset(-grow, -grow),
                                androidx.compose.ui.geometry.Size(size.width + grow * 2, size.height + grow * 2),
                                androidx.compose.ui.geometry.CornerRadius(D.R12.toPx() + grow), style = Stroke(3.dp.toPx()))
                        }
                    }
                    .background(Indigo, RoundedCornerShape(D.R12))
                    .pressable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        scope.launch { ring.snapTo(0.01f); ring.animateTo(1f, tween(520, easing = FastOutSlowInEasing)) }
                        notice = true
                    },
                contentAlignment = Alignment.Center,
            ) { Text("Send Money", style = T.button, color = Color.White) }
            AnimatedVisibility(notice, enter = expandVertically(M.bar()) + fadeIn(), exit = shrinkVertically(M.bar()) + fadeOut()) {
                Text("Demo app: transfers are not sent. This is where a real app would confirm the recipient and payment.",
                    style = T.small, color = C.Danger, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 10.dp))
            }
            Spacer(Modifier.height(D.Side))
            Row {
                Column(Modifier.weight(1f)) { Text("Fee", style = T.smallM, color = C.Ink); Text("No fees", style = T.small, color = C.Brand) }
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) { Text("ETA", style = T.smallM, color = C.Ink); Text("Within 30 minutes*", style = T.small, color = C.Ink2) }
            }
        }
    }
}

@Composable
private fun AmountRow(label: String, code: String, flag: @Composable () -> Unit, value: @Composable () -> Unit) {
    Column(Modifier.fillMaxWidth().border(1.dp, C.Line, RoundedCornerShape(D.R12)).padding(horizontal = D.Side, vertical = 10.dp)) {
        Text(label, style = T.small, color = C.Ink2)
        Row(verticalAlignment = Alignment.CenterVertically) {
            flag()
            Spacer(Modifier.width(6.dp))
            Text(code, style = T.bodyM.copy(fontWeight = FontWeight.Bold), color = C.Ink)
            Spacer(Modifier.width(D.Side))
            Box(Modifier.weight(1f)) { value() }
        }
    }
}
