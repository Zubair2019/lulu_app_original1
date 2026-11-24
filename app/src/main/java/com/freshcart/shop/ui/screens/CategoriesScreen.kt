package com.freshcart.shop.ui.screens


import com.freshcart.shop.ui.Plex
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshcart.shop.R
import com.freshcart.shop.data.AppState
import com.freshcart.shop.data.Route
import com.freshcart.shop.ui.C
import com.freshcart.shop.ui.T
import com.freshcart.shop.ui.pressable

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

@Composable
fun CategoryTile(name: String, image: Int, chevron: Boolean, onClick: () -> Unit) {
    Column(
        Modifier.width(75.dp).height(95.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFE1E1E1)).pressable(onClick),
    ) {
        Row(Modifier.fillMaxWidth().padding(start = 5.dp, top = 6.dp, end = 3.dp), verticalAlignment = Alignment.Top) {
            Text(name, style = TextStyle(fontFamily = Plex, fontSize = 11.sp, fontWeight = FontWeight.Bold, lineHeight = 12.sp, letterSpacing = (-0.2).sp), color = C.Ink, maxLines = 2, modifier = Modifier.weight(1f))
            if (chevron) Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = C.Ink, modifier = Modifier.size(14.dp))
        }
        Image(painterResource(image), name, contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 4.dp))
    }
}

@Composable
fun CategoriesScreen(state: AppState) {
    Column(Modifier.fillMaxSize().background(Color(0xFFEBEBEC))) {
        TitleBar("Categories", size = 14.sp)
        LazyColumn(Modifier.fillMaxSize().testTag("categories_list"), contentPadding = PaddingValues(bottom = 150.dp)) {
            CATEGORY_GROUPS.forEach { g ->
                item(key = g.title) {
                    Text(g.title, style = TextStyle(fontFamily = Plex, fontSize = 15.1.sp, fontWeight = FontWeight.Normal, lineHeight = 20.sp), color = C.Ink,
                        textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 11.dp))
                }
                g.depts.forEach { d ->
                    item(key = g.title + d.name) {
                        LazyRow(Modifier.padding(bottom = 11.dp), contentPadding = PaddingValues(horizontal = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            item { CategoryTile(d.name, d.image, true) { state.go(Route.Listing(d.name, d.dept)) } }
                            items(d.subs) { s -> CategoryTile(s.name, s.image, false) { state.go(Route.Listing(s.name, d.dept, s.query)) } }
                        }
                    }
                }
            }
        }
    }
}
