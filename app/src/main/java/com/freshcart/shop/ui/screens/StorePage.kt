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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.freshcart.shop.data.Store
import com.freshcart.shop.ui.BannerCarousel
import com.freshcart.shop.ui.C
import com.freshcart.shop.ui.CampaignHero
import com.freshcart.shop.ui.CashbackBanner
import com.freshcart.shop.ui.Chip
import com.freshcart.shop.ui.CouponStrip
import com.freshcart.shop.ui.DeptStrip
import com.freshcart.shop.ui.FlashSaleHeader
import com.freshcart.shop.ui.GridSection
import com.freshcart.shop.ui.HeroTiles
import com.freshcart.shop.ui.PromoBanner
import com.freshcart.shop.ui.SectionTitle
import com.freshcart.shop.ui.SlideCard
import com.freshcart.shop.ui.StoreHeader
import com.freshcart.shop.ui.T
import com.freshcart.shop.ui.Tile
import com.freshcart.shop.ui.list
import com.freshcart.shop.ui.open

private val COUPONS = listOf(10 to "SAVE10", 15 to "FRESH15", 50 to "MEGA50", 100 to "BIG100")

private fun pool(vararg depts: String): List<Product> =
    Catalog.dept(*depts).sortedByDescending { it.offPercent }

/** Products for the selected chip: chip 0 is the campaign mix, the rest narrow every section to one department. */
private fun chipDepts(store: Store, chip: Int): Array<String>? = when (store) {
    Store.Minutes -> when (chip) { 1 -> arrayOf("toys", "beauty", "fashion"); 2 -> arrayOf("electronics", "mobiles"); 3 -> arrayOf("grocery", "fresh", "beverages", "snacks"); 4 -> arrayOf("beauty", "fashion"); else -> null }
    else -> when (chip) { 1 -> arrayOf("toys", "beauty", "fashion"); 2 -> arrayOf("electronics", "mobiles"); 3 -> arrayOf("beauty"); 4 -> arrayOf("electronics", "mobiles"); 5 -> arrayOf("grocery", "fresh", "beverages", "snacks"); 6 -> arrayOf("home"); else -> null }
}

@Composable
fun HomeScreen(state: AppState) = StorePage(state, Store.Hypermarket)

@Composable
fun MinutesScreen(state: AppState) = StorePage(state, Store.Minutes)

@Composable
fun StorePage(state: AppState, store: Store) {
    val list = rememberLazyListState()
    val density = LocalDensity.current
    val maxCollapse = with(density) { com.freshcart.shop.ui.HEADER_COLLAPSE.toPx() }
    val collapse by remember {
        derivedStateOf { if (list.firstVisibleItemIndex > 0) maxCollapse else list.firstVisibleItemScrollOffset.toFloat().coerceAtMost(maxCollapse) }
    }
    var chip by remember(store) { mutableIntStateOf(0) }
    val minutes = store == Store.Minutes
    val chips = if (minutes) listOf(
        Chip("MEGA", Icons.Rounded.Bolt), Chip("Gifting Store", Icons.Outlined.CardGiftcard), Chip("Electronics", Icons.Outlined.Headphones),
        Chip("Grocery", Icons.Outlined.LocalGroceryStore), Chip("Premium", Icons.Outlined.WorkspacePremium),
    ) else listOf(
        Chip("MEGA", Icons.Rounded.Bolt), Chip("Gifting Store", Icons.Outlined.CardGiftcard), Chip("Gaming", Icons.Outlined.SportsEsports),
        Chip("Beauty", Icons.Outlined.Spa), Chip("Electronics", Icons.Outlined.Headphones), Chip("Grocery", Icons.Outlined.LocalGroceryStore),
        Chip("Home", Icons.Outlined.Kitchen), Chip("Fashion", Icons.Outlined.Checkroom),
    )
    val headerFull = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + com.freshcart.shop.ui.HEADER_FULL
    val narrow = chipDepts(store, chip)
    val open: (Product) -> Unit = { state.open(it.id) }

    Box(Modifier.fillMaxSize().background(C.Page)) {
        LazyColumn(
            state = list, modifier = Modifier.fillMaxSize().testTag("store_list"),
            contentPadding = PaddingValues(top = headerFull, bottom = 150.dp),
        ) {
            item(key = "hero") {
                Column(Modifier.fillMaxWidth().background(C.Campaign)) {
                    CampaignHero()
                    HeroTiles(
                        if (minutes) listOf(
                            Tile("Super Deals", R.drawable.t_store_specials) { state.list("Super Deals", deals = true) },
                            Tile("Fruits & Veggies", R.drawable.t_fruits_veg) { state.list("Fruits & Veggies", "fresh") },
                            Tile("Pantry Staples", R.drawable.t_pantry) { state.list("Pantry Staples", "grocery") },
                            Tile("Mobiles", R.drawable.d_123) { state.list("Mobiles", "mobiles") },
                            Tile("Baby Products", R.drawable.t_baby_care) { state.list("Baby Products", "household") },
                        ) else listOf(
                            Tile("Super Deals", R.drawable.t_store_specials) { state.list("Super Deals", deals = true) },
                            Tile("Mobiles", R.drawable.d_123) { state.list("Mobiles", "mobiles") },
                            Tile("Appliances", R.drawable.t_food_processor) { state.list("Appliances", "home") },
                            Tile("Electronics", R.drawable.d_78) { state.list("Electronics", "electronics") },
                            Tile("Fruits & Veg", R.drawable.t_fruits_veg) { state.list("Fruits & Veg", "fresh") },
                        ),
                    )
                    Spacer(Modifier.height(10.dp))
                }
            }
            item(key = "coupon") { Column(Modifier.background(C.Page).padding(vertical = 12.dp)) { CouponStrip(COUPONS) } }
            if (narrow != null) {
                narrowSections(state, narrow, open)
            } else if (minutes) {
                minutesSections(state, open)
            } else {
                hyperSections(state, open)
            }
        }
        Box(Modifier.fillMaxWidth()) {
            StoreHeader(
                state, collapse, C.Campaign, if (minutes) C.Campaign else C.Brand,
                if (minutes) "Get it by 4 PM" else "Today, 8:00 pm – 10:00 pm",
                chips, chip, { chip = it },
            )
        }
    }
}

