package com.harsh.playspot.ui.core

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import playspot.composeapp.generated.resources.Res
import playspot.composeapp.generated.resources.ic_facebook_logo
import playspot.composeapp.generated.resources.ic_google_logo
import playspot.composeapp.generated.resources.signup_continue_with_facebook
import playspot.composeapp.generated.resources.signup_continue_with_google

@Composable
fun LargeButton(
    modifier: Modifier = Modifier, label: String, enabled: Boolean = true, onClick: () -> Unit = {}
) {
    Button(
        enabled = enabled, modifier = modifier.height(60.dp), colors = ButtonDefaults.buttonColors(
            disabledContainerColor = DisabledOnSurface,
            disabledContentColor = MaterialTheme.colorScheme.onPrimary
        ), onClick = onClick
    ) {
        Text2(text = label, style = Text2StyleToken.BodyLarge, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun TextField(
    modifier: Modifier = Modifier,
    text: String = "",
    style: Text2StyleToken = Text2StyleToken.BodyMedium,
    isError: Boolean = false,
    singleLine: Boolean = false,
    staticLabelText: String = "",
    placeHolderText: String = "",
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val text = remember {
        mutableStateOf(text)
    }
    Column(modifier = modifier) {
        StaticLabel(staticLabelText)
        val shape = RoundedCornerShape(8.dp)
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.extendedColors.widgetBg,
                    shape = shape
                ),
            value = text.value,
            onValueChange = {
                text.value = it
            },
            trailingIcon = trailingIcon,
            leadingIcon = leadingIcon,
            textStyle = style.toTextStyle(),
            colors = OutlinedTextFieldDefaults.colors(

                focusedTextColor = InputTextColor,
                unfocusedTextColor = InputTextColor,
                errorTextColor = InputTextColor,

                // Label Colors
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,

                // Icon Colors (Leading)
                focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                errorLeadingIconColor = MaterialTheme.colorScheme.error,

                // Icon Colors (Trailing)
                focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,

                // Border/Indicator Colors
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.extendedColors.outline,

                cursorColor = MaterialTheme.colorScheme.primary
            ),
            placeholder = {
                Text2(text = placeHolderText, style = style)
            },
            isError = isError,
            singleLine = singleLine,
            visualTransformation = visualTransformation,
            shape = shape
        )
    }
}

@Composable
fun TextFieldPassword(
    modifier: Modifier = Modifier,
    text: String = "",
    style: Text2StyleToken = Text2StyleToken.BodyMedium,
    isError: Boolean = false,
    singleLine: Boolean = false,
    staticLabelText: String = "",
    placeHolderText: String = "",
    leadingIcon: @Composable (() -> Unit)? = null
) {
    var visualTransformation: VisualTransformation by remember {
        mutableStateOf(
            PasswordVisualTransformation('\u00B7')
        )
    }
    TextField(
        modifier = modifier,
        text = text,
        style = style,
        isError = isError,
        singleLine = singleLine,
        staticLabelText = staticLabelText,
        placeHolderText = placeHolderText,
        leadingIcon = leadingIcon,
        trailingIcon = {
            PasswordTrailingIcon {
                visualTransformation = if (it) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation('\u00B7')
                }
            }
        },
        visualTransformation = visualTransformation
    )
}

@Composable
fun PasswordTrailingIcon(onPasswordVisibilityChange: (Boolean) -> Unit = {}) {
    val passwordVisible = remember { mutableStateOf(false) }
    Icon(
        modifier = Modifier.wrapContentWidth().clickable {
            passwordVisible.value = !passwordVisible.value
            onPasswordVisibilityChange(passwordVisible.value)
        },
        imageVector = if (passwordVisible.value) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
        contentDescription = null
    )
}

@Composable
fun StaticLabel(text: String) {
    Text2(
        modifier = Modifier.wrapContentWidth().padding(bottom = Padding.padding8Dp),
        text = text,
        fontWeight = FontWeight.SemiBold,
    )
}


@Composable
fun Text2(
    modifier: Modifier = Modifier,
    text: String,
    style: Text2StyleToken = Text2StyleToken.BodyMedium,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Unspecified
) {
    Text(
        modifier = modifier,
        text = text,
        style = style.toTextStyle(),
        color = color,
        fontWeight = fontWeight,
        textAlign = textAlign
    )
}

