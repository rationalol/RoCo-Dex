package com.yinpei.rocodex.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yinpei.rocodex.data.energyConsumptionIconPath
import com.yinpei.rocodex.data.getElementColor
import com.yinpei.rocodex.data.mapElementIconPath
import com.yinpei.rocodex.data.mapSkillIconPath
import com.yinpei.rocodex.data.mapSkillTypeIconPath
import com.yinpei.rocodex.data.model.Skill
import com.yinpei.rocodex.ui.theme.RoCoFamily

/** 卡片内描述最大字符数（按 Char 计数），超出则加省略号 */
private const val SKILL_CARD_DESC_MAX_CHARS = 38

/** 统一卡片高度（与详情页、招式列表一致） */
private val SkillCardFixedHeight = 130.dp

private fun String.truncateWithEllipsis(maxChars: Int): String {
    if (length <= maxChars) return this
    return take(maxChars).trimEnd() + "\u2026"
}

private val SkillFontSize = 8.sp
private val SkillTitleFontSize = 10.sp

val BadgeDefaultFontStyle = TextStyle(
    platformStyle = PlatformTextStyle(
        includeFontPadding = false // 核心代码：移除强制的字体上下间距
    ),
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.Both
    )
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SkillCard(
    skill: Skill,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val primaryColor = getElementColor(skill.element)
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            primaryColor.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.background
        )
    )
    val shape = RoundedCornerShape(12.dp)

    // 1. 移除 descPreview 的手动截断逻辑，交给 Text 组件处理更强壮
    // 手动截断无法感知系统字体大小，交给 Text 的 maxLines 适配性更好

    Surface(
        modifier = modifier
            .fillMaxWidth()
            // 2. 核心改动：使用 heightIn 而不是 fixed height
            // 保证普通状态下有高度，大字体状态下能撑开
            .heightIn(min = SkillCardFixedHeight)
            .clip(shape)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                        Color.Transparent
                    )
                ),
                shape = shape
            )
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(gradientBrush)
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                // 3. 移除 fillMaxHeight，让内容自适应
            ) {
                // 第一行：图标 + 标题 + 类型
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // 使用间距管理
                ) {
                    AsyncImage(
                        model = mapSkillIconPath(skill.element, skill.name),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )

                    Text(
                        text = skill.name,
                        fontSize = SkillTitleFontSize,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                        fontFamily = RoCoFamily
                    )

                    // 类型标识
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = skill.type,
                            fontSize = SkillFontSize,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            style = BadgeDefaultFontStyle,
                            maxLines = 1 // 强制单行
                        )
                        AsyncImage(
                            model = mapSkillTypeIconPath(skill.type),
                            contentDescription = null,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 第二行：状态标签（Badge）
                // 4. 使用 FlowRow (如果 Compose 版本支持) 或简单 Row
                // 如果标签很多，建议用 FlowRow 防止在大 DPI 下挤出屏幕
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SkillElementBadge(element = skill.element, power = skill.power)
                    StarConsumeBadge(element = skill.element, cost = skill.cost)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 第三行：描述文字
                Text(
                    text = skill.desc, // 直接传全文
                    fontSize = SkillFontSize,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 2, // 靠这个自动截断
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp, // 稍微加大行高，增强可读性
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SkillElementBadge(
    element: String,
    power: Int,
    modifier: Modifier = Modifier,
    uniformWidth: Boolean = false
) {
    val color = getElementColor(element)

    Row(
        modifier = modifier
            // 1. 适应性宽度：使用 minWidth 而非 fixed width
            .defaultMinSize(minWidth = if (uniformWidth) 72.dp else 0.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            // 2. 适当增加垂直 padding，为字体放大留出呼吸空间
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        // 3. 使用 spacedBy 代替固定的 Spacer，让间距在布局挤压时更智能
        horizontalArrangement = if (uniformWidth) {
            Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
        } else {
            Arrangement.spacedBy(6.dp, Alignment.Start)
        }
    ) {
        // 4. 图标自适应：如果文字很大，图标太小会很难看
        // 如果你希望图标随字号缩放，可以考虑使用 1.4.em (相对于字号的比例)
        AsyncImage(
            model = mapElementIconPath(element),
            contentDescription = null,
            modifier = Modifier.size(14.dp)
        )

        Text(
            text = element,
            color = color,
            fontSize = SkillFontSize,
            fontWeight = FontWeight.Bold,
            // 5. 关键控制：禁止换行，防止 Badge 高度被撑开导致 UI 崩坏
            maxLines = 1,
            overflow = TextOverflow.Visible,
            softWrap = false,
            style = BadgeDefaultFontStyle,
        )

        if (power > 0) {
            // 6. 次要信息处理：稍微减弱颜色或字重，突出核心元素名
            Text(
                text = "$power",
                fontSize = SkillFontSize,
                color = color.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                style = BadgeDefaultFontStyle,
            )
        }
    }
}

@Composable
fun StarConsumeBadge(
    element: String,
    cost: Int,
    modifier: Modifier = Modifier,
    uniformWidth: Boolean = false
) {
    val color = getElementColor(element)

    Row(
        modifier = modifier
            // 1. 使用 defaultMinSize 而不是 fixed width，给组件留呼吸空间
            .defaultMinSize(minWidth = if (uniformWidth) 60.dp else 0.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            // 2. 纵向 padding 建议稍微多一点，防止大字体顶满边框
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (uniformWidth) Arrangement.Center else Arrangement.Start
    ) {
        AsyncImage(
            model = energyConsumptionIconPath("kid_star"),
            contentDescription = null,
            modifier = Modifier.size(14.dp)
        )

        Text(
            text = "能耗",
            color = color,
            fontSize = SkillFontSize,
            fontWeight = FontWeight.Bold,
            // 3. 限制单行，防止系统字体过大时强制换行撑破 Badge
            maxLines = 1,
            // 4. 关键：如果空间实在不够，允许缩小字体以放下文字
            softWrap = false,
            style = BadgeDefaultFontStyle,
        )

        // 5. 动态间距：用 weight(1f) 或限制最大宽度的 Spacer
        // 这种小组件，Spacer 建议改为相对于字号的比例，或者直接给很小的 gap
        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "$cost",
            fontSize = SkillFontSize,
            color = color,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            style = BadgeDefaultFontStyle,
        )

        // 移除末尾多余的 Spacer，靠 Row 的 padding 来控制右侧间距
    }
}