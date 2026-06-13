package uz.tayanch.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = TayanchGreen,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = TayanchMint,
    onPrimaryContainer = TayanchGreenDark,
    secondary = TayanchGreen,
    tertiary = TayanchAmber,
    error = TayanchRed,
    background = TayanchSurface,
    surface = androidx.compose.ui.graphics.Color.White,
)

private val DarkColors = darkColorScheme(
    primary = TayanchMint,
    onPrimary = TayanchGreenDark,
    primaryContainer = TayanchGreenDark,
    onPrimaryContainer = TayanchMint,
    secondary = TayanchMint,
    tertiary = TayanchAmber,
    error = TayanchRed,
    background = TayanchSurfaceDark,
)

@Composable
fun TayanchTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = TayanchTypography,
        content = content,
    )
}
