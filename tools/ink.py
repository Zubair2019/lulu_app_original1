"""Ink box of dark text inside a dp region of a capture PNG, and the Plex size that reproduces its width.

python ink.py <png> <l,t,r,b dp> <text> <weight-file: regular|medium|semibold|bold> [threshold]
"""
import sys

from PIL import Image, ImageFont

D = 2.75
png, region, text, wfile = sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4]
thr = int(sys.argv[5]) if len(sys.argv) > 5 else 110
l, t, r, b = (float(v) * D for v in region.split(","))
im = Image.open(png).convert("L").crop((int(l), int(t), int(r), int(b)))
mask = im.point(lambda v: 255 if v < thr else 0)
box = mask.getbbox()
if not box:
    sys.exit("no ink found")
w_dp, h_dp = (box[2] - box[0]) / D, (box[3] - box[1]) / D
f = ImageFont.truetype(rf"D:\lulu_app\app\src\main\res\font\plex_{wfile}.ttf", 100)
bb = f.getbbox(text)
size = w_dp / ((bb[2] - bb[0]) / 100)
print(f"{text!r}: ink {w_dp:.1f} x {h_dp:.1f} dp at x {l / D + box[0] / D:.1f}, y {t / D + box[1] / D:.1f} -> Plex {wfile} {size:.2f} sp")
