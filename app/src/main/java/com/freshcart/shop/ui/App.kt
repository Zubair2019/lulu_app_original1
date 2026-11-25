package com.freshcart.shop.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.freshcart.shop.data.AppState
import com.freshcart.shop.data.Catalog
import com.freshcart.shop.data.Route
import com.freshcart.shop.data.Store
import com.freshcart.shop.data.Tab
import com.freshcart.shop.ui.screens.AccountScreen
import com.freshcart.shop.ui.screens.AisleScreen
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
import com.freshcart.shop.ui.screens.ProductSheet
import com.freshcart.shop.ui.screens.SearchScreen

@OptIn(ExperimentalSharedTransitionApi::class)
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
    // Dark status-bar icons on the light screens, light ones over a store header and over the dimmed product sheet.
    val base = state.base
    val lightTop = state.top !is Route.ProductPage && !(base == Route.Tabs && state.tab == Tab.Home)
    val view = LocalView.current
    SideEffect { activity?.window?.let { WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = lightTop } }

    SharedTransitionLayout(Modifier.fillMaxSize().background(C.Page).semantics { testTagsAsResourceId = true }) {
        CompositionLocalProvider(LocalShared provides this) {
            // The screen under any product sheet. A new screen slides in from the right on a spring and leaves the same way.
            AnimatedContent(
                targetState = base,
                transitionSpec = {
                    if (state.forward) (slideInHorizontally(M.sheet()) { it / 3 } + fadeIn(M.bar())) togetherWith fadeOut(M.snap())
                    else fadeIn(M.bar()) togetherWith (slideOutHorizontally(M.sheet()) { it / 3 } + fadeOut(M.snap()))
                },
                label = "route",
            ) { route ->
                when (route) {
                    Route.Tabs -> TabsHost(state)
                    is Route.Search -> SearchScreen(state, route.query)
                    is Route.Listing -> ListingScreen(state, route)
                    is Route.Aisle -> AisleScreen(state, route)
                    is Route.ProductPage -> Unit
                    Route.Cart -> CartScreen(state)
                    Route.Checkout -> CheckoutScreen(state)
                    Route.Payment -> PaymentScreen(state)
                    is Route.OrderPlaced -> OrderPlacedScreen(state, route.number)
                }
            }
            ProductSheetHost(state)
        }
    }
}

/**
 * The product page is a sheet over whatever screen it was opened from: that screen stays where it was and dims,
 * and the sheet rises from the bottom edge on a spring. Tapping the dimmed part, the close button or Back sends it
 * back down.
 */
@Composable
private fun ProductSheetHost(state: AppState) {
    val top = state.top as? Route.ProductPage
    val last = remember { arrayOfNulls<Route.ProductPage>(1) }   // what to keep drawing while the sheet leaves
    if (top != null) last[0] = top
    AnimatedVisibility(top != null, enter = fadeIn(M.bar()), exit = fadeOut(M.bar())) {
        Box(Modifier.fillMaxSize().background(C.Scrim).clickable(remember { MutableInteractionSource() }, null) { state.back() })
    }
    AnimatedVisibility(top != null, enter = slideInVertically(M.sheet()) { it }, exit = slideOutVertically(M.sheet()) { it }) {
        last[0]?.let { ProductSheet(state, it) }
    }
}

private val STORES = setOf(Store.Hypermarket.name, Store.Minutes.name, Store.Money.name)

