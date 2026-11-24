package com.freshcart.shop.ui.screens


import com.freshcart.shop.ui.Plex
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshcart.shop.data.AppState
import com.freshcart.shop.data.Catalog
import com.freshcart.shop.data.DEPT_NAMES
import com.freshcart.shop.data.Product
import com.freshcart.shop.data.Route
import com.freshcart.shop.ui.BottomBar
import com.freshcart.shop.ui.C
import com.freshcart.shop.ui.Dirham
import com.freshcart.shop.ui.OldPrice
import com.freshcart.shop.ui.Price
import com.freshcart.shop.ui.ProductCard
import com.freshcart.shop.ui.T
import com.freshcart.shop.ui.fmt
import com.freshcart.shop.ui.open
import com.freshcart.shop.ui.pressable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// ---------------------------------------------------------------- shared bits

@Composable
private fun RoundIcon(icon: ImageVector, desc: String, tint: Color = C.Ink, onClick: () -> Unit) {
    Box(Modifier.size(40.dp).shadow(2.dp, CircleShape).background(Color.White, CircleShape).clip(CircleShape).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Icon(icon, desc, tint = tint, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun Card(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier.padding(horizontal = 12.dp).fillMaxWidth().background(Color.White, RoundedCornerShape(12.dp)).border(1.dp, C.Line.copy(alpha = 0.6f), RoundedCornerShape(12.dp))) { content() }
}

@Composable
private fun Label(text: String) {
    Text(text, style = TextStyle(fontFamily = Plex, fontSize = 11.8.sp, fontWeight = FontWeight.Bold), color = Color(0xFF828283),
        modifier = Modifier.padding(start = 10.dp, top = 16.dp, bottom = 8.dp))
}

@Composable
private fun BlackPill(text: String, modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit) {
    Box(
        modifier.height(40.dp).background(if (enabled) C.Ink else Color(0xFFBDBDBF), RoundedCornerShape(20.dp)).then(if (enabled) Modifier.pressable(onClick) else Modifier).padding(horizontal = 22.dp),
        contentAlignment = Alignment.Center,
    ) { Text(text, style = T.smallM.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp), color = Color.White, maxLines = 1) }
}

@Composable
private fun TopBar(title: String, centered: Boolean, onBack: () -> Unit) {
    Box(Modifier.fillMaxWidth().background(Color(0xFFF6F6F6)).statusBarsPadding().height(48.dp)) {
        Row(Modifier.align(Alignment.CenterStart).padding(start = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(36.dp).then(if (centered) Modifier.border(1.dp, C.Line, CircleShape) else Modifier).clip(CircleShape).clickable(onClick = onBack), contentAlignment = Alignment.Center) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = C.Ink, modifier = Modifier.size(20.dp))
            }
            if (!centered) { Spacer(Modifier.width(6.dp)); Text(title, style = T.h3.copy(fontSize = 15.9.sp, fontWeight = FontWeight.SemiBold), color = C.Ink) }
        }
        if (centered) Text(title, style = TextStyle(fontFamily = Plex, fontSize = 16.sp, fontWeight = FontWeight.Normal), color = C.Ink, modifier = Modifier.align(Alignment.Center))
    }
}

// ---------------------------------------------------------------- Product page

