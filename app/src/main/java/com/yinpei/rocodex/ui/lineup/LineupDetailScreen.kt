package com.yinpei.rocodex.ui.lineup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.yinpei.rocodex.data.getElementColor
import com.yinpei.rocodex.data.mapAvatarPath
import com.yinpei.rocodex.data.mapElementIconPath
import com.yinpei.rocodex.data.mapSkillIconPath
import com.yinpei.rocodex.data.model.LineupPet
import com.yinpei.rocodex.data.model.Nature
import com.yinpei.rocodex.data.model.Pet
import com.yinpei.rocodex.data.model.Skill
import com.yinpei.rocodex.data.model.StatType
import com.yinpei.rocodex.ui.theme.RoCoFamily
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LineupDetailScreen(
    lineupId: Int,
    onBack: () -> Unit,
    onAddPetClick: (Int) -> Unit, // passes lineupId
    onPetClick: (Int) -> Unit,
    viewModel: LineupViewModel = viewModel()
) {
    val lineups by viewModel.lineups.collectAsState()
    val lineup = lineups.find { it.id == lineupId }

    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }

    if (lineup == null) {
        // Handle deleted or not found
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("队伍不存在或已删除")
        }
        return
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = { Text(lineup.name, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        editName = lineup.name
                        showEditDialog = true
                    }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "重命名")
                    }
                }
            )
        },
        floatingActionButton = {
            if (lineup.pets.size < 6) {
                FloatingActionButton(
                    onClick = { onAddPetClick(lineup.id) },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(Icons.Default.Add, contentDescription = "添加精灵")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (lineup.pets.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "队伍为空，点击右下角添加精灵",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(lineup.pets.size) { index ->
                        val lineupPet = lineup.pets[index]
                        val pet = viewModel.getPetById(lineupPet.petId)
                        if (pet != null) {
                            LineupPetCard(
                                pet = pet,
                                lineupPet = lineupPet,
                                onClick = { onPetClick(index) },
                                onRemove = { viewModel.removePetFromLineup(lineup, index) }
                            )
                        }
                    }
                }
            }
        }

        if (showEditDialog) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("修改队伍名称") },
                text = {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("队伍名称") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (editName.isNotBlank()) {
                                viewModel.updateLineupName(lineup, editName)
                                showEditDialog = false
                            }
                        }
                    ) {
                        Text("确定")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

@Composable
fun SwipeToRevealCard(
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val maxSwipe = with(density) { -80.dp.toPx() }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        coroutineScope.launch {
                            if (offsetX.value < maxSwipe / 2) {
                                offsetX.animateTo(maxSwipe)
                            } else {
                                offsetX.animateTo(0f)
                            }
                        }
                    },
                    onDragCancel = {
                        coroutineScope.launch {
                            offsetX.animateTo(0f)
                        }
                    }
                ) { change, dragAmount ->
                    val newOffset = (offsetX.value + dragAmount).coerceIn(maxSwipe, 0f)
                    if (newOffset != offsetX.value) {
                        change.consume()
                        coroutineScope.launch {
                            offsetX.snapTo(newOffset)
                        }
                    }
                }
            }
    ) {
        // Background
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                modifier = Modifier
                    .padding(end = 28.dp)
                    .clickable {
                        onRemove()
                        coroutineScope.launch {
                            offsetX.snapTo(0f)
                        }
                    },
                tint = MaterialTheme.colorScheme.errorContainer
            )
        }

        // Foreground
        Box(
            modifier = Modifier.offset { IntOffset(offsetX.value.roundToInt(), 0) }
        ) {
            content()
        }
    }
}

@Composable
fun LineupPetCard(
    pet: Pet,
    lineupPet: LineupPet,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    val nature = lineupPet.nature?.let { Nature.fromLabel(it) }
    val ivLabels = lineupPet.ivs.keys.mapNotNull { key ->
        StatType.entries.find { it.name == key }?.label
    }

    val allSkills = pet.skills.group1 + pet.skills.group2 + pet.skills.group3
    
    val skillDataList = (0..3).mapNotNull { i ->
        val skillName = lineupPet.skills.getOrNull(i)
        val skill = skillName?.let { name -> allSkills.find { it.name == name } }
        if (skill != null) {
            SkillData(
                name = skill.name,
                iconUrl = mapSkillIconPath(skill.element, skill.name)
            )
        } else null
    }

    val monsterData = MonsterData(
        name = pet.name,
        avatarUrl = mapAvatarPath(pet.avatar),
        elements = pet.element.map { mapElementIconPath(it) },
        primaryElement = pet.element.firstOrNull() ?: "普通",
        bloodlineElement = lineupPet.bloodline ?: pet.element.firstOrNull(),
        natureLabel = lineupPet.nature,
        natureUp = nature?.up?.label,
        natureDown = nature?.down?.label,
        ivs = ivLabels,
        skills = skillDataList
    )

    SwipeToRevealCard(
        onRemove = onRemove,
        modifier = Modifier.fillMaxWidth()
    ) {
        MonsterStatusCard(
            data = monsterData,
            modifier = Modifier.clickable(onClick = onClick)
        )
    }
}

