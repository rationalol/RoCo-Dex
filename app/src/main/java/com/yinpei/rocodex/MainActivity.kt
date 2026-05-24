package com.yinpei.rocodex

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import com.yinpei.rocodex.ui.MainScreen
import com.yinpei.rocodex.ui.theme.LocalWindowSizeClass
import com.yinpei.rocodex.ui.theme.RoCoDexTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 适配高刷新率 (120Hz等)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay?.supportedModes?.maxByOrNull { it.refreshRate }?.let { mode ->
                window.attributes = window.attributes.apply {
                    preferredDisplayModeId = mode.modeId
                }
            }
        }

        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)

            // 使用 CompositionLocalProvider 将值注入整个 App
            CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
                RoCoDexTheme(
                    dynamicColor = false
                ) {
                    MainScreen()
                }
            }
        }
    }
}
