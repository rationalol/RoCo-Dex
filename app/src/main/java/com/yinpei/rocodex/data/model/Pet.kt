package com.yinpei.rocodex.data.model

data class Pet(
    val id: Int,
    val pindex: String,
    val name: String,
    val element: List<String>,
    val shiny:Int,
    val avatar: String,
    val hp: Int,
    val atk: Int,
    val mat: Int,
    val def: Int,
    val mdf: Int,
    val spd: Int,
    val trait: Trait,
    val skills: SkillGroupList,
    val height: String? = "0.9 m",
    val weight: String? = "20.5 kg",
    val nick: String? = "喜欢与人并肩作战",
    val description: String? = "拥有纯洁之心的光明精灵，和人类建立了深厚的羁绊。为了守护重要的伙伴，迪莫会倾尽全力。",
    val loc: String? = "未知", // 获取地点
    val evo: List<Evo>, // 进化链
    val forms: List<PetForm> //其他形态
)

data class Trait(
    val name: String,
    val desc: String
)

data class SkillGroupList(
    val group1: List<Skill>,
    val group2: List<Skill>,
    val group3: List<Skill>,
)

data class Skill(
    val name: String,
    val element: String,
    val type: String,
    val cost: Int,
    val power: Int,
    val desc: String,
    val lv: String
)

data class Evo(
    val id:Int,
    val name:String,
    val name2:String,
    val stage:String,
    val lv:String,
    val avatar:String,
)

data class PetForm(
    val id:Int,
    val name:String,
    val type:String,
    val avatar:String,
)

enum class StatType(val label: String) {
    HP("HP"),
    ATK("物攻"),
    MAT("魔攻"),
    DEF("物防"),
    MDF("魔防"),
    SPD("速度")
}
