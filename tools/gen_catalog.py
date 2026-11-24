"""Generate app/src/main/java/com/freshcart/shop/data/Catalog.kt from images/dummyjson/catalog.json plus the
hand-written grocery list below (photos p_* from Open Food Facts / Wikimedia, see IMAGE_CREDITS.md).

Run: uv run --quiet --project D:\\uimatch python -I D:\\lulu_app\\tools\\gen_catalog.py
"""
import json
import math
from pathlib import Path

ROOT = Path(r"D:\lulu_app")
SRC = ROOT / "images" / "dummyjson" / "catalog.json"
OUT = ROOT / "app" / "src" / "main" / "java" / "com" / "freshcart" / "shop" / "data" / "Catalog.kt"

# DummyJSON category -> (our department id)
DEPT = {
    "smartphones": "mobiles", "tablets": "mobiles", "mobile-accessories": "electronics", "laptops": "electronics",
    "kitchen-accessories": "home", "home-decoration": "home", "furniture": "home",
    "beauty": "beauty", "skin-care": "beauty", "fragrances": "beauty",
    "groceries": "grocery", "sports-accessories": "sports", "sunglasses": "fashion", "mens-watches": "fashion",
    "womens-watches": "fashion", "womens-bags": "fashion", "womens-jewellery": "fashion", "mens-shoes": "fashion",
}

# Grocery and household items with our own names (id, name, unit, price, old price or 0, dept)
GROCERY = [
    ("p_ariel", "Liquid Laundry Detergent Front & Top Load", "4 L", 44.50, 100.00, "household"),
    ("p_ariel_pods", "Perfect Wash Laundry Capsules", "26 pcs", 27.32, 49.50, "household"),
    ("p_persil", "Deep Clean Laundry Gel Lavender", "2.9 L", 39.95, 59.00, "household"),
    ("p_downy", "Concentrate Fabric Softener Vanilla", "1.38 L", 29.99, 55.05, "household"),
    ("p_comfort", "Fabric Conditioner Fresh Blossom", "860 ml", 16.99, 28.95, "household"),
    ("p_dettol", "Antiseptic Disinfectant Liquid", "1 L", 21.99, 45.00, "household"),
    ("p_voi_wipes", "Gentle Baby Wet Wipes", "3 x 72 pcs", 14.25, 19.00, "household"),
    ("p_basmati", "Extra Long Grain Basmati Rice", "5 kg", 42.75, 54.00, "grocery"),
    ("p_canola_oil", "Pure Canola Cooking Oil", "1.5 L", 18.50, 23.95, "grocery"),
    ("p_truffle_oil", "Black Truffle Infused Olive Oil", "250 ml", 36.00, 0.0, "grocery"),
    ("p_himalayan_salt", "Pink Himalayan Rock Salt Grinder", "390 g", 11.75, 0.0, "grocery"),
    ("p_kabuli_chana", "Kabuli Chickpeas Premium", "1 kg", 9.95, 12.50, "grocery"),
    ("p_bayara_walnuts", "Chilean Walnut Kernels", "400 g", 31.50, 38.00, "grocery"),
    ("p_manuka", "Manuka Honey UMF 10+", "250 g", 89.00, 119.00, "grocery"),
    ("p_parmesan", "Parmigiano Reggiano Wedge", "200 g", 24.50, 0.0, "fresh"),
    ("p_eggs", "Free Range White Eggs", "30 pcs", 24.95, 29.50, "fresh"),
    ("p_chicken_breast", "Fresh Chicken Breast Fillets", "1 kg", 32.50, 38.00, "fresh"),
    ("p_sadia_chicken", "Frozen Whole Chicken Grade A", "1.2 kg", 17.95, 21.00, "fresh"),
    ("p_chicken_burger", "Chicken Burgers Classic", "8 pcs", 15.75, 0.0, "fresh"),
    ("p_salmon", "Norwegian Salmon Fillet", "500 g", 48.00, 59.00, "fresh"),
    ("p_lemon_mint", "Lemon Mint Juice Drink", "1.5 L", 7.50, 0.0, "beverages"),
    ("p_nescafe_red_mug", "Classic Instant Coffee Jar", "200 g", 26.75, 33.00, "beverages"),
    ("p_choc_box", "Hazelnut Pralines Gift Box", "24 pcs", 39.95, 49.00, "snacks"),
    ("p_dark_choc", "Extra Dark Chocolate 85% Cocoa", "100 g", 12.95, 0.0, "snacks"),
    ("p_hershey_kisses", "Milk Chocolate Kisses Bag", "306 g", 21.50, 25.00, "snacks"),
    ("p_chupa_chups", "Fruit Lollipops Assorted", "50 pcs", 18.25, 0.0, "snacks"),
    ("p_pantene", "Curl Perfection Shampoo", "400 ml", 19.95, 27.50, "beauty"),
    ("p_hair_oil", "Argan Hair Oil Treatment", "100 ml", 34.00, 52.00, "beauty"),
    ("p_lipstick", "Matte Lipstick Collection", "5 shades", 59.00, 85.00, "beauty"),
    ("p_perfume", "Oud Eau de Parfum", "100 ml", 149.00, 229.00, "beauty"),
    ("p_teddy", "Plush Teddy Bear Large", "60 cm", 49.00, 79.00, "toys"),
    ("p_wallet", "Leather Bi-fold Wallet", "1 pc", 79.00, 129.00, "fashion"),
]


def aed(usd: float) -> float:
    v = usd * 3.67
    if v < 20:
        return round(math.floor(v) + 0.95, 2)
    if v < 200:
        return float(math.floor(v)) + 0.99 if v < 100 else float(math.floor(v))
    return float(int(v // 10 * 10 - 1))


def kt(s: str) -> str:
    return '"' + s.replace("\\", "\\\\").replace('"', '\\"').replace("$", "\\$") + '"'


def main():
    items = json.loads(SRC.read_text(encoding="utf-8"))
    rows = []
    for p in items:
        price = aed(p["price"])
        off = int(round(p["off"]))
        old = round(price / (1 - off / 100), 2) if off >= 5 else 0.0
        if old:
            old = float(math.ceil(old)) if old > 100 else round(math.ceil(old * 2) / 2, 2)
        dept = DEPT.get(p["category"], "electronics")
        desc = p["description"]
        rows.append(f'    Product({kt(p["id"])}, {kt(p["title"])}, {kt(p["brand"])}, "1 pc", {price:.2f}, {old:.2f}, '
                    f'R.drawable.{p["id"]}, {kt(dept)}, {p["rating"]:.1f}f, {kt(desc)}),')
    for pid, name, unit, price, old, dept in GROCERY:
        rows.append(f'    Product({kt(pid)}, {kt(name)}, "", {kt(unit)}, {price:.2f}, {old:.2f}, R.drawable.{pid}, '
                    f'{kt(dept)}, 4.4f, {kt(name + " " + unit + ". Carefully sourced and delivered fresh to your door.")}),')
    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text(
        "// Generated by tools/gen_catalog.py. Do not edit by hand.\n"
        "package com.freshcart.shop.data\n\nimport com.freshcart.shop.R\n\n"
        "val CATALOG: List<Product> = listOf(\n" + "\n".join(rows) + "\n)\n", encoding="utf-8")
    print(len(rows), "products ->", OUT)


main()