private val PAGE_ORDER = listOf(
    Store.Hypermarket.name, Store.Minutes.name, Store.Money.name, Tab.Categories.name, Tab.BuyAgain.name, Tab.Deals.name, Tab.Account.name,
)

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
        // Pages slide a short way in the direction of the tab that was chosen. Between the three stores the page
        // changes at once: their headers share the store tabs, and only the selected tab glides across, so two
        // headers never show on top of each other. What is below the header makes its own entrance.
        AnimatedContent(
            page, label = "tab",
            transitionSpec = {
                if (targetState in STORES && initialState in STORES) EnterTransition.None togetherWith ExitTransition.None
                else {
                    val dir = if (PAGE_ORDER.indexOf(targetState) >= PAGE_ORDER.indexOf(initialState)) 1 else -1
                    (slideInHorizontally(M.bar()) { dir * it / 8 } + fadeIn(M.bar())) togetherWith
                        (slideOutHorizontally(M.bar()) { -dir * it / 8 } + fadeOut(M.snap()))
                }
            },
        ) { p ->
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
            AnimatedVisibility(barsShown && showBars, enter = slideInVertically(M.bar()) { it } + fadeIn(), exit = slideOutVertically(M.bar()) { it } + fadeOut()) {
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

/**
 * Floating frosted bar, the third of the three tab rows: 5 items of 48 dp inside a 60 dp pill. One grey pill sits
 * behind the selected icon and springs to the next one with a little overshoot; the icon it lands on pops up.
 */
@Composable
fun BottomBar(state: AppState) {
    val haptic = LocalHapticFeedback.current
    val idx = NAV.indexOfFirst { it.tab == state.tab }
    BoxWithConstraints(
        Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.5.dp).fillMaxWidth().height(60.dp)
            .shadow(10.dp, RoundedCornerShape(30.dp), ambientColor = Color.Black.copy(alpha = 0.25f))
            .background(C.Glass, RoundedCornerShape(30.dp))
            .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(30.dp))
            .padding(horizontal = 4.5.dp)
            .testTag("bottom_bar"),
    ) {
        val itemW = maxWidth / NAV.size
        val x by animateDpAsState(itemW * idx + (itemW - 52.dp) / 2, spring(dampingRatio = 0.68f, stiffness = 420f), label = "navPill")
        Box(Modifier.offset(x = x, y = 8.dp).width(52.dp).height(28.dp).background(C.NavPill, RoundedCornerShape(14.dp)))
        Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            NAV.forEachIndexed { i, item ->
                val sel = i == idx
                val pop by animateFloatAsState(if (sel) 1f else 0f, M.pop(), label = "navPop")
                Column(
                    Modifier.weight(1f).height(48.dp).testTag("nav_${item.tab.name.lowercase()}")
                        .clickable(remember { MutableInteractionSource() }, null) {
                            if (!sel) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            state.popToTabs(); state.tab = item.tab
                        },
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
                ) {
                    Box(Modifier.width(52.dp).height(28.dp), contentAlignment = Alignment.Center) {
                        Icon(
                            if (sel) item.selected else item.icon, null,
                            tint = if (sel) (if (state.store == Store.Minutes && item.tab == Tab.Home) C.Campaign else C.Brand) else C.Ink2,
                            modifier = Modifier.size(22.dp).graphicsLayer { val s = 1f + 0.16f * pop; scaleX = s; scaleY = s; translationY = -1.dp.toPx() * pop },
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(item.label, style = if (sel) T.nav.copy(fontWeight = FontWeight.Medium) else T.nav, color = if (sel) C.NavSel else C.Ink2, maxLines = 1)
                }
            }
        }
    }
}

/**
 * Floating "View cart" pill: thumbnail, title, "n items · price", chevron. 250 x 57.5 dp. It springs up when the
 * first item is added and gives a small bump on every add; the count and the amount roll to their new values.
 */
@Composable
fun ViewCartPill(state: AppState, modifier: Modifier = Modifier) {
    val bump = remember { Animatable(1f) }
    val n = state.count
    LaunchedEffect(n) { if (n > 0) { bump.snapTo(1.07f); bump.animateTo(1f, M.pop()) } }
    AnimatedVisibility(n > 0, enter = slideInVertically(M.pop()) { it } + fadeIn(), exit = slideOutVertically(M.bar()) { it } + fadeOut(), modifier = modifier) {
        val first = state.lines.firstOrNull()?.product
        Row(
            Modifier.graphicsLayer { scaleX = bump.value; scaleY = bump.value }.width(250.dp).height(57.5.dp)
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
                    RollingText("$n item${if (n == 1) "" else "s"}", n.toDouble(), T.viewCartSub, C.Slate)
                    Text(" · ", style = T.viewCartSub, color = C.Slate)
                    Dirham(9.dp, C.Slate)
                    Spacer(Modifier.width(2.dp))
                    RollingText(fmt(state.subtotal), state.subtotal, T.viewCartSub, C.Slate)
                }
            }
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = C.Ink, modifier = Modifier.size(20.dp))
        }
    }
}

/** Opens a product as a sheet. If a card asked for it, its picture travels to the sheet. */
fun AppState.open(id: String) {
    val hero = pendingHero
    pendingHero = null
    go(Route.ProductPage(id, hero))
}
fun AppState.list(title: String, dept: String? = null, deals: Boolean = false) = go(Route.Listing(title, dept, deals = deals))
val AppState.catalog get() = Catalog
