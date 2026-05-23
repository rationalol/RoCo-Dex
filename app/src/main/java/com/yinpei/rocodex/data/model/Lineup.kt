package com.yinpei.rocodex.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lineups")
data class Lineup(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val pets: List<LineupPet> // List of configured pets, max 6
)

data class LineupPet(
    val petId: Int,
    val skills: List<String> = emptyList(), // Selected skill names (max 4)
    val nature: String? = null, // Nature label, e.g. "固执"
    val ivs: Map<String, Int> = emptyMap(), // Map of StatType name to IV value (e.g. "ATK" to 10)
    val bloodline: String? = null // 属性血脉，例如 "火"
)
