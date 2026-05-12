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

/** 精灵属性列表（含"全部"） */
val allElements: List<String> = listOf(
    "全部", "普通", "草", "火", "水", "光", "地", "冰", "龙", "电",
    "毒", "虫", "武", "翼", "萌", "幽", "恶", "机械", "幻"
)

/** 获取元素颜色，支持暗黑模式调整 */
fun getElementColor(element: String): Color {
    return elementColors[element] ?: Color.Gray
}
