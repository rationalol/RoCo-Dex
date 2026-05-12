import json

with open('data/data.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

img_links = set()

for pet in data.values():
    for evo in pet.get('evo', []):
        img = evo.get('img')
        if img:
            img_links.add(img)
    for form in pet.get('forms', []):
        img = form.get('img')
        if img:
            img_links.add(img)
    is_shiny =  pet.get('si','')
    if is_shiny != '':
        img_links.add(is_shiny)

with open('data/img_links.txt', 'w', encoding='utf-8') as f:
    for link in sorted(img_links):
        f.write(link + '\n')

print(f'Done. {len(img_links)} unique img links written to img_links.txt')
