package com.freshcart.shop.ui.screens


import com.freshcart.shop.ui.Plex
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.AssignmentReturn
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshcart.shop.R
import com.freshcart.shop.data.AppState
import com.freshcart.shop.data.Catalog
import com.freshcart.shop.data.Product
import com.freshcart.shop.data.Route
import com.freshcart.shop.data.Tab
import com.freshcart.shop.ui.C
import com.freshcart.shop.ui.CouponStrip
import com.freshcart.shop.ui.Dirham
import com.freshcart.shop.ui.FlashSaleHeader
import com.freshcart.shop.ui.GridSection
import com.freshcart.shop.ui.ProductCard
import com.freshcart.shop.ui.ProductRail
import com.freshcart.shop.ui.PromoBanner
import com.freshcart.shop.ui.SectionHeader
import com.freshcart.shop.ui.T
import com.freshcart.shop.ui.list
import com.freshcart.shop.ui.open
import com.freshcart.shop.ui.pressable

// ---------------------------------------------------------------- Buy Again

@Composable
fun FilterChip(icon: ImageVector, label: String, onClick: () -> Unit = {}) {
    Row(
        Modifier.height(30.dp).border(1.dp, C.Line, RoundedCornerShape(15.dp)).background(Color.White, RoundedCornerShape(15.dp))
            .clip(RoundedCornerShape(15.dp)).clickable(onClick = onClick).padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = C.Ink, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(5.dp))
        Text(label, style = T.small.copy(fontSize = 11.4.sp), color = C.Ink)
    }
}

@Composable
fun BuyAgainScreen(state: AppState) {
    var sortAsc by remember { mutableStateOf(false) }
    val items = state.purchasedProducts().let { if (sortAsc) it.sortedBy { p -> p.price } else it }
    Column(Modifier.fillMaxSize().background(Color.White)) {
        TitleBar("Buy Again") {
            Box(Modifier.size(32.dp).border(1.dp, C.Line, CircleShape).clip(CircleShape).clickable { state.go(Route.Search()) }, contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.Search, "Search", tint = C.Ink, modifier = Modifier.size(18.dp))
            }
        }
        Row(Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 8.dp, vertical = 9.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            FilterChip(Icons.Rounded.Tune, "Filter")
            FilterChip(Icons.Rounded.SwapVert, "Sort by") { sortAsc = !sortAsc }
        }
        Column(Modifier.padding(start = 9.dp, top = 12.dp)) {
            Text("Frequently Purchased", style = T.h3.copy(fontSize = 15.75.sp, fontWeight = FontWeight.Bold), color = C.Ink)
            Text("Quickly reorder whole baskets from earlier orders", style = T.small.copy(fontSize = 11.85.sp), color = Color(0xFF373738))
        }
        if (items.isEmpty()) {
            Column(Modifier.fillMaxWidth().padding(top = 84.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.size(110.dp).background(Color(0xFFEBEBEC), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.ShoppingBag, null, tint = C.Ink, modifier = Modifier.size(40.dp))
                }
                Spacer(Modifier.height(24.dp))
                Text("Nothing to reorder yet — your past\nitems will show up here", style = T.h3.copy(fontSize = 15.75.sp, fontWeight = FontWeight.Bold), color = C.Ink, textAlign = TextAlign.Center)
                Spacer(Modifier.height(26.dp))
                Box(
                    Modifier.padding(horizontal = 32.dp).fillMaxWidth().height(46.dp).background(C.Ink, RoundedCornerShape(23.dp)).pressable { state.tab = Tab.Home },
                    contentAlignment = Alignment.Center,
                ) { Text("GO TO HOME PAGE", style = T.smallM.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp), color = Color.White) }
            }
        } else {
            LazyVerticalGrid(GridCells.Fixed(3), Modifier.fillMaxSize(), contentPadding = PaddingValues(start = 8.dp, end = 8.dp, top = 12.dp, bottom = 150.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items, key = { it.id }) { ProductCard(state, it, { p -> state.open(p.id) }) }
            }
        }
    }
}

fun AppState.purchasedProducts(): List<Product> = purchased.mapNotNull { Catalog.get(it) }

