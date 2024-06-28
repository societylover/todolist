package com.homework.todolist.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
private fun ColorPalettePreview(
    palette: TodoListColorsPalette,
    paletteTitle: String,
    paletteName: String,
    isDark: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = paletteTitle,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(8.dp)
        )
        ColorRow(
            listOf(
                ColorName(palette.separatorColor, "Support [$paletteName] / Separator"),
                ColorName(palette.overlayColor, "Support [$paletteName] / Overlay", if (isDark) Color.White else Color.Black),
            )
        )
        ColorRow(
            listOf(
                ColorName(palette.labelPrimaryColor, "Label [$paletteName] / Primary", if (isDark) Color.Black else Color.White),
                ColorName(palette.labelSecondaryColor, "Label [$paletteName] / Secondary", if (isDark) Color.Black else Color.White),
                ColorName(palette.labelTertiaryColor, "Label [$paletteName] / Tertiary", if (isDark) Color.Black else Color.White),
                ColorName(palette.labelDisableColor, "Label [$paletteName] / Disable")
            )
        )
        ColorRow(
            listOf(
                ColorName(palette.redColor, "Color [$paletteName] / Red", Color.White),
                ColorName(palette.greenColor, "Color [$paletteName] / Green", Color.White),
                ColorName(palette.blueColor, "Color [$paletteName] / Blue", Color.White),
                ColorName(palette.grayColor, "Color [$paletteName] / Gray", if (isDark) Color.White else Color.Black),
                ColorName(palette.grayLightColor, "Color [$paletteName] / Gray Light", if (isDark) Color.White else Color.Black),
                ColorName(palette.whiteColor, "Color [$paletteName] / White"),
                ColorName(palette.yellowColor, "Color [$paletteName] / Yellow")
            )
        )

        ColorRow(
            listOf(
                ColorName(palette.backPrimaryColor, "Back [$paletteName] / Primary", if (isDark) Color.White else Color.Black),
                ColorName(palette.backSecondaryColor, "Back [$paletteName] / Secondary", if (isDark) Color.White else Color.Black),
                ColorName(palette.backElevatedColor, "Back [$paletteName] / Elevated", if (isDark) Color.White else Color.Black)
            )
        )
    }
}

private data class ColorName(
    val color: Color,
    val name: String,
    val textColor: Color = Color.Black
)

@Composable
private fun ColorRow(list: List<ColorName>) {
    Row(
        modifier = Modifier
            .height(100.dp)
            .padding(bottom = 20.dp)
    ) {
        list.forEach {
            Box(
                modifier = Modifier
                    .background(it.color)
                    .width(240.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(text = it.name,
                    modifier = Modifier.padding(start = 12.dp, bottom = 10.dp),
                    fontSize = 10.sp,
                    color = it.textColor)
            }
        }
    }
}

@Composable
private fun ColorRow(colorName: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(4.dp)
    ) {
        Text(text = colorName, modifier = Modifier
            .weight(1f)
            .padding(8.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .background(color)
                .fillMaxHeight()
        )
    }
}

@Preview(showBackground = true, widthDp = 240 * 6)
@Composable
private fun LightColorsPreview() {
    ColorPalettePreview(
        palette = TodoListLightColors(),
        paletteTitle = "Light Palette Colors",
        paletteName = "Light"
    )
}

@Preview(showBackground = false, widthDp = 240 * 6)
@Composable
private fun DarkColorsPreview() {
    ColorPalettePreview(
        palette = TodoListDarkColors(),
        paletteTitle = "Dark Palette Colors",
        paletteName = "Dark",
        isDark = true
    )
}

@Composable
private fun SchemePalettePreview(scheme: ColorScheme, schemeTitle: String, schemeName: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = schemeTitle,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(8.dp)
        )
        ColorRow(colorName = "Primary [$schemeName] ", color = scheme.primary)
        ColorRow(colorName = "Secondary [$schemeName]", color = scheme.secondary)
        ColorRow(colorName = "Tertiary [$schemeName]", color = scheme.tertiary)
        ColorRow(colorName = "Background [$schemeName]", color = scheme.background)
        ColorRow(colorName = "Surface [$schemeName]", color = scheme.surface)
    }
}

@Preview(showBackground = true)
@Composable
private fun LightSchemePreview() {
    SchemePalettePreview(
        scheme = LightColorScheme,
        schemeTitle = "Light Colors Scheme",
        schemeName = "Light"
    )
}

@Preview(showBackground = false)
@Composable
private fun DarkSchemePreview() {
    SchemePalettePreview(
        scheme = DarkColorScheme,
        schemeTitle = "Dark Colors Scheme",
        schemeName = "Dark"
    )
}

@Composable
private fun TodoTypographyPreview(todoTypography: TodoTypography) {
    Column(modifier = Modifier.padding(16.dp)) {
        TextSample(text = "Large title — 32/38", style = todoTypography.largeTitle)
        TextSample(text = "Title — 20/32", style = todoTypography.title)
        TextSample(text = "BUTTON — 14/24", style = todoTypography.button)
        TextSample(text = "Body — 16/20", style = todoTypography.body)
        TextSample(text = "Subhead — 14/20", style = todoTypography.subhead)
    }
}

@Composable
private fun TextSample(text: String, style: TextStyle) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = text, style = style)
    }
}

@Preview(showBackground = true)
@Composable
private fun TodoTypographyPreview() {
    TodoTypographyPreview(todoTypography = TodoTypography.Default)
}