@Composable
fun ProductScreen(state: AppState, id: String) {
    val p = Catalog.get(id) ?: return
    val similar = remember(id) { Catalog.dept(p.dept).filter { it.id != p.id }.sortedByDescending { it.offPercent }.take(12) }
    var summary by remember { mutableStateOf(true) }
    var description by remember { mutableStateOf(false) }
    val pager = rememberPagerState { 3 }
    Box(Modifier.fillMaxSize().background(Color(0xFFF6F6F6))) {
        LazyColumn(Modifier.fillMaxSize().testTag("pdp_list"), contentPadding = PaddingValues(bottom = 170.dp)) {
            item {
                Column(Modifier.fillMaxWidth().background(Color.White)) {
                    HorizontalPager(pager, Modifier.fillMaxWidth().height(380.dp).statusBarsPadding()) { k ->
                        Box(Modifier.fillMaxSize().padding(top = 44.dp, start = 24.dp, end = 24.dp, bottom = 8.dp), contentAlignment = Alignment.Center) {
                            Image(painterResource(p.image), p.name, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize().padding(if (k == 0) 0.dp else 30.dp))
                        }
                    }
                    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.Center) {
                        repeat(3) { k ->
                            Box(Modifier.padding(horizontal = 2.dp).width(if (k == pager.currentPage) 12.dp else 6.dp).height(6.dp)
                                .background(if (k == pager.currentPage) C.Ink2 else Color(0xFFD6D6D8), RoundedCornerShape(3.dp)))
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                }
            }
            item {
                Column(Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp).fillMaxWidth().shadow(1.dp, RoundedCornerShape(12.dp)).background(Color.White, RoundedCornerShape(12.dp)).padding(13.dp)) {
                    Row(Modifier.clickable { state.go(Route.Listing(p.brand.ifBlank { DEPT_NAMES[p.dept] ?: "More" }, p.dept)) }, verticalAlignment = Alignment.CenterVertically) {
                        Text("Explore more from ${p.brand.ifBlank { DEPT_NAMES[p.dept] }}", style = T.small.copy(fontSize = 12.3.sp, fontWeight = FontWeight.Normal),
                            color = Color(0xFF162A6F), textDecoration = TextDecoration.Underline)
                        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = Color(0xFF1F3C88), modifier = Modifier.size(14.dp))
                    }
                    Spacer(Modifier.height(6.dp))
                    Text("${p.name}${if (p.unit != "1 pc") " " + p.unit else ""}", style = TextStyle(fontFamily = Plex, fontSize = 15.sp, fontWeight = FontWeight.Bold, lineHeight = 21.1.sp), color = C.Ink)
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Price(p.price, T.price.copy(fontSize = 15.sp), C.Ink, 11.dp)
                        if (p.offPercent > 0) {
                            Spacer(Modifier.width(6.dp)); OldPrice(p.oldPrice, 11.6.sp)
                            Spacer(Modifier.width(6.dp)); Text("${p.offPercent}%", style = T.smallM.copy(fontSize = 11.7.sp), color = Color(0xFFDD0C19))
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Star, null, tint = Color(0xFFF5A623), modifier = Modifier.size(14.dp))
                        Text(" %.1f".format(p.rating), style = T.smallM, color = C.Ink)
                        Text("  ·  Free delivery over ", style = T.small, color = C.Ink2)
                        Dirham(8.dp, C.Ink2); Text("100", style = T.small, color = C.Ink2)
                    }
                }
            }
            item {
                Column(Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp).fillMaxWidth().background(Color.White, RoundedCornerShape(12.dp)).animateContentSize()) {
                    Row(Modifier.fillMaxWidth().height(44.dp).clickable { summary = !summary }.padding(horizontal = 13.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Product Summary", style = T.smallM.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp), color = C.Ink, modifier = Modifier.weight(1f))
                        Icon(if (summary) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown, null, tint = C.Ink)
                    }
                    if (summary) {
                        Column(Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp).fillMaxWidth().background(Color(0xFFF6F6F6), RoundedCornerShape(8.dp)).padding(12.dp)) {
                            bullets(p).forEach { b ->
                                Row(Modifier.padding(vertical = 3.dp)) {
                                    Text("•", style = T.small, color = C.Ink, modifier = Modifier.width(12.dp))
                                    Text(b, style = T.small.copy(lineHeight = 17.sp), color = C.Ink)
                                }
                            }
                        }
                    }
                    HorizontalDivider(color = C.Line, modifier = Modifier.padding(horizontal = 10.dp))
                    Column(Modifier.padding(10.dp).fillMaxWidth().background(Color(0xFFF6F6F6), RoundedCornerShape(8.dp)).padding(12.dp)) {
                        Text("Product Information", style = T.smallM.copy(fontSize = 11.5.sp), color = C.Ink)
                        listOf("Brand" to p.brand.ifBlank { "Demo Cart Select" }, "Category" to (DEPT_NAMES[p.dept] ?: ""), "Content" to p.unit, "Rating" to "%.1f / 5".format(p.rating)).forEach { (k, v) ->
                            Row(Modifier.padding(top = 14.dp)) {
                                Text(k, style = T.small, color = C.Ink2, modifier = Modifier.width(82.dp))
                                Text(v, style = T.small, color = C.Ink)
                            }
                        }
                    }
                    Row(Modifier.fillMaxWidth().height(44.dp).clickable { description = !description }.padding(horizontal = 13.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Product Description", style = T.smallM.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp), color = C.Ink, modifier = Modifier.weight(1f))
                        Icon(if (description) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown, null, tint = C.Ink)
                    }
                    if (description) Text(p.description, style = T.small.copy(lineHeight = 18.sp), color = C.Ink2, modifier = Modifier.padding(start = 13.dp, end = 13.dp, bottom = 14.dp))
                }
            }
            item {
                Text("Similar Products", style = T.h3.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp), color = C.Ink, modifier = Modifier.padding(start = 12.dp, top = 18.dp, bottom = 10.dp))
                LazyRow(contentPadding = PaddingValues(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(similar, key = { it.id }) { ProductCard(state, it, { s -> state.open(s.id) }) }
                }
            }
        }
        // Floating top buttons
        Row(Modifier.fillMaxWidth().statusBarsPadding().padding(start = 12.dp, end = 12.dp, top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            RoundIcon(Icons.AutoMirrored.Rounded.ArrowBack, "Back") { state.back() }
            Spacer(Modifier.weight(1f))
            val wished = p.id in state.wishlist
            RoundIcon(if (wished) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder, "Save", if (wished) C.Danger else C.Ink) { state.toggleWish(p.id) }
            Spacer(Modifier.width(12.dp))
            RoundIcon(Icons.Outlined.Share, "Share") { }
        }
        // Sticky price bar + bottom navigation
        Column(Modifier.align(Alignment.BottomCenter).navigationBarsPadding()) {
            Row(
                Modifier.padding(horizontal = 12.dp).fillMaxWidth().height(62.dp).shadow(8.dp, RoundedCornerShape(16.dp)).background(Color.White, RoundedCornerShape(16.dp)).padding(start = 13.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Price(p.price, T.price.copy(fontSize = 15.sp), C.Ink, 11.dp)
                    if (p.offPercent > 0) Row(verticalAlignment = Alignment.CenterVertically) {
                        OldPrice(p.oldPrice, 11.6.sp); Spacer(Modifier.width(5.dp))
                        Text("${p.offPercent}%", style = T.smallM.copy(fontSize = 11.7.sp), color = Color(0xFFDD0C19))
                    }
                }
                val q = state.qty(p.id)
                if (q == 0) {
                    BlackPill("ADD TO CART", Modifier.width(129.8.dp).testTag("pdp_add")) { state.add(p.id) }
                } else {
                    Row(Modifier.width(129.8.dp).height(40.dp).background(C.Ink, RoundedCornerShape(20.dp)), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(40.dp).clickable { state.remove(p.id) }, contentAlignment = Alignment.Center) { Icon(Icons.Rounded.Remove, "Remove one", tint = Color.White, modifier = Modifier.size(18.dp)) }
                        Text("$q", style = T.bodyM.copy(fontWeight = FontWeight.Bold), color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                        Box(Modifier.size(40.dp).clickable { state.add(p.id) }, contentAlignment = Alignment.Center) { Icon(Icons.Rounded.Add, "Add one", tint = Color.White, modifier = Modifier.size(18.dp)) }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            BottomBar(state)
        }
    }
}

private fun bullets(p: Product): List<String> {
    val sentences = p.description.split(". ").map { it.trim().trimEnd('.') }.filter { it.length > 3 }
    return (sentences.take(3) + listOfNotNull(
        if (p.unit != "1 pc") "Pack size: ${p.unit}" else null,
        "Delivered by Demo Cart in a scheduled slot of your choice",
    ))
}

// ---------------------------------------------------------------- Cart

@Composable
fun CartScreen(state: AppState) {
    var tab by remember { mutableIntStateOf(0) }
    var couponOpen by remember { mutableStateOf(false) }
    val tabs = listOf("Top Picks" to arrayOf("fresh", "grocery"), "Snacks & Treats" to arrayOf("snacks"), "Drinks" to arrayOf("beverages"), "Home care" to arrayOf("household"))
    Column(Modifier.fillMaxSize().background(Color(0xFFF6F6F6))) {
        TopBar("My Cart", centered = false) { state.back() }
        if (state.count == 0) {
            Column(Modifier.fillMaxWidth().weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("Your cart is empty", style = T.h2, color = C.Ink)
                Spacer(Modifier.height(8.dp))
                Text("Add items from the store to see them here.", style = T.body, color = C.Ink2)
                Spacer(Modifier.height(20.dp))
                BlackPill("START SHOPPING") { state.home() }
            }
            return@Column
        }
        LazyColumn(Modifier.weight(1f).testTag("cart_list"), contentPadding = PaddingValues(bottom = 16.dp)) {
            item {
                Card(Modifier.padding(top = 8.dp)) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Home, null, tint = C.Ink, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Delivering to:", style = T.tiny.copy(fontWeight = FontWeight.Normal), color = C.Ink2)
                            Text("${state.address.label}  ${state.address.line}", style = T.small.copy(fontSize = 11.sp), color = C.Ink, maxLines = 2)
                        }
                        Box(Modifier.border(1.dp, C.Line, RoundedCornerShape(16.dp)).clip(RoundedCornerShape(16.dp)).clickable { }.padding(horizontal = 12.dp, vertical = 6.dp)) {
                            Text("Change", style = T.small.copy(fontSize = 11.sp), color = C.Ink)
                        }
                    }
                }
            }
            item { Label("DELIVERY ITEMS") }
            item {
                Card {
                    Row(Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Timer, null, tint = C.Ink, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Row {
                                Text("Scheduled Delivery", style = T.smallM.copy(fontWeight = FontWeight.Bold, fontSize = 14.1.sp, lineHeight = 18.2.sp), color = C.Ink)
                                Text("  |  ${state.count} Item${if (state.count == 1) "" else "s"}", style = T.small, color = C.Ink2)
                            }
                            Text("Pick your delivery slot at checkout", style = T.small.copy(fontSize = 10.5.sp), color = C.Ink3)
                        }
                    }
                    state.lines.forEach { line ->
                        val p = line.product
                        Row(Modifier.fillMaxWidth().clickable { state.open(p.id) }.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Thumb(p.image, 48.dp)
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text("${p.name} ${if (p.unit != "1 pc") p.unit else ""}".trim(), style = T.small.copy(fontSize = 11.5.sp), color = C.Ink, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Spacer(Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Price(p.price * line.qty, T.smallM.copy(fontWeight = FontWeight.Bold), C.Ink, 8.dp)
                                    if (p.offPercent > 0) { Spacer(Modifier.width(4.dp)); OldPrice(p.oldPrice * line.qty) }
                                }
                            }
                            Spacer(Modifier.width(8.dp))
                            Stepper(line.qty, { state.remove(p.id) }, { state.add(p.id) })
                        }
                    }
                }
            }
            item {
                Row(
                    Modifier.padding(start = 8.dp, end = 8.dp, top = 12.dp).fillMaxWidth().height(50.dp).clip(RoundedCornerShape(8.dp))
                        .background(androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(Color(0xFF0D2346), Color(0xFF1F4E8C)))).padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Outlined.CreditCard, null, tint = C.Accent, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Partner bank card", style = T.smallM, color = Color.White, modifier = Modifier.weight(1f))
                    Box(Modifier.background(C.Accent, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                        Text("Earn up to 7% back", style = T.smallM.copy(fontWeight = FontWeight.Bold), color = C.Ink)
                    }
                }
            }
            item { Label("SAVINGS CORNER") }
            item {
                Card {
                    SavingRow(Icons.Outlined.LocalOffer, if (state.coupon != null) "Coupon ${state.coupon} applied" else "Apply Coupon",
                        if (state.coupon != null) "You save ${fmt(state.couponValue)} on this order" else "Save more with a promo code") { couponOpen = true }
                    HorizontalDivider(color = C.Line, modifier = Modifier.padding(start = 48.dp))
                    SavingRow(Icons.Outlined.CardGiftcard, "Apply Gift Card", "Use your gift card balance") { }
                }
            }
            item {
                LazyRow(Modifier.padding(top = 16.dp), contentPadding = PaddingValues(horizontal = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(tabs) { k, (name, _) ->
                        val sel = k == tab
                        Box(Modifier.height(30.dp).background(if (sel) C.Ink else Color.White, RoundedCornerShape(15.dp)).border(1.dp, if (sel) C.Ink else C.Line, RoundedCornerShape(15.dp))
                            .clip(RoundedCornerShape(15.dp)).clickable { tab = k }.padding(horizontal = 12.dp), contentAlignment = Alignment.Center) {
                            Text(name, style = T.small.copy(fontSize = 11.8.sp, fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal), color = if (sel) Color.White else C.Ink)
                        }
                    }
                }
                Text("Customers also bought", style = T.h3.copy(fontSize = 15.9.sp, fontWeight = FontWeight.Bold), color = C.Ink, modifier = Modifier.padding(start = 12.dp, top = 14.dp, bottom = 8.dp))
                LazyRow(contentPadding = PaddingValues(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(Catalog.dept(*tabs[tab].second).filter { it.id !in state.cart }.take(10), key = { it.id }) { ProductCard(state, it, { p -> state.open(p.id) }, width = 104.dp) }
                }
            }
            item {
                Card(Modifier.padding(top = 14.dp)) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.ReceiptLong, null, tint = C.Ink, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Bill Summary", style = T.bodyM.copy(fontSize = 15.7.sp, fontWeight = FontWeight.Bold), color = C.Ink)
                    }
                    BillRow("Sub Total ( ${state.count} Items )", state.subtotal)
                    if (state.couponValue > 0) BillRow("Coupon ${state.coupon}", -state.couponValue, C.Brand)
                    BillRow("Service Fee", 2.0)
                    HorizontalDivider(color = C.Line, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                    BillRow("Total (Incl. VAT)", (state.subtotal - state.couponValue).coerceAtLeast(0.0) + 2.0, bold = true)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
        StickyTotal((state.subtotal - state.couponValue).coerceAtLeast(0.0) + 2.0, "GO TO CHECKOUT", enabled = true, tag = "go_checkout") { state.go(Route.Checkout) }
    }
    if (couponOpen) CouponSheet(state) { couponOpen = false }
}

@Composable
private fun SavingRow(icon: ImageVector, title: String, sub: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = C.Ink, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = T.smallM.copy(fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 18.2.sp), color = C.Ink)
            Text(sub, style = T.small.copy(fontSize = 11.85.sp), color = C.Ink)
        }
        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = C.Ink)
    }
}

