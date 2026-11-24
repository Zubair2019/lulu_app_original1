package com.freshcart.shop.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.freshcart.shop.data.AppState
import com.freshcart.shop.data.Catalog
import com.freshcart.shop.data.Route
import com.freshcart.shop.data.Store
import com.freshcart.shop.data.Tab
import com.freshcart.shop.ui.screens.AccountScreen
import com.freshcart.shop.ui.screens.BuyAgainScreen
import com.freshcart.shop.ui.screens.CartScreen
import com.freshcart.shop.ui.screens.CategoriesScreen
import com.freshcart.shop.ui.screens.CheckoutScreen
import com.freshcart.shop.ui.screens.DealsScreen
import com.freshcart.shop.ui.screens.HomeScreen
import com.freshcart.shop.ui.screens.ListingScreen
import com.freshcart.shop.ui.screens.MinutesScreen
import com.freshcart.shop.ui.screens.MoneyScreen
import com.freshcart.shop.ui.screens.OrderPlacedScreen
import com.freshcart.shop.ui.screens.PaymentScreen
import com.freshcart.shop.ui.screens.ProductScreen
import com.freshcart.shop.ui.screens.SearchScreen

@Composable
fun App() {
    val state = remember { AppState() }
    val activity = LocalContext.current as? Activity
    BackHandler {
        when {
            state.back() -> Unit
            state.store != Store.Hypermarket -> state.store = Store.Hypermarket
            state.tab != Tab.Home -> state.tab = Tab.Home
            else -> activity?.finish()
        }
    }
    Box(Modifier.fillMaxSize().background(C.Page).semantics { testTagsAsResourceId = true }) {
        AnimatedContent(
            targetState = state.top,
            transitionSpec = {
                if (state.forward) (slideInHorizontally(tween(260)) { it / 3 } + fadeIn(tween(200))) togetherWith fadeOut(tween(160))
                else fadeIn(tween(200)) togetherWith (slideOutHorizontally(tween(240)) { it / 3 } + fadeOut(tween(200)))
            },
            label = "route",
        ) { route ->
            when (route) {
                Route.Tabs -> TabsHost(state)
                is Route.Search -> SearchScreen(state, route.query)
                is Route.Listing -> ListingScreen(state, route)
                is Route.ProductPage -> ProductScreen(state, route.id)
                Route.Cart -> CartScreen(state)
                Route.Checkout -> CheckoutScreen(state)
                Route.Payment -> PaymentScreen(state)
                is Route.OrderPlaced -> OrderPlacedScreen(state, route.number)
            }
        }
    }
}

/** Tab pages with the floating bottom bar and floating View cart pill; both hide while scrolling down. */
@Composable
fun TabsHost(state: AppState) {
    var barsShown by remember { mutableStateOf(true) }
    val hideOnScroll = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -6f) barsShown = false else if (available.y > 6f) barsShown = true
                return Offset.Zero
            }
        }
    }
    Box(Modifier.fillMaxSize().nestedScroll(hideOnScroll)) {
        val page = if (state.tab == Tab.Home) state.store.name else state.tab.name
        AnimatedContent(page, transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(120)) }, label = "tab") { p ->
            when (p) {
                Store.Hypermarket.name -> HomeScreen(state)
                Store.Minutes.name -> MinutesScreen(state)
                Store.Money.name -> MoneyScreen(state)
                Tab.Categories.name -> CategoriesScreen(state)
                Tab.BuyAgain.name -> BuyAgainScreen(state)
                Tab.Deals.name -> DealsScreen(state)
                else -> AccountScreen(state)
            }
        }
        val showBars = state.store != Store.Money || state.tab != Tab.Home
        Column(Modifier.align(Alignment.BottomCenter).navigationBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally) {
            ViewCartPill(state, Modifier.padding(bottom = if (barsShown && showBars) 10.dp else 16.dp))
            AnimatedVisibility(barsShown && showBars, enter = slideInVertically { it } + fadeIn(), exit = slideOutVertically { it } + fadeOut()) {
                BottomBar(state)
            }
        }
    }
}

