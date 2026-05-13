package com.yinpei.rocodex.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yinpei.rocodex.data.model.Pet
import com.yinpei.rocodex.data.model.Skill
import com.yinpei.rocodex.data.model.SkillCatalogEntry
import java.io.IOException

data class PetDataBundle(
    val elementColors: Map<String, String>,
    val allElements: List<String>,
    val weaknessTable: Map<String, ElementWeakness>,
    val pets: List<Pet>
)

data class ElementWeakness(
    @com.google.gson.annotations.SerializedName("very-effective")
    val veryEffective: List<String>,
    @com.google.gson.annotations.SerializedName("not-effective")
    val notEffective: List<String>
)

class PetRepository(private val context: Context) {

    private val gson = Gson()
    private var cachedBundle: PetDataBundle? = null
    private var cachedPets: List<Pet>? = null
    private var cachedSkills: List<Skill>? = null
    private var cachedSkillCatalog: List<SkillCatalogEntry>? = null

    fun getBundle(): PetDataBundle {
        if (cachedBundle == null) {
            cachedBundle = loadBundle()
        }
        return cachedBundle!!
    }

    fun getAllPets(): List<Pet> {
        if (cachedPets == null) {
            cachedPets = getBundle().pets
        }
        return cachedPets!!
    }

    fun getPetById(id: Int): Pet? {
        return getAllPets().find { it.id == id }
    }

    /** 全量招式列表（与图鉴等处的 [Skill] 模型一致） */
    fun getAllSkills(): List<Skill> {
        if (cachedSkills == null) {
            cachedSkills = getSkillCatalogEntries().map { it.asSkill() }
        }
        return cachedSkills!!
    }

    /**
     * 带「可学精灵」的招式目录，顺序稳定（用于详情页按索引导航）。
     */
    fun getSkillCatalogEntries(): List<SkillCatalogEntry> {
        if (cachedSkillCatalog == null) {
            cachedSkillCatalog = loadSkillCatalogFromAssets()
        }
        return cachedSkillCatalog!!
    }

    fun getSkillCatalogEntry(index: Int): SkillCatalogEntry? {
        val list = getSkillCatalogEntries()
        return list.getOrNull(index)
    }

    /** 与 [getSkillCatalogEntries] 顺序一致的下标，用于从图鉴 [Skill] 跳转招式详情 */
    fun getSkillCatalogIndex(skill: Skill): Int? {
        val idx = getSkillCatalogEntries().indexOfFirst {
            val s = it.asSkill()
            // 忽略 lv 字段进行比较，因为 pets.json 中的 lv 是学习等级，而 skills_output.json 中通常为空或固定值
            s.name == skill.name &&
            s.element == skill.element &&
            s.type == skill.type &&
            s.cost == skill.cost &&
            s.power == skill.power &&
            s.desc == skill.desc
        }
        return idx.takeIf { it >= 0 }
    }

    private fun loadSkillCatalogFromAssets(): List<SkillCatalogEntry> {
        return try {
            val json = context.assets.open("skills_output.json")
                .bufferedReader()
                .use { it.readText() }
            val type = object : TypeToken<List<SkillCatalogEntry>>() {}.type
            val raw = gson.fromJson<List<SkillCatalogEntry>>(json, type) ?: emptyList()
            raw.sortedWith(
                compareBy<SkillCatalogEntry> { it.name }
                    .thenBy { it.type }
                    .thenBy { it.cost }
                    .thenBy { it.power }
            )
        } catch (e: IOException) {
            throw RuntimeException("Failed to load skills_output.json from assets", e)
        }
    }

    fun getPetsByElement(element: String): List<Pet> {
        if (element == "全部") return getAllPets()
        return getAllPets().filter { pet -> element in pet.element }
    }

    fun getWeaknessTable(): Map<String, ElementWeakness> {
        return getBundle().weaknessTable
    }

    fun getElementColors(): Map<String, String> {
        return getBundle().elementColors
    }

    private fun loadBundle(): PetDataBundle {
        return try {
            val json = context.assets.open("pets.json")
                .bufferedReader()
                .use { it.readText() }
            gson.fromJson(json, PetDataBundle::class.java)
        } catch (e: IOException) {
            throw RuntimeException("Failed to load pets.json from assets", e)
        }
    }
}
