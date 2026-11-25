package com.freshcart.shop.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshcart.shop.data.AppState
import com.freshcart.shop.data.Product
import kotlinx.coroutines.delay

private fun heavy(size: Int, italic: Boolean = true) =
    TextStyle(fontSize = size.sp, lineHeight = (size * 0.95).sp, fontWeight = FontWeight.Black, fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal)

/** Slim campaign strip: the name of the season on a dark slab, the headline discount beside it, and a way in. 64 dp. */
@Composable
fun CampaignStrip(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().height(64.dp).pressable(onClick).padding(horizontal = D.Side), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.rotate(-4f).background(C.Slab, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 2.dp)) {
            Text("MEGA WEEK", style = heavy(20), color = Color.White)
        }
        Spacer(Modifier.width(12.dp))
        Text("up to ", style = heavy(15), color = C.Accent)
        Text("70% OFF", style = heavy(26), color = C.Accent)
        Spacer(Modifier.weight(1f))
        Box(Modifier.size(28.dp).background(Color.White, CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.AutoMirrored.Rounded.ArrowForward, "Shop the deals", tint = C.Ink, modifier = Modifier.size(16.dp))
        }
    }
}

data class Tile(val title: String, val image: Int, val onClick: () -> Unit)

/** Offer tiles under the campaign strip (96 x 114 dp, 16 dp corners): title on top, picture below. They arrive one after another. */
@Composable
fun OfferTiles(tiles: List<Tile>, modifier: Modifier = Modifier) {
    LazyRow(modifier.fillMaxWidth(), contentPadding = PaddingValues(horizontal = D.Side), horizontalArrangement = Arrangement.spacedBy(D.GridGap)) {
        itemsIndexed(tiles) { i, t ->
            Column(
                Modifier.appear(i, "tile-${t.title}", sideways = true).width(D.TileW).height(D.TileH)
                    .background(Color.White, RoundedCornerShape(D.R16)).pressable(t.onClick),
            ) {
                Text(t.title, style = T.tile, color = C.Ink, maxLines = 2, minLines = 2, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp))
                Image(painterResource(t.image), null, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxWidth().weight(1f).padding(8.dp))
            }
        }
    }
}

/** The rounded top edge of the light page, drawn over the store colour so the page seems to rise out of the header. */
@Composable
fun SheetCap(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().height(D.R24).background(C.Page, RoundedCornerShape(topStart = D.R24, topEnd = D.R24)))
}

/** Section heading with an optional "See all". The action is 48 dp tall to the touch. */
@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier, action: String? = null, onAction: () -> Unit = {}) {
    Row(modifier.fillMaxWidth().padding(start = D.Side, end = 8.dp, top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = T.h2, color = C.Ink, modifier = Modifier.weight(1f))
        if (action != null) {
            Row(Modifier.height(D.Touch).clip(RoundedCornerShape(D.R12)).clickable(onClick = onAction).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(action, style = T.smallM, color = C.Brand)
                Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = C.Brand, modifier = Modifier.size(16.dp))
            }
        } else Spacer(Modifier.height(D.Touch))
    }
}

/**
 * A shelf of deals inside one soft card: a header (badge, title, the promise that goes with it, "View all") over a
 * row of products that scrolls sideways. The products arrive one after another the first time the shelf shows.
 */
@Composable
fun DealShelf(
    state: AppState, title: String, subtitle: String, items: List<Product>, onOpen: (Product) -> Unit, onViewAll: () -> Unit,
    origin: String, modifier: Modifier = Modifier,
) {
    Column(
        modifier.padding(horizontal = D.GridGap).fillMaxWidth().background(C.BrandSoft, RoundedCornerShape(D.R20))
            .border(1.dp, C.Line, RoundedCornerShape(D.R20)).testTag("shelf_$origin"),
    ) {
        Row(Modifier.fillMaxWidth().padding(start = D.GridGap, end = 4.dp, top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            BoltBadge(36.dp)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = T.h2, color = C.Ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, style = T.small, color = C.Ink2, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Row(Modifier.height(D.Touch).clip(RoundedCornerShape(D.R12)).clickable(onClick = onViewAll).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("View all", style = T.smallM, color = C.Brand)
                Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = C.Brand, modifier = Modifier.size(16.dp))
            }
        }
        CompositionLocalProvider(LocalHeroOrigin provides origin) {
            LazyRow(contentPadding = PaddingValues(start = D.GridGap, end = D.GridGap, top = 4.dp, bottom = D.GridGap), horizontalArrangement = Arrangement.spacedBy(D.GridGap)) {
                itemsIndexed(items, key = { _, p -> p.id }) { i, p ->
                    ProductCard(state, p, onOpen, Modifier.appear(i, "$origin-${p.id}", sideways = true), width = D.DishW)
                }
            }
        }
    }
}