private data class NavItem(val tab: Tab, val label: String, val icon: ImageVector, val selected: ImageVector)

private val NAV = listOf(
    NavItem(Tab.Home, "Home", Icons.Outlined.Home, Icons.Rounded.Home),
    NavItem(Tab.Categories, "Categories", Icons.Outlined.GridView, Icons.Rounded.GridView),
    NavItem(Tab.BuyAgain, "Buy Again", Icons.Outlined.History, Icons.Rounded.History),
    NavItem(Tab.Deals, "Deals", Icons.Outlined.LocalOffer, Icons.Rounded.LocalOffer),
    NavItem(Tab.Account, "Account", Icons.Outlined.AccountCircle, Icons.Rounded.AccountCircle),
)

/** Floating frosted bar: 5 items of 72.7 x 48 dp inside a 10 dp inset pill. */
@Composable
fun BottomBar(state: AppState) {
    Row(
        Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.5.dp).fillMaxWidth().height(60.dp)
            .shadow(10.dp, RoundedCornerShape(30.dp), ambientColor = Color.Black.copy(alpha = 0.25f))
            .background(C.Glass, RoundedCornerShape(30.dp))
            .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(30.dp))
            .padding(horizontal = 4.5.dp)
            .testTag("bottom_bar"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NAV.forEach { item ->
            val sel = state.tab == item.tab
            Column(
                Modifier.weight(1f).height(48.dp).testTag("nav_${item.tab.name.lowercase()}")
                    .clickable(remember { MutableInteractionSource() }, null) { state.popToTabs(); state.tab = item.tab },
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
            ) {
                Box(
                    Modifier.width(52.dp).height(28.dp).background(if (sel) Color(0xFFDADADA) else Color.Transparent, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(if (sel) item.selected else item.icon, null, tint = if (sel) (if (state.store == Store.Minutes && item.tab == Tab.Home) C.Campaign else C.Brand) else C.Ink2, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.height(2.dp))
                Text(item.label, style = if (sel) T.nav.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium) else T.nav, color = if (sel) C.NavSel else C.Ink2, maxLines = 1)
            }
        }
    }
}

/** Floating "View cart" pill: thumbnail, title, "n items · price", chevron. 250 x 57.5 dp. */
@Composable
fun ViewCartPill(state: AppState, modifier: Modifier = Modifier) {
    AnimatedVisibility(state.count > 0, enter = slideInVertically { it } + fadeIn(), exit = slideOutVertically { it } + fadeOut(), modifier = modifier) {
        val first = state.lines.firstOrNull()?.product
        Row(
            Modifier.width(250.dp).height(57.5.dp)
                .shadow(12.dp, RoundedCornerShape(29.dp), ambientColor = Color.Black.copy(alpha = 0.3f))
                .background(C.Glass, RoundedCornerShape(29.dp))
                .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(29.dp))
                .pressable { state.go(Route.Cart) }
                .padding(start = 9.dp, end = 14.dp)
                .testTag("view_cart"),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(40.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                if (first != null) Image(painterResource(first.image), null, contentScale = ContentScale.Fit, modifier = Modifier.size(32.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text("View cart", style = T.viewCart, color = C.Slate)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val n = state.count
                    Text("$n item${if (n == 1) "" else "s"} · ", style = T.viewCartSub, color = Color(0xFF3D3D3E))
                    Price(state.subtotal, T.viewCartSub, Color(0xFF3D3D3E), 9.dp)
                }
            }
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = C.Ink, modifier = Modifier.size(20.dp))
        }
    }
}

/** Helper so screens can open things without knowing the stack. */
fun AppState.open(id: String) = go(Route.ProductPage(id))
fun AppState.list(title: String, dept: String? = null, deals: Boolean = false) = go(Route.Listing(title, dept, deals = deals))
val AppState.catalog get() = Catalog