@Composable
private fun BillRow(label: String, value: Double, color: Color = C.Ink, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = if (bold) T.bodyM.copy(fontSize = 15.2.sp, fontWeight = FontWeight.Medium) else T.small.copy(fontSize = 13.7.sp, fontWeight = FontWeight.SemiBold), color = if (bold) C.Ink else Color(0xFF363637), modifier = Modifier.weight(1f))
        if (value < 0) Text("− ", style = T.smallM, color = color)
        Price(kotlin.math.abs(value), if (bold) T.bodyM.copy(fontSize = 15.2.sp, fontWeight = FontWeight.SemiBold) else T.small.copy(fontSize = 13.7.sp, fontWeight = FontWeight.SemiBold), color, 8.dp)
    }
}

@Composable
private fun Stepper(q: Int, onMinus: () -> Unit, onPlus: () -> Unit) {
    Row(Modifier.height(30.dp).border(1.dp, C.Line, RoundedCornerShape(15.dp)), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(30.dp).clip(CircleShape).clickable(onClick = onMinus), contentAlignment = Alignment.Center) {
            Icon(if (q == 1) Icons.Outlined.DeleteOutline else Icons.Rounded.Remove, "Remove one", tint = C.Ink, modifier = Modifier.size(16.dp))
        }
        Text("$q", style = T.smallM, color = C.Ink, textAlign = TextAlign.Center, modifier = Modifier.width(16.dp))
        Box(Modifier.size(30.dp).clip(CircleShape).clickable(onClick = onPlus), contentAlignment = Alignment.Center) {
            Icon(Icons.Rounded.Add, "Add one", tint = C.Ink, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun StickyTotal(total: Double, cta: String, enabled: Boolean, tag: String, warning: String? = null, onClick: () -> Unit) {
    Column(Modifier.fillMaxWidth().shadow(10.dp).background(Color.White).navigationBarsPadding()) {
        if (warning != null) {
            Row(Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp).fillMaxWidth().background(Color(0xFFFFF3E0), RoundedCornerShape(8.dp)).padding(horizontal = 10.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Info, null, tint = Color(0xFFB26A00), modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(6.dp))
                Text(warning, style = T.small.copy(fontSize = 11.sp), color = Color(0xFF8A5300))
            }
        }
        Row(Modifier.fillMaxWidth().height(68.dp).padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Total (Incl. VAT)", style = T.small.copy(fontSize = 12.7.sp), color = C.Hint)
                Price(total, T.h3.copy(fontWeight = FontWeight.SemiBold, fontSize = 17.7.sp), C.Ink, 12.dp)
            }
            Box(
                Modifier.width(150.dp).height(44.dp).background(if (enabled) C.Ink else Color(0xFFC4C4C6), RoundedCornerShape(22.dp))
                    .then(if (enabled) Modifier.pressable(onClick) else Modifier).testTag(tag),
                contentAlignment = Alignment.Center,
            ) { Text(cta, style = T.smallM.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp), color = Color.White) }
        }
    }
}

