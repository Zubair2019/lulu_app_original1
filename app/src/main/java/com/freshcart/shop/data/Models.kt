package com.freshcart.shop.data

import androidx.annotation.DrawableRes
import kotlin.math.roundToInt

data class Product(
    val id: String,
    val name: String,
    val brand: String,
    val unit: String,
    val price: Double,
    val oldPrice: Double,
    @DrawableRes val image: Int,
    val dept: String,
    val rating: Float,
    val description: String,
) {
    val offPercent: Int get() = if (oldPrice > price) ((1 - price / oldPrice) * 100).roundToInt() else 0
}

object Catalog {
    val all: List<Product> get() = CATALOG
    private val byId by lazy { CATALOG.associateBy { it.id } }
    fun get(id: String): Product? = byId[id]
    fun dept(vararg d: String): List<Product> = CATALOG.filter { it.dept in d }
    fun deals(min: Int = 20): List<Product> = CATALOG.filter { it.offPercent >= min }.sortedByDescending { it.offPercent }

    /** Search by every word of the query across name, brand, department and description; name hits rank first. */
    fun search(query: String): List<Product> {
        val words = query.lowercase().split(' ', ',', '-').filter { it.isNotBlank() }
        if (words.isEmpty()) return emptyList()
        return CATALOG.mapNotNull { p ->
            val name = p.name.lowercase()
            val hay = "$name ${p.brand.lowercase()} ${p.dept} ${DEPT_NAMES[p.dept]?.lowercase() ?: ""} ${p.description.lowercase()}"
            if (words.all { w -> hay.contains(w) || hay.contains(w.trimEnd('s')) }) {
                val score = words.count { name.contains(it) } * 10 + if (name.startsWith(words.first())) 5 else 0
                p to score
            } else null
        }.sortedByDescending { it.second }.map { it.first }
    }

    fun suggestions(query: String): List<String> {
        val q = query.lowercase().trim()
        if (q.isEmpty()) return emptyList()
        val names = CATALOG.map { it.name }.filter { it.lowercase().contains(q) }
        val depts = DEPT_NAMES.values.filter { it.lowercase().contains(q) }
        return (depts + names).distinct().take(8)
    }
}

val DEPT_NAMES = mapOf(
    "mobiles" to "Mobiles & Tablets",
    "electronics" to "Electronics",
    "home" to "Home & Kitchen",
    "beauty" to "Beauty & Fragrance",
    "grocery" to "Food Cupboard",
    "fresh" to "Fresh Food",
    "beverages" to "Beverages",
    "snacks" to "Snacks & Confectionery",
    "household" to "Household",
    "sports" to "Sports & Fitness",
    "fashion" to "Fashion & Accessories",
    "toys" to "Toys & Gifts",
)

data class CartLine(val product: Product, val qty: Int)

data class Address(val label: String, val line: String)

enum class Store { Hypermarket, Minutes, Money }
