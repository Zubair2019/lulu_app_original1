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
    data class ProductPage(val id: String) : Route
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
    var store by mutableStateOf(Store.Hypermarket)
    val top: Route get() = stack.last()
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
    var slot by mutableStateOf(0)
    var payment by mutableStateOf(0)
    var address by mutableStateOf(Address("Home", "Marina Walk, Dubai Marina, Dubai"))
    val deliveryFee: Double get() = if (subtotal >= 100) 0.0 else 9.0
    val total: Double get() = (subtotal - couponValue).coerceAtLeast(0.0) + deliveryFee + 2.0

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
        cart.clear(); cartOrder.clear(); coupon = null
        return "DC" + (100000 + (System.currentTimeMillis() % 900000)).toString()
    }
}
