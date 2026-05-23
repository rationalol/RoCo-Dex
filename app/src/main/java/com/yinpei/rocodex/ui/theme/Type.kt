package com.yinpei.rocodex.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.yinpei.rocodex.R


// 1. 先定义字体族
val RoCoFamily = FontFamily(
    Font(R.font.roco1, FontWeight.Normal)
)

val RoCoFamily2 = FontFamily(
    Font(R.font.roco2, FontWeight.Normal)
)


// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = RoCoFamily2,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        // 1. 关闭系统底层的字体默认内边距
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
        // 2. 修剪行高带来的多余空白
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both // 核心：同时修剪顶部和底部的行高空白
        )
    ),
    titleLarge = TextStyle(
        fontFamily = RoCoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        // 1. 关闭系统底层的字体默认内边距
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
        // 2. 修剪行高带来的多余空白
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both // 核心：同时修剪顶部和底部的行高空白
        )
    ),
    /* Other default text styles to override

    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