// ---------------------------------------------------------------- Deals

@Composable
fun DealsScreen(state: AppState) {
    val open: (Product) -> Unit = { state.open(it.id) }
    Column(Modifier.fillMaxSize().background(C.Page)) {
        TitleBar("Deals")
        LazyColumn(Modifier.fillMaxSize().testTag("deals_list"), contentPadding = PaddingValues(bottom = 150.dp)) {
            item { FlashSaleHeader(endsInMs = 6L * 3600_000 + 55 * 60_000 + 23_000) }
            item { GridSection(state, "Flash sale", Catalog.deals(25).filter { it.dept in setOf("grocery", "snacks", "household", "fresh", "beauty") }.take(16), open, { state.list("Flash Sale", deals = true) }) }
            item { Column(Modifier.background(C.Page).padding(vertical = 12.dp)) { CouponStrip(listOf(100 to "BIG100", 50 to "MEGA50", 15 to "FRESH15")) } }
            item {
                PromoBanner("UP TO", "60% OFF", "Travel Essentials", listOf(Color(0xFFE9F3FF), Color(0xFF9CC3F5)),
                    listOf(R.drawable.d_172, R.drawable.d_174, R.drawable.d_175), dark = true) { state.list("Travel Essentials", "fashion") }
            }
            item { GridSection(state, "Travel essentials", Catalog.dept("fashion").sortedByDescending { it.offPercent }.take(16), open, { state.list("Travel Essentials", "fashion") }) }
            item {
                PromoBanner("GLOW", "UP", "Beauty that lasts", listOf(Color(0xFFE7D7F7), Color(0xFFC6A8EC)),
                    listOf(R.drawable.d_2, R.drawable.d_4, R.drawable.d_8), dark = true) { state.list("Beauty", "beauty") }
            }
            item { GridSection(state, "Beauty", Catalog.dept("beauty").sortedByDescending { it.offPercent }.take(16), open, { state.list("Beauty", "beauty") }) }
            item {
                PromoBanner("SAVE", "5 OFF", "Dairy & breakfast", listOf(Color(0xFF0E5A9C), Color(0xFF4FA3E8)),
                    listOf(R.drawable.p_eggs, R.drawable.p_parmesan, R.drawable.t_butter)) { state.list("Dairy & Breakfast", "fresh") }
            }
            item { GridSection(state, "Dairy & breakfast", Catalog.dept("fresh", "beverages").take(16), open, { state.list("Dairy & Breakfast", "fresh") }) }
            item { SectionHeader("Today's top deals") }
            item { ProductRail(state, Catalog.deals(40).take(14), open, origin = "top-deals") }
        }
    }
}

// ---------------------------------------------------------------- Account

@Composable
private fun Avatar(initials: String, progress: Float) {
    Box(Modifier.size(58.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(58.dp)) {
            drawArc(Color.White.copy(alpha = 0.25f), 0f, 360f, false, style = Stroke(4.dp.toPx()))
            drawArc(Color.White, -90f, 360f * progress, false, style = Stroke(4.dp.toPx(), cap = StrokeCap.Round))
        }
        Text(initials, style = TextStyle(fontFamily = Plex, fontSize = 18.sp, fontWeight = FontWeight.Bold), color = Color.White)
        Box(Modifier.align(Alignment.BottomCenter).offset(y = 4.dp).background(Color.White, RoundedCornerShape(6.dp)).padding(horizontal = 4.dp)) {
            Text("${(progress * 100).toInt()} %", style = T.tiny.copy(fontSize = 8.sp), color = C.Ink)
        }
    }
}

@Composable
private fun GroupLabel(text: String) {
    Text(text, style = TextStyle(fontFamily = Plex, fontSize = 12.7.sp, fontWeight = FontWeight.Bold), color = Color(0xFF828283),
        modifier = Modifier.padding(start = 12.dp, top = 22.dp, bottom = 8.dp))
}

@Composable
private fun MenuCard(content: @Composable () -> Unit) {
    Column(Modifier.padding(horizontal = 12.dp).fillMaxWidth().background(Color.White, RoundedCornerShape(10.dp)).border(1.dp, C.Line, RoundedCornerShape(10.dp))) { content() }
}

