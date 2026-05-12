import json
import os

script_dir = os.path.dirname(os.path.abspath(__file__))
data_path = os.path.join(script_dir, "data", "pets_converted.json")
output_path = os.path.join(script_dir, "skills_output.json")

with open(data_path, "r", encoding="utf-8") as f:
    pets = json.load(f)

skill_map = {}

for pet in pets:
    pet_info = {
        "id": pet["id"],
        "name": pet["name"],
        "element": pet["element"],
        "avatar": pet["avatar"]
    }
    skills = pet.get("skills", {})
    for group_key in ["group1", "group2", "group3"]:
        group = skills.get(group_key, [])
        for skill in group:
            skill_name = skill["name"]
            if skill_name not in skill_map:
                skill_map[skill_name] = {
                    "name": skill_name,
                    "lv": skill["lv"],
                    "element": skill["element"],
                    "type": skill["type"],
                    "cost": skill["cost"],
                    "power": skill["power"],
                    "desc": skill["desc"],
                    "pets": []
                }
            existing_pet_ids = [p["id"] for p in skill_map[skill_name]["pets"]]
            if pet_info["id"] not in existing_pet_ids:
                skill_map[skill_name]["pets"].append(pet_info)

result = sorted(skill_map.values(), key=lambda x: x["name"])

with open(output_path, "w", encoding="utf-8") as f:
    json.dump(result, f, ensure_ascii=False, indent=2)

print(f"共 {len(result)} 个不重复技能")
print(f"结果已保存至: {output_path}")
