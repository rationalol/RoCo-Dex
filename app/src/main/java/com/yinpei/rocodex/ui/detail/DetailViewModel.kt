package com.yinpei.rocodex.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.yinpei.rocodex.data.model.Pet
import com.yinpei.rocodex.data.model.Skill
import com.yinpei.rocodex.data.repository.PetRepository

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PetRepository(application)

    fun getPet(id: Int): Pet? {
        return repository.getPetById(id)
    }

    fun getSkillCatalogIndex(skill: Skill): Int? = repository.getSkillCatalogIndex(skill)
}
