"""Download the DummyJSON demo catalogue (free fake store data for prototypes) and turn it into app resources.

  1. products JSON + thumbnails -> images/dummyjson/ (raw downloads, untouched)
  2. thumbnails re-encoded as 320 px WebP -> app/src/main/res/drawable-nodpi/d_<id>.webp
  3. writes images/dummyjson/catalog.json (the fields the app uses), read by gen_catalog.py

Run with: uv run --quiet --project D:\\uimatch python -I D:\\lulu_app\\tools\\fetch_catalog.py
"""
import io
import json
import urllib.request
from pathlib import Path

from PIL import Image

ROOT = Path(r"D:\lulu_app")
RAW = ROOT / "images" / "dummyjson"
RES = ROOT / "app" / "src" / "main" / "res" / "drawable-nodpi"
UA = {"User-Agent": "Mozilla/5.0 (demo-app asset fetch)"}
KEEP = {"smartphones", "laptops", "tablets", "mobile-accessories", "kitchen-accessories", "beauty", "fragrances",
        "groceries", "home-decoration", "skin-care", "sports-accessories", "sunglasses", "mens-watches",
        "womens-bags", "furniture", "womens-jewellery", "mens-shoes", "womens-watches"}


def get(url: str) -> bytes:
    with urllib.request.urlopen(urllib.request.Request(url, headers=UA), timeout=30) as r:
        return r.read()


def main():
    RAW.mkdir(parents=True, exist_ok=True)
    RES.mkdir(parents=True, exist_ok=True)
    data = json.loads(get("https://dummyjson.com/products?limit=0&select=title,price,discountPercentage,category,"
                          "thumbnail,brand,rating,description,stock"))
    (RAW / "products.json").write_text(json.dumps(data, indent=1), encoding="utf-8")
    out = []
    for p in data["products"]:
        if p["category"] not in KEEP:
            continue
        name = f"d_{p['id']}"
        raw = RAW / f"{name}.img"
        try:
            if not raw.exists():
                raw.write_bytes(get(p["thumbnail"]))
            im = Image.open(io.BytesIO(raw.read_bytes())).convert("RGBA")
            bg = Image.new("RGBA", im.size, (255, 255, 255, 255))
            bg.alpha_composite(im)
            im = bg.convert("RGB")
            im.thumbnail((320, 320))
            im.save(RES / f"{name}.webp", "WEBP", quality=80)
        except Exception as e:  # noqa: BLE001
            print("skip", p["id"], e)
            continue
        out.append({"id": name, "title": p["title"], "price": p["price"], "off": p["discountPercentage"],
                    "category": p["category"], "brand": p.get("brand") or "", "rating": p["rating"],
                    "description": p["description"], "stock": p["stock"]})
    (RAW / "catalog.json").write_text(json.dumps(out, indent=1, ensure_ascii=False), encoding="utf-8")
    print(len(out), "products ->", RES)


main()
