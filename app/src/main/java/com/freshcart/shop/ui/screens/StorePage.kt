package com.freshcart.shop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.LocalGroceryStore
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.freshcart.shop.R
import com.freshcart.shop.data.AppState
import com.freshcart.shop.data.Catalog
import com.freshcart.shop.data.Product
import com.freshcart.shop.data.Route
import com.freshcart.shop.data.Store
import com.freshcart.shop.ui.AisleCard
import com.freshcart.shop.ui.BannerCarousel
import com.freshcart.shop.ui.C
import com.freshcart.shop.ui.CampaignStrip
import com.freshcart.shop.ui.CashbackBanner
import com.freshcart.shop.ui.Chip
import com.freshcart.shop.ui.CouponStrip
import com.freshcart.shop.ui.D
import com.freshcart.shop.ui.DealShelf
import com.freshcart.shop.ui.FlashSaleHeader
import com.freshcart.shop.ui.GridSection
import com.freshcart.shop.ui.HEADER_COLLAPSE
import com.freshcart.shop.ui.HEADER_FULL
import com.freshcart.shop.ui.LocalSeen
import com.freshcart.shop.ui.MindRow
import com.freshcart.shop.ui.OfferTiles
import com.freshcart.shop.ui.PromoBanner
import com.freshcart.shop.ui.SectionHeader
import com.freshcart.shop.ui.SheetCap
import com.freshcart.shop.ui.SlideCard
import com.freshcart.shop.ui.StoreHeader
import com.freshcart.shop.ui.Tile
import com.freshcart.shop.ui.fmt
import com.freshcart.shop.ui.list
import com.freshcart.shop.ui.open

private val COUPONS = listOf(10 to "SAVE10", 15 to "FRESH15", 50 to "MEGA50", 100 to "BIG100")

private fun pool(vararg depts: String): List<Product> =
    Catalog.dept(*depts).sortedByDescending { it.offPercent }

/** Products for the selected tab of the second row: tab 0 is the campaign mix, the rest narrow every section to one department. */
private fun chipDepts(store: Store, chip: Int): Array<String>? = when (store) {
    Store.Minutes -> when (chip) { 1 -> arrayOf("toys", "beauty", "fashion"); 2 -> arrayOf("electronics", "mobiles"); 3 -> arrayOf("grocery", "fresh", "beverages", "snacks"); 4 -> arrayOf("beauty", "fashion"); else -> null }
    else -> when (chip) { 1 -> arrayOf("toys", "beauty", "fashion"); 2 -> arrayOf("electronics", "mobiles"); 3 -> arrayOf("beauty"); 4 -> arrayOf("electronics", "mobiles"); 5 -> arrayOf("grocery", "fresh", "beverages", "snacks"); 6 -> arrayOf("home"); else -> null }
}

private val ALL_DEPTS = CATEGORY_GROUPS.flatMap { it.depts }

/** The round "what are you shopping for" row: the grocery aisles first, then the rest. */
private val MIND = ALL_DEPTS.take(12)

/** The aisles shown as big cards on the home page. */
private val AISLES = listOf("Fruits & Vegetables", "Food Cupboard", "Household", "Electronics", "Beauty & Wellness", "Cooking & Dining")
    .mapNotNull { n -> ALL_DEPTS.firstOrNull { it.name == n } }

@Composable
fun HomeScreen(state: AppState) = StorePage(state, Store.Hypermarket)

@Composable
fun MinutesScreen(state: AppState) = StorePage(state, Store.Minutes)

/**
 * A store's home page. Under the header: the campaign strip and offer tiles on the store colour, then the light
 * page rising over it with a shelf of deals, the round aisle row, a flash sale, the aisles as big cards, and
 * product grids. Choosing a tab in the second row narrows the page to that department.
 */
