"""Contact sheet of a capture's screens: python sheet.py <capture_dir> <out.jpg> [regex] [per_row] [width]"""
import re
import sys
from pathlib import Path

from PIL import Image, ImageDraw

cap, out = Path(sys.argv[1]), Path(sys.argv[2])
pat = re.compile(sys.argv[3]) if len(sys.argv) > 3 else re.compile(".")
per_row = int(sys.argv[4]) if len(sys.argv) > 4 else 4
w = int(sys.argv[5]) if len(sys.argv) > 5 else 360

shots = sorted(p for p in (cap / "screens").glob("*.png") if pat.search(p.stem))
if not shots:
    shots = sorted(p for p in cap.rglob("*.png") if pat.search(p.stem))
imgs = []
for p in shots:
    im = Image.open(p).convert("RGB")
    im = im.resize((w, round(im.height * w / im.width)))
    ImageDraw.Draw(im).rectangle([0, 0, 120, 22], fill="black")
    ImageDraw.Draw(im).text((4, 4), p.stem, fill="yellow")
    imgs.append(im)
rows = [imgs[i:i + per_row] for i in range(0, len(imgs), per_row)]
h = max(i.height for i in imgs)
sheet = Image.new("RGB", (per_row * (w + 6), len(rows) * (h + 6)), "white")
for r, row in enumerate(rows):
    for c, im in enumerate(row):
        sheet.paste(im, (c * (w + 6), r * (h + 6)))
sheet.save(out, quality=80)
print(out, len(imgs), "screens")
