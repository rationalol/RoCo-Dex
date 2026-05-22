package com.yinpei.rocodex.data

import android.net.Uri

/** 将数据中的 avatar 路径映射为可加载的 asset URI */
fun mapAvatarPath(avatar: String): String {
    // /public/images/宠物/立绘/291.png → extract 291.png
    // /public/images/1.png → extract 1.png
    val filename = avatar.substringAfterLast('/')
    return "file:///android_asset/imagesV2/$filename"
}

fun mapAvatarShinyPath(avatar: String): String {
    // /public/images/宠物/立绘/291.png → extract 291.png
    // /public/images/1.png → extract 1.png
    val filename = avatar.substringAfterLast('/')
    return "file:///android_asset/imagesV2/${filename.split(".png")[0]}_shiny.png"
}


/** 将精灵属性映射为图标 asset URI */
fun mapElementIconPath(element: String): String {
    return "file:///android_asset/icons/$element.png"
}

/** 将技能映射为图标 asset URI */
fun mapSkillIconPath(element: String, skillName: String): String {
    return "file:///android_asset/skills/$element/$skillName.png"
}

/** 技能类型图标 asset URI */
fun mapSkillTypeIconPath(skillName: String, isDarkTheme: Boolean): String {
    val folder = if (isDarkTheme) "skillstypeDark" else "skillstype"
    return "file:///android_asset/$folder/$skillName.png"
}

/** 将特性映射为图标 asset URI */
fun mapTraitIconPath(traitName: String): String {
    return "file:///android_asset/traits/$traitName.png"
}

/** 异色图标 */
fun shinyIconPath(name: String): String {
    return "file:///android_asset/shiny/$name.png"
}

fun energyConsumptionIconPath(name: String): String{
    return "file:///android_asset/skills/$name.svg"
}

fun mapBaseStatsIconPath(name: String, isDarkTheme: Boolean): String {
    val folder = if (isDarkTheme) "baseStatsIcon" else "baseStatsIconLight"
    return "file:///android_asset/$folder/$name.png"
}