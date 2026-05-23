package com.yinpei.rocodex.ui.lineup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yinpei.rocodex.data.model.LineupPet
import com.yinpei.rocodex.data.model.Nature
import com.yinpei.rocodex.data.model.StatType
import com.yinpei.rocodex.ui.components.SkillCard
import com.yinpei.rocodex.data.allElements
import coil.compose.AsyncImage
import com.yinpei.rocodex.data.mapElementIconPath

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LineupPetConfigScreen(
    lineupId: Int,
    petIndex: Int,
    onBack: () -> Unit,
    viewModel: LineupViewModel = viewModel()
) {
    val lineups by viewModel.lineups.collectAsState()
    val lineup = lineups.find { it.id == lineupId }
    
    android.util.Log.d("LineupDebug", "LineupPetConfigScreen: composed for lineupId=$lineupId, petIndex=$petIndex")
    
    if (lineup == null) {
        android.util.Log.d("LineupDebug", "LineupPetConfigScreen: lineup is null")
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("队伍不存在")
        }
        return
    }

    // If petIndex is equal to size, it means we are adding a new pet (but it might not be in the DB yet due to async)
    // Or it might be already in the DB. We handle both cases.
    val isNewPet = petIndex >= lineup.pets.size
    val lineupPet = lineup.pets.getOrNull(petIndex)
    
    android.util.Log.d("LineupDebug", "LineupPetConfigScreen: lineup found, pets size=${lineup.pets.size}, isNewPet=$isNewPet, lineupPet is null=${lineupPet == null}")
    
    // If it's a new pet that hasn't been saved to DB yet, we need the petId. 
    // But we don't have petId in the route arguments. 
    // Actually, if we just wait for the flow to emit, it will recompose.
    // Let's just show a loading indicator if it's not available yet but we expect it to be.
    if (lineupPet == null) {
        // If the petIndex is out of bounds, it means the database update hasn't propagated to the UI yet.
        // We show a loading indicator until the flow emits the new list.
        android.util.Log.d("LineupDebug", "LineupPetConfigScreen: showing CircularProgressIndicator")
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val pet = viewModel.getPetById(lineupPet.petId)
    if (pet == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("精灵数据不存在")
        }
        return
    }

    // Local state for editing
    var ivs by remember { mutableStateOf(lineupPet.ivs) }
    var nature by remember { mutableStateOf(Nature.fromLabel(lineupPet.nature)) }
    var bloodline by remember { mutableStateOf(lineupPet.bloodline ?: pet.element.firstOrNull() ?: "普通") }
    var selectedSkills by remember { mutableStateOf(lineupPet.skills) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("配置 ${pet.name}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        val updatedPet = lineupPet.copy(
                            ivs = ivs,
                            nature = nature?.label,
                            skills = selectedSkills,
                            bloodline = bloodline
                        )
                        viewModel.updatePetInLineup(lineup, petIndex, updatedPet)
                        onBack()
                    }) {
                        Text("保存", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                IvSection(
                    ivs = ivs,
                    onIvChange = { statName, value ->
                        val newIvs = ivs.toMutableMap()
                        if (value == 0) {
                            newIvs.remove(statName)
                        } else {
                            newIvs[statName] = value
                        }
                        ivs = newIvs
                    }
                )
            }

            item {
                NatureSection(
                    selectedNature = nature,
                    onNatureSelect = { nature = it }
                )
            }

            item {
                BloodlineSection(
                    selectedBloodline = bloodline,
                    onBloodlineSelect = { newBloodline -> 
                        bloodline = newBloodline
                        // Remove any selected bloodline skills that don't match the new bloodline
                        val invalidBloodlineSkills = pet.skills.group2.filter { it.element != newBloodline }.map { it.name }
                        selectedSkills = selectedSkills.filter { it !in invalidBloodlineSkills }
                    }
                )
            }

            item {
                SkillSection(
                    pet = pet,
                    selectedSkills = selectedSkills,
                    bloodline = bloodline,
                    onSkillToggle = { skillName, isBloodline ->
                        val newSkills = selectedSkills.toMutableList()
                        if (newSkills.contains(skillName)) {
                            newSkills.remove(skillName)
                        } else {
                            if (newSkills.size < 4) {
                                if (isBloodline) {
                                    val allBloodlineSkills = pet.skills.group2.map { it.name }
                                    val hasBloodline = newSkills.any { it in allBloodlineSkills }
                                    if (!hasBloodline) {
                                        newSkills.add(skillName)
                                    }
                                } else {
                                    newSkills.add(skillName)
                                }
                            }
                        }
                        selectedSkills = newSkills
                    }
                )
            }
        }
    }
}

