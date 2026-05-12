package com.yinpei.rocodex.ui.gallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yinpei.rocodex.data.model.Pet
import com.yinpei.rocodex.data.repository.PetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PetRepository(application)

    private val _allPets = MutableStateFlow<List<Pet>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedElements = MutableStateFlow<Set<String>>(emptySet())
    val selectedElements: StateFlow<Set<String>> = _selectedElements.asStateFlow()

    private val _isShinyOnly = MutableStateFlow(false)
    val isShinyOnly: StateFlow<Boolean> = _isShinyOnly.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val pets: StateFlow<List<Pet>> = combine(_allPets, _selectedElements, _isShinyOnly, _searchQuery) { all, elements, shinyOnly, query ->
        all.filter { pet ->
            val elementMatch = elements.isEmpty() || pet.element.any { it in elements }
            val shinyMatch = !shinyOnly || pet.shiny == 1
            val nameMatch = query.isEmpty() || pet.name.contains(query, ignoreCase = true) || pet.id.toString().contains(query)
            val pindexNotEmpty = pet.pindex.isNotEmpty()
            elementMatch && shinyMatch && nameMatch && pindexNotEmpty
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadPets()
    }

    private fun loadPets() {
        viewModelScope.launch {
            _isLoading.value = true
            _allPets.value = repository.getAllPets()
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

    fun toggleShiny() {
        _isShinyOnly.value = !_isShinyOnly.value
    }

    fun clearFilters() {
        _selectedElements.value = emptySet()
        _isShinyOnly.value = false
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}