data class MonsterData(
    val name: String,
    val avatarUrl: String,
    val elements: List<String>,
    val primaryElement: String,
    val bloodlineElement: String?,
    val natureLabel: String?,
    val natureUp: String?,
    val natureDown: String?,
    val ivs: List<String>,
    val skills: List<SkillData>
)

data class SkillData(
    val name: String,
    val iconUrl: String
)

@Composable
fun MonsterStatusCard(
    data: MonsterData,
    modifier: Modifier = Modifier
) {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val labelBgColor = if (isDark) Color(0xFFB38022) else Color(0xFFFFC14D)
    val labelTextColor = if (isDark) Color(0xFF3E2C0C) else Color(0xFF5A4011)

    val primaryColor = getElementColor(data.primaryElement)

    // 渐变背景：从属性色淡色到白色
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            primaryColor.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.background
        )
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
    ) {
        Box(
            modifier = Modifier.background(gradientBrush)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Left: Avatar
                Box(
                    modifier = Modifier
                        .weight(0.35f)
                        .aspectRatio(1f) // 基于宽度计算高度，保持正方形
                        .clip(RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = data.avatarUrl,
                        contentDescription = data.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                // Right: Info
                Column(
                    modifier = Modifier.weight(0.65f),
                    verticalArrangement = Arrangement.spacedBy(10.dp) // 增加间距
                ) {
                    // Row 1: Name and Elements
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = data.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = RoCoFamily
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            data.elements.forEach { elemUrl ->
                                AsyncImage(
                                    model = elemUrl,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            if (data.bloodlineElement != null) {
                                Spacer(modifier = Modifier.width(4.dp))
                                AsyncImage(
                                    model = mapElementIconPath(data.bloodlineElement),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                    // Row 2: Nature
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LabelBadge(
                            text = "性  格",
                            bgColor = labelBgColor,
                            textColor = labelTextColor
                        )
                        NatureValueBadge(
                            natureLabel = data.natureLabel,
                            natureUp = data.natureUp,
                            natureDown = data.natureDown
                        )
                    }

                    // Row 3: IVs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LabelBadge(
                            text = "个体资质",
                            bgColor = labelBgColor,
                            textColor = labelTextColor,
                        )
                        IvValueBadge(ivs = data.ivs)
                    }

                    // Row 4: Skills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val displayCount = maxOf(4, data.skills.size)
                        for (i in 0 until displayCount) {
                            val skill = data.skills.getOrNull(i)
                            SkillSlot(skill)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LabelBadge(text: String, bgColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            fontFamily = RoCoFamily
        )
    }
}

@Composable
fun NatureValueBadge(natureLabel: String?, natureUp: String?, natureDown: String?) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (natureLabel == null) {
            Text("未配置", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            if (natureUp == null && natureDown == null) {
                Text(
                    natureLabel,
                    fontSize = 8.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        natureUp ?: "",
                        fontSize = 8.sp,
                        color = Color(0xFF65C04A),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        natureDown ?: "",
                        fontSize = 8.sp,
                        color = Color(0xFFD9534F),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun IvValueBadge(ivs: List<String>) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (ivs.isEmpty()) {
            Text("未配置", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ivs.forEach { iv ->
                    Text(
                        iv,
                        fontSize = 8.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun RowScope.SkillSlot(skill: SkillData?) {
    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (skill != null) {
            AsyncImage(
                model = skill.iconUrl,
                contentDescription = skill.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Text overlay at bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = skill.name,
                    color = Color.White,
                    fontSize = 4.sp,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        // 1. 关闭系统底层的字体默认内边距
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        ),
                        // 2. 修剪行高带来的多余空白
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.Both // 核心：同时修剪顶部和底部的行高空白
                        )
                    )
                )
            }
        } else {
            // Empty slot
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Empty Skill",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}
