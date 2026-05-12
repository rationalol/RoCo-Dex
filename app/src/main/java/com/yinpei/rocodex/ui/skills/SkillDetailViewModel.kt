package com.yinpei.rocodex.ui.skills

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.yinpei.rocodex.data.model.Pet
import com.yinpei.rocodex.data.model.SkillCatalogEntry
import com.yinpei.rocodex.data.repository.PetRepository

class SkillDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PetRepository(application)

    fun getSkillCatalogEntry(index: Int): SkillCatalogEntry? =
        repository.getSkillCatalogEntry(index)

    fun getPet(id: Int): Pet? = repository.getPetById(id)
}