/** "What are you shopping for?": round pictures with a label under each, scrolling sideways. Each opens its aisle. */
@Composable
fun MindRow(title: String, items: List<Tile>, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        SectionHeader(title)
        LazyRow(contentPadding = PaddingValues(horizontal = D.GridGap), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            itemsIndexed(items) { i, t ->
                Column(Modifier.appear(i, "mind-${t.title}").width(84.dp).pressable(t.onClick), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painterResource(t.image), null, contentScale = ContentScale.Crop, modifier = Modifier.size(72.dp).clip(CircleShape).background(C.Card))
                    Spacer(Modifier.height(8.dp))
                    Text(t.title, style = T.smallM, color = C.Ink, maxLines = 2, minLines = 2, textAlign = TextAlign.Center, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

/**
 * Big list card for one aisle: its picture on the left with the best offer written over the bottom, and on the
 * right its name, how much is in it, what it holds and when it can arrive.
 */
@Composable
fun AisleCard(name: String, image: Int, offer: Int, facts: String, holds: String, promise: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().pressable(onClick).padding(horizontal = D.Side, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(128.dp).clip(RoundedCornerShape(D.R16)).background(C.Card)) {
            Image(painterResource(image), null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            if (offer > 0) {
                Column(Modifier.align(Alignment.BottomStart).fillMaxWidth().background(Brush.verticalGradient(listOf(Color.Transparent, C.Scrim, C.Slab))).padding(start = 10.dp, top = 20.dp, bottom = 8.dp)) {
                    Text("UP TO", style = T.cardUnit.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    Text("$offer% OFF", style = heavy(20, false), color = Color.White)
                }
            }
        }
        Spacer(Modifier.width(D.Side))
        Column(Modifier.weight(1f)) {
            Text(name, style = T.h2, color = C.Ink, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp))
            Text(facts, style = T.smallM, color = C.Ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp))
            Text(holds, style = T.small, color = C.Ink2, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp))
            Text(promise, style = T.small, color = C.Brand, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

/** Rotating coupon strip: "Flat Đ10 Off | USE CODE XXX", changes every 2.5 s with a vertical slide. */
@Composable
fun CouponStrip(codes: List<Pair<Int, String>>, modifier: Modifier = Modifier) {
    var i by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) { while (true) { delay(2500); i = (i + 1) % codes.size } }
    Box(
        modifier.fillMaxWidth().padding(horizontal = D.Side).height(56.dp).clip(RoundedCornerShape(D.R16))
            .background(Brush.horizontalGradient(listOf(Color(0xFFE00403), Color(0xFFF5141B), Color(0xFFE00403)))).testTag("coupon_strip"),
    ) {
        AnimatedContent(i, transitionSpec = { (slideInVertically(tween(350)) { it } + fadeIn()) togetherWith (slideOutVertically(tween(350)) { -it } + fadeOut()) }, label = "coupon") { k ->
            val (amount, code) = codes[k]
            Row(Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Flat", style = TextStyle(fontFamily = Plex, fontSize = 13.sp, fontWeight = FontWeight.Bold, lineHeight = 13.sp), color = Color.White)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Dirham(26.dp, Color.White, Modifier.padding(bottom = 4.dp))
                        Text("$amount", style = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.Black, lineHeight = 34.sp), color = Color.White)
                        Text(" Off", style = TextStyle(fontFamily = Plex, fontSize = 12.sp, fontWeight = FontWeight.Medium), color = Color.White, modifier = Modifier.padding(bottom = 4.dp))
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("USE CODE", style = TextStyle(fontFamily = Plex, fontSize = 10.sp, fontWeight = FontWeight.Medium), color = Color.White)
                    Box(Modifier.background(C.Accent, RoundedCornerShape(4.dp)).padding(horizontal = 12.dp, vertical = 2.dp)) {
                        Text(code, style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Black), color = C.Ink)
                    }
                }
            }
        }
    }
}

/** Dark cashback banner with a string of lights and a strip of offer chips. */
@Composable
fun CashbackBanner(state: AppState, items: List<Product>, onOpen: (Product) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().padding(horizontal = D.Side, vertical = 8.dp).height(240.dp).clip(RoundedCornerShape(D.R20)).background(Brush.verticalGradient(listOf(Color(0xFF15192B), Color(0xFF242A44), Color(0xFFD7262B))))) {
        Canvas(Modifier.fillMaxWidth().height(14.dp)) {
            val n = 26
            for (k in 0 until n) {
                val x = size.width * (k + 0.5f) / n
                drawCircle(if (k % 2 == 0) Color(0xFFFFD54F) else Color(0xFFFFF3C4), 2.2f * density, Offset(x, size.height * 0.6f))
            }
        }
        Column(Modifier.fillMaxWidth().weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("FLAT ", style = heavy(20, false), color = C.Accent)
                Dirham(18.dp, C.Accent)
                Text(" 500", style = heavy(20, false), color = C.Accent)
            }
            Text("MEGA", style = heavy(38, false), color = Color.White)
            Text("CASHBACK", style = heavy(38, false), color = C.Accent)
            Spacer(Modifier.height(6.dp))
            Box(Modifier.background(Color.White, RoundedCornerShape(50)).padding(horizontal = 12.dp, vertical = 3.dp)) {
                Text("with partner bank cards · T&C apply", style = T.tiny, color = C.Ink)
            }
        }
        LazyRow(Modifier.fillMaxWidth().height(54.dp), contentPadding = PaddingValues(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items, key = { it.id }) { p ->
                Row(
                    Modifier.width(117.dp).height(52.dp).background(Color.White, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)).pressable { onOpen(p) }.padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("${p.offPercent}% OFF", style = T.badge, color = C.OffBlue)
                        Price(p.price, T.smallM, C.Ink, 8.dp)
                    }
                    Image(painterResource(p.image), null, Modifier.size(38.dp), contentScale = ContentScale.Fit)
                }
            }
        }
    }
}

