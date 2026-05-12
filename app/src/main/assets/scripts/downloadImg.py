import os
import urllib.request
import urllib.parse  # 导入用于编码的模块

base_url = "https://static.gamecenter.qq.com/xgame/roco-kingdom/compendium/"
output_dir = "../imagesV2"
os.makedirs(output_dir, exist_ok=True)

with open('data/img_links.txt', 'r', encoding='utf-8') as f:
    links = [line.strip() for line in f if line.strip()]

total = len(links)
print(f'Total images to download: {total}')

for i, path in enumerate(links, 1):
    # 【关键修复】对包含中文的路径进行 URL 编码
    # quote 会把中文转义，但保留斜杠 /
    encoded_path = urllib.parse.quote(path)
    url = base_url + encoded_path

    filename = path.split('/')[-1]
    filepath = os.path.join(output_dir, filename)

    if os.path.exists(filepath):
        print(f'[{i}/{total}] SKIP (exists): {filename}')
        continue

    try:
        # 打印提示（确保你的终端支持 UTF-8 否则此处 print 也会报错）
        print(f'[{i}/{total}] Downloading: {filename} ...', end=' ', flush=True)

        # 执行下载
        urllib.request.urlretrieve(url, filepath)
        print('Done.')

    except Exception as e:
        print(f'Error: {e}')

print('All tasks finished.')