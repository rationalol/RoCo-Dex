"""
将 baseStatsIcon 中的白色图标（适配黑夜模式）反转为深色图标（适配白天模式）。
源图标为白色(255,255,255)+透明度通道，反转后变为黑色(0,0,0)+原透明度通道。
输出到 baseStatsIconLight 目录。
"""
import os
from PIL import Image

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
ASSETS_DIR = os.path.dirname(SCRIPT_DIR)
SRC_DIR = os.path.join(ASSETS_DIR, "baseStatsIcon")
OUT_DIR = os.path.join(ASSETS_DIR, "baseStatsIconLight")

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
                pixels[x, y] = (0, 0, 0, a)

    out_path = os.path.join(OUT_DIR, fn)
    img.save(out_path)
    print(f"  {fn} ({w}x{h}) -> baseStatsIconLight/{fn}")

print(f"\nDone. {sum(1 for f in os.listdir(OUT_DIR) if f.endswith('.png'))} icons generated.")
