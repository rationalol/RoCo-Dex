package com.yinpei.rocodex.ui.tools

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.NavigateNext
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material.icons.outlined.CompareArrows
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.SyncAlt
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material.icons.outlined.ViewList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen(
    onWeaknessClick: () -> Unit,
    onLineupClick: () -> Unit,
    onPlaceholderClick: (String) -> Unit,
    onMapClick: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = { Text("功能") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            // 设置菜单项之间的垂直间距
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            MenuItem(
                icon = Icons.Outlined.Person,
                title = "性格特点"
            ){
                onPlaceholderClick("性格特点")
            }
            // 注意：Compose 官方图标库可能没有完全一模一样的特殊图标，
            // 这里使用 VideoLibrary 近似代替“投稿管理”，实际开发中你可以传入自定义的 drawable 资源
            MenuItem(
                icon = Icons.Outlined.ClearAll,
                title = "阵容搭配"
            ) {
                onLineupClick()
            }
            MenuItem(
                icon = Icons.Outlined.SyncAlt,
                title = "属性克制"
            ){
                onWeaknessClick()
            }
            MenuItem(
                icon = Icons.Outlined.Map,
                title = "地图"
            ) {
                onMapClick()
            }
        }
    }
}



/**
 * 封装的单个菜单项组件
 */
@Composable
fun MenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick) // 添加点击涟漪效果和事件
            .padding(vertical = 8.dp), // 增加一点上下内边距让点击区域更大
        verticalAlignment = Alignment.CenterVertically // 内部元素垂直居中对齐
    ) {
        // 1. 左侧图标
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFF555555), // 偏深灰色的图标，与原图相近
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp)) // 图标和文字之间的间距

        // 2. 中间文字
        Text(
            text = title,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f) // 关键点：占据剩余的所有空间，从而将右侧的箭头推到最右边
        )

        // 3. 右侧箭头图标
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "进入",
            tint = Color(0xFF999999), // 浅灰色的箭头
            modifier = Modifier.size(24.dp)
        )
    }
}
