package com.yinpei.rocodex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yinpei.rocodex.data.getElementColor
import com.yinpei.rocodex.data.mapElementIconPath

/** 克制页等场景：约束整行布局最小宽度（padding 在内侧），需容纳两字属性名 + 图标 */
private val ElementBadgeUniformMinWidth = 92.dp

@Composable
fun ElementBadge(
    element: String,
    modifier: Modifier = Modifier,
    /** 为 true 时保证最小宽度一致，内容居中（属性克制等场景） */
    uniformWidth: Boolean = false
) {
    val color = getElementColor(element)
    Row(
        modifier = modifier
            .then(if (uniformWidth) Modifier.widthIn(min = ElementBadgeUniformMinWidth) else Modifier)
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (uniformWidth) Arrangement.Center else Arrangement.Start
    ) {
        AsyncImage(
            model = mapElementIconPath(element),
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = element,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ElementBadgeRow(
    elements: List<String>,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        elements.forEach { element ->
            ElementBadge(element = element)
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}
