package com.freshcart.shop.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshcart.shop.R
import com.freshcart.shop.data.AppState
import com.freshcart.shop.data.Catalog
import com.freshcart.shop.data.Product
import com.freshcart.shop.data.Route
import com.freshcart.shop.ui.AddButton
import com.freshcart.shop.ui.AddKind
import com.freshcart.shop.ui.C
import com.freshcart.shop.ui.D
import com.freshcart.shop.ui.IconCircle
import com.freshcart.shop.ui.LocalHeroOrigin
import com.freshcart.shop.ui.LocalSeen
import com.freshcart.shop.ui.M
import com.freshcart.shop.ui.OldPrice
import com.freshcart.shop.ui.Plex
import com.freshcart.shop.ui.Price
import com.freshcart.shop.ui.SearchField
import com.freshcart.shop.ui.T
import com.freshcart.shop.ui.ViewCartPill
import com.freshcart.shop.ui.appear
import com.freshcart.shop.ui.hero
import com.freshcart.shop.ui.open
import com.freshcart.shop.ui.pressable
import com.freshcart.shop.ui.reducedMotion

data class Sub(val name: String, val image: Int, val query: String? = null)
data class Dept(val name: String, val image: Int, val dept: String, val subs: List<Sub>)
data class Group(val title: String, val depts: List<Dept>)

