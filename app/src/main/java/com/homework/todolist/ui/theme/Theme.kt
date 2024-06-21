package com.homework.todolist.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = LabelDarkPrimaryColor,
    secondary = LabelDarkSecondaryColor,
    tertiary = LabelDarkTertiaryColor,
    background = BackDarkPrimaryColor,
    surface = BackDarkPrimaryColor
)

private val LightColorScheme = lightColorScheme(
    primary = LabelLightPrimaryColor,
    secondary = LabelLightSecondaryColor,
    tertiary = LabelLightTertiaryColor,
    background = BackLightPrimaryColor,
    surface = BackLightPrimaryColor
    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

private val TodoListLightColors = TodoListColorsPalette(
    separatorColor = LightSeparatorColor,
    overlayColor = LightOverlayColor,
    labelPrimaryColor = LabelLightPrimaryColor,
    labelSecondaryColor = LabelLightSecondaryColor,
    labelTertiaryColor = LabelLightTertiaryColor,
    labelDisableColor = LabelLightDisableColor,
    redColor = RedLightColor,
    greenColor = GreenLightColor,
    blueColor = BlueLightColor,
    grayColor = GrayLightColor,
    grayLightColor = GrayLightLightColor,
    whiteColor = WhiteLightColor,
    backPrimaryColor = BackLightPrimaryColor,
    backSecondaryColor = BackLightSecondaryColor,
    backElevatedColor = BackLightElevatedColor
)

private val TodoListDarkColors = TodoListColorsPalette(
    separatorColor = DarkSeparatorColor,
    overlayColor = DarkOverlayColor,
    labelPrimaryColor = LabelDarkPrimaryColor,
    labelSecondaryColor = LabelDarkSecondaryColor,
    labelTertiaryColor = LabelDarkTertiaryColor,
    labelDisableColor = LabelDarkDisableColor,
    redColor = RedDarkColor,
    greenColor = GreenDarkColor,
    blueColor = BlueDarkColor,
    grayColor = GrayDarkColor,
    grayLightColor = GrayLightDarkColor,
    whiteColor = WhiteDarkColor,
    backPrimaryColor = BackDarkPrimaryColor,
    backSecondaryColor = BackDarkSecondaryColor,
    backElevatedColor = BackDarkElevatedColor
)

@Composable
fun TodolistTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    val customColorsPalette = if (darkTheme) TodoListDarkColors else TodoListLightColors

    CompositionLocalProvider(LocalCustomColorsPalette provides customColorsPalette)
    {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}