@Composable
private fun MenuRow(icon: ImageVector, title: String, subtitle: String?, last: Boolean = false, trailing: (@Composable () -> Unit)? = null, onClick: () -> Unit = {}) {
    Column {
        Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 13.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(32.dp).border(1.dp, C.Line, RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = C.Ink2, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(17.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = T.bodyM.copy(fontSize = 13.9.sp, fontWeight = FontWeight.Bold), color = Color(0xFF212024))
                if (subtitle != null) Text(subtitle, style = T.small.copy(fontSize = 11.85.sp), color = Color(0xFF717172))
            }
            if (trailing != null) trailing() else Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = C.Ink3, modifier = Modifier.size(20.dp))
        }
        if (!last) Box(Modifier.padding(start = 62.dp, end = 12.dp).fillMaxWidth().height(0.7.dp).background(C.Line))
    }
}

@Composable
fun AccountScreen(state: AppState) {
    var lang by remember { mutableIntStateOf(1) }
    var notify by remember { mutableStateOf(true) }
    LazyColumn(Modifier.fillMaxSize().background(Color(0xFFF7F7F8)).testTag("account_list"), contentPadding = PaddingValues(bottom = 150.dp)) {
        item {
            Box {
                Column(Modifier.fillMaxWidth().height(256.dp).background(Brush.verticalGradient(listOf(Color(0xFF1E3A8A), Color(0xFF2747B8), Color(0xFF3D5FD6)))).statusBarsPadding().padding(horizontal = 12.dp)) {
                    Spacer(Modifier.height(40.dp))
                    Text("My Account", style = T.h2.copy(fontSize = 17.8.sp), color = Color.White)
                    Spacer(Modifier.height(18.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Avatar("GU", 0.5f)
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Guest User", style = T.h3.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            Text("guest@democart.app", style = T.small, color = Color.White.copy(alpha = 0.85f))
                        }
                        Row(Modifier.border(1.dp, Color.White, RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Edit, null, tint = Color.White, modifier = Modifier.size(13.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("EDIT", style = T.tiny.copy(fontSize = 11.3.sp, fontWeight = FontWeight.Bold), color = Color.White)
                        }
                    }
                }
                Column(
                    Modifier.padding(start = 10.dp, end = 10.dp, top = 200.dp).fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(10.dp)).border(1.dp, C.Line, RoundedCornerShape(10.dp)).padding(12.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Demo Points", style = T.bodyM.copy(fontWeight = FontWeight.Bold, fontSize = 13.9.sp), color = Color(0xFF363637))
                        Text(" · scan in store to redeem", style = T.small.copy(fontSize = 10.sp), color = C.Ink2)
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(18.dp).background(Brush.radialGradient(listOf(Color(0xFFB04DE0), Color(0xFF7A16B8))), CircleShape))
                        Spacer(Modifier.width(8.dp))
                        Text("${(state.purchased.size * 12)} pts", style = TextStyle(fontFamily = Plex, fontSize = 19.4.sp, fontWeight = FontWeight.Bold), color = Color(0xFFA41BB8))
                        Text("  ~ ", style = T.small, color = C.Ink2)
                        Dirham(8.dp, C.Ink2)
                        Text("%.2f".format(state.purchased.size * 0.12), style = T.small, color = C.Ink2)
                    }
                    Text("View rewards ›", style = T.smallM.copy(fontSize = 11.8.sp), color = Color(0xFFA41BB8), modifier = Modifier.align(Alignment.End))
                }
            }
        }
        item { GroupLabel("SHOPPING") }
        item {
            MenuCard {
                MenuRow(Icons.AutoMirrored.Outlined.ReceiptLong, "My Orders", "Manage & Track")
                MenuRow(Icons.Outlined.AssignmentReturn, "Returns & Refunds", "Track & Initiate")
                MenuRow(Icons.Outlined.History, "Past Orders", "Previous & Archived")
                MenuRow(Icons.Outlined.ShoppingCart, "In-Store Purchases", "View receipts from stores")
                MenuRow(Icons.Outlined.FavoriteBorder, "Favourites", "${state.wishlist.size} saved items", last = true) { state.go(Route.Listing("Favourites", query = "__wishlist")) }
            }
        }
        item { GroupLabel("ACCOUNTS & PAYMENTS") }
        item {
            MenuCard {
                MenuRow(Icons.Outlined.Person, "My Profile", "Personal details")
                MenuRow(Icons.Outlined.LocationOn, "My Addresses", "Saved addresses")
                MenuRow(Icons.Outlined.CreditCard, "Cards", "Saved payment methods")
                MenuRow(Icons.Outlined.LocalOffer, "Vouchers", "View available vouchers")
                MenuRow(Icons.Outlined.AddCircleOutline, "Demo Rewards", "View your rewards and points", last = true)
            }
        }
        item { GroupLabel("SETTINGS") }
        item {
            MenuCard {
                MenuRow(Icons.Outlined.Settings, "Country", "United Arab Emirates", trailing = {
                    Row(verticalAlignment = Alignment.CenterVertically) { UaeFlag(); Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = C.Ink3, modifier = Modifier.size(20.dp)) }
                })
                MenuRow(Icons.Outlined.Translate, "Language", "English / العربية", trailing = {
                    Row(Modifier.background(Color(0xFFEFEFF0), RoundedCornerShape(6.dp))) {
                        listOf("عربي", "En").forEachIndexed { k, l ->
                            Box(Modifier.width(34.dp).height(30.dp).background(if (lang == k) C.Ink else Color.Transparent, RoundedCornerShape(6.dp)).clickable { lang = k }, contentAlignment = Alignment.Center) {
                                Text(l, style = T.small.copy(fontSize = 11.sp), color = if (lang == k) Color.White else C.Ink)
                            }
                        }
                    }
                })
                MenuRow(Icons.Outlined.NotificationsNone, "Notifications", null, trailing = {
                    MiniToggle(notify) { notify = it }
                })
                MenuRow(Icons.Outlined.SupportAgent, "Support", "Help & Customer Care", last = true)
            }
        }
        item {
            Row(
                Modifier.padding(start = 10.dp, end = 10.dp, top = 24.dp).fillMaxWidth().height(48.dp).border(1.dp, C.Danger, RoundedCornerShape(24.dp)),
                horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.AutoMirrored.Outlined.Logout, null, tint = C.Danger, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("LOGOUT", style = T.tiny.copy(fontWeight = FontWeight.Bold), color = C.Danger)
            }
        }
        item {
            Column(Modifier.fillMaxWidth().padding(top = 22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.size(30.dp).background(C.Brand, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.ShoppingBag, null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.height(10.dp))
                Text("Version 1.0", style = T.small.copy(fontSize = 10.sp), color = C.Ink2)
            }
        }
    }
}