val CATEGORY_GROUPS = listOf(
    Group("FRESH FOOD", listOf(
        Dept("Fruits & Vegetables", R.drawable.t_fruits_veg, "fresh", listOf(
            Sub("Fruits", R.drawable.t_exotic_fruits), Sub("Vegetables", R.drawable.t_vegetables), Sub("Salad Vegetables", R.drawable.t_salad),
            Sub("Fruit Cuts", R.drawable.t_fruit_cuts), Sub("Exotic Fruits", R.drawable.d_16))),
        Dept("Dairy & Eggs", R.drawable.t_butter, "fresh", listOf(
            Sub("Eggs", R.drawable.p_eggs, "eggs"), Sub("Butter", R.drawable.t_butter), Sub("Cheese", R.drawable.p_parmesan, "parmigiano"),
            Sub("Juices", R.drawable.t_juices), Sub("Desserts", R.drawable.t_bake_art))),
        Dept("Butchery", R.drawable.t_butchery, "fresh", listOf(
            Sub("Fresh Fish", R.drawable.t_fresh_fish, "salmon"), Sub("Fresh Chicken", R.drawable.t_fresh_chicken, "chicken"),
            Sub("Fresh Lamb", R.drawable.t_fresh_lamb), Sub("Beef", R.drawable.t_beef), Sub("Deli", R.drawable.t_deli))),
        Dept("Store Specials", R.drawable.t_store_specials, "grocery", listOf(
            Sub("Kitchen", R.drawable.t_kitchen), Sub("Bake Art", R.drawable.t_bake_art), Sub("Roastery", R.drawable.t_roastery, "walnut"),
            Sub("Artisanal Snacks", R.drawable.t_artisanal_snacks))),
    )),
    Group("GROCERY ESSENTIALS", listOf(
        Dept("Food Cupboard", R.drawable.t_food_cupboard, "grocery", listOf(
            Sub("Rice, Pasta & Noodles", R.drawable.t_rice_pasta, "rice"), Sub("Cooking Ingredients", R.drawable.t_cooking_ingredients, "oil"),
            Sub("Sugar, Flour & More", R.drawable.t_sugar_flour), Sub("Premium Staples", R.drawable.t_premium_staples), Sub("Pantry", R.drawable.t_pantry))),
        Dept("Frozen Food", R.drawable.t_frozen_food, "fresh", listOf(
            Sub("Frozen Fish", R.drawable.t_frozen_fish), Sub("Frozen Meat", R.drawable.t_frozen_meat, "chicken"),
            Sub("Ready Meals", R.drawable.t_ready_meals), Sub("Appetizers", R.drawable.t_appetizers))),
        Dept("Bakery", R.drawable.t_bakery, "grocery", listOf(
            Sub("Bread & Rolls", R.drawable.t_bread_rolls), Sub("Croissants", R.drawable.t_croissant), Sub("Cake & Sweets", R.drawable.t_cake),
            Sub("Snacks", R.drawable.t_artisanal_snacks))),
        Dept("Beverages", R.drawable.t_juices, "beverages", listOf(
            Sub("Water", R.drawable.t_water), Sub("Soft Drinks", R.drawable.t_craft_bev), Sub("Coffee", R.drawable.t_coffee, "coffee"),
            Sub("Tea", R.drawable.t_tea), Sub("Juices", R.drawable.t_juices, "juice"))),
        Dept("Snacks & Sweets", R.drawable.t_chocolates, "snacks", listOf(
            Sub("Chocolates", R.drawable.t_chocolates, "chocolate"), Sub("Nuts & Dates", R.drawable.t_nuts_dates), Sub("Candy", R.drawable.p_chupa_chups, "lollipop"),
            Sub("Gift Boxes", R.drawable.p_choc_box, "box"))),
    )),
    Group("ELECTRONICS & APPLIANCES", listOf(
        Dept("Electronics", R.drawable.d_78, "electronics", listOf(
            Sub("Mobiles", R.drawable.d_123), Sub("Tablets", R.drawable.d_160, "tab"), Sub("Laptops", R.drawable.d_78, "laptop"),
            Sub("Mobile Accessories", R.drawable.t_mobile_acc), Sub("Audio", R.drawable.d_101, "airpods"), Sub("Cameras", R.drawable.t_cameras))),
        Dept("Gaming, Audio & more", R.drawable.t_wired_headphones, "electronics", listOf(
            Sub("Headphones", R.drawable.t_wired_headphones, "airpods"), Sub("Smart Speakers", R.drawable.d_99, "echo"), Sub("IT Accessories", R.drawable.t_it_acc),
            Sub("Chargers", R.drawable.d_104, "charger"))),
        Dept("Kitchen Appliances", R.drawable.t_kettle, "home", listOf(
            Sub("Food Processors", R.drawable.t_food_processor), Sub("Blenders", R.drawable.t_blender, "blender"), Sub("Kettles", R.drawable.t_kettle),
            Sub("Cookware", R.drawable.t_cooking_pots))),
        Dept("Home Appliances", R.drawable.t_irons, "home", listOf(
            Sub("Fans", R.drawable.t_fans), Sub("Irons", R.drawable.t_irons), Sub("Garment Steamers", R.drawable.t_steamer), Sub("Lamps", R.drawable.d_47, "lamp"))),
    )),
    Group("HOUSEHOLD ESSENTIALS", listOf(
        Dept("Household", R.drawable.t_household, "household", listOf(
            Sub("Laundry", R.drawable.p_ariel, "laundry"), Sub("Cleaning Essentials", R.drawable.p_dettol), Sub("Dishwashing", R.drawable.t_dishwashing),
            Sub("Fabric Care", R.drawable.p_downy, "fabric"))),
        Dept("Beauty & Wellness", R.drawable.t_skincare, "beauty", listOf(
            Sub("Makeup", R.drawable.t_makeup), Sub("Fragrances", R.drawable.p_perfume, "parfum"), Sub("Shower & Bath", R.drawable.t_shower),
            Sub("Hair Care", R.drawable.p_pantene, "hair"), Sub("Skin Care", R.drawable.t_skincare), Sub("Dental", R.drawable.t_dental))),
        Dept("Baby care", R.drawable.t_baby_care, "household", listOf(
            Sub("Diapers & Wipes", R.drawable.p_voi_wipes, "wipes"), Sub("Baby Lotion", R.drawable.t_baby_care), Sub("Feeding", R.drawable.t_feeding))),
        Dept("Pet Essentials", R.drawable.t_dog_food, "grocery", listOf(
            Sub("Dog Food", R.drawable.t_dog_food), Sub("Cat Food", R.drawable.d_18, "cat"))),
    )),
    Group("HOME & LIVING", listOf(
        Dept("Cooking & Dining", R.drawable.t_cooking_dining, "home", listOf(
            Sub("Cookware Sets", R.drawable.t_cooking_pots), Sub("Kitchen Tools", R.drawable.d_48, "spatula"), Sub("Dinner Sets", R.drawable.d_49, "cup"),
            Sub("Chopping Boards", R.drawable.d_53, "board"))),
        Dept("Home Furnishing", R.drawable.d_12, "home", listOf(
            Sub("Bedding", R.drawable.d_11, "bed"), Sub("Home Décor", R.drawable.d_45), Sub("Plant Pots", R.drawable.d_46, "plant"), Sub("Lighting", R.drawable.d_47, "lamp"))),
        Dept("Kids Corner", R.drawable.p_teddy, "toys", listOf(
            Sub("Toys", R.drawable.p_teddy), Sub("Stationery", R.drawable.t_stationery), Sub("Sports & Fitness", R.drawable.d_140))),
    )),
    Group("LIFESTYLE", listOf(
        Dept("Fashion & Luggage", R.drawable.d_172, "fashion", listOf(
            Sub("Bags", R.drawable.d_174, "bag"), Sub("Watches", R.drawable.d_94, "watch"), Sub("Sunglasses", R.drawable.d_154, "glasses"),
            Sub("Shoes", R.drawable.d_88, "nike"), Sub("Jewellery", R.drawable.d_182, "earring"))),
        Dept("Gifting", R.drawable.t_beauty_gifts, "beauty", listOf(
            Sub("Gifts for Him", R.drawable.t_gifts_him), Sub("Beauty Gifts", R.drawable.t_beauty_gifts), Sub("Wallets", R.drawable.p_wallet, "wallet"))),
    )),
)

