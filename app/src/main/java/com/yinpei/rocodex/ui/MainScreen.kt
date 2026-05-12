package com.yinpei.rocodex.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yinpei.rocodex.ui.navigation.NavGraph
import com.yinpei.rocodex.ui.navigation.Routes
import com.yinpei.rocodex.ui.theme.RoCoFamily

sealed class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Gallery : TopLevelDestination(Routes.GALLERY, "图鉴", Icons.Default.Pets)
    object Tools : TopLevelDestination(Routes.TOOLS, "功能", Icons.Default.Build)
    object Skills : TopLevelDestination(Routes.SKILLS, "招式", Icons.AutoMirrored.Filled.List)
}

val destinations = listOf(
    TopLevelDestination.Gallery,
    TopLevelDestination.Tools,
    TopLevelDestination.Skills
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    // 只有在顶级目的地时才显示导航栏
    val showNavigation = destinations.any { it.route == currentRoute }
    // 配置导航栏的颜色样式
    // 1. 配置容器颜色 (背景)
    val mySuiteColors = NavigationSuiteDefaults.colors(
        navigationBarContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        navigationRailContainerColor = MaterialTheme.colorScheme.surfaceContainer
    )

    // 2. 配置条目颜色 (图标和文字的状态颜色)
    val myItemColors = NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            indicatorColor = MaterialTheme.colorScheme.secondaryContainer // 选中时的胶囊状指示器颜色
        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary
        )
    )

    if (showNavigation) {
        NavigationSuiteScaffold(
            containerColor = MaterialTheme.colorScheme.background, // 整个页面的底色
            navigationSuiteColors = mySuiteColors,
            navigationSuiteItems = {
                // 只有在 showNavigation 为 true 时才添加 item
                destinations.forEach { destination ->
                    item(
                        selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label
                            )
                        },
                        label = { Text(destination.label, fontFamily = RoCoFamily) },
                        colors = myItemColors
                    )
                }
            }
        ) {
            // 这里是界面的主体内容
            NavGraph(navController = navController)
        }
    } else {
        NavGraph(navController = navController)
    }
}