private fun LazyListScope.hyperSections(state: AppState, open: (Product) -> Unit) {
    item(key = "cashback") { CashbackBanner(state, Catalog.deals(30).take(10), open) }
    item(key = "g1") { GridSection(state, pool("electronics", "mobiles", "home").take(16), open, { state.list("Mega Week Deals", deals = true) }) }
    item(key = "t2") {
        Column(Modifier.fillMaxWidth().background(C.Surface).padding(top = 18.dp, bottom = 6.dp)) {
            SectionTitle("Mega Week Exclusive Deals")
        }
    }
    item(key = "g2") { GridSection(state, pool("mobiles", "electronics").drop(4).take(16), open, { state.list("Exclusive Deals", "electronics") }, bg = C.Surface, top = 4.dp, viewAllBg = Color(0xFFE9E9EA)) }
    item(key = "b3") {
        PromoBanner("UP TO", "45% OFF", "Mobiles & More", listOf(Color(0xFF0D2346), Color(0xFF2B5EA7), Color(0xFF8FB7E8)),
            listOf(R.drawable.d_123, R.drawable.d_126, R.drawable.d_100)) { state.list("Mobiles & More", "mobiles") }
    }
    item(key = "g3") { GridSection(state, pool("mobiles").take(16), open, { state.list("Mobiles & More", "mobiles") }) }
    item(key = "flash") { FlashSaleHeader() }
    item(key = "g4") { GridSection(state, pool("toys", "sports", "fashion", "electronics").take(16), open, { state.list("Flash Sale", deals = true) }, top = 0.dp) }
    item(key = "carousel") { HomeCarousel() }
    item(key = "b5") {
        PromoBanner("UP TO", "60% OFF", "Laundry Essentials", listOf(Color(0xFFE9F3FF), Color(0xFFBFD9FA)),
            listOf(R.drawable.p_ariel, R.drawable.p_persil, R.drawable.p_downy), dark = true) { state.list("Laundry Essentials", "household") }
    }
    item(key = "g5") { GridSection(state, pool("household").take(16), open, { state.list("Laundry Essentials", "household") }) }
    item(key = "b6") {
        PromoBanner("WELLNESS", "WEEK", "Beauty & care picks", listOf(Color(0xFFB0145E), Color(0xFFE2408C)),
            listOf(R.drawable.p_perfume, R.drawable.p_hair_oil, R.drawable.p_pantene)) { state.list("Beauty & Care", "beauty") }
    }
    item(key = "g6") { GridSection(state, pool("beauty").take(16), open, { state.list("Beauty & Care", "beauty") }, bg = Color(0xFFD81B70)) }
    item(key = "b7") {
        PromoBanner("MEGA", "HOME", "Appliances for every room", listOf(Color(0xFFD7262B), Color(0xFFF0434A)),
            listOf(R.drawable.t_food_processor, R.drawable.t_kettle, R.drawable.t_blender)) { state.list("Home & Kitchen", "home") }
    }
    item(key = "d7") {
        DeptStrip(listOf(
            Tile("Large Appliances", R.drawable.t_fans) { state.list("Large Appliances", "home") },
            Tile("Home Appliances", R.drawable.t_irons) { state.list("Home Appliances", "home") },
            Tile("Kitchen Appliances", R.drawable.t_kettle) { state.list("Kitchen Appliances", "home") },
            Tile("Food Processors", R.drawable.t_food_processor) { state.list("Food Processors", "home") },
            Tile("Garment Steamers", R.drawable.t_steamer) { state.list("Garment Steamers", "home") },
        ))
    }
    item(key = "g7") { GridSection(state, pool("home").take(16), open, { state.list("Home & Kitchen", "home") }, top = 0.dp) }
    item(key = "b8") {
        PromoBanner("MEGA", "FRESH", "Groceries at their best", listOf(Color(0xFFD7262B), Color(0xFFF56A3F)),
            listOf(R.drawable.p_eggs, R.drawable.p_basmati, R.drawable.p_salmon)) { state.list("Groceries", "grocery") }
    }
    item(key = "d8") {
        DeptStrip(listOf(
            Tile("Dairy, Eggs & Cheese", R.drawable.t_butter) { state.list("Dairy & Eggs", "fresh") },
            Tile("Snacks", R.drawable.t_chocolates) { state.list("Snacks", "snacks") },
            Tile("Rice & Pasta", R.drawable.t_rice_pasta) { state.list("Rice & Pasta", "grocery") },
            Tile("Beverages", R.drawable.t_juices) { state.list("Beverages", "beverages") },
            Tile("Bakery", R.drawable.t_bakery) { state.list("Bakery", "grocery") },
        ))
    }
    item(key = "g8") { GridSection(state, pool("grocery", "fresh", "snacks", "beverages").take(16), open, { state.list("Groceries", "grocery") }, top = 0.dp) }
}

