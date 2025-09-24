package com.example.contactsapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val ClaroFuchsia = Color(0xFFE30613)
val ClaroLightBlue = Color(0xFFBBDFF6)
val ClaroLavender = Color(0xFFE6E6FA)
val ClaroLightGray = Color(0xFFE5E5E5)
val ClaroWhite = Color(0xFFFFFFFF)
val ClaroDarkGray = Color(0xFF424242)
val ClaroBlack = Color(0xFF212121)

private val ClaroLightColorScheme = lightColorScheme(
    primary = ClaroFuchsia,
    onPrimary = ClaroWhite,
    primaryContainer = ClaroLightBlue,
    onPrimaryContainer = ClaroDarkGray,

    secondary = ClaroLightBlue,
    onSecondary = ClaroDarkGray,
    secondaryContainer = ClaroLavender,
    onSecondaryContainer = ClaroDarkGray,

    tertiary = ClaroLavender,
    onTertiary = ClaroDarkGray,
    tertiaryContainer = ClaroLightGray,
    onTertiaryContainer = ClaroDarkGray,

    background = ClaroWhite,
    onBackground = ClaroBlack,
    surface = ClaroWhite,
    onSurface = ClaroBlack,
    surfaceVariant = ClaroLightGray,
    onSurfaceVariant = ClaroDarkGray,
    surfaceContainer = ClaroLavender.copy(alpha = 0.5f),
    surfaceContainerHigh = ClaroLightGray,

    outline = ClaroLightGray,
    outlineVariant = ClaroLightGray.copy(alpha = 0.6f),

    error = Color(0xFFDC3545),
    onError = ClaroWhite,
    errorContainer = Color(0xFFF8D7DA),
    onErrorContainer = Color(0xFF721C24),

    inverseSurface = ClaroBlack,
    inverseOnSurface = ClaroWhite,
    inversePrimary = ClaroLightBlue
)

private val ClaroDarkColorScheme = darkColorScheme(
    onPrimary = ClaroWhite,
    primaryContainer = ClaroDarkGray.copy(alpha = 0.3f),
    onPrimaryContainer = ClaroWhite,

    secondary = ClaroLightBlue,
    onSecondary = ClaroBlack,
    secondaryContainer = ClaroLightBlue.copy(alpha = 0.3f),
    onSecondaryContainer = ClaroWhite,

    tertiary = ClaroLavender,
    onTertiary = ClaroBlack,
    tertiaryContainer = ClaroLavender.copy(alpha = 0.3f),
    onTertiaryContainer = ClaroWhite,

    background = ClaroBlack,
    onBackground = ClaroWhite,
    surface = Color(0xFF2C2C2C),
    onSurface = ClaroWhite,
    surfaceVariant = ClaroDarkGray,
    onSurfaceVariant = ClaroLightGray,
    surfaceContainer = ClaroDarkGray.copy(alpha = 0.8f),
    surfaceContainerHigh = ClaroDarkGray,

    outline = ClaroDarkGray,
    outlineVariant = ClaroDarkGray.copy(alpha = 0.6f),

    onError = ClaroFuchsia,

    inverseSurface = ClaroWhite,
    inverseOnSurface = ClaroBlack,
    inversePrimary = ClaroFuchsia
)

@Composable
fun ContactsAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> ClaroDarkColorScheme
        else -> ClaroLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}