package com.homework.todolist.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Figma's text styles
 */
val Typography = Typography(
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.5.sp
    ),

    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 40.sp,
        letterSpacing = 0.sp
    ),

    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.16.sp
    ),

    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),

    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    )
)

/**
 * Application typographies
 * @param title
 */
@Immutable
internal data class TodoTypography(
    val title: TextStyle = TextStyle.Default,
    val largeTitle: TextStyle = TextStyle.Default,
    val button: TextStyle = TextStyle.Default,
    val body: TextStyle = TextStyle.Default,
    val subhead: TextStyle = TextStyle.Default
) {
    companion object {
        /**
         * Default todo typography
         */
        internal val Default: TodoTypography
            get() = TodoTypography(
                title = TextStyle(
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp,
                    lineHeight = 32.sp,
                    letterSpacing = 0.5.sp
                ),
                largeTitle = TextStyle(
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Medium,
                    fontSize = 40.sp,
                    letterSpacing = 0.sp
                ),
                button = TextStyle(
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = 24.sp,
                    letterSpacing = 0.16.sp
                ),
                body = TextStyle(
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    letterSpacing = 0.sp
                ),
                subhead = TextStyle(
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    letterSpacing = 0.sp
                )
            )
    }
}

/**
 * Default todo typography values composition
 */
internal val TodoAppTypography =
    staticCompositionLocalOf { TodoTypography.Default }