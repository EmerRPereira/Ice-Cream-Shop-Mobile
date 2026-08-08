package com.iceshop.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Cores personalizadas da sorveteria
val IceCreamPrimary = Color(0xFF6C3E2B)      // Marrom (chocolate)
val IceCreamSecondary = Color(0xFFF5A623)      // Amarelo (baunilha)
val IceCreamPink = Color(0xFFFF6B8A)           // Rosa (morango)
val IceCreamMint = Color(0xFF4CAF50)           // Verde (menta)
val IceCreamBackground = Color(0xFFFFF8F0)     // Bege claro

@Composable
fun IceCreamShopTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = IceCreamPrimary,
            secondary = IceCreamSecondary,
            tertiary = IceCreamPink,
            background = Color(0xFF1A1A1A),
            surface = Color(0xFF2D2D2D)
        )
    } else {
        lightColorScheme(
            primary = IceCreamPrimary,
            secondary = IceCreamSecondary,
            tertiary = IceCreamPink,
            background = IceCreamBackground,
            surface = Color.White
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