private fun LazyListScope.minutesSections(state: AppState, open: (Product) -> Unit) {
    item(key = "mt") {
        Column(Modifier.fillMaxWidth().background(C.Page).padding(top = 4.dp, bottom = 6.dp)) { SectionTitle("Grocery Super Deals") }
    }
    item(key = "mg1") { GridSection(state, pool("grocery", "snacks", "beverages", "household").take(16), open, { state.list("Grocery Super Deals", "grocery") }, bg = C.Page, top = 2.dp, viewAllBg = Color(0xFFE9E9EA)) }
    item(key = "mb2") {
        PromoBanner("UP TO", "60% OFF", "Laundry Essentials", listOf(Color(0xFFE9F3FF), Color(0xFFBFD9FA)),
            listOf(R.drawable.p_ariel, R.drawable.p_persil, R.drawable.p_downy), dark = true) { state.list("Laundry Essentials", "household") }
    }
    item(key = "mg2") { GridSection(state, pool("household").take(16), open, { state.list("Laundry Essentials", "household") }) }
    item(key = "mflash") { FlashSaleHeader(endsInMs = 6L * 3600_000 + 51 * 60_000 + 27_000) }
    item(key = "mg3") { GridSection(state, pool("fresh", "snacks", "beauty").take(16), open, { state.list("Flash Sale", deals = true) }, top = 0.dp) }
    item(key = "mcar") { HomeCarousel() }
    item(key = "mb4") {
        PromoBanner("WELLNESS", "WEEK", "Beauty & care picks", listOf(Color(0xFFB0145E), Color(0xFFE2408C)),
            listOf(R.drawable.p_perfume, R.drawable.p_hair_oil, R.drawable.p_pantene)) { state.list("Beauty & Care", "beauty") }
    }
    item(key = "mg4") { GridSection(state, pool("beauty").take(16), open, { state.list("Beauty & Care", "beauty") }, bg = Color(0xFFD81B70)) }
}

private fun LazyListScope.narrowSections(state: AppState, depts: Array<String>, open: (Product) -> Unit) {
    val items = pool(*depts)
    item(key = "nt") { Column(Modifier.fillMaxWidth().background(C.Surface).padding(top = 16.dp, bottom = 6.dp)) { SectionTitle("Top picks for you") } }
    item(key = "ng1") { GridSection(state, items.take(16), open, { state.list("Top picks", depts.first()) }, bg = C.Surface, top = 4.dp, viewAllBg = Color(0xFFE9E9EA)) }
    if (items.size > 16) item(key = "ng2") { GridSection(state, items.drop(16).take(16), open, { state.list("More to explore", depts.first()) }) }
    item(key = "nend") { Spacer(Modifier.height(12.dp)); Text("", style = T.small) }
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