@Composable
fun UaeFlag(modifier: Modifier = Modifier) {
    Row(modifier.width(20.dp).height(13.dp).border(0.5.dp, C.Line)) {
        Box(Modifier.width(5.dp).height(13.dp).background(Color(0xFFEF3340)))
        Column(Modifier.weight(1f)) {
            Box(Modifier.fillMaxWidth().height(4.33.dp).background(Color(0xFF009739)))
            Box(Modifier.fillMaxWidth().height(4.33.dp).background(Color.White))
            Box(Modifier.fillMaxWidth().height(4.33.dp).background(Color.Black))
        }
    }
}

/** Small toggle like the source's: light 44 x 22 dp track, black thumb that slides to the end when on. */
@Composable
fun MiniToggle(on: Boolean, onChange: (Boolean) -> Unit) {
    val x by androidx.compose.animation.core.animateDpAsState(if (on) 22.dp else 0.dp, label = "toggle")
    Box(Modifier.width(44.dp).height(22.dp).background(Color(0xFFE6E6E8), RoundedCornerShape(11.dp)).clip(RoundedCornerShape(11.dp)).clickable { onChange(!on) }) {
        Box(Modifier.offset(x = x).size(22.dp).background(if (on) C.Ink else Color(0xFFBDBDBF), CircleShape))
    }
}