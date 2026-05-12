package com.yinpei.rocodex.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yinpei.rocodex.data.getElementColor
import com.yinpei.rocodex.data.mapAvatarPath
import com.yinpei.rocodex.data.mapAvatarShinyPath
import com.yinpei.rocodex.data.mapElementIconPath
import com.yinpei.rocodex.data.model.Pet
import com.yinpei.rocodex.data.shinyIconPath
import com.yinpei.rocodex.ui.theme.RoCoFamily

@Composable
fun PetCard(
    pet: Pet,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = getElementColor(pet.element.firstOrNull() ?: "普通")

    // 渐变背景：从属性色淡色到白色
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            primaryColor.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.background
        )
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            ),
        color = Color.Transparent
    ) {


        Box(
            modifier = Modifier
                .background(gradientBrush)
                .padding(12.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 顶部：编号和收藏图标
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 编号 Badge
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = pet.pindex,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = BadgeDefaultFontStyle
                        )
                    }

                    if (pet.shiny == 1)
                        AsyncImage(
                            model = shinyIconPath("1"),
                            contentDescription = pet.name,
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .aspectRatio(1.1f),
                            contentScale = ContentScale.Fit
                        )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 定义动画状态：0f 代表正常，1f 代表异色
                val infiniteTransition = rememberInfiniteTransition(label = "ShinyTransition")
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        // 持续 2 秒，平滑往返
                        animation = tween(2000, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "AlphaAnimation"
                )

                // 精灵立绘 来回渐变
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .aspectRatio(1.1f),
                    contentAlignment = Alignment.Center
                ) {
                    // 底层：正常颜色图片
                    AsyncImage(
                        model = mapAvatarPath(pet.avatar),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )

                    // 上层：异色图片（通过 alpha 控制显示）
                    AsyncImage(
                        model = mapAvatarShinyPath(pet.avatar),
                        contentDescription = pet.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(alpha), // 关键：利用 alpha 实现渐变覆盖
                        contentScale = ContentScale.Fit
                    )
                }

                // 精灵立绘
//                AsyncImage(
//                    model = when (pet.shiny == 1) {
//                        true -> mapAvatarShinyPath(pet.avatar)
//                        else -> mapAvatarPath(pet.avatar)
//                    },
//                    contentDescription = pet.name,
//                    modifier = Modifier
//                        .fillMaxWidth(0.9f)
//                        .aspectRatio(1.1f),
//                    contentScale = ContentScale.Fit
//                )

                Spacer(modifier = Modifier.height(8.dp))

                // 名称
                Text(
                    text = pet.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = RoCoFamily
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 属性标签（居中，白色背景）
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    pet.element.forEach { el ->
                        ElementBadgeV2(element = el)
                        if (el != pet.element.last()) {
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }
                }
            }
        }
    }
}

/** 专门为卡片设计的属性标签：白色胶囊样式 */
@Composable
fun ElementBadgeV2(
    element: String,
    modifier: Modifier = Modifier,
    /** 为 true 时保证最小宽度一致，内容居中（属性克制等场景） */
    uniformWidth: Boolean = false
) {
    val color = getElementColor(element)
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 2.dp, vertical = 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (uniformWidth) Arrangement.Center else Arrangement.Start
    ) {
        AsyncImage(
            model = mapElementIconPath(element),
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = element,
            color = color,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            style = BadgeDefaultFontStyle
        )
        Spacer(modifier = Modifier.width(3.dp))
    }
}
