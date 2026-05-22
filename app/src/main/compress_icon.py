#!/usr/bin/env python3
"""Compress ic_launcher-playstore.png to ≤50KB."""

from PIL import Image
import os

INPUT = "ic_launcher-playstore.png"
OUTPUT = "ic_launcher-playstore_compressed.png"
MAX_SIZE = 50 * 1024  # 50KB

img = Image.open(INPUT).convert("RGBA")

# Try quantization (reduce colors) first — best for PNG icons
methods = [
    # (quantize colors, dither)
    (256, Image.Quantize.FASTOCTREE),
    (128, Image.Quantize.FASTOCTREE),
    (64, Image.Quantize.FASTOCTREE),
    (32, Image.Quantize.FASTOCTREE),
]

for colors, method in methods:
    quantized = img.quantize(colors=colors, method=method, dither=Image.Dither.FLOYDSTEINBERG)
    quantized.save(OUTPUT, optimize=True)
    size = os.path.getsize(OUTPUT)
    print(f"  colors={colors}: {size/1024:.1f}KB")
    if size <= MAX_SIZE:
        print(f"Done: {size/1024:.1f}KB ({size} bytes)")
        break
else:
    # Last resort: reduce dimensions
    img_small = img.resize((256, 256), Image.LANCZOS)
    img_small.save(OUTPUT, optimize=True)
    size = os.path.getsize(OUTPUT)
    print(f"  resized to 256x256: {size/1024:.1f}KB")
    print(f"Done: {size/1024:.1f}KB ({size} bytes)")
