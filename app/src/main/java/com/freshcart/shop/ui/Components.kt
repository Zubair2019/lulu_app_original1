package com.freshcart.shop.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshcart.shop.data.AppState
import com.freshcart.shop.data.Product
import java.text.DecimalFormat

private val money = DecimalFormat("#,##0.00")
fun fmt(v: Double): String = money.format(v)

/** The dirham sign, drawn (a D with two bars) so it renders on every phone. */
@Composable
fun Dirham(size: Dp, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier.size(width = size * 1.05f, height = size)) {
        val w = this.size.width; val h = this.size.height; val sw = h * 0.13f
        val left = w * 0.22f
        drawLine(color, Offset(left, h * 0.08f), Offset(left, h * 0.92f), sw, StrokeCap.Butt)
        drawArc(color, -90f, 180f, false, topLeft = Offset(left - w * 0.36f, h * 0.08f),
            size = androidx.compose.ui.geometry.Size(w * 1.0f, h * 0.84f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(sw))
        drawLine(color, Offset(0f, h * 0.38f), Offset(w * 0.86f, h * 0.38f), sw * 0.8f)
        drawLine(color, Offset(0f, h * 0.62f), Offset(w * 0.86f, h * 0.62f), sw * 0.8f)
    }
}

@Composable
fun Price(value: Double, style: TextStyle = T.price, color: Color = C.Ink, symbol: Dp = 10.dp, modifier: Modifier = Modifier, gap: Dp = 2.dp) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Dirham(symbol, color)
        Spacer(Modifier.width(gap))
        Text(fmt(value), style = style, color = color, maxLines = 1, softWrap = false)
    }
}

@Composable
fun OldPrice(value: Double, size: TextUnit = 8.7.sp) {
    Text(fmt(value), style = TextStyle(fontFamily = Plex, fontSize = size, lineHeight = size * 1.3f), color = C.Strike,
        textDecoration = TextDecoration.LineThrough, maxLines = 1, softWrap = false, overflow = TextOverflow.Clip)
}

/** Scale-down-on-press feedback used by every tappable card and button. */
@Composable
fun Modifier.pressable(onClick: () -> Unit): Modifier {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val s by animateFloatAsState(if (pressed) 0.96f else 1f, spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium), label = "press")
    return this.scale(s).clickable(interactionSource = src, indication = null, onClick = onClick)
}

/** How the add control is drawn: a plus on the picture, the word ADD on an aisle card, or the wide button of the product sheet. */
enum class AddKind(val height: Dp, val closed: Dp, val open: Dp) {
    Plus(34.dp, 34.dp, 88.dp), Word(36.dp, 64.dp, 88.dp), Large(44.dp, 112.dp, 112.dp)
}

/**
 * Add control that opens into a − qty + stepper once the item is in the cart. The shape springs wider with a little
 * overshoot, the fill turns to the brand colour and the count rolls. The drawn shape is [AddKind.height] tall; the
 * touch area is 48 dp.
 */
@Composable
fun AddButton(state: AppState, p: Product, modifier: Modifier = Modifier, kind: AddKind = AddKind.Plus, tag: String = "add_${p.id}") {
    val q = state.qty(p.id)
    val inCart = q > 0
    val haptic = LocalHapticFeedback.current
    val w by animateDpAsState(if (inCart) kind.open else kind.closed, M.pop(), label = "addW")
    val t by animateFloatAsState(if (inCart) 1f else 0f, M.snap(), label = "addT")   // 0 = add, 1 = stepper
    val fill by animateColorAsState(if (inCart) C.Brand else Color.White, M.snap(), label = "addFill")
    val shape = RoundedCornerShape(if (kind == AddKind.Word) 10.dp else D.R12)
    val tick = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) }
    Box(modifier.height(D.Touch).width(maxOf(w, D.Touch)), contentAlignment = Alignment.CenterEnd) {
        Box(
            Modifier.width(w).height(kind.height).shadow(if (kind == AddKind.Plus) 3.dp else 0.dp, shape).background(fill, shape)
                .then(if (kind == AddKind.Plus) Modifier else Modifier.border(1.dp, C.Brand, shape)),
        )
        if (!inCart) {
            Box(Modifier.matchParentSize().testTag(tag).clickable(remember { MutableInteractionSource() }, null) { tick(); state.add(p.id) }) {
                Box(Modifier.align(Alignment.CenterEnd).width(kind.closed).height(kind.height).graphicsLayer { alpha = 1f - t }, contentAlignment = Alignment.Center) {
                    if (kind == AddKind.Plus) Icon(Icons.Rounded.Add, "Add to cart", tint = C.Ink, modifier = Modifier.size(20.dp))
                    else Text("ADD", style = if (kind == AddKind.Large) T.button else T.smallM.copy(fontWeight = FontWeight.Bold), color = C.Brand)
                }
            }
        } else {
            Row(Modifier.width(w).fillMaxHeight().graphicsLayer { alpha = t }, verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f).fillMaxHeight().clickable(remember { MutableInteractionSource() }, null) { tick(); state.remove(p.id) }, contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Remove, "Remove one", tint = Color.White, modifier = Modifier.size(18.dp))
                }
                RollingText("$q", q.toDouble(), T.price, Color.White)
                Box(Modifier.weight(1f).fillMaxHeight().clickable(remember { MutableInteractionSource() }, null) { tick(); state.add(p.id) }, contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Add, "Add one", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

/**
 * Product card used by every rail and grid. No box around it: a square picture well with rounded corners, the offer
 * badge top-left and the add control bottom-right on the picture; then the name on two lines, the pack size on its
 * own line, and the price. 117 dp wide by default ([D.CardH] tall).
 */
@Composable
fun ProductCard(state: AppState, p: Product, onOpen: (Product) -> Unit, modifier: Modifier = Modifier, width: Dp? = 117.dp) {
    val hero = "${LocalHeroOrigin.current}-${p.id}"
    Column(
        modifier.then(if (width != null) Modifier.width(width) else Modifier.fillMaxWidth())
            .pressable { state.pendingHero = hero; onOpen(p) }.testTag("card_${p.id}"),
    ) {
        Box(Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(D.R16)).background(C.Card)) {
            // the picture travels to the product sheet and back
            Image(painterResource(p.image), p.name, contentScale = ContentScale.Fit,
                modifier = Modifier.hero("img-$hero", !state.heroOpen(hero)).fillMaxSize().padding(10.dp))
            if (p.offPercent > 0) {
                Text("${p.offPercent}% OFF", style = T.badge.copy(lineHeight = 16.sp), color = C.OffBlue,
                    modifier = Modifier.padding(start = 6.dp, top = 6.dp).background(Color.White, RoundedCornerShape(D.R12))
                        .padding(horizontal = 8.dp, vertical = 2.dp))
            }
            AddButton(state, p, Modifier.align(Alignment.BottomEnd).padding(end = 6.dp))
        }
        Text(p.name, style = T.cardName, color = C.Ink, maxLines = 2, minLines = 2, overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 2.dp, end = 2.dp, top = 7.dp))
        // pack size on its own line, always in the same place under the name, so sizes can be compared across cards
        // even when a long name is cut short
        Text(p.unit, style = T.cardUnit, color = C.Ink2, maxLines = 1, overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 2.dp, end = 2.dp, top = D.CardUnitGap).testTag("unit_${p.id}"))
        Row(Modifier.padding(start = 2.dp, top = D.CardPriceGap, bottom = 4.4.dp), verticalAlignment = Alignment.CenterVertically) {
            Price(p.price, T.price, C.Ink, 9.5.dp, gap = 1.8.dp)
            if (p.oldPrice > p.price) { Spacer(Modifier.width(4.dp)); OldPrice(p.oldPrice, T.cardUnit.fontSize) }
        }
    }
}

