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

    private val _selectedElements = MutableStateFlow<Set<String>>(emptySet())
    val selectedElements: StateFlow<Set<String>> = _selectedElements.asStateFlow()

    private val _selectedSkillTypes = MutableStateFlow<Set<String>>(emptySet())
    val selectedSkillTypes: StateFlow<Set<String>> = _selectedSkillTypes.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val skillItems: StateFlow<List<IndexedSkillEntry>> = combine(
        _allIndexed,
        _selectedElements,
        _selectedSkillTypes,
        _searchQuery
    ) { indexed, elements, types, query ->
        val q = query.trim()
        indexed.filter { item ->
            val skill = item.entry.asSkill()
            val elementMatch = elements.isEmpty() || skill.element in elements
            val typeMatch = types.isEmpty() || skill.type in types
            val searchMatch = q.isEmpty() ||
                skill.name.contains(q, ignoreCase = true) ||
                skill.desc.contains(q, ignoreCase = true) ||
                skill.type.contains(q, ignoreCase = true) ||
                skill.element.contains(q, ignoreCase = true) ||
                skill.power.toString().contains(q) ||
                skill.cost.toString().contains(q)
            elementMatch && typeMatch && searchMatch
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

    fun toggleElement(element: String) {
        _selectedElements.value = if (_selectedElements.value.contains(element)) {
            _selectedElements.value - element
        } else {
            _selectedElements.value + element
        }
    }

    fun toggleSkillType(type: String) {
        _selectedSkillTypes.value = if (_selectedSkillTypes.value.contains(type)) {
            _selectedSkillTypes.value - type
        } else {
            _selectedSkillTypes.value + type
        }
    }

    fun clearFilters() {
        _selectedElements.value = emptySet()
        _selectedSkillTypes.value = emptySet()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    private var listScrollIndex = 0
    private var listScrollOffset = 0

    fun initialListScrollIndex(): Int = listScrollIndex

    fun initialListScrollOffset(): Int = listScrollOffset

    fun saveListScrollPosition(index: Int, offset: Int) {
        listScrollIndex = index
        listScrollOffset = offset
    }
}
