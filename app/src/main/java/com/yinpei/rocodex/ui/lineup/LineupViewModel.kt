package com.yinpei.rocodex.ui.lineup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yinpei.rocodex.data.local.AppDatabase
import com.yinpei.rocodex.data.model.Lineup
import com.yinpei.rocodex.data.model.Pet
import com.yinpei.rocodex.data.repository.LineupRepository
import com.yinpei.rocodex.data.repository.PetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LineupViewModel(application: Application) : AndroidViewModel(application) {

    private val lineupDao = AppDatabase.getDatabase(application).lineupDao()
    private val lineupRepository = LineupRepository(lineupDao)
    private val petRepository = PetRepository(application)

    val lineups: StateFlow<List<Lineup>> = lineupRepository.getAllLineups()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // To display pets in a lineup, we need pet details
    fun getPetById(id: Int): Pet? {
        return petRepository.getPetById(id)
    }

    fun getAllPets(): List<Pet> {
        return petRepository.getAllPets()
    }

    fun createLineup(name: String) {
        viewModelScope.launch {
            val newLineup = Lineup(name = name, pets = emptyList())
            lineupRepository.insertLineup(newLineup)
        }
    }

    fun deleteLineup(lineup: Lineup) {
        viewModelScope.launch {
            lineupRepository.deleteLineup(lineup)
        }
    }

    fun updateLineupName(lineup: Lineup, newName: String) {
        viewModelScope.launch {
            lineupRepository.updateLineup(lineup.copy(name = newName))
        }
    }

    fun addPetToLineup(lineup: Lineup, petId: Int, onComplete: () -> Unit = {}) {
        android.util.Log.d("LineupDebug", "LineupViewModel: addPetToLineup called for petId=$petId, current size=${lineup.pets.size}")
        if (lineup.pets.size < 6) {
            viewModelScope.launch {
                android.util.Log.d("LineupDebug", "LineupViewModel: addPetToLineup coroutine started")
                val newPets = lineup.pets.toMutableList().apply { 
                    add(com.yinpei.rocodex.data.model.LineupPet(petId = petId)) 
                }
                lineupRepository.updateLineup(lineup.copy(pets = newPets))
                android.util.Log.d("LineupDebug", "LineupViewModel: updateLineup finished in DB")
                // Switch to Main thread to execute the callback
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    android.util.Log.d("LineupDebug", "LineupViewModel: executing onComplete callback on Main thread")
                    onComplete()
                }
            }
        } else {
            android.util.Log.d("LineupDebug", "LineupViewModel: lineup is full, cannot add")
        }
    }

    fun removePetFromLineup(lineup: Lineup, index: Int) {
        viewModelScope.launch {
            val newPets = lineup.pets.toMutableList().apply { removeAt(index) }
            lineupRepository.updateLineup(lineup.copy(pets = newPets))
        }
    }

    fun updatePetInLineup(lineup: Lineup, index: Int, updatedPet: com.yinpei.rocodex.data.model.LineupPet) {
        viewModelScope.launch {
            val newPets = lineup.pets.toMutableList().apply { set(index, updatedPet) }
            lineupRepository.updateLineup(lineup.copy(pets = newPets))
        }
    }
}
