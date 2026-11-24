package com.freshcart.shop.ui.screens


import com.freshcart.shop.ui.Plex
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.CallMade
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshcart.shop.data.AppState
import com.freshcart.shop.data.Catalog
import com.freshcart.shop.data.DEPT_NAMES
import com.freshcart.shop.data.Product
import com.freshcart.shop.data.Route
import com.freshcart.shop.ui.C
import com.freshcart.shop.ui.ProductCard
import com.freshcart.shop.ui.T
import com.freshcart.shop.ui.ViewCartPill
import com.freshcart.shop.ui.open
import com.freshcart.shop.ui.pressable

private val TRENDING = listOf("p_eggs", "t_vegetables", "p_basmati", "p_lemon_mint", "p_canola_oil", "p_chicken_breast", "p_ariel_pods", "p_dark_choc")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(state: AppState, initial: String) {
    var query by remember { mutableStateOf(initial) }
    var submitted by remember { mutableStateOf(initial.isNotBlank()) }
    val focus = remember { FocusRequester() }
    val fm = LocalFocusManager.current
    LaunchedEffect(Unit) { if (initial.isBlank()) focus.requestFocus() }
    fun submit(q: String) {
        query = q; submitted = q.isNotBlank(); state.remember(q); fm.clearFocus()
    }
    Box(Modifier.fillMaxSize().background(Color.White)) {
        Column(Modifier.fillMaxSize().imePadding()) {
            Row(Modifier.fillMaxWidth().statusBarsPadding().padding(start = 4.dp, end = 10.dp, top = 6.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(40.dp).clip(CircleShape).clickable { state.back() }, contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = C.Ink, modifier = Modifier.size(22.dp))
                }
                Row(
                    Modifier.weight(1f).height(40.dp).background(Color(0xFFF3F3F4), RoundedCornerShape(10.dp)).padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Rounded.Search, null, tint = C.Ink2, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Box(Modifier.weight(1f)) {
                        if (query.isEmpty()) Text("Search for anything", style = T.search, color = C.Ink3)
                        BasicTextField(
                            query, { query = it; submitted = false }, singleLine = true, textStyle = T.search.copy(color = C.Ink),
                            cursorBrush = SolidColor(C.Brand),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { submit(query) }),
                            modifier = Modifier.fillMaxWidth().focusRequester(focus).testTag("search_input"),
                        )
                    }
                    if (query.isNotEmpty()) {
                        Icon(Icons.Rounded.Close, "Clear", tint = C.Ink2, modifier = Modifier.size(18.dp).clickable { query = ""; submitted = false; focus.requestFocus() })
                    }
                }
            }
            when {
                submitted -> Results(state, query)
                query.isNotBlank() -> Suggestions(query) { submit(it) }
                else -> LazyColumn(Modifier.fillMaxSize()) {
                    if (state.recent.isNotEmpty()) item {
                        Row(Modifier.fillMaxWidth().padding(start = 12.dp, end = 12.dp, top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("RECENT SEARCHES", style = TextStyle(fontFamily = Plex, fontSize = 10.5.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.4.sp), color = C.Ink2, modifier = Modifier.weight(1f))
                            Text("Clear all", style = T.small.copy(fontSize = 11.sp), color = C.Ink2, modifier = Modifier.clickable { state.recent.clear() })
                        }
                        FlowRow(Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp, top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            state.recent.toList().forEach { r ->
                                Row(
                                    Modifier.height(30.dp).border(1.dp, C.Line, RoundedCornerShape(4.dp)).clip(RoundedCornerShape(4.dp)).clickable { submit(r) }.padding(start = 8.dp, end = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(r, style = T.small.copy(fontSize = 12.sp), color = C.Ink)
                                    Spacer(Modifier.width(6.dp))
                                    Box(Modifier.size(16.dp).background(Color(0xFFE4E4E6), CircleShape).clickable { state.recent.remove(r) }, contentAlignment = Alignment.Center) {
                                        Icon(Icons.Rounded.Close, "Remove", tint = C.Ink2, modifier = Modifier.size(11.dp))
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFFF0F0F1), thickness = 6.dp)
                    }
                    item {
                        Text("Trending in Every Cart", style = T.h3.copy(fontWeight = FontWeight.Bold), color = C.Ink, modifier = Modifier.padding(start = 12.dp, top = 18.dp, bottom = 10.dp))
                        LazyRow(contentPadding = PaddingValues(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(TRENDING.mapNotNull { Catalog.get(it) } + Catalog.deals(45).take(6)) { p -> ProductCard(state, p, { state.open(it.id) }, width = 104.dp) }
                        }
                    }
                    item {
                        Text("Popular categories", style = T.h3.copy(fontWeight = FontWeight.Bold), color = C.Ink, modifier = Modifier.padding(start = 12.dp, top = 20.dp, bottom = 6.dp))
                        DEPT_NAMES.forEach { (id, name) ->
                            Row(Modifier.fillMaxWidth().clickable { state.go(Route.Listing(name, id)) }.padding(horizontal = 12.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Search, null, tint = C.Ink3, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(12.dp))
                                Text(name, style = T.body, color = C.Ink, modifier = Modifier.weight(1f))
                                Icon(Icons.AutoMirrored.Rounded.CallMade, null, tint = C.Ink3, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(Modifier.height(40.dp))
                    }
                }
            }
        }
        if (submitted) ViewCartPill(state, Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp))
    }
}

@Composable
private fun Suggestions(query: String, onPick: (String) -> Unit) {
    val list = Catalog.suggestions(query)
    LazyColumn(Modifier.fillMaxSize().testTag("search_suggestions")) {
        item {
            Row(Modifier.fillMaxWidth().clickable { onPick(query) }.padding(horizontal = 14.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Search, null, tint = C.Brand, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(12.dp))
                Text("Search for \"$query\"", style = T.bodyM, color = C.Brand)
            }
        }
        items(list) { s ->
            Row(Modifier.fillMaxWidth().clickable { onPick(s) }.padding(horizontal = 14.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Search, null, tint = C.Ink3, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(12.dp))
                val i = s.lowercase().indexOf(query.lowercase().trim())
                Text(buildAnnotatedString {
                    if (i < 0) append(s) else {
                        append(s.substring(0, i))
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(s.substring(i, i + query.trim().length)) }
                        append(s.substring(i + query.trim().length))
                    }
                }, style = T.body, color = C.Ink, maxLines = 1, modifier = Modifier.weight(1f))
                Icon(Icons.AutoMirrored.Rounded.CallMade, null, tint = C.Ink3, modifier = Modifier.size(16.dp))
            }
            HorizontalDivider(color = Color(0xFFF0F0F1), modifier = Modifier.padding(start = 44.dp))
        }
    }
}

@Composable
private fun Results(state: AppState, query: String) {
    val results = remember(query) { Catalog.search(query) }
    ProductGrid(state, results, header = "${results.size} results for \"$query\"", empty = "No products match \"$query\". Try another word, like milk or phone.")
}

/** Two-column grid with Filter / Sort chips, used by search results and every listing. */
@Composable
fun ProductGrid(state: AppState, items: List<Product>, header: String, empty: String) {
    var sort by remember { mutableIntStateOf(0) }
    val sorted = when (sort) { 1 -> items.sortedBy { it.price }; 2 -> items.sortedByDescending { it.price }; 3 -> items.sortedByDescending { it.offPercent }; else -> items }
    val sortNames = listOf("Relevance", "Price: low to high", "Price: high to low", "Biggest discount")
    LazyVerticalGrid(
        GridCells.Fixed(2), Modifier.fillMaxSize().background(Color(0xFFF7F7F8)).testTag("product_grid"),
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 120.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(span = { GridItemSpan(2) }) {
            Column {
                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(Icons.Rounded.Tune, "Filter")
                    FilterChip(Icons.Rounded.SwapVert, if (sort == 0) "Sort by" else sortNames[sort]) { sort = (sort + 1) % sortNames.size }
                }
                Text(header, style = T.small, color = C.Ink2, modifier = Modifier.padding(start = 2.dp, bottom = 2.dp))
            }
        }
        if (sorted.isEmpty()) {
            item(span = { GridItemSpan(2) }) {
                Column(Modifier.fillMaxWidth().padding(top = 60.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.SearchOff, null, tint = C.Ink3, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(12.dp))
                    Text(empty, style = T.body, color = C.Ink2, modifier = Modifier.padding(horizontal = 32.dp))
                }
            }
        }
        items(sorted, key = { it.id }) { p -> ProductCard(state, p, { state.open(it.id) }, width = null) }
    }
}

@Composable
fun ListingScreen(state: AppState, r: Route.Listing) {
    val items = remember(r) {
        when {
            r.query == "__wishlist" -> state.wishlist.mapNotNull { Catalog.get(it) }
            r.deals -> Catalog.deals(20)
            r.dept != null && r.query != null -> Catalog.dept(r.dept).let { all ->
                val hit = all.filter { p -> Catalog.search(r.query).any { it.id == p.id } }
                hit.ifEmpty { all }
            }
            r.dept != null -> Catalog.dept(r.dept).sortedByDescending { it.offPercent }
            r.query != null -> Catalog.search(r.query)
            else -> Catalog.all
        }
    }
    Box(Modifier.fillMaxSize().background(Color.White)) {
        Column(Modifier.fillMaxSize()) {
            TitleBar(r.title) {
                Box(Modifier.size(32.dp).clip(CircleShape).clickable { state.go(Route.Search()) }, contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Search, "Search", tint = C.Ink, modifier = Modifier.size(20.dp))
                }
            }
            ProductGrid(state, items, "${items.size} products", "Nothing here yet.")
        }
        Box(Modifier.statusBarsPadding().padding(start = 6.dp).size(40.dp).clip(CircleShape).clickable { state.back() }, contentAlignment = Alignment.Center) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = C.Ink, modifier = Modifier.size(22.dp))
        }
        ViewCartPill(state, Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp))
    }
}

@Composable
fun Thumb(image: Int, size: androidx.compose.ui.unit.Dp, modifier: Modifier = Modifier) {
    Box(modifier.size(size).clip(RoundedCornerShape(8.dp)).background(C.Card), contentAlignment = Alignment.Center) {
        Image(painterResource(image), null, contentScale = ContentScale.Fit, modifier = Modifier.size(size - 8.dp))
    }
}

@Composable
fun Tappable(text: String, onClick: () -> Unit) {
    Text(text, style = T.smallM, color = C.Brand, modifier = Modifier.pressable(onClick).padding(4.dp))
}
