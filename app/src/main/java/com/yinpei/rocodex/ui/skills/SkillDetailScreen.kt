package com.yinpei.rocodex.ui.skills

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.yinpei.rocodex.data.getElementColor
import com.yinpei.rocodex.data.mapAvatarPath
import com.yinpei.rocodex.data.mapSkillIconPath
import com.yinpei.rocodex.data.mapSkillTypeIconPath
import com.yinpei.rocodex.data.model.Skill
import com.yinpei.rocodex.data.model.SkillCatalogEntry
import com.yinpei.rocodex.data.model.SkillLearnerPet
import com.yinpei.rocodex.ui.components.ElementBadgeRow
import com.yinpei.rocodex.ui.components.PetCard
import com.yinpei.rocodex.ui.components.SkillCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillDetailScreen(
    skillIndex: Int,
    onBack: () -> Unit,
    onPetClick: (Int) -> Unit,
    viewModel: SkillDetailViewModel = viewModel()
) {


    val entry = remember(skillIndex) { viewModel.getSkillCatalogEntry(skillIndex) }
    val skill = entry?.asSkill()
    val primaryElement = skill?.element ?: "普通"
    val elementColor = getElementColor(primaryElement)
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            elementColor.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.surface
        )
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = skill?.name ?: "招式详情")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        modifier = Modifier.background(backgroundBrush)
    ) { paddingValues ->
        if (entry == null || skill == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "未找到该招式",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        } else {
            SkillDetailContent(
                entry = entry,
                viewModel = viewModel,
                onPetClick = onPetClick,
                modifier = Modifier.padding(paddingValues)
            )
        }

    }
}

@Composable
private fun SkillDetailContent(
    entry: SkillCatalogEntry,
    viewModel: SkillDetailViewModel,
    onPetClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val skill = entry.asSkill()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        SkillHeaderCard(entry = entry)

        Spacer(modifier = Modifier.height(16.dp))

        ElementBadgeRow(
            elements = listOf(skill.element),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        SkillMetaSection(skill = skill)

        Spacer(modifier = Modifier.height(16.dp))

        SkillDescriptionCard(desc = skill.desc)

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "可学精灵 (${entry.pets.size})",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (entry.pets.isEmpty()) {
            Text(
                text = "暂无关联精灵数据",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        } else {
            LearnersGrid(
                learners = entry.pets,
                viewModel = viewModel,
                onPetClick = onPetClick
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SkillHeaderCard(entry: SkillCatalogEntry) {
    val skill = entry.asSkill()
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            AsyncImage(
                model = mapSkillIconPath(skill.element, skill.name),
                contentDescription = skill.name,
                modifier = Modifier.size(96.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = skill.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = mapSkillTypeIconPath(skill.type, isSystemInDarkTheme()),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = skill.type,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun SkillMetaSection(skill: Skill) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "招式信息",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                MetaItem(
                    label = "属性",
                    value = skill.element,
                    modifier = Modifier.weight(1f)
                )
                MetaItem(
                    label = "能耗",
                    value = skill.cost.toString(),
                    modifier = Modifier.weight(1f)
                )
                MetaItem(
                    label = "威力",
                    value = if (skill.power > 0) skill.power.toString() else "—",
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "等级 / 条件",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Text(
                text = skill.lv.ifBlank { "—" },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun MetaItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SkillDescriptionCard(desc: String) {
    Text(
        text = "效果说明",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(12.dp))
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = desc,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
            lineHeight = 22.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun LearnersGrid(
    learners: List<SkillLearnerPet>,
    viewModel: SkillDetailViewModel,
    onPetClick: (Int) -> Unit
) {
    val rows = (learners.size + 2) / 3
    val idealHeightDp = rows * 220
    val gridHeightDp = idealHeightDp.coerceIn(120, 2000)
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(gridHeightDp.dp),
        userScrollEnabled = idealHeightDp > 2000
    ) {
        items(
            items = learners,
            key = { "${it.id}_${it.name}_${it.avatar}" }
        ) { learner ->
            val pet = viewModel.getPet(learner.id)
            if (pet != null) {
                PetCard(pet = pet, onClick = { onPetClick(learner.id) })
            } else {
                LearnerFallbackCard(learner = learner, onClick = { onPetClick(learner.id) })
            }
        }
    }
}

@Composable
private fun LearnerFallbackCard(
    learner: SkillLearnerPet,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            AsyncImage(
                model = mapAvatarPath(learner.avatar),
                contentDescription = learner.name,
                modifier = Modifier.size(56.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = learner.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            Text(
                text = "NO.${learner.id.toString().padStart(3, '0')}",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
            )
        }
    }
}
