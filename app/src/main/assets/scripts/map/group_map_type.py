import json
from collections import Counter


def summary_type_ids(file_path):
    try:
        # 读取 JSON 文件
        with open(file_path, "r", encoding="utf-8") as f:
            data = json.load(f)

        features = data.get("features", [])
        print(f"成功读取文件，共找到 {len(features)} 个数据点。")

        # 仅提取并统计 typeId
        type_ids = [
            feature.get("properties", {}).get("typeId") for feature in features
        ]
        id_counter = Counter(type_ids)

        # 打印汇总结果
        print("\n" + "=" * 25)
        print(f"{'typeId':<12} | {'数量 (Count)'}")
        print("=" * 25)

        # 按数量从大到小排序输出
        for type_id, count in id_counter.most_common():
            # 将 None 转换为字符串显示，防止报错
            display_id = "None" if type_id is None else type_id
            print(f"{display_id:<12} | {count}")

        print("=" * 25)

    except FileNotFoundError:
        print(f"错误：找不到文件 '{file_path}'")
    except json.JSONDecodeError:
        print("错误：文件不是有效的 JSON 格式。")


# 使用示例：将 'all_points.json' 替换为你的文件实际路径
if __name__ == "__main__":
    file_path = "C:/Users/27209/Desktop/rocokingdom/roco-kingdom-pet-compose/app/src/main/assets/map/all_points.json"
    summary_type_ids(file_path)