@Composable
private fun CouponSheet(state: AppState, onClose: () -> Unit) {
    val sheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onClose, sheetState = sheet, containerColor = Color.White) {
        Column(Modifier.padding(horizontal = 16.dp).padding(bottom = 24.dp)) {
            Text("Apply a coupon", style = T.h2, color = C.Ink)
            Spacer(Modifier.height(12.dp))
            listOf("SAVE10" to "Flat 10 off any order", "FRESH15" to "Flat 15 off fresh food").forEach { (code, desc) ->
                val applied = state.coupon == code
                Row(Modifier.fillMaxWidth().padding(vertical = 6.dp).border(1.dp, if (applied) C.Brand else C.Line, RoundedCornerShape(10.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(code, style = T.bodyM.copy(fontWeight = FontWeight.Bold), color = C.Ink)
                        Text(desc, style = T.small, color = C.Ink2)
                    }
                    Text(if (applied) "REMOVE" else "APPLY", style = T.smallM.copy(fontWeight = FontWeight.Bold), color = C.Brand,
                        modifier = Modifier.clickable { state.coupon = if (applied) null else code; onClose() }.padding(6.dp))
                }
            }
        }
    }
}

// ---------------------------------------------------------------- Checkout (stops at payment)

private data class Day(val top: String, val bottom: String)

