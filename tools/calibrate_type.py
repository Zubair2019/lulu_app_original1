"""Solve IBM Plex Sans sizes (sp at font scale 1) that reproduce the source's measured ink widths.

For each sample: render the string with the variable font at the measured weight, measure its ink width at
100 px, then size = source_ink_w_dp / (ink_w_at_100 / 100).
"""
from PIL import Image, ImageDraw, ImageFont

FONT = r"D:\lulu_app\app\src\main\res\font\ibm_plex_sans.ttf"

# (style, text, source ink width dp, weight)  -- from measure_text on lulu_home home_00 / home_01
SAMPLES = [
    ("tab", "Hypermarket", 77.8, 500), ("tab", "Minutes", 46.2, 500), ("tab", "Send Money", 72.4, 500),
    ("delivery", "Today, 8:00 pm – 10:00 pm", 164.0, 560),
    ("search", "Search", 42.2, 400),
    ("chip", "Gifting Store", 68.0, 500), ("chip", "Gaming", 41.1, 500), ("chip", "Beauty", 37.1, 500), ("chip", "Electronics", 59.6, 500),
    ("nav", "Categories", 52.4, 400), ("nav", "Buy Again", 48.4, 400), ("nav", "Deals", 26.5, 400), ("nav", "Account", 41.1, 400), ("nav", "Home", 28.7, 500),
    ("viewCart", "View cart", 70.5, 650), ("viewCartSub", "1 item", 36.0, 400),
    ("price", "3,299.00", 58.9, 600), ("price", "4,699.00", 58.9, 600), ("price", "999.00", 43.3, 600),
    ("strike", "3,679.00", 32.7, 400), ("strike", "129.00", 33.8 * 0.83, 400),
    ("badge", "10% OFF", 42.9, 700), ("badge", "34% OFF", 42.9, 700),
    ("viewAll", "VIEW ALL", 46.9, 650),
    ("section", "Super Friday Exclusive Deals", 216.0, 680),
]


def ink_w(text: str, weight: int, px: int = 100) -> float:
    f = ImageFont.truetype(FONT, px)
    try:
        f.set_variation_by_axes([100, weight])  # wdth, wght
    except Exception:  # noqa: BLE001
        f.set_variation_by_axes([weight])
    im = Image.new("L", (px * len(text), px * 2), 0)
    ImageDraw.Draw(im).text((10, 10), text, font=f, fill=255)
    box = im.getbbox()
    return (box[2] - box[0]) if box else 0


f = ImageFont.truetype(FONT, 40)
print("axes:", f.get_variation_axes())
out = {}
for style, text, src_w, wt in SAMPLES:
    size = src_w / (ink_w(text, wt) / 100)
    out.setdefault(style, []).append(size)
    print(f"{style:12} {text:32} src {src_w:6.1f} dp -> {size:5.2f} sp")
print()
for style, v in out.items():
    print(f"{style:12} median {sorted(v)[len(v) // 2]:5.2f} sp")