@Composable
fun StorePage(state: AppState, store: Store) {
    val list = rememberLazyListState()
    val density = LocalDensity.current
    val maxCollapse = with(density) { HEADER_COLLAPSE.toPx() }
    val collapse by remember {
        derivedStateOf { if (list.firstVisibleItemIndex > 0) maxCollapse else list.firstVisibleItemScrollOffset.toFloat().coerceAtMost(maxCollapse) }
    }
    var chip by remember(store) { mutableIntStateOf(0) }
    val minutes = store == Store.Minutes
    val chips = if (minutes) listOf(
        Chip("Mega", Icons.Rounded.Bolt), Chip("Gifting Store", Icons.Outlined.CardGiftcard), Chip("Electronics", Icons.Outlined.Headphones),
        Chip("Grocery", Icons.Outlined.LocalGroceryStore), Chip("Premium", Icons.Outlined.WorkspacePremium),
    ) else listOf(
        Chip("Mega", Icons.Rounded.Bolt), Chip("Gifting Store", Icons.Outlined.CardGiftcard), Chip("Gaming", Icons.Outlined.SportsEsports),
        Chip("Beauty", Icons.Outlined.Spa), Chip("Electronics", Icons.Outlined.Headphones), Chip("Grocery", Icons.Outlined.LocalGroceryStore),
        Chip("Home", Icons.Outlined.Kitchen), Chip("Fashion", Icons.Outlined.Checkroom),
    )
    val headerFull = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + HEADER_FULL
    val narrow = chipDepts(store, chip)
    val open: (Product) -> Unit = { state.open(it.id) }
    val promise = if (minutes) "Get it by 4 PM" else "Today, 8:00 pm – 10:00 pm"
    val seen = remember(store) { mutableSetOf<Any>() }

    CompositionLocalProvider(LocalSeen provides seen) {
        Box(Modifier.fillMaxSize().background(C.Page)) {
            LazyColumn(
                state = list, modifier = Modifier.fillMaxSize().testTag("store_list"),
                contentPadding = PaddingValues(top = headerFull, bottom = 150.dp),
            ) {
                item(key = "hero") {
                    Column(Modifier.fillMaxWidth().background(C.Campaign)) {
                        CampaignStrip({ state.list("Mega Week Deals", deals = true) })
                        OfferTiles(
                            if (minutes) listOf(
                                Tile("Super Deals", R.drawable.t_store_specials) { state.list("Super Deals", deals = true) },
                                Tile("Fruits & Veggies", R.drawable.t_fruits_veg) { state.go(Route.Aisle("Fruits & Vegetables")) },
                                Tile("Pantry Staples", R.drawable.t_pantry) { state.go(Route.Aisle("Food Cupboard")) },
                                Tile("Mobiles", R.drawable.d_123) { state.list("Mobiles", "mobiles") },
                                Tile("Baby Products", R.drawable.t_baby_care) { state.go(Route.Aisle("Baby care")) },
                            ) else listOf(
                                Tile("Super Deals", R.drawable.t_store_specials) { state.list("Super Deals", deals = true) },
                                Tile("Mobiles", R.drawable.d_123) { state.list("Mobiles", "mobiles") },
                                Tile("Appliances", R.drawable.t_food_processor) { state.go(Route.Aisle("Kitchen Appliances")) },
                                Tile("Electronics", R.drawable.d_78) { state.go(Route.Aisle("Electronics")) },
                                Tile("Fruits & Veg", R.drawable.t_fruits_veg) { state.go(Route.Aisle("Fruits & Vegetables")) },
                            ),
                        )
                        Spacer(Modifier.height(D.Side))
                        SheetCap()
                    }
                }
                item(key = "coupon") { Column(Modifier.background(C.Page).padding(bottom = D.Side)) { CouponStrip(COUPONS) } }
                if (narrow != null) {
                    narrowSections(state, narrow, chips[chip].label, open)
                } else if (minutes) {
                    minutesSections(state, promise, open)
                } else {
                    hyperSections(state, promise, open)
                }
            }
            Box(Modifier.fillMaxWidth()) {
                StoreHeader(state, collapse, C.Campaign, if (minutes) C.Campaign else C.Brand, promise, chips, chip, { chip = it })
            }
        }
    }
}