/** Photo-and-headline promo banner (own artwork): headline + subtitle on a gradient with product cut-outs. */
@Composable
fun PromoBanner(
    kicker: String, headline: String, subtitle: String, colors: List<Color>, images: List<Int>,
    modifier: Modifier = Modifier, height: Dp = 150.dp, dark: Boolean = false, onClick: () -> Unit = {},
) {
    val ink = if (dark) C.Ink else Color.White
    Box(modifier.fillMaxWidth().padding(horizontal = D.Side, vertical = 8.dp).height(height).clip(RoundedCornerShape(D.R20)).background(Brush.linearGradient(colors)).pressable(onClick)) {
        Row(Modifier.fillMaxSize().padding(start = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1.1f)) {
                Text(kicker, style = heavy(18), color = if (dark) C.Danger else C.Accent)
                Text(headline, style = heavy(36), color = if (dark) C.Danger else C.Accent)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(subtitle, style = TextStyle(fontFamily = Plex, fontSize = 15.sp, fontWeight = FontWeight.SemiBold), color = ink)
                    Spacer(Modifier.width(6.dp))
                    Box(Modifier.size(16.dp).background(if (dark) C.Danger else Color.White.copy(alpha = 0.9f), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowForward, null, tint = if (dark) Color.White else C.Ink, modifier = Modifier.size(11.dp))
                    }
                }
            }
            Box(Modifier.weight(1f).fillMaxHeight()) {
                images.take(3).forEachIndexed { k, img ->
                    Image(
                        painterResource(img), null, contentScale = ContentScale.Fit,
                        modifier = Modifier.align(Alignment.BottomEnd).offset(x = (-58 * k).dp, y = (-6 + 8 * k).dp)
                            .size((112 - 18 * k).dp).clip(RoundedCornerShape(10.dp)),
                    )
                }
            }
        }
    }
}

/** A titled section holding a two-row grid of product cards that scrolls sideways. */
@Composable
fun GridSection(
    state: AppState, title: String, items: List<Product>, onOpen: (Product) -> Unit, onViewAll: () -> Unit,
    modifier: Modifier = Modifier, origin: String = title,
) {
    Column(modifier.fillMaxWidth().background(C.Page).padding(bottom = 8.dp)) {
        SectionHeader(title, action = "See all", onAction = onViewAll)
        CompositionLocalProvider(LocalHeroOrigin provides origin) {
            LazyHorizontalGrid(
                rows = GridCells.Fixed(2), modifier = Modifier.fillMaxWidth().height(D.CardH * 2 + D.GridGap),
                contentPadding = PaddingValues(horizontal = D.Side), horizontalArrangement = Arrangement.spacedBy(D.GridGap),
                verticalArrangement = Arrangement.spacedBy(D.GridGap),
            ) {
                items(items, key = { it.id }) { ProductCard(state, it, onOpen) }
            }
        }
    }
}

