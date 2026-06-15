package uz.tayanch.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Tayanch design system — a COMPLETE Material 3 color scheme built around the
 * brand green. Every role (including the neutral surfaces) is defined here on a
 * green-tinted ramp. This is deliberate: if neutral roles like [surfaceVariant],
 * the `surfaceContainer*` tiers or `secondaryContainer` are left undefined,
 * Material falls back to its baseline **purple** palette — which is what used to
 * bleed through cards, chips, dialogs and badges across the app.
 */
private val LightColors = lightColorScheme(
    primary = TayanchGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA6EFC4),
    onPrimaryContainer = Color(0xFF00382A),
    inversePrimary = Color(0xFF8AD4AC),

    secondary = Color(0xFF4D6358),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCFE9D9),
    onSecondaryContainer = Color(0xFF0A3A2C),

    tertiary = Color(0xFFB7791F),          // amber accent (warnings/secondary highlights)
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFDE6C4),
    onTertiaryContainer = Color(0xFF4A3000),

    error = TayanchRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = TayanchSurface,
    onBackground = Color(0xFF181D1A),
    surface = Color(0xFFFCFEFB),
    onSurface = Color(0xFF181D1A),
    surfaceVariant = Color(0xFFDCE5DD),
    onSurfaceVariant = Color(0xFF404A43),
    surfaceTint = TayanchGreen,

    surfaceDim = Color(0xFFD9DDD8),
    surfaceBright = Color(0xFFFCFEFB),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFFAFCF9),
    surfaceContainer = Color(0xFFF4F7F3),
    surfaceContainerHigh = Color(0xFFEEF2ED),
    surfaceContainerHighest = Color(0xFFE8ECE7),

    outline = Color(0xFF707972),
    outlineVariant = Color(0xFFC2CBC2),
    scrim = Color.Black,
    inverseSurface = Color(0xFF2D322E),
    inverseOnSurface = Color(0xFFEFF1EC),
)

private val DarkColors = darkColorScheme(
    primary = TayanchMint,
    onPrimary = Color(0xFF00382A),
    primaryContainer = TayanchGreenDark,
    onPrimaryContainer = Color(0xFFA6EFC4),
    inversePrimary = TayanchGreen,

    secondary = Color(0xFFB3CCBE),
    onSecondary = Color(0xFF1F352B),
    secondaryContainer = Color(0xFF354B40),
    onSecondaryContainer = Color(0xFFCFE9D9),

    tertiary = Color(0xFFEAC08C),
    onTertiary = Color(0xFF432F08),
    tertiaryContainer = Color(0xFF5C431B),
    onTertiaryContainer = Color(0xFFFDE6C4),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = Color(0xFF0F140F),
    onBackground = Color(0xFFDFE4DE),
    surface = Color(0xFF0F140F),
    onSurface = Color(0xFFDFE4DE),
    surfaceVariant = Color(0xFF404A43),
    onSurfaceVariant = Color(0xFFC0C9C0),
    surfaceTint = TayanchMint,

    surfaceDim = Color(0xFF0F140F),
    surfaceBright = Color(0xFF353A35),
    surfaceContainerLowest = Color(0xFF0A0F0A),
    surfaceContainerLow = Color(0xFF181D18),
    surfaceContainer = Color(0xFF1C211C),
    surfaceContainerHigh = Color(0xFF262B26),
    surfaceContainerHighest = Color(0xFF313630),

    outline = Color(0xFF8A938B),
    outlineVariant = Color(0xFF404A43),
    scrim = Color.Black,
    inverseSurface = Color(0xFFDFE4DE),
    inverseOnSurface = Color(0xFF2D322E),
)

/** Shared corner-radius scale — rounded, friendly, consistent across the app. */
val TayanchShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

@Composable
fun TayanchTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = TayanchTypography,
        shapes = TayanchShapes,
        content = content,
    )
}
