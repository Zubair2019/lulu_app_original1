package com.freshcart.shop.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** Screens the app can show on top of the tab pages. */
sealed interface Route {
    data object Tabs : Route
    data class Search(val query: String = "") : Route
    data class Listing(val title: String, val dept: String? = null, val query: String? = null, val deals: Boolean = false) : Route
    /** [hero] names the card the page was opened from, so its picture can travel to the page and back. */
    data class ProductPage(val id: String, val hero: String? = null) : Route
    /** One department laid out as an aisle: its sections in a side rail, products beside it. [sub] is the rail position (0 = all). */
    data class Aisle(val dept: String, val sub: Int = 0) : Route
    data object Cart : Route
    data object Checkout : Route
    data object Payment : Route
    data class OrderPlaced(val number: String) : Route
}

enum class Tab { Home, Categories, BuyAgain, Deals, Account }

class AppState {
    // navigation
    val stack = mutableStateListOf<Route>(Route.Tabs)
    var tab by mutableStateOf(Tab.Home)
    /** The store shown before the last switch; the store tabs start their slide from it. */
    var prevStore by mutableStateOf(Store.Hypermarket)
    private var storeState by mutableStateOf(Store.Hypermarket)
    var store: Store
        get() = storeState
        set(value) { if (value != storeState) { prevStore = storeState; storeState = value } }
    val top: Route get() = stack.last()
    /** The screen under any open product sheets. */
    val base: Route get() = stack.last { it !is Route.ProductPage }
    /** Set by a product card just before it opens its product; see [Route.ProductPage.hero]. */
    var pendingHero: String? = null
    fun heroOpen(key: String): Boolean = stack.any { it is Route.ProductPage && it.hero == key }
    var forward by mutableStateOf(true)
    fun go(r: Route) { forward = true; stack.add(r) }
    fun back(): Boolean = if (stack.size > 1) { forward = false; stack.removeAt(stack.lastIndex); true } else false
    fun popToTabs() { if (stack.size > 1) forward = false; while (stack.size > 1) stack.removeAt(stack.lastIndex) }
    fun home() { popToTabs(); tab = Tab.Home }

    // cart: product id -> quantity, in the order items were added
    val cart = mutableStateMapOf<String, Int>()
    val cartOrder = mutableStateListOf<String>()
    fun qty(id: String) = cart[id] ?: 0
    fun add(id: String) {
        if (id !in cart) cartOrder.add(id)
        cart[id] = (cart[id] ?: 0) + 1
    }
    fun remove(id: String) {
        val q = (cart[id] ?: 0) - 1
        if (q <= 0) { cart.remove(id); cartOrder.remove(id) } else cart[id] = q
    }
    fun delete(id: String) { cart.remove(id); cartOrder.remove(id) }
    val lines: List<CartLine> get() = cartOrder.mapNotNull { id -> Catalog.get(id)?.let { CartLine(it, cart[id] ?: 0) } }
    val count: Int get() = cart.values.sum()
    val subtotal: Double get() = lines.sumOf { it.product.price * it.qty }
    val savings: Double get() = lines.sumOf { (if (it.product.oldPrice > it.product.price) it.product.oldPrice - it.product.price else 0.0) * it.qty }

    // checkout
    var coupon by mutableStateOf<String?>(null)
    val couponValue: Double get() = when (coupon) { "SAVE10" -> 10.0; "FRESH15" -> 15.0; else -> 0.0 }
    /** The delivery slot the customer chose, as (day, window) indexes; null until one is picked, in the cart or at checkout. */
    var slot by mutableStateOf<Pair<Int, Int>?>(null)
    var payment by mutableStateOf(0)
    var address by mutableStateOf(Address("Home", "Marina Walk, Dubai Marina, Dubai"))

    // Fees and total: the one place both the cart and the checkout read them from, so the cart total is what is paid.
    val freeDeliveryFrom = 100.0
    val deliveryFeeFull = 10.0
    val deliveryFee: Double get() = if (subtotal >= freeDeliveryFrom) 0.0 else deliveryFeeFull
    val packagingFee = 1.0
    val serviceFee = 2.0
    val total: Double get() = (subtotal - couponValue).coerceAtLeast(0.0) + deliveryFee + packagingFee + serviceFee

    // search + history
    val recent = mutableStateListOf("milk", "bread", "headphones")
    fun remember(q: String) {
        val t = q.trim(); if (t.isEmpty()) return
        recent.remove(t); recent.add(0, t); while (recent.size > 8) recent.removeAt(recent.lastIndex)
    }
    // Empty until the first order, like a new account; placing an order fills Buy Again.
    val purchased = mutableStateListOf<String>()
    val wishlist = mutableStateListOf<String>()
    fun toggleWish(id: String) { if (id in wishlist) wishlist.remove(id) else wishlist.add(id) }

    fun placeOrder(): String {
        purchased.addAll(0, cartOrder.filter { it !in purchased })
        cart.clear(); cartOrder.clear(); coupon = null; slot = null
        return "DC" + (100000 + (System.currentTimeMillis() % 900000)).toString()
    }
}