/** Flash sale header with a live countdown chip. */
@Composable
fun FlashSaleHeader(modifier: Modifier = Modifier, endsInMs: Long = 7L * 3600_000 + 2 * 60_000 + 11_000) {
    var left by remember { mutableLongStateOf(endsInMs) }
    LaunchedEffect(Unit) { while (left > 0) { delay(1000); left -= 1000 } }
    val h = left / 3600_000; val m = (left / 60_000) % 60; val s = (left / 1000) % 60
    Box(modifier.fillMaxWidth().padding(horizontal = D.Side, vertical = 8.dp).height(140.dp).clip(RoundedCornerShape(D.R20)).background(C.CampaignDeep).padding(horizontal = 16.dp)) {
        Column(Modifier.align(Alignment.CenterStart).rotate(-4f)) {
            Text("FLASH", style = heavy(44), color = Color.White)
            Box(Modifier.background(Color(0xFF1B1C1E)).padding(horizontal = 10.dp)) { Text("SALE", style = heavy(44), color = C.Accent) }
        }
        Column(Modifier.align(Alignment.CenterEnd).padding(top = 34.dp), horizontalAlignment = Alignment.End) {
            Text("FRESH DEALS", style = heavy(18), color = Color.White)
            Text("EVERY DAY", style = heavy(18), color = C.Accent)
        }
        Row(
            Modifier.align(Alignment.TopEnd).padding(top = 14.dp).background(Color.White, RoundedCornerShape(50)).padding(horizontal = 10.dp, vertical = 4.dp)
                .testTag("flash_timer"),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.Timer, null, tint = C.Danger, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text("Ends in ", style = T.small, color = C.Ink2)
            Text("${h}h ${m}m ${s}s", style = T.smallM.copy(fontWeight = FontWeight.Bold), color = C.Ink)
        }
    }
}

/** Auto-advancing banner carousel with peeking neighbours and a dot indicator. */
@Composable
fun BannerCarousel(slides: List<@Composable () -> Unit>, modifier: Modifier = Modifier) {
    val pager = rememberPagerState { slides.size }
    LaunchedEffect(pager) {
        while (true) { delay(3500); pager.animateScrollToPage((pager.currentPage + 1) % slides.size) }
    }
    Column(modifier.fillMaxWidth().background(C.Page).padding(vertical = 12.dp)) {
        HorizontalPager(pager, contentPadding = PaddingValues(start = D.Side, end = 60.dp), pageSpacing = D.GridGap, modifier = Modifier.height(140.dp)) { k ->
            Box(Modifier.fillMaxSize().clip(RoundedCornerShape(D.R20))) { slides[k]() }
        }
        Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.Center) {
            repeat(slides.size) { k ->
                val sel = k == pager.currentPage
                Box(Modifier.padding(horizontal = 2.5.dp).width(if (sel) 16.dp else 6.dp).height(6.dp).background(if (sel) C.Ink else Color(0xFFCCCCCC), RoundedCornerShape(3.dp)))
            }
        }
    }
}

/** Small promo slide used in the carousel. */
@Composable
fun SlideCard(kicker: String, big: String, sub: String, dates: String, colors: List<Color>, image: Int) {
    Box(Modifier.fillMaxSize().background(Brush.linearGradient(colors))) {
        Column(Modifier.padding(12.dp).fillMaxHeight(), verticalArrangement = Arrangement.Center) {
            Box(Modifier.background(Color(0xFF1B1C1E), RoundedCornerShape(2.dp)).padding(horizontal = 5.dp, vertical = 1.dp)) {
                Text(kicker, style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic), color = Color.White)
            }
            Text("UP TO", style = heavy(14), color = Color.White)
            Text(big, style = heavy(36), color = Color.White)
            Text(sub, style = TextStyle(fontFamily = Plex, fontSize = 10.sp, fontWeight = FontWeight.Bold), color = C.Accent)
            Spacer(Modifier.height(6.dp))
            Text(dates, style = T.tiny, color = Color.White)
        }
        Image(painterResource(image), null, contentScale = ContentScale.Fit, modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp).size(120.dp))
    }
}

@Composable
fun BoltBadge(size: Dp = 46.dp) {
    Box(Modifier.size(size).background(Color(0xFFC5F301), RoundedCornerShape(size / 3)), contentAlignment = Alignment.Center) {
        Icon(Icons.Rounded.Bolt, null, tint = C.Ink, modifier = Modifier.size(size * 0.6f))
    }
}
