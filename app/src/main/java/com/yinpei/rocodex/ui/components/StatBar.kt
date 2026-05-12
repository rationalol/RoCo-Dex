package com.yinpei.rocodex.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yinpei.rocodex.data.mapAvatarPath
import com.yinpei.rocodex.data.mapAvatarShinyPath
import com.yinpei.rocodex.data.mapBaseStatsIconPath
import com.yinpei.rocodex.data.model.StatType

@Composable
fun StatBar(
    statType: StatType,
    value: Int,
    maxValue: Int = 255,
    color: Color,
    modifier: Modifier = Modifier
) {
    val fraction = (value.toFloat() / maxValue).coerceIn(0f, 1f)
    val animatedFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(durationMillis = 600),
        label = "statAnim"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model =
                mapBaseStatsIconPath(statType.label),
            contentDescription = statType.label,
            modifier = Modifier
                .size(12.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(8.dp))
        // 标签
        Text(
            text = statType.label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(36.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 数值
        Text(
            text = value.toString(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(32.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 进度条
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedFraction)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun StatBarGroup(
    hp: Int,
    atk: Int,
    mat: Int,
    def: Int,
    mdf: Int,
    spd: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
            .border(
                1.dp,
                MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)
    ) {
        StatBar(StatType.HP, hp, color = MaterialTheme.colorScheme.onBackground)
        StatBar(StatType.ATK, atk, color = MaterialTheme.colorScheme.onBackground)
        StatBar(StatType.MAT, mat, color = MaterialTheme.colorScheme.onBackground)
        StatBar(StatType.DEF, def, color = MaterialTheme.colorScheme.onBackground)
        StatBar(StatType.MDF, mdf, color = MaterialTheme.colorScheme.onBackground)
        StatBar(StatType.SPD, spd, color = MaterialTheme.colorScheme.onBackground)
    }
}
