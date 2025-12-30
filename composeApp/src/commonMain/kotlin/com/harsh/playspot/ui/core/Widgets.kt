package com.harsh.playspot.ui.core

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Park
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LargeButton(
    modifier: Modifier = Modifier,
    label: String,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    Button(
        enabled = enabled,
        modifier = modifier.height(60.dp),
        colors = ButtonDefaults.buttonColors(disabledContainerColor = Color(0x802563EB)),
        onClick = onClick
    ) {
        Text2(text = label, style = Text2StyleToken.LabelLarge)
    }
}

@Composable
fun TextField(modifier: Modifier = Modifier, style: Text2StyleToken = Text2StyleToken.BodyMedium) {
    OutlinedTextField(
        modifier = modifier,
        value = "",
        onValueChange = {},
        label = { Text2("Hint", style = style, color = Color.Gray) },
        trailingIcon = {
            Icon(imageVector = Icons.Filled.Park, contentDescription = null)
        },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Email, contentDescription = null)
        },
        textStyle = style.toTextStyle()
    )
}


@Composable
fun Text2(
    text: String,
    style: Text2StyleToken = Text2StyleToken.BodyMedium,
    color: Color = Color.Unspecified
) {
    Text(text = text, style = style.toTextStyle(), color = color)
}


@Preview
@Composable
fun LargeButtonPreview() {
    AppTheme {
        Column {
            LargeButton(modifier = Modifier.fillMaxWidth(), label = "Button")
            TextField(modifier = Modifier.fillMaxWidth())
        }
    }
}


sealed class Text2StyleToken() {
    object DisplayLarge : Text2StyleToken()
    object DisplayMedium : Text2StyleToken()
    object DisplaySmall : Text2StyleToken()
    object HeadlineLarge : Text2StyleToken()
    object HeadlineMedium : Text2StyleToken()
    object HeadlineSmall : Text2StyleToken()
    object TitleLarge : Text2StyleToken()
    object TitleMedium : Text2StyleToken()
    object TitleSmall : Text2StyleToken()
    object BodyLarge : Text2StyleToken()
    object BodyMedium : Text2StyleToken()
    object BodySmall : Text2StyleToken()
    object LabelLarge : Text2StyleToken()
    object LabelMedium : Text2StyleToken()
    object LabelSmall : Text2StyleToken()

    @Composable
    fun toTextStyle(): TextStyle {
        return when (this) {
            DisplayLarge -> MaterialTheme.typography.displayLarge
            DisplayMedium -> MaterialTheme.typography.displayMedium
            DisplaySmall -> MaterialTheme.typography.displaySmall
            HeadlineLarge -> MaterialTheme.typography.headlineLarge
            HeadlineMedium -> MaterialTheme.typography.headlineMedium
            HeadlineSmall -> MaterialTheme.typography.headlineSmall
            TitleLarge -> MaterialTheme.typography.titleLarge
            TitleMedium -> MaterialTheme.typography.titleMedium
            TitleSmall -> MaterialTheme.typography.titleSmall
            BodyLarge -> MaterialTheme.typography.bodyLarge
            BodyMedium -> MaterialTheme.typography.bodyMedium
            BodySmall -> MaterialTheme.typography.bodySmall
            LabelLarge -> MaterialTheme.typography.labelLarge
            LabelMedium -> MaterialTheme.typography.labelMedium
            LabelSmall -> MaterialTheme.typography.labelSmall
        }
    }
}

/*
*  val displayLarge: TextStyle = TypographyTokens.DisplayLarge,
    val displayMedium: TextStyle = TypographyTokens.DisplayMedium,
    val displaySmall: TextStyle = TypographyTokens.DisplaySmall,
    val headlineLarge: TextStyle = TypographyTokens.HeadlineLarge,
    val headlineMedium: TextStyle = TypographyTokens.HeadlineMedium,
    val headlineSmall: TextStyle = TypographyTokens.HeadlineSmall,
    val titleLarge: TextStyle = TypographyTokens.TitleLarge,
    val titleMedium: TextStyle = TypographyTokens.TitleMedium,
    val titleSmall: TextStyle = TypographyTokens.TitleSmall,
    val bodyLarge: TextStyle = TypographyTokens.BodyLarge,
    val bodyMedium: TextStyle = TypographyTokens.BodyMedium,
    val bodySmall: TextStyle = TypographyTokens.BodySmall,
    val labelLarge: TextStyle = TypographyTokens.LabelLarge,
    val labelMedium: TextStyle = TypographyTokens.LabelMedium,
    val labelSmall: TextStyle = TypographyTokens.LabelSmall,
* */