@Composable
fun IvSection(
    ivs: Map<String, Int>,
    onIvChange: (String, Int) -> Unit
) {
    Column {
        Text("个体值 (最多选择3项，7-10)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(12.dp))
        
        val stats = listOf(
            StatType.HP, StatType.ATK, StatType.DEF, 
            StatType.MAT, StatType.MDF, StatType.SPD
        )

        stats.forEach { stat ->
            val currentValue = ivs[stat.name] ?: 0
            val isSelected = currentValue > 0
            val canSelectMore = ivs.size < 3

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { checked ->
                        if (checked && canSelectMore) {
                            onIvChange(stat.name, 10) // default to 10 when selected
                        } else if (!checked) {
                            onIvChange(stat.name, 0)
                        }
                    },
                    enabled = isSelected || canSelectMore
                )
                Text(stat.label, modifier = Modifier.width(48.dp))
                
                if (isSelected) {
                    Slider(
                        value = currentValue.toFloat(),
                        onValueChange = { onIvChange(stat.name, it.toInt()) },
                        valueRange = 7f..10f,
                        steps = 2, // 7, 8, 9, 10 -> 3 intervals, so 2 steps between
                        modifier = Modifier.weight(1f)
                    )
                    Text(currentValue.toString(), modifier = Modifier.width(32.dp))
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                    Text("0", modifier = Modifier.width(32.dp), color = Color.Gray)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NatureSection(
    selectedNature: Nature?,
    onNatureSelect: (Nature?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text("性格", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedNature?.let { "${it.label} ${getNatureDesc(it)}" } ?: "未选择",
                onValueChange = {},
                readOnly = true,
                shape = CircleShape,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("未选择") },
                    onClick = {
                        onNatureSelect(null)
                        expanded = false
                    }
                )
                Nature.entries.forEach { nature ->
                    DropdownMenuItem(
                        text = { Text("${nature.label} ${getNatureDesc(nature)}") },
                        onClick = {
                            onNatureSelect(nature)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodlineSection(
    selectedBloodline: String,
    onBloodlineSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text("血脉", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedBloodline,
                onValueChange = {},
                readOnly = true,
                shape = CircleShape,
                leadingIcon = {
                    AsyncImage(
                        model = mapElementIconPath(selectedBloodline),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                val elements = allElements.filter { it != "全部" } + "首领"
                elements.forEach { element ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AsyncImage(
                                    model = mapElementIconPath(element),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(element)
                            }
                        },
                        onClick = {
                            onBloodlineSelect(element)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

fun getNatureDesc(nature: Nature): String {
    return "(+10%${nature.up.label} -10%${nature.down.label})"
}

@Composable
fun SkillSection(
    pet: com.yinpei.rocodex.data.model.Pet,
    selectedSkills: List<String>,
    bloodline: String?,
    onSkillToggle: (String, Boolean) -> Unit
) {
    Column {
        Text("技能 (已选 ${selectedSkills.size}/4)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        val bloodlineSkills = pet.skills.group2.filter { it.element == bloodline }
        SkillGroupList("精灵技能", pet.skills.group1, selectedSkills) {
            onSkillToggle(it, false)
        }
        SkillGroupList("可学技能", pet.skills.group3, selectedSkills) {
            onSkillToggle(it, false)
        }
        SkillGroupList("血脉技能", bloodlineSkills, selectedSkills) {
            onSkillToggle(it, false)
        }
    }
}

@Composable
fun SkillGroupList(
    title: String,
    skills: List<com.yinpei.rocodex.data.model.Skill>,
    selectedSkills: List<String>,
    onSkillToggle: (String) -> Unit
) {
    if (skills.isEmpty()) return

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(title, fontWeight = FontWeight.Medium, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(skills) { skill ->
                val isSelected = selectedSkills.contains(skill.name)
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onSkillToggle(skill.name) }

                ) {
                    SkillCard(skill = skill, onClick = { onSkillToggle(skill.name) })
                    if (isSelected) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}