@Composable
fun TitleBar(title: String, modifier: Modifier = Modifier, size: androidx.compose.ui.unit.TextUnit = 15.5.sp, end: @Composable (() -> Unit)? = null) {
    Column(modifier.fillMaxWidth().background(Color.White)) {
        Box(Modifier.fillMaxWidth().statusBarsPadding().height(40.dp)) {
            Text(title, style = TextStyle(fontFamily = Plex, fontSize = size, fontWeight = FontWeight.Bold, lineHeight = 20.sp), color = C.Ink, modifier = Modifier.align(Alignment.Center))
            if (end != null) Box(Modifier.align(Alignment.CenterEnd).padding(end = 12.dp)) { end() }
        }
        HorizontalDivider(color = C.Line, thickness = 0.6.dp)
    }
}

// ---------------------------------------------------------------- Categories: every aisle on one page

/**
 * Categories as a page of tiles: each group has a heading and its aisles in a grid of four, a picture on a soft
 * ground with the name under it. Tapping one opens that aisle.
 */
@Composable
fun CategoriesScreen(state: AppState) {
    val seen = remember { mutableSetOf<Any>() }
    CompositionLocalProvider(LocalSeen provides seen) {
        Column(Modifier.fillMaxSize().background(C.Page)) {
            TitleBar("Categories", size = 15.5.sp)
            LazyColumn(Modifier.fillMaxSize().testTag("categories_list"), contentPadding = PaddingValues(bottom = 150.dp)) {
                item(key = "search") {
                    Spacer(Modifier.height(D.GridGap))
                    SearchField(state, bg = C.Surface)
                }
                CATEGORY_GROUPS.forEachIndexed { g, group ->
                    item(key = group.title) {
                        Text(group.title.lowercase().replaceFirstChar { it.uppercase() }, style = T.h2, color = C.Ink,
                            modifier = Modifier.padding(start = D.GridGap, top = 20.dp, bottom = D.GridGap))
                    }
                    val rows = group.depts.chunked(4)
                    rows.forEachIndexed { r, row ->
                        item(key = group.title + r) {
                            Row(Modifier.fillMaxWidth().padding(horizontal = D.GridGap).padding(bottom = D.GridGap), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                row.forEachIndexed { k, d -> AisleTile(d, Modifier.weight(1f).appear(g + r + k, "cat-${d.name}")) { state.go(Route.Aisle(d.name)) } }
                                repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** One aisle on the categories page: the whole picture on a soft rounded ground, name on two lines under it. */
@Composable
private fun AisleTile(d: Dept, modifier: Modifier, onClick: () -> Unit) {
    Column(modifier.pressable(onClick).testTag("aisle_${d.dept}_${d.name.take(3).lowercase()}"), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painterResource(d.image), null, contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth().aspectRatio(0.95f).clip(RoundedCornerShape(D.R12)).background(C.BrandSoft).padding(8.dp))
        Spacer(Modifier.height(8.dp))
        Text(d.name, style = T.smallM, color = C.Ink, maxLines = 2, minLines = 2, textAlign = TextAlign.Center, overflow = TextOverflow.Ellipsis)
    }
}

// ---------------------------------------------------------------- One aisle: sections in a rail, products beside it

/** The products of one rail entry. Entry 0 is the whole department; the others narrow it by their word, or reshuffle it. */
private fun aisleProducts(dept: Dept, index: Int, sub: Sub): List<Product> {
    val all = Catalog.dept(dept.dept).sortedByDescending { it.offPercent }
    if (index == 0 || all.isEmpty()) return all
    val words = (sub.query ?: sub.name).lowercase().split(' ', ',', '&').map { it.trim().trimEnd('s') }.filter { it.length > 2 }
    val hits = all.filter { p -> words.any { w -> p.name.lowercase().contains(w) } }
    return hits.ifEmpty { all.drop(index % all.size) + all.take(index % all.size) }
}

/**
 * One department as an aisle. A rail on the left holds its sections as round pictures with a bar marking the one
 * showing; the products fill the rest in two columns, under a row of filter chips. The rail slides in from the left
 * edge and the products from the right as the page opens, like a drawer; changing section slides the products up or
 * down the way the rail moved.
 */
@Composable
fun AisleScreen(state: AppState, r: Route.Aisle) {
    val dept = remember(r.dept) { CATEGORY_GROUPS.flatMap { it.depts }.firstOrNull { it.name == r.dept } }
    if (dept == null) { LaunchedEffect(Unit) { state.back() }; return }
    val rail = remember(dept) { listOf(Sub("All", dept.image)) + dept.subs }
    var sel by remember(r) { mutableIntStateOf(r.sub.coerceIn(0, rail.lastIndex)) }
    var sort by remember { mutableIntStateOf(0) }
    var offers by remember { mutableStateOf(false) }
    val sortNames = listOf("Sort", "Price: low to high", "Price: high to low", "Biggest discount")
    val reduced = reducedMotion()
    val enter = remember { Animatable(if (reduced) 1f else 0f) }
    LaunchedEffect(Unit) { enter.animateTo(1f, M.sheet()) }
    val railPx = with(LocalDensity.current) { D.RailW.toPx() }
    val seen = remember { mutableSetOf<Any>() }

    CompositionLocalProvider(LocalSeen provides seen, LocalHeroOrigin provides "aisle") {
        Box(Modifier.fillMaxSize().background(C.Page)) {
            Column(Modifier.fillMaxSize()) {
                Row(Modifier.fillMaxWidth().statusBarsPadding().height(56.dp).padding(horizontal = D.GridGap), verticalAlignment = Alignment.CenterVertically) {
                    IconCircle(Icons.AutoMirrored.Rounded.ArrowBack, "Back", { state.back() })
                    Spacer(Modifier.width(D.GridGap))
                    Column(Modifier.weight(1f)) {
                        Text(dept.name, style = T.h3.copy(fontWeight = FontWeight.Bold), color = C.Ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Row {
                            Text("Delivering to ", style = T.small, color = C.Brand)
                            Text(state.address.label, style = T.smallM, color = C.Ink, maxLines = 1)
                        }
                    }
                    IconCircle(Icons.Rounded.Search, "Search", { state.go(Route.Search()) })
                }
                HorizontalDivider(color = C.Line)
                Row(Modifier.fillMaxSize()) {
                    Rail(rail, sel, Modifier.graphicsLayer { translationX = -(1f - enter.value) * railPx; alpha = enter.value.coerceIn(0f, 1f) }) { sel = it }
                    Box(Modifier.width(1.dp).fillMaxHeight().background(C.Line))
                    Column(Modifier.weight(1f).fillMaxHeight().graphicsLayer { translationX = (1f - enter.value) * railPx * 0.6f; alpha = enter.value.coerceIn(0f, 1f) }) {
                        Row(Modifier.fillMaxWidth().padding(horizontal = D.GridGap).height(D.Touch), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            AisleChip(Icons.Rounded.Tune, "Filters", false) { }
                            AisleChip(Icons.Rounded.SwapVert, sortNames[sort], sort != 0) { sort = (sort + 1) % sortNames.size }
                            AisleChip(Icons.Outlined.LocalOffer, "Offers", offers) { offers = !offers }
                        }
                        AnimatedContent(
                            sel, label = "aisle", modifier = Modifier.fillMaxSize(),
                            transitionSpec = {
                                val down = targetState > initialState
                                (slideInVertically(M.bar()) { if (down) it / 5 else -it / 5 } + fadeIn(M.bar())) togetherWith
                                    (slideOutVertically(M.bar()) { if (down) -it / 5 else it / 5 } + fadeOut(M.snap()))
                            },
                        ) { index ->
                            val base = remember(dept, index) { aisleProducts(dept, index, rail[index]) }
                            val items = base.filter { !offers || it.offPercent > 0 }.let { l ->
                                when (sort) { 1 -> l.sortedBy { it.price }; 2 -> l.sortedByDescending { it.price }; 3 -> l.sortedByDescending { it.offPercent }; else -> l }
                            }
                            if (items.isEmpty()) {
                                Column(Modifier.fillMaxSize().padding(D.R24), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Spacer(Modifier.height(48.dp))
                                    Text("Nothing on offer here right now", style = T.h3, color = C.Ink, textAlign = TextAlign.Center)
                                    Spacer(Modifier.height(8.dp))
                                    Text("Switch Offers off to see everything in ${rail[index].name}.", style = T.body, color = C.Ink2, textAlign = TextAlign.Center)
                                }
                            } else {
                                LazyVerticalGrid(
                                    GridCells.Fixed(2), Modifier.fillMaxSize().testTag("product_grid"),
                                    contentPadding = PaddingValues(start = D.GridGap, end = D.GridGap, top = 4.dp, bottom = 150.dp),
                                    horizontalArrangement = Arrangement.spacedBy(D.GridGap), verticalArrangement = Arrangement.spacedBy(D.Side),
                                ) {
                                    itemsIndexed(items, key = { _, p -> p.id }) { i, p -> AisleProduct(state, p, Modifier.animateItem().appear(i, "$index-${p.id}")) }
                                }
                            }
                        }
                    }
                }
            }
            ViewCartPill(state, Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = D.GridGap))
        }
    }
}

/** The side rail: one round picture and label per section, and a bar on its inner edge that springs to the chosen one. */
@Composable
private fun Rail(items: List<Sub>, selected: Int, modifier: Modifier, onSelect: (Int) -> Unit) {
    val haptic = LocalHapticFeedback.current
    val y by animateDpAsState(D.RailItemH * selected, M.bar(), label = "railBar")
    Box(modifier.width(D.RailW).fillMaxHeight().verticalScroll(rememberScrollState()).padding(bottom = 150.dp).testTag("aisle_rail")) {
        Column {
            items.forEachIndexed { i, s ->
                val sel = i == selected
                val on by animateFloatAsState(if (sel) 1f else 0f, M.pop(), label = "railOn")
                Column(
                    Modifier.fillMaxWidth().height(D.RailItemH).testTag("rail_$i")
                        .clickable(remember { MutableInteractionSource() }, null) {
                            if (!sel) { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); onSelect(i) }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
                ) {
                    Box(Modifier.size(56.dp).background(if (sel) C.BrandSoft else C.Surface, CircleShape), contentAlignment = Alignment.BottomCenter) {
                        Image(painterResource(s.image), null, contentScale = ContentScale.Crop,
                            modifier = Modifier.size(48.dp).graphicsLayer { val k = 1f + 0.16f * on; scaleX = k; scaleY = k; translationY = -4.dp.toPx() * (1f - on * 0.5f) }.clip(CircleShape))
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(s.name, style = if (sel) T.railLabel.copy(fontWeight = FontWeight.Bold) else T.railLabel, color = if (sel) C.Ink else C.Ink2,
                        maxLines = 2, textAlign = TextAlign.Center, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(horizontal = 4.dp))
                }
            }
        }
        Box(Modifier.align(Alignment.TopEnd).offset(y = y + 12.dp).width(4.dp).height(D.RailItemH - 24.dp).background(C.Brand, RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp)))
    }
}

/** Filter chip of the aisle page: 36 dp drawn, filled with the soft brand colour while it is on. */
@Composable
private fun AisleChip(icon: ImageVector, label: String, on: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(10.dp)
    Row(
        Modifier.height(36.dp).background(if (on) C.BrandSoft else C.Page, shape).border(1.dp, if (on) C.Brand else C.Line, shape).clip(shape)
            .clickable(onClick = onClick).padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = if (on) C.Brand else C.Ink, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(label, style = T.smallM, color = if (on) C.Brand else C.Ink, maxLines = 1)
    }
}

/**
 * Product card of the aisle page: the picture in a bordered tile with the pack size written on its lower left and
 * ADD hanging over its lower right corner; under it the price, large, then the name on up to three lines and the rating.
 */
@Composable
private fun AisleProduct(state: AppState, p: Product, modifier: Modifier) {
    val hero = "${LocalHeroOrigin.current}-${p.id}"
    Column(modifier.fillMaxWidth().pressable { state.pendingHero = hero; state.open(p.id) }.testTag("card_${p.id}")) {
        Box(Modifier.fillMaxWidth()) {
            Box(Modifier.fillMaxWidth().aspectRatio(0.92f).clip(RoundedCornerShape(D.R12)).background(C.Card).border(1.dp, C.Line, RoundedCornerShape(D.R12))) {
                Image(painterResource(p.image), p.name, contentScale = ContentScale.Fit,
                    modifier = Modifier.hero("img-$hero", !state.heroOpen(hero)).fillMaxSize().padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 36.dp))
                if (p.offPercent > 0) {
                    Text("${p.offPercent}% OFF", style = T.badge.copy(lineHeight = 16.sp), color = C.OffBlue,
                        modifier = Modifier.padding(start = 6.dp, top = 6.dp).background(Color.White, RoundedCornerShape(D.R12)).padding(horizontal = 8.dp, vertical = 2.dp))
                }
                Text(p.unit, style = T.cardUnit, color = C.Ink, maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.align(Alignment.BottomStart).padding(start = 8.dp, bottom = 10.dp, end = 76.dp).testTag("unit_${p.id}"))
            }
            AddButton(state, p, Modifier.align(Alignment.BottomEnd).offset(x = 4.dp, y = 6.dp), AddKind.Word)
        }
        Row(Modifier.padding(top = D.GridGap), verticalAlignment = Alignment.CenterVertically) {
            Price(p.price, T.h2, C.Ink, 11.dp)
            if (p.oldPrice > p.price) { Spacer(Modifier.width(6.dp)); OldPrice(p.oldPrice, T.cardUnit.fontSize) }
        }
        Text(p.name, style = T.body, color = C.Ink, maxLines = 3, minLines = 2, overflow = TextOverflow.Ellipsis)
        Row(Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Star, null, tint = C.Star, modifier = Modifier.size(12.dp))
            Text(" %.1f".format(p.rating), style = T.cardUnit, color = C.Ink2)
        }
    }
}
