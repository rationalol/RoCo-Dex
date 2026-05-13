package com.yinpei.rocodex.ui.navigation

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.yinpei.rocodex.ui.common.PlaceholderScreen
import com.yinpei.rocodex.ui.detail.DetailScreen
import com.yinpei.rocodex.ui.gallery.GalleryScreen
import com.yinpei.rocodex.ui.skills.SkillDetailScreen
import com.yinpei.rocodex.ui.skills.SkillsScreen
import com.yinpei.rocodex.ui.tools.ToolsScreen
import com.yinpei.rocodex.ui.weakness.WeaknessScreen

object Routes {
    const val GALLERY = "gallery"
    const val TOOLS = "tools"
    const val SKILLS = "skills"
    const val SKILL_DETAIL = "skill_detail/{skillIndex}"
    const val DETAIL = "detail/{petId}"
    const val WEAKNESS = "weakness"
    const val PLACEHOLDER = "placeholder/{title}"

    fun detail(petId: Int) = "detail/$petId"

    fun placeholder(title: String) = "placeholder/${Uri.encode(title)}"

    fun skillDetail(skillIndex: Int) = "skill_detail/$skillIndex"
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.GALLERY,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it }, // 从右侧进入
                animationSpec = tween(400)
            ) + fadeIn(animationSpec = tween(400))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it }, // 向左侧退出
                animationSpec = tween(400)
            ) + fadeOut(animationSpec = tween(400))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it }, // 返回时从左侧进入
                animationSpec = tween(400)
            ) + fadeIn(animationSpec = tween(400))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it }, // 返回时向右侧退出
                animationSpec = tween(400)
            ) + fadeOut(animationSpec = tween(400))
        }
    ) {

        composable(Routes.GALLERY) {
            GalleryScreen(
                onPetClick = { petId ->
                    navController.navigate(Routes.detail(petId))
                }
            )
        }

        composable(Routes.TOOLS) {
            ToolsScreen(
                onWeaknessClick = {
                    navController.navigate(Routes.WEAKNESS)
                },
                onPlaceholderClick = { title ->
                    navController.navigate(Routes.placeholder(title))
                }
            )
        }

        composable(Routes.SKILLS) {
            SkillsScreen(
                onSkillClick = { skillIndex ->
                    Log.d("技能页技能点击后",skillIndex.toString())
                    navController.navigate(Routes.skillDetail(skillIndex))
                }
            )
        }

        composable(
            route = Routes.SKILL_DETAIL,
            arguments = listOf(
                navArgument("skillIndex") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val skillIndex = backStackEntry.arguments?.getInt("skillIndex") ?: 0
            SkillDetailScreen(
                skillIndex = skillIndex,
                onBack = { navController.popBackStack() },
                onPetClick = { petId ->
                    navController.navigate(Routes.detail(petId))
                }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(
                navArgument("petId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getInt("petId") ?: 1

            DetailScreen(
                petId = petId,
                onBack = { navController.popBackStack() },
                onPetClick = { newPetId ->
                    navController.navigate(Routes.detail(newPetId))
                },
                onSkillClick = { skillIndex ->
                    Log.d("详细页技能点击后",skillIndex.toString())
                    navController.navigate(Routes.skillDetail(skillIndex))
                }
            )

        }

        composable(Routes.WEAKNESS) {
            WeaknessScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.PLACEHOLDER,
            arguments = listOf(
                navArgument("title") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title").orEmpty()
            PlaceholderScreen(
                title = title,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
