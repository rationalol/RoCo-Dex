import json
from collections import Counter
import os

with open('data/source_main.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

with open('data/source_element_shiny.json', 'r', encoding='utf-8') as f:
    elementList = json.load(f)

with open('data/source_egg_data.json', 'r', encoding='utf-8') as f:
    eggData = json.load(f)

# 构建蛋组映射: "NO.001" -> ["动物组", "天空组"]
eggGroupMap = {}
for groupName, groupData in eggData.get('group_spirits', {}).items():
    for spirit in groupData.get('spirits', []):
        number = spirit.get('number', '')
        if number not in eggGroupMap:
            eggGroupMap[number] = []
        if groupName not in eggGroupMap[number]:
            eggGroupMap[number].append(groupName)


def getEggGroups(petIndex, petId):
    groups = eggGroupMap.get(petIndex, [])
    if not groups:
        number = f"NO.{petId:03d}"
        groups = eggGroupMap.get(number, [])
    return groups


pets = []

for key, d in data.items():

    element = ["首领"]

    petIndex = '首领化'

    shiny = 0

    for p in elementList:
        if int(key) == int(p.get("i")):
            element = []
            petIndex = p.get("n")
            shiny = p.get('sh')
            element.append(p.get("e"))
            if p.get("e2") != "":
                element.append(p.get("e2"))


    # 转换技能
    skills = {
        "group1": [],
        "group2": [],
        "group3": [],
    }
    for skill in d.get('sk', {}).get('s', []):
        skills["group1"].append({
            'name': skill.get('nm', ''),
            'lv': skill.get('lv', ''),
            'element': skill.get('el', ''),
            'type': skill.get('tp', ''),
            'cost': int(skill.get('ec', 0)),
            'power': int(skill.get('pw', 0)),
            'desc': skill.get('ef', '')
        })

    for skill in d.get('sk', {}).get('b', []):
        skills["group2"].append({
            'name': skill.get('nm', ''),
            'lv': skill.get('lv', ''),
            'element': skill.get('el', ''),
            'type': skill.get('tp', ''),
            'cost': int(skill.get('ec', 0)),
            'power': int(skill.get('pw', 0)),
            'desc': skill.get('ef', '')
        })

    for skill in d.get('sk', {}).get('t', []):
        skills["group3"].append({
            'name': skill.get('nm', ''),
            'lv': skill.get('lv', ''),
            'element': skill.get('el', ''),
            'type': skill.get('tp', ''),
            'cost': int(skill.get('ec', 0)),
            'power': int(skill.get('pw', 0)),
            'desc': skill.get('ef', '')
        })

    evoList = []
    for evo in d['evo']:
        evoList.append({
            'id': evo['i'],
            'name': evo['nm'],
            'name2': evo['fn'],
            'stage': evo['s'],
            'lv': evo['lv'],
            'avatar': os.path.basename(evo['img']),
        })

    formList = []
    for form in d['forms']:
        formList.append({
            'id': form['i'],
            'name': form['fn'],
            'type': form['f'],
            'avatar': os.path.basename(form['img']),
        })

    currentEvo = {}
    for evo in evoList:
        if int(key) == int(evo['id']):
            currentEvo = evo


    petName = currentEvo.get('name', '')

    # 常规形态
    if petName != '':
        pets.append({
            'id': int(key),
            'pindex':petIndex,
            'name': currentEvo.get('name', ''),
            'element': element,
            'shiny':shiny,
            'avatar': currentEvo.get('avatar', ''),
            'forms': formList,
            'evo': evoList,
            'eggGroups': getEggGroups(petIndex, int(key)),
            'hp': d['hp'],
            'atk': d['atk'],
            'mat': d['matk'],
            'def': d['df'],
            'mdf': d['mdf'],
            'spd': d['spd'],
            'height': d['h'],
            'weight': d['w'],
            'loc': d['loc'],
            'nick': d['nick'],
            'description': d['desc'],
            'trait': {
                'name': d.get('tn', ''),
                'desc': d.get('te', '')
            },
            'skills': skills,
        })
    else:
        # print(int(key),pets[int(key)-2])
        petInfo = {}

        for petForms in pets[int(key)-2]['forms']:
            if petForms['id'] == int(key):
                petInfo = petForms

        # 地区形态 超进化
        pets.append({
            'id': int(key),
            'pindex':'',
            'name': petInfo.get('name',''),
            'element': pets[int(key)-2]['element'],
            'shiny':shiny,
            'avatar': petInfo.get('avatar',''),
            'forms': formList,
            'evo': evoList,
            'eggGroups': getEggGroups(petIndex, int(key)),
            'hp': d['hp'],
            'atk': d['atk'],
            'mat': d['matk'],
            'def': d['df'],
            'mdf': d['mdf'],
            'spd': d['spd'],
            'height': d['h'],
            'weight': d['w'],
            'loc': d['loc'],
            'nick': d['nick'],
            'description': d['desc'],
            'trait': {
                'name': d.get('tn', ''),
                'desc': d.get('te', '')
            },
            'skills': skills,
        })

with open('data/converted_main.json', 'w', encoding='utf-8') as f:
    json.dump(pets, f, ensure_ascii=False, indent=2)

try:
    with open('../pets.json','r',encoding='utf-8') as f:
        content = f.read().strip()
        target_data = json.loads(content) if content else {}
except (FileNotFoundError, json.JSONDecodeError):
    target_data = {}

target_data['pets'] = pets

with open('../pets.json', 'w', encoding='utf-8') as f:
    json.dump(target_data, f, ensure_ascii=False, indent=2)

print(f'Done. {len(pets)} pets converted -> converted_main.json')
