package com.yinpei.rocodex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yinpei.rocodex.data.allElements
import com.yinpei.rocodex.data.getElementColor
import com.yinpei.rocodex.data.mapElementIconPath

@Composable
fun FilterBar(
    selectedElement: String,
    onElementSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        allElements.forEach { element ->
            val isSelected = selectedElement == element
            val elementColor = if (element == "全部") {
                MaterialTheme.colorScheme.primary
            } else {
                getElementColor(element)
            }

            // 选中状态使用：属性色 -> 白色半透明 的垂直渐变
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
                // 未选中状态使用低透明度的表面色（毛玻璃感）
                Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .then(backgroundModifier)
                    .clickable { onElementSelected(element) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (element != "全部") {
                        AsyncImage(
                            model = mapElementIconPath(element),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = element,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
