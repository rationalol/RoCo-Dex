package com.yinpei.rocodex.ui.weakness

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.yinpei.rocodex.data.allElements
import com.yinpei.rocodex.data.getElementColor
import com.yinpei.rocodex.data.mapElementIconPath
import com.yinpei.rocodex.data.mapSkillTypeIconPath
import com.yinpei.rocodex.ui.components.ElementBadge

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WeaknessScreen(
    onBack: () -> Unit,
    viewModel: WeaknessViewModel = viewModel()
) {
    val selectedElements by viewModel.selectedElements.collectAsState()
    val dealDamageUp by viewModel.dealDamageUp.collectAsState()
    val dealDamageDown by viewModel.dealDamageDown.collectAsState()
    val takeDamageUp by viewModel.takeDamageUp.collectAsState()
    val takeDamageDown by viewModel.takeDamageDown.collectAsState()
    val hasSelection = selectedElements.isNotEmpty()
    val selectionCount = selectedElements.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("属性克制") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "最多可选 2 个属性，再次点击已选属性可取消",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 属性选择：每行 4 个，折行
            allElements.chunked(4).forEach { rowElements ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    rowElements.forEach { element ->
                        val color = getElementColor(element)
                        val isSelected = element in selectedElements
                        Box(modifier = Modifier.weight(1f)) {
                            WeaknessElementChip(
                                element = element,
                                elementColor = color,
                                isSelected = isSelected,
                                onClick = { viewModel.toggleElement(element) }
                            )
                        }
                    }
                    repeat(4 - rowElements.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            val emptyHint = if (hasSelection) "无" else "请先选择属性"
            val dealDamageDownEmptyMessage = when {
                !hasSelection -> "请先选择属性"
                selectionCount >= 2 -> "双属性下不计算此项"
                else -> "无"
            }

            EffectivenessSection(
                title = "造成伤害增加",
                fontColor = 0xFF4FBD72,
                type = true,
                elements = if (hasSelection) dealDamageUp else emptyList(),
                emptyMessage = emptyHint,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            EffectivenessSection(
                title = "造成伤害降低",
                fontColor = 0xFFC4945A,
                type = true,
                elements = if (hasSelection) dealDamageDown else emptyList(),
                emptyMessage = dealDamageDownEmptyMessage,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            EffectivenessSection(
                title = "受到伤害增加",
                fontColor = 0xFF4FBD72,
                type = false,
                elements = if (hasSelection) takeDamageUp else emptyList(),
                emptyMessage = emptyHint,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            EffectivenessSection(
                title = "受到伤害降低",
                fontColor = 0xFFC4945A,
                type = false,
                elements = if (hasSelection) takeDamageDown else emptyList(),
                emptyMessage = emptyHint,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun WeaknessElementChip(
    element: String,
    elementColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundModifier = if (isSelected) {
        Modifier.background(
            Brush.verticalGradient(
                colors = listOf(
                    elementColor,
                    MaterialTheme.colorScheme.background.copy(alpha = 0.4f)
                )
            )
        )
    } else {
        Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .then(backgroundModifier)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = mapElementIconPath(element),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = element,
                color = if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EffectivenessSection(
    title: String,
    fontColor: Long,
    type: Boolean,
    elements: List<String>,
    modifier: Modifier = Modifier,
    emptyMessage: String = "无"
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = modifier
                    .clip(RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = mapSkillTypeIconPath(
                        when (type) {
                            true -> "物攻"
                            else -> "防御"
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(fontColor)
                )
            }


            Spacer(modifier = Modifier.height(10.dp))

            if (elements.isEmpty()) {
                Text(
                    text = emptyMessage,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    elements.forEach { element ->
                        ElementBadge(element = element, uniformWidth = true)
                    }
                }
            }
        }
    }
}
