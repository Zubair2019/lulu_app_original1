"""Sample colours from a capture screen at dp points: python px.py <png> x,y[,label] ..."""
import sys

from PIL import Image

D = 2.75
im = Image.open(sys.argv[1]).convert("RGB")
for arg in sys.argv[2:]:
    parts = arg.split(",")
    x, y = float(parts[0]), float(parts[1])
    label = parts[2] if len(parts) > 2 else ""
    r, g, b = im.getpixel((min(im.width - 1, int(x * D)), min(im.height - 1, int(y * D))))
    print(f"{label:>18} ({x:6.1f},{y:6.1f}) #{r:02X}{g:02X}{b:02X}")
