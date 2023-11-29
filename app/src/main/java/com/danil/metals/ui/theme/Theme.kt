package com.danil.metals.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xffc4bdab),
    background = Color(0xff363636),
    onBackground = Color(0xffffffff),
    secondary = Color(0xff282828),
    onSecondary = Color(0xffffffff),
    tertiary = Color(0xffc4bdac),
    onTertiary = Color(0xff000000),
    surface = Color(0x58665E63),
    onSurface = Color(0xFFFFFFFF)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xff282828),
    background = Color(0xfff8f4f3),
    onBackground = Color(0xff000000),
    secondary = Color(0xffffffff),
    onSecondary = Color(0xff000000),
    tertiary = Color(0xffe9d6d6),
    onTertiary = Color(0xff000000),
    surface = Color(0x597D7275),
    onSurface = Color(0xFFFFFFFF)
)

@Composable
fun MetalsTheme(
    darkTheme: Boolean,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
        shapes = Shape
    )
}