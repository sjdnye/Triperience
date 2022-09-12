package com.example.triperience.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    onSurface = White,
    surface = Black,
    onBackground = White,
    background = Black,
    onSecondary = LightBlue50,
    secondary = LightBlue800,
    onPrimary = LightBlue50,
    primary = LightBlue700,

    )

private val LightColorPalette = lightColors(
    onSurface = Black,
    surface = White,
    onBackground = Black,
    background = White,
    onSecondary = LightBlue50,
    secondary = LightBlue800,
    onPrimary = LightBlue50,
    primary = LightBlue700,
)

@Composable
fun TriperienceTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

//    val systemUiController = rememberSystemUiController()
//    SideEffect {
//        systemUiController.setStatusBarColor(
//            color =  if (darkTheme) Color.Black else Color.White
//        )
//    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}