private fun days(): List<Day> {
    val f = DateTimeFormatter.ofPattern("d MMM"); val d = DateTimeFormatter.ofPattern("EEE")
    val today = LocalDate.now()
    return (0 until 5).map { k -> val day = today.plusDays(k.toLong()); Day(when (k) { 0 -> "Today"; 1 -> "Tomorrow"; else -> day.format(d) }, day.format(f)) }
}

private val TIMES = listOf("8 – 10 AM", "12 – 2 PM", "4 – 6 PM", "8 – 10 PM")

@Composable
fun CheckoutScreen(state: AppState) {
    var slotOpen by remember { mutableStateOf(false) }
    var slot by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var pay by remember { mutableIntStateOf(0) }
    var wallet by remember { mutableStateOf(false) }
    var points by remember { mutableStateOf(false) }
    var noBell by remember { mutableStateOf(false) }
    var door by remember { mutableStateOf(false) }
    var terms by remember { mutableStateOf(false) }
    var cardNotice by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { slotOpen = true }
    val delivery = if (state.subtotal >= 100) 0.0 else 10.0
    val total = (state.subtotal - state.couponValue).coerceAtLeast(0.0) + delivery + 1.0 + 2.0
    val ready = slot != null && terms
    Column(Modifier.fillMaxSize().background(Color(0xFFF6F6F6))) {
        TopBar("Checkout", centered = true) { state.back() }
        LazyColumn(Modifier.weight(1f).testTag("checkout_list"), contentPadding = PaddingValues(top = 6.dp, bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Card {
                    Text("Delivery Address", style = T.bodyM.copy(fontWeight = FontWeight.Bold), color = C.Ink, modifier = Modifier.padding(start = 12.dp, top = 12.dp))
                    Row(Modifier.padding(12.dp).fillMaxWidth().border(1.dp, C.Line, RoundedCornerShape(10.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.LocationOn, null, tint = C.Ink, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Row { Text(state.address.label, style = T.smallM.copy(fontWeight = FontWeight.Bold), color = C.Ink); Text("  +971 50 000 0000", style = T.small, color = C.Ink2) }
                            Text(state.address.line, style = T.small, color = C.Ink2)
                        }
                    }
                }
            }
            item {
                Card {
                    Row(Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Scheduled Delivery: ${state.count} item(s)", style = T.smallM.copy(fontWeight = FontWeight.Bold, fontSize = 12.5.sp), color = C.Ink, modifier = Modifier.weight(1f))
                        Text("Shipment 1", style = T.small.copy(fontSize = 10.5.sp), color = C.Ink2)
                        Icon(Icons.Rounded.KeyboardArrowUp, null, tint = C.Ink2, modifier = Modifier.size(16.dp))
                    }
                    Row(
                        Modifier.padding(12.dp).height(32.dp).background(C.Ink, RoundedCornerShape(16.dp)).pressable { slotOpen = true }.padding(horizontal = 12.dp).testTag("select_slot"),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Outlined.CalendarMonth, null, tint = Color.White, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(6.dp))
                        val label = slot?.let { (d, t) -> "${days()[d].top}, ${TIMES[t]}" } ?: "Select delivery slot"
                        Text(label, style = T.small.copy(fontSize = 11.5.sp), color = Color.White)
                        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    LazyRow(contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.lines) { l ->
                            Column {
                                Box { Thumb(l.product.image, 56.dp); Text("x${l.qty}", style = T.tiny, color = C.Ink, modifier = Modifier.align(Alignment.BottomEnd).padding(3.dp)) }
                                Price(l.product.price * l.qty, T.tiny, C.Ink2, 7.dp, Modifier.padding(top = 3.dp))
                            }
                        }
                    }
                }
            }
            item {
                Card {
                    Text("Pay with", style = T.bodyM.copy(fontWeight = FontWeight.Bold), color = C.Ink, modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 4.dp))
                    PayOption(0, pay, "Credit/Debit Card", Icons.Outlined.CreditCard) { pay = 0 }
                    AnimatedVisibility(pay == 0, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                        Column(Modifier.padding(horizontal = 24.dp)) {
                            FakeField("Card number", "1234 5678 9012 3456")
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                FakeField("Expiry", "MM/YY", Modifier.weight(1f)); FakeField("CVV", "Secure Code", Modifier.weight(1f))
                            }
                            FakeField("Card Holder Name", "Name on card")
                            Text("Demo app: card payments are switched off. Choose Cash on Delivery to place a test order.",
                                style = T.small.copy(fontSize = 10.5.sp), color = C.Danger, modifier = Modifier.padding(top = 6.dp, bottom = 10.dp))
                        }
                    }
                    PayOption(1, pay, "Cash on Delivery", Icons.Outlined.Payments) { pay = 1 }
                    PayOption(2, pay, "Card on Delivery", Icons.Outlined.CreditCard) { pay = 2 }
                    Spacer(Modifier.height(10.dp))
                }
            }
            item {
                Card {
                    Text("Wallet or Rewards", style = T.bodyM.copy(fontWeight = FontWeight.Bold), color = C.Ink, modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 8.dp))
                    Column(Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp).border(1.dp, C.Line, RoundedCornerShape(10.dp))) {
                        ToggleRow(Icons.Outlined.AccountBalanceWallet, Color(0xFFFFE0B2), "Demo Cart Wallet", "Available balance: 0.00", wallet) { wallet = it }
                        HorizontalDivider(color = C.Line)
                        ToggleRow(Icons.Outlined.CardGiftcard, Color(0xFFE1BEE7), "Demo Points", "0.00 (${state.purchased.size * 12} points)", points) { points = it }
                    }
                }
            }
            item {
                Card {
                    Text("Delivery Instructions", style = T.bodyM.copy(fontWeight = FontWeight.Bold), color = C.Ink, modifier = Modifier.padding(start = 12.dp, top = 12.dp))
                    Row(Modifier.padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        SmallCheck(noBell) { noBell = it }; Text("Don't ring the bell", style = T.small, color = C.Ink)
                        Spacer(Modifier.width(10.dp))
                        SmallCheck(door) { door = it }; Text("Leave at my door", style = T.small, color = C.Ink)
                    }
                    Box(Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp).fillMaxWidth().height(40.dp).border(1.dp, C.Line, RoundedCornerShape(8.dp)).padding(horizontal = 12.dp), contentAlignment = Alignment.CenterStart) {
                        Text("Add delivery instructions", style = T.small, color = C.Ink3)
                    }
                }
            }
            item {
                Card {
                    Row(Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 4.dp)) {
                        Text("Order Summary", style = T.bodyM.copy(fontWeight = FontWeight.Bold), color = C.Ink, modifier = Modifier.weight(1f))
                        Text("How fees work", style = T.small.copy(fontSize = 11.sp), color = C.Ink2)
                    }
                    BillRow("Subtotal (${state.count} items)", state.subtotal)
                    if (state.couponValue > 0) BillRow("Coupon ${state.coupon}", -state.couponValue, C.Brand)
                    BillRow("Delivery Fee", delivery)
                    BillRow("Packaging fee", 1.0)
                    BillRow("Service Fee", 2.0)
                    HorizontalDivider(color = C.Line, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                    BillRow("Total (Incl. VAT)", total, bold = true)
                    Spacer(Modifier.height(8.dp))
                }
            }
            item {
                Card {
                    Row(Modifier.padding(end = 12.dp, top = 4.dp, bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(terms, { terms = it }, colors = CheckboxDefaults.colors(checkedColor = C.Ink), modifier = Modifier.testTag("terms"))
                        Text("I have read and agree to the Terms and Conditions", style = T.small.copy(fontSize = 11.sp), color = C.Ink)
                    }
                }
            }
            if (cardNotice) item {
                Text("Card payment is not available in this demo. Switch to Cash on Delivery to continue.", style = T.small, color = C.Danger,
                    textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp))
            }
        }
        StickyTotal(
            total, "Checkout", enabled = ready, tag = "place_order",
            warning = when { slot == null -> "Please pick a delivery slot to continue."; !terms -> "Please accept the Terms and Conditions."; else -> null },
        ) {
            if (pay == 0) cardNotice = true else state.go(Route.OrderPlaced(state.placeOrder()))
        }
    }
    if (slotOpen) SlotSheet(slot, { slot = it; slotOpen = false }) { slotOpen = false }
}

