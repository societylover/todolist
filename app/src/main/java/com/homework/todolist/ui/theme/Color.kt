package com.homework.todolist.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Light colors
val LightSeparatorColor = Color(0x33000000)
val LightOverlayColor = Color(0x0F000000)

val LabelLightPrimaryColor = Color(0xFF000000)
val LabelLightSecondaryColor = Color(0x99000000)
val LabelLightTertiaryColor = Color(0x4D000000)
val LabelLightDisableColor = Color(0x26000000)

val RedLightColor = Color(0xFFFF3B30)
val GreenLightColor = Color(0xFF34C759)
val BlueLightColor = Color(0xFF007AFF)
val GrayLightColor = Color(0xFF8E8E93)
val GrayLightLightColor = Color(0xFFD1D1D6)
val WhiteLightColor = Color(0xFFFFFFFF)
val YellowLightColor = Color(0xFFFFD600)

val BackLightPrimaryColor = Color(0xFFF7F6F2)
val BackLightSecondaryColor = Color(0xFFFFFFFF)
val BackLightElevatedColor = Color(0xFFFFFFFF)

// Dark colors
val DarkSeparatorColor = Color(0x33FFFFFF)
val DarkOverlayColor = Color(0x52000000)

val LabelDarkPrimaryColor = Color(0xFFFFFFFF)
val LabelDarkSecondaryColor = Color(0x99FFFFFF)
val LabelDarkTertiaryColor = Color(0x66FFFFFF)
val LabelDarkDisableColor = Color(0x26FFFFFF)

val RedDarkColor = Color(0xFFFF453A)
val GreenDarkColor = Color(0xFF32D74B)
val BlueDarkColor = Color(0xFF0A84FF)
val GrayDarkColor = Color(0xFF8E8E93)
val GrayLightDarkColor = Color(0xFF48484A)
val WhiteDarkColor = Color(0xFFFFFFFF)
val YellowDarkColor = Color(0xFFFFAB00)


val BackDarkPrimaryColor = Color(0xFF161618)
val BackDarkSecondaryColor = Color(0xFF252528)
val BackDarkElevatedColor = Color(0xFF3C3C3F)

// Colors container
@Immutable
data class TodoListColorsPalette(
    val separatorColor: Color = Color.Unspecified,
    val overlayColor: Color = Color.Unspecified,
    val labelPrimaryColor: Color = Color.Unspecified,
    val labelSecondaryColor: Color = Color.Unspecified,
    val labelTertiaryColor: Color = Color.Unspecified,
    val labelDisableColor: Color = Color.Unspecified,
    val redColor: Color = Color.Unspecified,
    val greenColor: Color = Color.Unspecified,
    val blueColor: Color = Color.Unspecified,
    val grayColor: Color = Color.Unspecified,
    val grayLightColor: Color = Color.Unspecified,
    val whiteColor: Color = Color.Unspecified,
    val yellowColor: Color = Color.Unspecified,
    val backPrimaryColor: Color = Color.Unspecified,
    val backSecondaryColor: Color = Color.Unspecified,
    val backElevatedColor: Color = Color.Unspecified)

/**
 * Todo list light colors implementation
 */
internal fun TodoListLightColors() = TodoListColorsPalette(
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
    yellowColor = YellowLightColor,
    backPrimaryColor = BackLightPrimaryColor,
    backSecondaryColor = BackLightSecondaryColor,
    backElevatedColor = BackLightElevatedColor)

/**
 * Todo list dark colors implementation
 */
internal fun TodoListDarkColors() = TodoListColorsPalette(
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
    yellowColor = YellowDarkColor,
    backPrimaryColor = BackDarkPrimaryColor,
    backSecondaryColor = BackDarkSecondaryColor,
    backElevatedColor = BackDarkElevatedColor)

internal val TodoColorsPalette =
    staticCompositionLocalOf { TodoListColorsPalette() }