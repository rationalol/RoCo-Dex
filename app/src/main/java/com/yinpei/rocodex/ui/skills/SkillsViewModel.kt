package com.yinpei.rocodex.ui.skills

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yinpei.rocodex.data.model.SkillCatalogEntry
import com.yinpei.rocodex.data.repository.PetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** 招式目录中的稳定下标（与 [PetRepository.getSkillCatalogEntry] 一致） */
data class IndexedSkillEntry(
    val catalogIndex: Int,
    val entry: SkillCatalogEntry
)

class SkillsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PetRepository(application)

    private val _allIndexed = MutableStateFlow<List<IndexedSkillEntry>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedElement = MutableStateFlow("全部")
    val selectedElement: StateFlow<String> = _selectedElement.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val skillItems: StateFlow<List<IndexedSkillEntry>> = combine(
        _allIndexed,
        _selectedElement,
        _searchQuery
    ) { indexed, element, query ->
        val q = query.trim()
        indexed.filter { item ->
            val skill = item.entry.asSkill()
            val elementMatch = element == "全部" || skill.element == element
            val searchMatch = q.isEmpty() ||
                skill.name.contains(q, ignoreCase = true) ||
                skill.desc.contains(q, ignoreCase = true) ||
                skill.type.contains(q, ignoreCase = true) ||
                skill.element.contains(q, ignoreCase = true) ||
                skill.power.toString().contains(q) ||
                skill.cost.toString().contains(q)
            elementMatch && searchMatch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadSkills()
    }

    private fun loadSkills() {
        viewModelScope.launch {
            _isLoading.value = true
            _allIndexed.value = repository.getSkillCatalogEntries().mapIndexed { i, e ->
                IndexedSkillEntry(catalogIndex = i, entry = e)
            }
            _isLoading.value = false
        }
    }

    fun selectElement(element: String) {
        _selectedElement.value = element
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}
