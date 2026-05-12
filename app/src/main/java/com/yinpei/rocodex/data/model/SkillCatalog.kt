package com.yinpei.rocodex.data.model

/** skills_output.json 中可学习该招式的精灵条目 */
data class SkillLearnerPet(
    val id: Int,
    val name: String,
    val element: List<String>,
    val avatar: String
)

/** skills_output.json 单条招式（含可学精灵列表） */
data class SkillCatalogEntry(
    val name: String,
    val lv: String,
    val element: String,
    val type: String,
    val cost: Int,
    val power: Int,
    val desc: String,
    val pets: List<SkillLearnerPet> = emptyList()
) {
    fun asSkill(): Skill = Skill(
        name = name,
        element = element,
        type = type,
        cost = cost,
        power = power,
        desc = desc,
        lv = lv
    )
}