@Composable
private fun PayOption(k: Int, sel: Int, title: String, icon: ImageVector, onClick: () -> Unit) {
    val on = k == sel
    Row(
        Modifier.padding(horizontal = 12.dp, vertical = 4.dp).fillMaxWidth().height(48.dp).border(if (on) 1.5.dp else 1.dp, if (on) C.Ink else C.Line, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp)).clickable(onClick = onClick).padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(on, onClick, colors = RadioButtonDefaults.colors(selectedColor = C.Ink))
        Text(title, style = T.bodyM.copy(fontWeight = FontWeight.SemiBold), color = C.Ink, modifier = Modifier.weight(1f))
        Icon(icon, null, tint = C.Ink, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(6.dp))
        Icon(if (on) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown, null, tint = C.Ink, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
    }
}

/** Looks like a card field but takes no input: this demo never collects card details. */
@Composable
private fun FakeField(label: String, hint: String, modifier: Modifier = Modifier) {
    Column(modifier.padding(top = 8.dp)) {
        Row { Text(label, style = T.small.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium), color = C.Ink); Text(" *", style = T.small, color = C.Danger) }
        Box(Modifier.padding(top = 4.dp).fillMaxWidth().height(40.dp).border(1.dp, C.Line, RoundedCornerShape(8.dp)).background(Color(0xFFFAFAFA), RoundedCornerShape(8.dp)).padding(horizontal = 10.dp), contentAlignment = Alignment.CenterStart) {
            Text(hint, style = T.small, color = C.Ink3)
        }
    }
}

