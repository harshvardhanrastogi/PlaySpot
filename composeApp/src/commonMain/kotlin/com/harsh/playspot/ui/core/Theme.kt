package com.harsh.playspot.ui.core

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import playspot.composeapp.generated.resources.Exo2_Black
import playspot.composeapp.generated.resources.Exo2_BlackItalic
import playspot.composeapp.generated.resources.Exo2_Bold
import playspot.composeapp.generated.resources.Exo2_BoldItalic
import playspot.composeapp.generated.resources.Exo2_ExtraBold
import playspot.composeapp.generated.resources.Exo2_ExtraBoldItalic
import playspot.composeapp.generated.resources.Exo2_ExtraLight
import playspot.composeapp.generated.resources.Exo2_ExtraLightItalic
import playspot.composeapp.generated.resources.Exo2_Italic
import playspot.composeapp.generated.resources.Exo2_Light
import playspot.composeapp.generated.resources.Exo2_LightItalic
import playspot.composeapp.generated.resources.Exo2_Medium
import playspot.composeapp.generated.resources.Exo2_MediumItalic
import playspot.composeapp.generated.resources.Exo2_Regular
import playspot.composeapp.generated.resources.Exo2_SemiBold
import playspot.composeapp.generated.resources.Exo2_SemiBoldItalic
import playspot.composeapp.generated.resources.Exo2_Thin
import playspot.composeapp.generated.resources.Exo2_ThinItalic
import playspot.composeapp.generated.resources.Res

private val lightColors =
    lightColorScheme(
        primary = Color(0xff2563eb),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFF6F6F8),
        onSurface = Color(0xFF2B64FF),
        onSurfaceVariant = Color(0xFFFFFFFF)
    )


private val darkColors = darkColorScheme(
    primary = Color(0xff2563eb),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF101622),
    onSurface = Color(0xFF2B64FF),
    onSurfaceVariant = Color(0xFFFFFFFF)
)

val ExoTypography
    @Composable
    get() = Typography(
        displayLarge = TextStyle(
            fontFamily = ExoFontFamily,
            fontWeight = FontWeight.Black,
            fontSize = 57.sp,
            lineHeight = 64.sp,
            letterSpacing = (-0.25).sp
        ),
        displayMedium = TextStyle(
            fontFamily = ExoFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 45.sp,
            lineHeight = 52.sp,
            letterSpacing = 0.sp
        ),
        displaySmall = TextStyle(
            fontFamily = ExoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            letterSpacing = 0.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = ExoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = ExoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = ExoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = ExoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = ExoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily = ExoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = ExoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = ExoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = ExoFontFamily,
            fontWeight = FontWeight.Light,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp
        ),
        labelLarge = TextStyle(
            fontFamily = ExoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = ExoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = ExoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        )
    )


private val ExoFontFamily
    @Composable
    get() = FontFamily(
        Font(Res.font.Exo2_Thin, FontWeight.Thin),
        Font(Res.font.Exo2_ThinItalic, FontWeight.Thin, FontStyle.Italic),
        Font(Res.font.Exo2_ExtraLight, FontWeight.ExtraLight),
        Font(Res.font.Exo2_ExtraLightItalic, FontWeight.ExtraLight, FontStyle.Italic),
        Font(Res.font.Exo2_Light, FontWeight.Light),
        Font(Res.font.Exo2_LightItalic, FontWeight.Light, FontStyle.Italic),
        Font(Res.font.Exo2_Regular, FontWeight.Normal),
        Font(Res.font.Exo2_Italic, FontWeight.Normal, FontStyle.Italic),
        Font(Res.font.Exo2_Medium, FontWeight.Medium),
        Font(Res.font.Exo2_MediumItalic, FontWeight.Medium, FontStyle.Italic),
        Font(Res.font.Exo2_SemiBold, FontWeight.SemiBold),
        Font(Res.font.Exo2_SemiBoldItalic, FontWeight.SemiBold, FontStyle.Italic),
        Font(Res.font.Exo2_Bold, FontWeight.Bold),
        Font(Res.font.Exo2_BoldItalic, FontWeight.Bold, FontStyle.Italic),
        Font(Res.font.Exo2_ExtraBold, FontWeight.ExtraBold),
        Font(Res.font.Exo2_ExtraBoldItalic, FontWeight.ExtraBold, FontStyle.Italic),
        Font(Res.font.Exo2_Black, FontWeight.Black),
        Font(Res.font.Exo2_BlackItalic, FontWeight.Black, FontStyle.Italic)
    )

@Composable
fun AppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) {
        darkColors
    } else {
        lightColors
    }
    MaterialTheme(colorScheme = colorScheme, typography = ExoTypography, content = content)
}



