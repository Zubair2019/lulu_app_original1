package com.freshcart.shop.ui

import androidx.compose.animation.AnimatedContent
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

/** White round "+" that turns into a − qty + stepper once the item is in the cart. */
@Composable
fun AddButton(state: AppState, p: Product, modifier: Modifier = Modifier, height: Dp = 32.dp) {
    val q = state.qty(p.id)
    val haptic = LocalHapticFeedback.current
    AnimatedContent(q > 0, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "add", modifier = modifier) { inCart ->
        if (!inCart) {
            Box(
                Modifier.size(height).shadow(3.dp, CircleShape).background(Color.White, CircleShape)
                    .testTag("add_${p.id}")
                    .clickable { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); state.add(p.id) },
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Rounded.Add, "Add to cart", tint = C.Ink, modifier = Modifier.size(20.dp)) }
        } else {
            Row(
                Modifier.height(height).shadow(3.dp, RoundedCornerShape(50)).background(C.Brand, RoundedCornerShape(50)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(height).clickable { state.remove(p.id) }, contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Remove, "Remove one", tint = Color.White, modifier = Modifier.size(18.dp))
                }
                Text("${state.qty(p.id)}", style = T.price, color = Color.White, modifier = Modifier.wrapContentWidth())
                Box(Modifier.size(height).clickable { state.add(p.id) }, contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Add, "Add one", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

/** Product card used by every rail and grid: 117 dp wide, 115 dp image well, badge, + button, 2-line name, price. */
@Composable
fun ProductCard(state: AppState, p: Product, onOpen: (Product) -> Unit, modifier: Modifier = Modifier, width: Dp? = 117.dp) {
    Column(
        modifier.then(if (width != null) Modifier.width(width) else Modifier.fillMaxWidth()).background(Color.White, RoundedCornerShape(8.dp))
            .border(1.dp, C.Line.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .pressable { onOpen(p) }.padding(1.dp).testTag("card_${p.id}"),
    ) {
        Box(Modifier.fillMaxWidth().aspectRatio(0.996f).clip(RoundedCornerShape(7.dp)).background(C.Card)) {
            Image(painterResource(p.image), p.name, contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 10.dp))
            if (p.offPercent > 0) {
                // measured: white pill at the well's top-left, text 14.5 dp from the well's left and 6.9 dp from its top
                Text("${p.offPercent}% OFF", style = T.badge.copy(lineHeight = 16.sp), color = C.OffBlue,
                    modifier = Modifier.padding(start = 6.5.dp, top = 5.dp).background(Color.White, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 1.6.dp))
            }
            AddButton(state, p, Modifier.align(Alignment.BottomEnd).padding(end = 5.8.dp, bottom = 6.2.dp))
        }
        // measured: name 5.5 dp under the well, 2 lines of 16 dp; price row 8.3 dp under the name, 6.6 dp to the bottom
        Text(p.name, style = T.cardName, color = C.Ink, maxLines = 2, minLines = 2, overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 5.9.dp, end = 5.9.dp, top = 5.5.dp))
        // measured: symbol 7.3 dp in, price 1.8 dp after it, old price right after the price
        Row(Modifier.padding(start = 6.3.dp, top = 8.3.dp, bottom = 5.6.dp), verticalAlignment = Alignment.CenterVertically) {
            Price(p.price, T.price, C.Ink, 9.5.dp, gap = 1.8.dp)
            if (p.oldPrice > p.price) OldPrice(p.oldPrice)
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
fun ProductRail(state: AppState, items: List<Product>, onOpen: (Product) -> Unit, modifier: Modifier = Modifier) {
    LazyRow(modifier, contentPadding = PaddingValues(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items, key = { it.id }) { ProductCard(state, it, onOpen) }
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
