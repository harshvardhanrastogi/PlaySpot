package com.harsh.playspot.ui.core

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
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
        onSurface = Color(0xFF0F172A),
        onSurfaceVariant = Color(0xFF94A3B8),
        outline = Color(0xFF94A3B8),
        outlineVariant = Color(0xFF64748B),
        background = Color(0xFFF1F1F3)
    )


private val darkColors = darkColorScheme(
    primary = Color(0xFF3B82F6), // Blue 500 (Vibrant for Dark Mode)
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF1E293B), // Deep Slate
    onSurface = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF94A3B8),
    outlineVariant = Color(0xFF92a4c9),
    background = Color(0xFF0F172A) // Slate
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
    val extendedColors = if (darkTheme) darkExtendedColors else lightExtendedColors

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = ExoTypography,
            content = content
        )
    }
}

val DisabledOnSurface = Color(0x802563EB)
 val InputTextColor = Color(0xFF0F172A)
private val InputTextColorDark = Color(0xFFF8FAFC) // Slate 50 (Dark Mode)

val TextMediumDark = Color(0xFF64748B)
val TextMediumDarkTheme = Color(0xFF92a4c9)

val TextLightGray = Color(0xFF92a4c9)

val TextLighterGray = Color(0xFF94A3B8)

val PrimaryRing = Color(0x2563EB1A)

val Purple = Color(0xFF9333EA)
val PurpleActionDark = Color(0xFFD8B4FE)   // purple-300
val PurpleContainer = Color(0xFFF3E8FF)     // purple-100
val PurpleContainerDark = Color(0xFF581C87)

val PurpleRing = Color(0xFFF3E8FF)

val Green = Color(0xFF16A34A)       // green-600
val GreenDark = Color(0xFF86EFAC)   // green-300 (for dark mode text)
val GreenRing = Color(0xFFDCFCE7)   // green-100 (for light mode bg)
val GreenRingDark = Color(0xFF14532D) // green-900 (for dark mode bg)
val Orange = Color(0xFFEA580C)       // orange-600 (Text/Icon Light)
val OrangeDark = Color(0xFFFDBA74)   // orange-300 (Text/Icon Dark)
val OrangeRing = Color(0xFFFFEDD5)   // orange-100 (Background Light)
val OrangeRingDark = Color(0xFF7C2D12) // orange-900 (Background Dark)

val WidgetBg = Color(0xFFFFFFFF)
val WidgetBgDark = Color(0xFF232f48)


data class ExtendedColors(
    val purple: Color,
    val purpleContainer: Color,
    val green: Color,
    val greenContainer: Color,
    val orange: Color,
    val orangeContainer: Color,
    val textDark: Color,
    val widgetBg: Color,
    val textMediumDark: Color,
    val outline: Color,
    val chipContainer: Color,
    val selectedChipContainer: Color,
    val chipText: Color,
    val selectedChipText: Color,
    val chipIconBg: Color
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        purple = Color.Unspecified,
        purpleContainer = Color.Unspecified,
        green = Color.Unspecified,
        greenContainer = Color.Unspecified,
        orange = Color.Unspecified,
        orangeContainer = Color.Unspecified,
        textDark = Color.Unspecified,
        widgetBg = Color.Unspecified,
        textMediumDark = Color.Unspecified,
        outline = Color.Unspecified,
        chipContainer = Color.Unspecified,
        selectedChipContainer = Color.Unspecified,
        chipText = Color.Unspecified,
        selectedChipText = Color.Unspecified,
        chipIconBg = Color.Unspecified
    )
}

private val lightExtendedColors = ExtendedColors(
    purple = Purple,
    purpleContainer = PurpleContainer,
    green = Green,
    greenContainer = GreenRing,
    orange = Orange,
    orangeContainer = OrangeRing,
    textDark = InputTextColor,
    widgetBg = WidgetBg,
    textMediumDark = TextMediumDark,
    outline = Color(0xFFE2E8F0),
    chipContainer = WidgetBg,
    selectedChipContainer = Color(0xff2563eb),
    chipText = Color(0xFF64748B),
    selectedChipText = Color.White,
    chipIconBg = Color(0xFFF1F5F9)
)

private val darkExtendedColors = ExtendedColors(
    purple = PurpleActionDark,
    purpleContainer = PurpleContainerDark,
    green = GreenDark,
    greenContainer = GreenRingDark,
    orange = OrangeDark,
    orangeContainer = OrangeRingDark,
    textDark = InputTextColorDark,
    widgetBg = WidgetBgDark,
    textMediumDark = TextMediumDarkTheme,
    outline = Color.Transparent,
    chipContainer = WidgetBgDark,
    selectedChipContainer = Color(0xFF3B82F6),
    chipText = TextLightGray,
    selectedChipText = Color.White,
    chipIconBg = Color(0x660F172A)
)

val ColorScheme.extendedColors: ExtendedColors
    @Composable
    get() = LocalExtendedColors.current