@Composable
fun Text2(
    modifier: Modifier = Modifier,
    text: AnnotatedString,
    style: Text2StyleToken = Text2StyleToken.BodyMedium,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Unspecified
) {
    Text(
        modifier = modifier,
        text = text,
        style = style.toTextStyle(),
        color = color,
        fontWeight = fontWeight,
        textAlign = textAlign
    )
}


@Composable
fun DisplayLarge(text: String) {
    Text2(text = text, style = Text2StyleToken.DisplayLarge, color = InputTextColor)
}

@Composable
fun DisplayMedium(text: String) {
    Text2(text = text, style = Text2StyleToken.DisplayMedium, color = TextMediumDark)
}


@Composable
fun DisplaySmall(text: String) {
    Text2(text = text, style = Text2StyleToken.DisplaySmall, color = InputTextColor)
}


//32 sp
@Composable
fun HeadlineLarge(
    modifier: Modifier = Modifier,
    text: String,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = MaterialTheme.colorScheme.extendedColors.textDark,
    textAlign: TextAlign = TextAlign.Unspecified
) {
    Text2(
        modifier = modifier,
        text = text,
        style = Text2StyleToken.HeadlineLarge,
        color = color,
        fontWeight = fontWeight,
        textAlign = textAlign
    )
}

//28 sp
@Composable
fun HeadlineMedium(text: String) {
    Text2(text = text, style = Text2StyleToken.HeadlineMedium, color = InputTextColor)
}

//24 sp
@Composable
fun HeadlineSmall(text: String) {
    Text2(text = text, style = Text2StyleToken.HeadlineSmall, color = InputTextColor)
}

//22 sp
@Composable
fun TitleLarge(text: String) {
    Text2(text = text, style = Text2StyleToken.TitleLarge, color = InputTextColor)
}

@Composable
fun TitleMedium(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = InputTextColor,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Text2(
        modifier = modifier,
        text = text,
        style = Text2StyleToken.TitleMedium,
        color = color,
        fontWeight = fontWeight
    )
}


@Composable
fun TitleSmall(text: String) {
    Text2(text = text, style = Text2StyleToken.TitleSmall, color = InputTextColor)
}

@Composable
fun BodyLarge(
    modifier: Modifier,
    text: String,
    color: Color = InputTextColor,
    textAlign: TextAlign = TextAlign.Unspecified,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Text2(
        modifier = modifier,
        text = text,
        style = Text2StyleToken.BodyLarge,
        color = color,
        textAlign = textAlign,
        fontWeight = fontWeight
    )
}

@Composable
fun BodyMedium(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = InputTextColor,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Unspecified
) {
    Text2(
        modifier = modifier,
        text = text,
        color = color,
        style = Text2StyleToken.BodyMedium,
        fontWeight = fontWeight,
        textAlign = textAlign
    )
}

@Composable
fun BodyMedium(
    modifier: Modifier = Modifier,
    text: AnnotatedString,
    color: Color = InputTextColor,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Unspecified
) {
    Text2(
        modifier = modifier,
        text = text,
        color = color,
        fontWeight = fontWeight,
        textAlign = textAlign
    )
}


@Composable
fun BodySmall(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = InputTextColor,
    textAlign: TextAlign = TextAlign.Unspecified,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Text2(
        modifier = modifier,
        text = text,
        style = Text2StyleToken.BodySmall,
        color = color,
        textAlign = textAlign,
        fontWeight = fontWeight
    )
}

@Composable
fun BodySmall(
    modifier: Modifier = Modifier,
    text: AnnotatedString,
    color: Color = InputTextColor,
    textAlign: TextAlign = TextAlign.Unspecified,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Text2(
        modifier = modifier,
        text = text,
        style = Text2StyleToken.BodySmall,
        color = color,
        textAlign = textAlign,
        fontWeight = fontWeight
    )
}

@Composable
fun LabelLarge(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = InputTextColor,
    textAlign: TextAlign = TextAlign.Unspecified,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Text2(
        modifier = modifier,
        text = text,
        style = Text2StyleToken.LabelLarge,
        color = color,
        fontWeight = fontWeight,
        textAlign = textAlign
    )
}

@Composable
fun LabelMedium(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = InputTextColor,
    textAlign: TextAlign = TextAlign.Unspecified,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Text2(
        modifier = modifier,
        text = text,
        style = Text2StyleToken.LabelMedium,
        color = color,
        fontWeight = fontWeight,
        textAlign = textAlign
    )
}


