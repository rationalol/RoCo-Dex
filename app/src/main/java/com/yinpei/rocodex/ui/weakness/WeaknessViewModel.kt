package com.yinpei.rocodex.ui.weakness

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.yinpei.rocodex.data.allElements
import com.yinpei.rocodex.data.repository.ElementWeakness
import com.yinpei.rocodex.data.repository.PetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WeaknessViewModel(application: Application) : AndroidViewModel(application) {

    private val weaknessTable = PetRepository(application).getWeaknessTable()

    private val elementDisplayOrder = allElements.filter { it != "全部" }

    private val _selectedElements = MutableStateFlow<List<String>>(emptyList())
    val selectedElements: StateFlow<List<String>> = _selectedElements.asStateFlow()

    /** 使用该属性攻击时，对哪些属性伤害更高（克制） */
    private val _dealDamageUp = MutableStateFlow<List<String>>(emptyList())
    val dealDamageUp: StateFlow<List<String>> = _dealDamageUp.asStateFlow()

    /** 使用该属性攻击时，对哪些属性伤害更低（抵抗）；双属性选择下不计算，保持空 */
    private val _dealDamageDown = MutableStateFlow<List<String>>(emptyList())
    val dealDamageDown: StateFlow<List<String>> = _dealDamageDown.asStateFlow()

    /** 受到哪些属性的攻击时，受伤更高（被克制） */
    private val _takeDamageUp = MutableStateFlow<List<String>>(emptyList())
    val takeDamageUp: StateFlow<List<String>> = _takeDamageUp.asStateFlow()

    /** 受到哪些属性的攻击时，受伤更低（抵抗） */
    private val _takeDamageDown = MutableStateFlow<List<String>>(emptyList())
    val takeDamageDown: StateFlow<List<String>> = _takeDamageDown.asStateFlow()

    fun toggleElement(element: String) {
        val current = _selectedElements.value.toMutableList()
        if (element in current) {
            current.remove(element)
        } else {
            if (current.size >= MAX_SELECTED_ELEMENTS) return
            current.add(element)
        }
        _selectedElements.value = current.toList()
        updateQuadrants()
    }

    private fun clearQuadrants() {
        _dealDamageUp.value = emptyList()
        _dealDamageDown.value = emptyList()
        _takeDamageUp.value = emptyList()
        _takeDamageDown.value = emptyList()
    }

    private fun updateQuadrants() {
        when (_selectedElements.value.size) {
            0 -> clearQuadrants()
            1 -> updateForElements(listOf(_selectedElements.value.single()))
            2 -> updateForElements(_selectedElements.value)
            else -> clearQuadrants()
        }
    }

    private fun updateForElements(defenderTypes: List<String>) {
        if (defenderTypes.size == 1) {
            val element = defenderTypes.single()
            val row = weaknessTable[element] ?: run {
                clearQuadrants()
                return
            }
            _dealDamageUp.value = sortByDisplayOrder(row.veryEffective)
            _dealDamageDown.value = sortByDisplayOrder(row.notEffective)
            computeTakeDamageSingle(element)
            return
        }

        if (defenderTypes.size == 2) {
            val (e1, e2) = defenderTypes[0] to defenderTypes[1]
            val r1 = weaknessTable[e1]
            val r2 = weaknessTable[e2]
            if (r1 == null || r2 == null) {
                clearQuadrants()
                return
            }
            val mergedSuper = (r1.veryEffective + r2.veryEffective).toSet()
            _dealDamageUp.value = sortByDisplayOrder(mergedSuper)
            _dealDamageDown.value = emptyList()
            computeTakeDamageDual(e1, e2)
        }
    }

    private fun computeTakeDamageSingle(defenderType: String) {
        val attackersSuper = mutableListOf<String>()
        val attackersResisted = mutableListOf<String>()
        for ((attackerType, w) in weaknessTable) {
            val m = attackMultiplierAgainstDefender(w, defenderType)
            when {
                m > 1.0 -> attackersSuper.add(attackerType)
                m < 1.0 -> attackersResisted.add(attackerType)
            }
        }
        _takeDamageUp.value = sortByDisplayOrder(attackersSuper)
        _takeDamageDown.value = sortByDisplayOrder(attackersResisted)
    }

    /** 与宝可梦双属性一致：对每种攻击属性，倍率 = 对第一属性倍率 × 对第二属性倍率 */
    private fun computeTakeDamageDual(def1: String, def2: String) {
        val attackersSuper = mutableListOf<String>()
        val attackersResisted = mutableListOf<String>()
        for ((attackerType, w) in weaknessTable) {
            val m1 = attackMultiplierAgainstDefender(w, def1)
            val m2 = attackMultiplierAgainstDefender(w, def2)
            val combined = m1 * m2
            when {
                combined > 1.0 -> attackersSuper.add(attackerType)
                combined < 1.0 -> attackersResisted.add(attackerType)
            }
        }
        _takeDamageUp.value = sortByDisplayOrder(attackersSuper)
        _takeDamageDown.value = sortByDisplayOrder(attackersResisted)
    }

    private fun sortByDisplayOrder(names: Collection<String>): List<String> =
        elementDisplayOrder.filter { it in names }

    private companion object {
        const val MAX_SELECTED_ELEMENTS = 2
    }

    private fun attackMultiplierAgainstDefender(attackerRow: ElementWeakness, defenderType: String): Double =
        when {
            defenderType in attackerRow.veryEffective -> 2.0
            defenderType in attackerRow.notEffective -> 0.5
            else -> 1.0
        }
}