@Composable
private fun ToggleRow(icon: ImageVector, iconBg: Color, title: String, sub: String, on: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(30.dp).background(iconBg, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = C.Ink, modifier = Modifier.size(18.dp)) }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) { Text(title, style = T.small.copy(fontSize = 12.sp), color = C.Ink); Text(sub, style = T.small.copy(fontSize = 10.sp), color = C.Ink3) }
        Switch(on, onChange, colors = SwitchDefaults.colors(checkedTrackColor = C.Ink, uncheckedTrackColor = Color(0xFFE4E4E6), uncheckedBorderColor = Color.Transparent))
    }
}

@Composable
private fun SmallCheck(on: Boolean, onChange: (Boolean) -> Unit) {
    Checkbox(on, onChange, colors = CheckboxDefaults.colors(checkedColor = C.Ink, uncheckedColor = C.Ink3))
}

@Composable
private fun SlotSheet(current: Pair<Int, Int>?, onConfirm: (Pair<Int, Int>) -> Unit, onClose: () -> Unit) {
    val sheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var day by remember { mutableIntStateOf(current?.first ?: 0) }
    var time by remember { mutableStateOf(current?.second) }
    val list = remember { days() }
    ModalBottomSheet(onClose, sheetState = sheet, containerColor = Color.White) {
        Column(Modifier.padding(horizontal = 10.dp).padding(bottom = 16.dp).testTag("slot_sheet")) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Select delivery slot", style = TextStyle(fontFamily = Plex, fontSize = 16.sp), color = C.Ink, modifier = Modifier.weight(1f))
                Box(Modifier.size(28.dp).background(Color(0xFFF0F0F1), CircleShape).clip(CircleShape).clickable(onClick = onClose), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Close, "Close", tint = C.Ink, modifier = Modifier.size(16.dp))
                }
            }
            Text("Choose a day", style = T.small.copy(fontSize = 11.sp), color = C.Ink2, modifier = Modifier.padding(top = 12.dp, bottom = 8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(list) { k, d ->
                    val sel = k == day
                    Column(
                        Modifier.width(78.dp).height(48.dp).background(if (sel) C.Ink else Color.White, RoundedCornerShape(10.dp)).border(1.dp, if (sel) C.Ink else C.Line, RoundedCornerShape(10.dp))
                            .clip(RoundedCornerShape(10.dp)).clickable { day = k; time = null },
                        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
                    ) {
                        Text(d.top, style = T.smallM.copy(fontWeight = FontWeight.Bold), color = if (sel) Color.White else C.Ink)
                        Text(d.bottom, style = T.tiny.copy(fontWeight = FontWeight.Normal), color = if (sel) Color.White else C.Ink3)
                    }
                }
            }
            Text("Choose a time", style = T.small.copy(fontSize = 11.sp), color = C.Ink2, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
            val times = if (day == 0) TIMES.takeLast(1) else TIMES
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                times.forEach { t ->
                    val k = TIMES.indexOf(t); val sel = time == k
                    Box(
                        Modifier.height(36.dp).background(if (sel) C.Ink else Color.White, RoundedCornerShape(18.dp)).border(1.dp, if (sel) C.Ink else C.Line, RoundedCornerShape(18.dp))
                            .clip(RoundedCornerShape(18.dp)).clickable { time = k }.padding(horizontal = 14.dp),
                        contentAlignment = Alignment.Center,
                    ) { Text(t, style = T.small.copy(fontSize = 11.5.sp), color = if (sel) Color.White else C.Ink) }
                }
            }
            Spacer(Modifier.height(28.dp))
            Box(
                Modifier.fillMaxWidth().height(44.dp).background(if (time != null) C.Ink else Color(0xFFBDBDBF), RoundedCornerShape(22.dp))
                    .then(if (time != null) Modifier.pressable { onConfirm(day to time!!) } else Modifier).testTag("slot_confirm"),
                contentAlignment = Alignment.Center,
            ) { Text("CONFIRM", style = T.smallM.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp), color = Color.White) }
        }
    }
}