@Composable
fun LabelSmall(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = InputTextColor,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Text2(
        modifier = modifier,
        text = text,
        style = Text2StyleToken.LabelSmall,
        color = color,
        fontWeight = fontWeight
    )
}

@Composable
fun IconText(
    modifier: Modifier = Modifier, icon: @Composable () -> Unit, text: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = modifier.fillMaxWidth()
            .clip(shape)
            .defaultMinSize(minHeight = 48.dp)
            .background(color = MaterialTheme.colorScheme.extendedColors.widgetBg)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.extendedColors.outline,
                shape = shape
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        icon()
        text()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransparentToolbar(onBackPressed: () -> Unit) {
    TopAppBar(
        navigationIcon = {
            Icon(
                modifier = Modifier.minimumInteractiveComponentSize().clickable {
                    onBackPressed()
                },
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.extendedColors.textDark
            )
        },
        title = {

        },
        colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = Color.Transparent),
    )
}

@Composable
fun AlternateAccountOptions() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = Padding.padding24Dp),
        horizontalArrangement = Arrangement.Center
    ) {
        IconText(modifier = Modifier.width(160.dp), icon = {
            Image(
                imageVector = vectorResource(Res.drawable.ic_google_logo),
                contentDescription = null
            )
        }, text = {
            BodyMedium(
                modifier = Modifier.padding(start = Padding.padding8Dp),
                text = stringResource(Res.string.signup_continue_with_google),
                fontWeight = FontWeight.W700,
                color = MaterialTheme.colorScheme.extendedColors.textDark
            )
        })

        Spacer(modifier = Modifier.width(Padding.padding16Dp))

        IconText(modifier = Modifier.width(160.dp), icon = {
            Image(
                imageVector = vectorResource(Res.drawable.ic_facebook_logo),
                contentDescription = null
            )
        }, text = {
            BodyMedium(
                modifier = Modifier.padding(start = Padding.padding8Dp),
                text = stringResource(Res.string.signup_continue_with_facebook),
                fontWeight = FontWeight.W700,
                color = MaterialTheme.colorScheme.extendedColors.textDark
            )
        })
    }
}

@Composable
fun ProfileAction(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconTint: Color,
    iconBgColor: Color,
    text: String,
    desc: String,
    trailing: @Composable () -> Unit,
    onActionClick: () -> Unit
) {
    val shape1 = RoundedCornerShape(999.dp)
    val shape2 = RoundedCornerShape(20.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = shape2)
            .background(
                color = MaterialTheme.colorScheme.extendedColors.widgetBg, shape = shape2
            ).clickable {
                onActionClick()
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(
                horizontal = Padding.padding16Dp, vertical = Padding.padding12Dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Padding.padding16Dp)
        ) {
            Box(
                modifier = Modifier.padding(vertical = 4.dp).size(48.dp).background(
                    color = iconBgColor, shape = shape1
                )
            ) {
                Icon(
                    modifier = Modifier.padding(8.dp).size(24.dp).align(Alignment.Center),
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint
                )
            }
            Column {
                BodyLarge(
                    modifier = Modifier,
                    text = text,
                    color = MaterialTheme.colorScheme.extendedColors.textDark,
                    fontWeight = FontWeight.Bold
                )
                BodyMedium(
                    modifier = Modifier,
                    text = desc,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            trailing()
        }
    }
}


@Preview(backgroundColor = 0xFF0000, showBackground = true)
@Composable
fun LargeButtonPreview() {
    AppTheme {
        Column {
//            LargeButton(modifier = Modifier.fillMaxWidth(), label = "Button")
            TextField(modifier = Modifier.fillMaxWidth())
//            TextField(modifier = Modifier.fillMaxWidth(), isError = true)
//            DisplayLarge(text = "Get Started")
//            DisplayMedium(text = "Get Started")
//            DisplaySmall(text = "Get Started")
//            HeadlineLarge(text = "Headline")
//            HeadlineMedium(text = "Headline")
//            HeadlineSmall(text = "Headline")
//            TitleLarge(text = "Title")
//            TitleMedium(text = "Title")
//            TitleSmall(text = "Title")
//            BodyLarge(text = "Body")
//            BodyMedium(text = "Body")
//            BodySmall(text = "Body",)
//            LabelLarge(text = "Label")
//            LabelMedium(text = "Label")
//            LabelSmall(text = "Label")
            AlternateAccountOptions()
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