/** The round aisle row, and the aisles as big cards: picture, best offer, what is in it, when it arrives. */
private fun LazyListScope.aisleSections(state: AppState, promise: String) {
    item(key = "mind") {
        MindRow("What are you shopping for?", MIND.map { d -> Tile(d.name, d.image) { state.go(Route.Aisle(d.name)) } })
    }
    item(key = "aisles") { Column(Modifier.padding(top = D.Side)) { SectionHeader("Aisles to explore") } }
    items(AISLES, key = { "aisle-" + it.name }) { d ->
        val products = remember(d) { Catalog.dept(d.dept) }
        AisleCard(
            d.name, d.image, offer = products.maxOfOrNull { it.offPercent } ?: 0,
            facts = "${products.size} products · from ${fmt(products.minOfOrNull { it.price } ?: 0.0)}",
            holds = d.subs.joinToString(" · ") { it.name }, promise = promise,
            onClick = { state.go(Route.Aisle(d.name)) },
        )
    }
}

private fun LazyListScope.hyperSections(state: AppState, promise: String, open: (Product) -> Unit) {
    item(key = "shelf") {
        DealShelf(state, "Mega Week picks", "Up to 70% off · free delivery over ${fmt(state.freeDeliveryFrom)}", Catalog.deals(30).take(12), open,
            { state.list("Mega Week Deals", deals = true) }, "shelf")
    }
    aisleSections(state, promise)
    item(key = "flash") { Column(Modifier.padding(top = D.Side)) { FlashSaleHeader() } }
    item(key = "g4") { GridSection(state, "Flash sale", pool("toys", "sports", "fashion", "electronics").take(16), open, { state.list("Flash Sale", deals = true) }) }
    item(key = "carousel") { HomeCarousel() }
    item(key = "g8") { GridSection(state, "Groceries", pool("grocery", "fresh", "snacks", "beverages").take(16), open, { state.list("Groceries", "grocery") }) }
    item(key = "cashback") { CashbackBanner(state, Catalog.deals(30).take(10), open) }
    item(key = "g1") { GridSection(state, "Mega Week deals", pool("electronics", "mobiles", "home").take(16), open, { state.list("Mega Week Deals", deals = true) }) }
    item(key = "b5") {
        PromoBanner("UP TO", "60% OFF", "Laundry Essentials", listOf(Color(0xFFE9F3FF), Color(0xFFBFD9FA)),
            listOf(R.drawable.p_ariel, R.drawable.p_persil, R.drawable.p_downy), dark = true) { state.go(Route.Aisle("Household")) }
    }
    item(key = "g5") { GridSection(state, "Laundry essentials", pool("household").take(16), open, { state.list("Laundry Essentials", "household") }) }
    item(key = "b3") {
        PromoBanner("UP TO", "45% OFF", "Mobiles & More", listOf(Color(0xFF0D2346), Color(0xFF2B5EA7), Color(0xFF8FB7E8)),
            listOf(R.drawable.d_123, R.drawable.d_126, R.drawable.d_100)) { state.list("Mobiles & More", "mobiles") }
    }
    item(key = "g3") { GridSection(state, "Mobiles & more", pool("mobiles").take(16), open, { state.list("Mobiles & More", "mobiles") }) }
    item(key = "b6") {
        PromoBanner("WELLNESS", "WEEK", "Beauty & care picks", listOf(Color(0xFFB0145E), Color(0xFFE2408C)),
            listOf(R.drawable.p_perfume, R.drawable.p_hair_oil, R.drawable.p_pantene)) { state.go(Route.Aisle("Beauty & Wellness")) }
    }
    item(key = "g6") { GridSection(state, "Beauty & care", pool("beauty").take(16), open, { state.list("Beauty & Care", "beauty") }) }
    item(key = "b7") {
        PromoBanner("MEGA", "HOME", "Appliances for every room", listOf(Color(0xFFD7262B), Color(0xFFF0434A)),
            listOf(R.drawable.t_food_processor, R.drawable.t_kettle, R.drawable.t_blender)) { state.go(Route.Aisle("Kitchen Appliances")) }
    }
    item(key = "g7") { GridSection(state, "Home & kitchen", pool("home").take(16), open, { state.list("Home & Kitchen", "home") }) }
}