@Composable
fun PaymentScreen(state: AppState) = CheckoutScreen(state)

@Composable
fun OrderPlacedScreen(state: AppState, number: String) {
    Column(Modifier.fillMaxSize().background(Color.White).statusBarsPadding().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Box(Modifier.size(96.dp).background(C.BrandSoft, CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Rounded.Check, null, tint = C.Brand, modifier = Modifier.size(56.dp))
        }
        Spacer(Modifier.height(20.dp))
        Text("Order placed!", style = T.h1, color = C.Ink)
        Spacer(Modifier.height(6.dp))
        Text("Order #$number · Cash on Delivery", style = T.body, color = C.Ink2)
        Text("This is a demo order — nothing will be delivered.", style = T.small, color = C.Ink3, modifier = Modifier.padding(top = 4.dp))
        Spacer(Modifier.height(28.dp))
        Box(Modifier.fillMaxWidth().height(48.dp).background(C.Ink, RoundedCornerShape(24.dp)).pressable { state.home() }, contentAlignment = Alignment.Center) {
            Text("CONTINUE SHOPPING", style = T.smallM.copy(fontWeight = FontWeight.Bold), color = Color.White)
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.clickable { state.home(); state.tab = com.freshcart.shop.data.Tab.BuyAgain }.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.CheckCircle, null, tint = C.Brand, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("See it in Buy Again", style = T.smallM, color = C.Brand)
        }
    }
}
