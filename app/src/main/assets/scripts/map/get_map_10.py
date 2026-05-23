import os
import math
import time
import json
from concurrent.futures import ThreadPoolExecutor
import requests

# ==================== 配置区域 ====================
# maps.json 的本地路径
MAPS_JSON_PATH = "../../map/maps.json"

# 【核心修改】将层级提升到 10
ZOOM = 10
TILE_SIZE = 256

# 线程数（建议 4-8，爬 10 层级大约总共几百张图，这个速度很快）
MAX_WORKERS = 6
# ==================================================


def lng_lat_to_tile(lng, lat, zoom):
    """
    根据墨卡托投影公式，将经纬度转换为指定缩放层级下的瓦片 X 和 Y 编号
    """
    total_width = TILE_SIZE * (1 << zoom)

    # 经度转绝对像素 X
    world_x = ((lng + 180.0) / 360.0) * total_width
    tile_x = int(math.floor(world_x / TILE_SIZE))

    # 纬度转绝对像素 Y
    lat_rad = math.radians(lat)
    lat_rad = max(-1.48442222, min(1.48442222, lat_rad))
    mercator = math.log(math.tan(math.pi / 4.0 + lat_rad / 2.0))
    world_y = (0.5 - mercator / (2.0 * math.pi)) * total_width
    tile_y = int(math.floor(world_y / TILE_SIZE))

    return tile_x, tile_y


def download_tile(url, file_path, file_name):
    """
    执行单张瓦片的下载与保存
    """
    if os.path.exists(file_path):
        return  # 已存在则跳过，方便断点续传

    headers = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
        "Referer": "https://static.gamecenter.qq.com/",
    }

    try:
        response = requests.get(url, headers=headers, timeout=10)
        if response.status_code == 404:
            return  # 边界外的空白切片，直接静默跳过

        if response.status_code == 200:
            with open(file_path, "wb") as f:
                f.write(response.content)
            print(f"[成功] 已下载: {file_name}")
        else:
            print(f"[错误] 状态码 {response.status_code}: {file_name}")
    except Exception as e:
        print(f"[异常] {file_name} 下载失败: {e}")


def process_map(map_data):
    map_id = map_data["id"]
    map_title = map_data["title"]

    slug_mapping = {
        61: "dalu",
        91: "mofaxueyuan"
    }
    dir_name = slug_mapping.get(map_id, map_data.get("slug", f"map_{map_id}").lower())

    # 【修改】目录名后缀改为 _10，清晰区分资产
    save_dir = f"roco_tiles_{dir_name}_10"
    if not os.path.exists(save_dir):
        os.makedirs(save_dir)

    # 解析 tile_sets 里的 bounds
    tile_sets = json.loads(map_data["tile_sets"])
    bounds = tile_sets[0]["bounds"]

    lng_min, lat_min, lng_max, lat_max = bounds

    # 动态推算当前地图在 Zoom=10 时合法的瓦片索引区间
    min_x, max_y = lng_lat_to_tile(lng_min, lat_min, ZOOM)
    max_x, min_y = lng_lat_to_tile(lng_max, lat_max, ZOOM)

    # 边缘稍微外扩 1 个瓦片防止裁剪穿帮
    min_x, max_x = min_x - 1, max_x + 1
    min_y, max_y = min_y - 1, max_y + 1

    print(f"\n==================================================")
    print(f"🗺️  开始处理层级 10 地图: {map_title} (ID: {map_id})")
    print(f"📂 本地保存目录: {save_dir}")
    print(f"📐 自动推算边界: X 范围 [{min_x}..{max_x}], Y 范围 [{min_y}..{max_y}]")
    print(f"==================================================")

    base_url_template = f"https://static.gamecenter.qq.com/xgame/roco-kingdom/nrc-map/tiles/{dir_name}/{ZOOM}/{{x}}_{{y}}.jpg"

    tasks = []
    for x in range(min_x, max_x + 1):
        for y in range(min_y, max_y + 1):
            url = base_url_template.format(x=x, y=y)
            file_name = f"{x}_{y}.jpg"
            file_path = os.path.join(save_dir, file_name)
            tasks.append((url, file_path, f"{dir_name}_z10/{file_name}"))

    with ThreadPoolExecutor(max_workers=MAX_WORKERS) as executor:
        for url, file_path, file_name in tasks:
            executor.submit(download_tile, url, file_path, file_name)


def main():
    if not os.path.exists(MAPS_JSON_PATH):
        print(f"❌ 找不到 {MAPS_JSON_PATH}，请检查路径。")
        return

    with open(MAPS_JSON_PATH, "r", encoding="utf-8") as f:
        maps_list = json.load(f)

    target_maps = [m for m in maps_list if m["id"] in [61, 91]]

    for map_data in target_maps:
        process_map(map_data)

    print("\n🎉 10层级地图瓦片下载任务处理完毕！")


if __name__ == "__main__":
    start_time = time.time()
    main()
    print(f"⏰ 总耗时: {time.time() - start_time:.2f} 秒")