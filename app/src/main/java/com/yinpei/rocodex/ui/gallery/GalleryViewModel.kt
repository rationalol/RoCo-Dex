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

    private val _selectedElement = MutableStateFlow("全部")
    val selectedElement: StateFlow<String> = _selectedElement.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val pets: StateFlow<List<Pet>> = combine(_allPets, _selectedElement, _searchQuery) { all, element, query ->
        all.filter { pet ->

            val elementMatch = element == "全部" || element in pet.element
            val nameMatch = query.isEmpty() || pet.name.contains(query, ignoreCase = true) || pet.id.toString().contains(query)
            val pindexNotEmpty = pet.pindex.isNotEmpty()
            elementMatch && nameMatch && pindexNotEmpty
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

    fun selectElement(element: String) {
        _selectedElement.value = element
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}
