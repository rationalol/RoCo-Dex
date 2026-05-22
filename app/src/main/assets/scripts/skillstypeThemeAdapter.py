"""
将 skillstype 中的深色图标（适配白天模式）反转为浅色图标（适配黑夜模式）。
源图标为深灰色 RGB(83,89,91)+透明度通道，反转后变为浅灰色 RGB(172,166,164)+原透明度通道。
输出到 skillstypeDark 目录。
"""
import os
from PIL import Image

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
ASSETS_DIR = os.path.dirname(SCRIPT_DIR)
SRC_DIR = os.path.join(ASSETS_DIR, "skillstype")
OUT_DIR = os.path.join(ASSETS_DIR, "skillstypeDark")

os.makedirs(OUT_DIR, exist_ok=True)

for fn in sorted(os.listdir(SRC_DIR)):
    if not fn.endswith(".png"):
        continue

    img = Image.open(os.path.join(SRC_DIR, fn)).convert("RGBA")
    w, h = img.size
    pixels = img.load()

    for y in range(h):
        for x in range(w):
            r, g, b, a = pixels[x, y]
            if a > 0:
                pixels[x, y] = (255 - r, 255 - g, 255 - b, a)

    out_path = os.path.join(OUT_DIR, fn)
    img.save(out_path)
    print(f"  {fn} ({w}x{h}) -> skillstypeDark/{fn}")

print(f"\nDone. {sum(1 for f in os.listdir(OUT_DIR) if f.endswith('.png'))} icons generated.")
