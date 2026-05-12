package com.yinpei.rocodex.ui.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.yinpei.rocodex.data.mapAvatarShinyPath
import com.yinpei.rocodex.data.mapTraitIconPath
import com.yinpei.rocodex.data.model.Evo
import com.yinpei.rocodex.data.model.Pet
import com.yinpei.rocodex.data.model.PetForm
import com.yinpei.rocodex.data.model.Skill
import com.yinpei.rocodex.ui.components.ElementBadgeRow
import com.yinpei.rocodex.ui.components.SkillCard
import com.yinpei.rocodex.ui.components.StatBarGroup
import com.yinpei.rocodex.ui.theme.LocalWindowSizeClass
import kotlin.math.ceil

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    petId: Int,
    onBack: () -> Unit,
    onPetClick: (Int) -> Unit = {},
    onSkillClick: (Int) -> Unit = {},
    viewModel: DetailViewModel = viewModel()
) {
    val pet = remember(petId) { viewModel.getPet(petId) }
    val primaryElement = pet?.element?.firstOrNull() ?: "普通"
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
                    Text(text = pet?.name ?: "精灵详情")
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
        if (pet == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "该精灵不存在",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        } else {
            DetailContent(
                pet = pet,
                onPetClick = onPetClick,
                onSkillClick = { skill ->
                    viewModel.getSkillCatalogIndex(skill)?.let(onSkillClick)
                },
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DetailContent(
    pet: Pet,
    onPetClick: (Int) -> Unit,
    onSkillClick: (Skill) -> Unit,
    modifier: Modifier = Modifier
) {
    val windowSize = LocalWindowSizeClass.current

    val cellsSkillsRow = when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 2
        WindowWidthSizeClass.Medium -> 3
        WindowWidthSizeClass.Expanded -> 4
        else -> 3
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 头像 + 基本信息
        PetHeader(pet = pet)

        Spacer(modifier = Modifier.height(16.dp))

        // 属性标签
        ElementBadgeRow(
            elements = pet.element,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 简介
        PetDescriptionSection(pet = pet)

        Spacer(modifier = Modifier.height(16.dp))

        // 特性
        TraitSection(pet = pet)

        Spacer(modifier = Modifier.height(16.dp))

        // 种族值
        Text(
            text = "种族值",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        StatBarGroup(
            hp = pet.hp,
            atk = pet.atk,
            mat = pet.mat,
            def = pet.def,
            mdf = pet.mdf,
            spd = pet.spd,
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 进化链
        if (pet.evo.size > 1) {
            EvolutionSection(pet.evo, onPetClick)
            Spacer(modifier = Modifier.height(20.dp))
        }
        // 其他形态
        if (pet.forms.isNotEmpty()) {
            FormsSection(pet.forms, onPetClick)
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 技能组
        SkillGroupSection("精灵技能", pet.skills.group1, cellsSkillsRow, onSkillClick)
        SkillGroupSection("血脉技能", pet.skills.group2, cellsSkillsRow, onSkillClick)
        SkillGroupSection("可学技能", pet.skills.group3, cellsSkillsRow, onSkillClick)

        Spacer(modifier = Modifier.height(24.dp))
    }
}

fun calculateCardHeight(totalItems: Int, itemsPerRow: Int, itemHeight: Int): Int {
    if (totalItems <= 0) return 0

    // 使用 ceil 进行向上取整，注意要先转为 Double 运算
    val rows = ceil(totalItems.toDouble() / itemsPerRow).toInt()

    return rows * itemHeight
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun SkillGroupSection(
    title: String,
    skills: List<Skill>,
    row: Int,
    onSkillClick: (Skill) -> Unit
) {
    if (skills.isEmpty()) return

    Text(
        text = "$title (${skills.size})",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(12.dp))
    LazyVerticalGrid(
        columns = GridCells.Fixed(row),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(calculateCardHeight(skills.size,row,140).dp),
        userScrollEnabled = false
    ) {
        items(skills) { skill ->
            SkillCard(skill = skill, onClick = { onSkillClick(skill) })
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
private fun PetHeader(pet: Pet) {
    val primaryElement = pet.element.firstOrNull() ?: "普通"
    val avatarBg = if (pet.element.size == 1) {
        Brush.linearGradient(
            colors = listOf(
                getElementColor(primaryElement).copy(alpha = 0.2f),
                getElementColor(primaryElement).copy(alpha = 0.05f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = pet.element.map { getElementColor(it).copy(alpha = 0.2f) }
        )
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // 适当减小内边距以适应双列
            verticalAlignment = Alignment.CenterVertically // 垂直居中对齐
        ) {
            // 第一份 Column (左侧)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f) // 占用一半空间
            ) {
                PetContent(pet) // 建议提取成一个小组件
            }
            if (pet.shiny == 1) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f) // 占用另一半空间
                ) {
                    PetContent(pet, true)
                }
            }

        }
    }
}

@Composable
private fun PetContent(pet: Pet, isShiny: Boolean? = false) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth() // 确保 Column 撑开到 Card 的宽度
            .padding(24.dp),
    ) {
        // 头像
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = if (isShiny == false) {
                    mapAvatarPath(pet.avatar)
                } else mapAvatarShinyPath(pet.avatar),
                contentDescription = pet.name,
                modifier = Modifier
                    .size(140.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 编号
        Text(
            text = pet.pindex,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // 名称
        Text(
            text = if (isShiny == false) {
                pet.name
            } else "异色形态",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


@Composable
private fun TraitSection(pet: Pet) {
    if (pet.trait.name.isEmpty()) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "无特性",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
        return
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = mapTraitIconPath(pet.trait.name),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = pet.trait.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "特性",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = pet.trait.desc,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun PetDescriptionSection(pet: Pet) {
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
                text = pet.nick ?: "",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = pet.description ?: "",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem("身高", pet.height ?: "未知")
                InfoItem("体重", pet.weight ?: "未知")
                InfoItem("获取地点", pet.loc ?: "未知")
            }
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun EvolutionSection(evoList: List<Evo>, onPetClick: (Int) -> Unit) {
    if (evoList.isEmpty()) return

    Column {
        Text(
            text = "进化链",
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                evoList.forEachIndexed { index, evo ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onPetClick(evo.id) }
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = mapAvatarPath(evo.avatar),
                                contentDescription = evo.name,
                                modifier = Modifier.size(56.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = evo.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = evo.lv,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }

                    if (index < evoList.size - 1) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FormsSection(forms: List<PetForm>, onPetClick: (Int) -> Unit) {
    if (forms.isEmpty()) return

    Column {
        Text(
            text = "其他形态",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(((forms.size + 2) / 3 * 140).dp),
            userScrollEnabled = false
        ) {
            items(forms) { form ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                    ),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.background.copy(alpha = 0.5f)
                    ),
                    onClick = { onPetClick(form.id) }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        AsyncImage(
                            model = mapAvatarPath(form.avatar),
                            contentDescription = form.name,
                            modifier = Modifier.size(56.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = form.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = when (form.type != "") {
                                true -> form.type
                                else -> "首领化"
                            },
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
