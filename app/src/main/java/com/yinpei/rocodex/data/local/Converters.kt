package com.yinpei.rocodex.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yinpei.rocodex.data.model.LineupPet

class Converters {
    @TypeConverter
    fun fromLineupPetList(value: List<LineupPet>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toLineupPetList(value: String): List<LineupPet> {
        val listType = object : TypeToken<List<LineupPet>>() {}.type
        return Gson().fromJson(value, listType) ?: emptyList()
    }
}