private fun LazyListScope.minutesSections(state: AppState, promise: String, open: (Product) -> Unit) {
    item(key = "mshelf") {
        DealShelf(state, "Grocery super deals", "Delivered fast · free delivery over ${fmt(state.freeDeliveryFrom)}",
            pool("grocery", "snacks", "beverages", "household").take(12), open, { state.list("Grocery Super Deals", "grocery") }, "mshelf")
    }
    aisleSections(state, promise)
    item(key = "mflash") { Column(Modifier.padding(top = D.Side)) { FlashSaleHeader(endsInMs = 6L * 3600_000 + 51 * 60_000 + 27_000) } }
    item(key = "mg3") { GridSection(state, "Flash sale", pool("fresh", "snacks", "beauty").take(16), open, { state.list("Flash Sale", deals = true) }) }
    item(key = "mcar") { HomeCarousel() }
    item(key = "mb2") {
        PromoBanner("UP TO", "60% OFF", "Laundry Essentials", listOf(Color(0xFFE9F3FF), Color(0xFFBFD9FA)),
            listOf(R.drawable.p_ariel, R.drawable.p_persil, R.drawable.p_downy), dark = true) { state.go(Route.Aisle("Household")) }
    }
    item(key = "mg2") { GridSection(state, "Laundry essentials", pool("household").take(16), open, { state.list("Laundry Essentials", "household") }) }
    item(key = "mb4") {
        PromoBanner("WELLNESS", "WEEK", "Beauty & care picks", listOf(Color(0xFFB0145E), Color(0xFFE2408C)),
            listOf(R.drawable.p_perfume, R.drawable.p_hair_oil, R.drawable.p_pantene)) { state.go(Route.Aisle("Beauty & Wellness")) }
    }
    item(key = "mg4") { GridSection(state, "Beauty & care", pool("beauty").take(16), open, { state.list("Beauty & Care", "beauty") }) }
}

private fun LazyListScope.narrowSections(state: AppState, depts: Array<String>, label: String, open: (Product) -> Unit) {
    val items = pool(*depts)
    item(key = "nshelf") {
        DealShelf(state, "Top picks in $label", "Best offers first · free delivery over ${fmt(state.freeDeliveryFrom)}", items.take(12), open,
            { state.list("Top picks", depts.first()) }, "nshelf-$label")
    }
    if (items.size > 12) item(key = "ng1") { GridSection(state, "More in $label", items.drop(12).take(16), open, { state.list("More to explore", depts.first()) }) }
    if (items.size > 28) item(key = "ng2") { GridSection(state, "Keep exploring", items.drop(28).take(16), open, { state.list("More to explore", depts.first()) }) }
}

@Composable
private fun HomeCarousel() {
    BannerCarousel(listOf(
        { SlideCard("MEGA WEEK", "60% OFF", "HOME & DÉCOR", "23 Nov - 03 Dec", listOf(Color(0xFFD7262B), Color(0xFF8E1418)), R.drawable.d_45) },
        { SlideCard("MEGA WEEK", "70% OFF", "SUNGLASSES", "23 Nov - 03 Dec", listOf(Color(0xFF1B1C1E), Color(0xFF4A4D55)), R.drawable.d_154) },
        { SlideCard("MEGA WEEK", "40% OFF", "KITCHEN", "23 Nov - 03 Dec", listOf(Color(0xFF0F7F4A), Color(0xFF0B5032)), R.drawable.t_cooking_pots) },
        { SlideCard("MEGA WEEK", "50% OFF", "FRAGRANCES", "23 Nov - 03 Dec", listOf(Color(0xFF6B21A8), Color(0xFF3B0F63)), R.drawable.p_perfume) },
        { SlideCard("MEGA WEEK", "35% OFF", "GAMING", "23 Nov - 03 Dec", listOf(Color(0xFF0E5A9C), Color(0xFF07325A)), R.drawable.t_wired_headphones) },
    ))
}
