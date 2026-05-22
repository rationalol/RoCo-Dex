package com.yinpei.rocodex.data

import androidx.compose.ui.graphics.Color

/** 18 种精灵属性对应的颜色 */
val elementColors: Map<String, Color> = mapOf(
    "普通" to Color(0xFF6C8BAD),
    "草" to Color(0xFF4CAF50),
    "火" to Color(0xFFE64A19),
    "水" to Color(0xFF2196F3),
    "光" to Color(0xFF29B6F6),
    "地" to Color(0xFF8D6E63),
    "冰" to Color(0xFF7AB5CA),
    "龙" to Color(0xFFE91E63),
    "电" to Color(0xFFFFC107),
    "毒" to Color(0xFF9C27B0),
    "虫" to Color(0xFF8BC34A),
    "武" to Color(0xFFFF9800),
    "翼" to Color(0xFF26C6DA),
    "萌" to Color(0xFFF06292),
    "幽" to Color(0xFF7E57C2),
    "恶" to Color(0xFFC2185B),
    "机械" to Color(0xFF26A69A),
    "幻" to Color(0xFF9575CD),
)

/** 精灵属性列表 */
val allElements: List<String> = listOf(
    "普通", "草", "火", "水", "光", "地", "冰", "龙", "电",
    "毒", "虫", "武", "翼", "萌", "幽", "恶", "机械", "幻"
)

/** 获取元素颜色，支持暗黑模式调整 */
fun getElementColor(element: String): Color {
    return elementColors[element] ?: Color.Gray
}

/** 蛋组列表（与 pets.json / source_egg_data.json 一致） */
val allEggGroups: List<String> = listOf(
    "两栖组", "动物组", "大地组", "天空组", "妖精组", "巨灵组", "拟人组",
    "昆虫组", "机械组", "植物组", "海洋组", "软体组", "魔力组", "龙组", "无法孵蛋"
)

val eggGroupColors: Map<String, Color> = mapOf(
    "两栖组" to Color(0xFF26A69A),
    "动物组" to Color(0xFF8D6E63),
    "大地组" to Color(0xFF795548),
    "天空组" to Color(0xFF42A5F5),
    "妖精组" to Color(0xFFEC407A),
    "巨灵组" to Color(0xFF7E57C2),
    "拟人组" to Color(0xFF5C6BC0),
    "昆虫组" to Color(0xFF8BC34A),
    "机械组" to Color(0xFF607D8B),
    "植物组" to Color(0xFF66BB6A),
    "海洋组" to Color(0xFF29B6F6),
    "软体组" to Color(0xFFAB47BC),
    "魔力组" to Color(0xFF9575CD),
    "龙组" to Color(0xFFE91E63),
    "无法孵蛋" to Color(0xFF9E9E9E),
)

fun getEggGroupColor(group: String): Color {
    return eggGroupColors[group] ?: Color.Gray
}
