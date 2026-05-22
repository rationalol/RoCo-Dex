import requests
import json
import re
import os



# 精灵主要数据

def pet_data():
    resp = requests.get("https://static.gamecenter.qq.com/xgame/roco-kingdom/compendium/d.json")
    resp.encoding = "utf-8"
    print("=== 精灵主要数据 ===")
    all_data = resp.json()
    source_main = resp.json()["d"]
    source_element_list = resp.json()["e"]
    source_element_shiny = resp.json()["l"]
    source_element_pic_list = resp.json()["_em"]
    source_skill_pic_list = resp.json()["_skm"]
    source_feature_pic_list = resp.json()["_tm"]



# 精灵蛋组数据
def egg_data():
    resp = requests.get("https://static.gamecenter.qq.com/xgame/roco-kingdom/compendium/egg.html")
    resp.encoding = "utf-8"
    html = resp.text

    # 保存原始 HTML
    script_dir = os.path.dirname(os.path.abspath(__file__))
    with open(os.path.join(script_dir, "source_egg.html"), "w", encoding="utf-8") as f:
        f.write(html)
    print("=== 精灵蛋组数据已保存到 source_egg.html ===")

    # 提取全量精灵数据 const data = { "s": [...], "g": [...] }
    data_match = re.search(r'const data = (\{.*?\});\s*allSpirits', html, re.DOTALL)
    if not data_match:
        print("未找到 data 数据")
        return
    data = json.loads(data_match.group(1))
    all_spirits = data["s"]  # 429 只精灵
    all_groups = data["g"]  # 15 个蛋组

    # 提取 _TD 异色/传递数据
    td = {}
    td_match = re.search(r'const _TD = (\{.*?\});', html, re.DOTALL)
    if td_match:
        td = json.loads(td_match.group(1))

    # 提取 EGG_DESC (JS 对象字面量，单引号 → 双引号)
    desc_match = re.search(r"const EGG_DESC = (\{.*?\});", html, re.DOTALL)
    egg_desc = {}
    if desc_match:
        js_obj = desc_match.group(1)
        js_obj = re.sub(r"'([^']*?)'\s*:", r'"\1":', js_obj)
        js_obj = re.sub(r":\s*'([^']*?)'", r': "\1"', js_obj)
        try:
            egg_desc = json.loads(js_obj)
        except json.JSONDecodeError:
            pass

    # 按蛋组归类精灵
    group_spirits = {}
    for g in all_groups:
        members = []
        for s in all_spirits:
            if g in s.get("eg", []):
                members.append({
                    "name": s.get("fn", s.get("nm", "")),
                    "number": s.get("n", ""),
                    "element": s.get("e", ""),
                    "element2": s.get("e2", ""),
                    "shiny": s.get("sh", 0),
                })
        group_spirits[g] = {
            "desc": egg_desc.get(g, ""),
            "count": len(members),
            "spirits": members,
        }

    # 组装最终蛋组数据
    egg_group_data = {
        "groups": all_groups,
        "group_spirits": group_spirits,
        "shiny_final_list": td.get("finalShiny", []),
        "transfers": td.get("transfers", {}),
    }

    output_path = os.path.join(script_dir, "source_egg_data.json")
    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(egg_group_data, f, ensure_ascii=False, indent=2)
    print(f"蛋组数据已提取并保存到 {output_path}，共 {len(all_groups)} 个蛋组，{len(all_spirits)} 只精灵")


egg_data()