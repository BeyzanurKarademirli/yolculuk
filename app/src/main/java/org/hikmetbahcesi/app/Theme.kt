package org.hikmetbahcesi.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Acik = lightColorScheme(
    primary = Color(0xFF1F4D45),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFCFE5DE),
    onPrimaryContainer = Color(0xFF0B2420),
    secondary = Color(0xFF8A6A1F),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF3E3BC),
    onSecondaryContainer = Color(0xFF2B1F00),
    background = Color(0xFFFBF8F1),
    onBackground = Color(0xFF1C1B18),
    surface = Color(0xFFFBF8F1),
    onSurface = Color(0xFF1C1B18),
    surfaceVariant = Color(0xFFEDE7DA),
    onSurfaceVariant = Color(0xFF4B463C),
    surfaceContainer = Color(0xFFF3EEE3),
    surfaceContainerLow = Color(0xFFF7F3EA),
    surfaceContainerHigh = Color(0xFFEDE7DA),
    outline = Color(0xFF7C766A),
    outlineVariant = Color(0xFFD5CEBF),
)

private val Koyu = darkColorScheme(
    primary = Color(0xFF9FD0C3),
    onPrimary = Color(0xFF003731),
    primaryContainer = Color(0xFF1F4D45),
    onPrimaryContainer = Color(0xFFCFE5DE),
    secondary = Color(0xFFE8C878),
    onSecondary = Color(0xFF3F2E00),
    secondaryContainer = Color(0xFF5A4511),
    onSecondaryContainer = Color(0xFFF3E3BC),
    background = Color(0xFF12191A),
    onBackground = Color(0xFFE4E2DC),
    surface = Color(0xFF12191A),
    onSurface = Color(0xFFE4E2DC),
    surfaceVariant = Color(0xFF2A3331),
    onSurfaceVariant = Color(0xFFC0C8C4),
    surfaceContainer = Color(0xFF1A2223),
    surfaceContainerLow = Color(0xFF161E1F),
    surfaceContainerHigh = Color(0xFF232C2C),
    outline = Color(0xFF8A928F),
    outlineVariant = Color(0xFF3F4846),
)

private fun yazilar(o: Float): Typography {
    val serif = FontFamily.Serif
    val t = Typography()
    fun TextStyle.olc() = copy(fontSize = fontSize * o, lineHeight = lineHeight * o)
    return Typography(
        displaySmall = t.displaySmall.copy(fontFamily = serif).olc(),
        headlineMedium = t.headlineMedium.copy(fontFamily = serif, fontWeight = FontWeight.SemiBold).olc(),
        headlineSmall = t.headlineSmall.copy(fontFamily = serif, fontWeight = FontWeight.SemiBold).olc(),
        titleLarge = t.titleLarge.copy(fontFamily = serif, fontWeight = FontWeight.SemiBold).olc(),
        titleMedium = t.titleMedium.copy(fontWeight = FontWeight.SemiBold).olc(),
        titleSmall = t.titleSmall.olc(),
        bodyLarge = t.bodyLarge.copy(fontSize = 17.sp, lineHeight = 27.sp).olc(),
        bodyMedium = t.bodyMedium.copy(fontSize = 15.sp, lineHeight = 23.sp).olc(),
        bodySmall = t.bodySmall.olc(),
        labelLarge = t.labelLarge.olc(),
        labelMedium = t.labelMedium.olc(),
        labelSmall = t.labelSmall.olc(),
    )
}

@Composable
fun HikmetTema(olcek: Float, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) Koyu else Acik,
        typography = yazilar(olcek),
        content = content,
    )
}
