package com.yinpei.rocodex.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yinpei.rocodex.data.allEggGroups
import com.yinpei.rocodex.data.allElements
import com.yinpei.rocodex.data.getEggGroupColor
import com.yinpei.rocodex.data.getElementColor
import com.yinpei.rocodex.data.mapElementIconPath

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterBar(
    selectedElements: Set<String>,
    onToggleElement: (String) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier,
    showShinyFilter: Boolean = false,
    isShinyOnly: Boolean = false,
    onToggleShiny: () -> Unit = {},
    showEggGroupFilter: Boolean = false,
    selectedEggGroups: Set<String> = emptySet(),
    onToggleEggGroup: (String) -> Unit = {}
) {
    var elementExpanded by remember { mutableStateOf(false) }
    var eggExpanded by remember { mutableStateOf(false) }

    val hasElementFilters = selectedElements.isNotEmpty()
    val hasEggGroupFilters = selectedEggGroups.isNotEmpty()
    val hasAnyFilter = hasElementFilters || hasEggGroupFilters || isShinyOnly

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // 属性筛选切换按钮
            Surface(
                onClick = { elementExpanded = !elementExpanded },
                shape = RoundedCornerShape(20.dp),
                color = if (hasElementFilters) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.height(36.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (hasElementFilters) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (!hasElementFilters) "属性筛选" else "属性 (${selectedElements.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (hasElementFilters) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (elementExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (hasElementFilters) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 蛋组筛选切换按钮
            if (showEggGroupFilter) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    onClick = { eggExpanded = !eggExpanded },
                    shape = RoundedCornerShape(20.dp),
                    color = if (hasEggGroupFilters) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.height(36.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = if (hasEggGroupFilters) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (!hasEggGroupFilters) "蛋组筛选" else "蛋组 (${selectedEggGroups.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (hasEggGroupFilters) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (eggExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = if (hasEggGroupFilters) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 异色筛选按钮 (与属性筛选同级)
            if (showShinyFilter) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    onClick = onToggleShiny,
                    shape = RoundedCornerShape(20.dp),
                    color = if (isShinyOnly) Color(0xFFFFD700).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = if (isShinyOnly) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700)) else null,
                    modifier = Modifier.height(36.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = "file:///android_asset/shiny/1.png",
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "异色",
                            fontSize = 13.sp,
                            fontWeight = if (isShinyOnly) FontWeight.Bold else FontWeight.Medium,
                            color = if (isShinyOnly) Color(0xFFB8860B) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            if (hasAnyFilter) {
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = onClearFilters,
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("重置", fontSize = 13.sp)
                }
            }
        }

        AnimatedVisibility(
            visible = elementExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
            ) {
                FlowRow(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allElements.forEach { element ->
                        val isSelected = selectedElements.contains(element)
                        val color = getElementColor(element)
                        
                        FilterBadge(
                            text = element,
                            iconUrl = mapElementIconPath(element),
                            isSelected = isSelected,
                            color = color,
                            onClick = { onToggleElement(element) }
                        )
                    }
                }
            }
        }

        if (showEggGroupFilter) {
            AnimatedVisibility(
                visible = eggExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 1.dp,
                ) {
                    FlowRow(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allEggGroups.forEach { group ->
                            val isSelected = selectedEggGroups.contains(group)
                            val color = getEggGroupColor(group)

                            FilterBadge(
                                text = group,
                                isSelected = isSelected,
                                color = color,
                                onClick = { onToggleEggGroup(group) }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun FilterBadge(
    text: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit,
    iconUrl: String? = null
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, color) else null,
        modifier = Modifier.height(32.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (iconUrl != null) {
                AsyncImage(
                    model = iconUrl,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