/** "VIEW ALL >" white pill under rails. */
@Composable
fun ViewAll(onClick: () -> Unit, modifier: Modifier = Modifier, bg: Color = Color.White) {
    Row(
        modifier.fillMaxWidth().padding(horizontal = 10.dp).height(34.dp)
            .background(bg, RoundedCornerShape(8.dp)).pressable(onClick).testTag("view_all"),
        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("VIEW ALL", style = T.viewAll, color = C.Ink)
        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = C.Ink, modifier = Modifier.size(16.dp))
    }
}

@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier, color: Color = C.Ink, action: String? = null, onAction: () -> Unit = {}) {
    Row(modifier.fillMaxWidth().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = T.section, color = color, modifier = Modifier.weight(1f))
        if (action != null) {
            Text(action, style = T.smallM, color = C.Brand, modifier = Modifier.clickable(onClick = onAction).padding(4.dp))
        }
    }
}

/** A horizontally scrolling rail of product cards. */
@Composable
fun ProductRail(state: AppState, items: List<Product>, onOpen: (Product) -> Unit, modifier: Modifier = Modifier, origin: String = "rail") {
    CompositionLocalProvider(LocalHeroOrigin provides origin) {
        LazyRow(modifier, contentPadding = PaddingValues(horizontal = D.Side), horizontalArrangement = Arrangement.spacedBy(D.GridGap)) {
            items(items, key = { it.id }) { ProductCard(state, it, onOpen) }
        }
    }
}

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, color: Color = C.Brand) {
    Box(
        modifier.fillMaxWidth().height(48.dp)
            .background(if (enabled) color else C.Line, RoundedCornerShape(12.dp))
            .then(if (enabled) Modifier.pressable(onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) { Text(text, style = T.button, color = if (enabled) Color.White else C.Ink3) }
}

@Composable
fun OutlineButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier.height(40.dp).border(BorderStroke(1.dp, C.Brand), RoundedCornerShape(10.dp)).pressable(onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) { Text(text, style = T.bodyM, color = C.Brand) }
}

@Composable
fun IconCircle(icon: ImageVector, desc: String, onClick: () -> Unit, modifier: Modifier = Modifier, bg: Color = C.Surface, tint: Color = C.Ink) {
    Box(modifier.size(40.dp).background(bg, CircleShape).clip(CircleShape).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Icon(icon, desc, tint = tint, modifier = Modifier.size(22.dp))
    }
}

/** Little count bubble used on the cart icon. */
@Composable
fun CountBubble(n: Int, modifier: Modifier = Modifier) {
    if (n <= 0) return
    AnimatedContent(n, transitionSpec = {
        (slideInVertically { -it } + fadeIn()) togetherWith (slideOutVertically { it } + fadeOut())
    }, label = "count", modifier = modifier) { v ->
        Box(Modifier.offset(0.dp, 0.dp).size(16.dp).background(C.Danger, CircleShape), contentAlignment = Alignment.Center) {
            Text("$v", style = T.tiny.copy(fontSize = 9.sp), color = Color.White)
        }
    }
}
