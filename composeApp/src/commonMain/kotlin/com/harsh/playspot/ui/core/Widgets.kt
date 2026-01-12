package com.harsh.playspot.ui.core

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SportsBaseball
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.SportsGolf
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.SportsVolleyball
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.SelectableChipElevation
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.harsh.playspot.ui.signup.SportChipState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import playspot.composeapp.generated.resources.Res
import playspot.composeapp.generated.resources.ic_badminton
import playspot.composeapp.generated.resources.ic_facebook_logo
import playspot.composeapp.generated.resources.ic_google_logo
import playspot.composeapp.generated.resources.profile_picture_take_photo
import playspot.composeapp.generated.resources.signup_continue_with_facebook
import playspot.composeapp.generated.resources.signup_continue_with_google

@Composable
fun LargeButton(
    modifier: Modifier = Modifier,
    label: String,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    onClick: () -> Unit = {}
) {
    val hapticFeedback = LocalHapticFeedback.current
    Button(
        enabled = enabled && !isLoading,
        modifier = modifier.height(60.dp),
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = if (isLoading) MaterialTheme.colorScheme.primary else DisabledOnSurface,
            disabledContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        onClick = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
            onClick()
        }
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text2(text = label, style = Text2StyleToken.BodyLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun OutlinedPrimaryButton(
    modifier: Modifier = Modifier,
    label: String,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    val hapticFeedback = LocalHapticFeedback.current
    OutlinedButton(
        onClick = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
            onClick()
        },
        modifier = modifier.height(44.dp),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.extendedColors.widgetBg,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (enabled) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            }
        )
    ) {
        LabelLarge(
            text = label,
            fontWeight = FontWeight.Bold,
            color = if (enabled) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            }
        )
    }
}

@Composable
fun DangerButton(
    modifier: Modifier = Modifier,
    label: String,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    onClick: () -> Unit = {}
) {
    val hapticFeedback = LocalHapticFeedback.current
    val dangerColor = MaterialTheme.extendedColors.red
    
    Button(
        onClick = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
            onClick()
        },
        modifier = modifier.fillMaxWidth(),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = dangerColor,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = dangerColor.copy(alpha = 0.5f)
        ),
        contentPadding = PaddingValues(16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = dangerColor,
                strokeWidth = 2.dp
            )
        } else {
            LabelLarge(
                text = label,
                fontWeight = FontWeight.SemiBold,
                color = if (enabled) dangerColor else dangerColor.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun TextField(
    modifier: Modifier = Modifier,
    value: String = "",
    onValueChange: (String) -> Unit = {},
    style: Text2StyleToken = Text2StyleToken.BodyMedium,
    isError: Boolean = false,
    errorText: String? = null,
    singleLine: Boolean = false,
    staticLabelText: String = "",
    placeHolderText: String = "",
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val hapticFeedback = LocalHapticFeedback.current
    Column(modifier = modifier) {
        StaticLabel(staticLabelText)
        val shape = RoundedCornerShape(8.dp)

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().onFocusChanged { state ->
                if (state.isFocused) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            }.background(
                color = MaterialTheme.extendedColors.widgetBg, shape = shape
            ),
            value = value,
            onValueChange = onValueChange,
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
                unfocusedBorderColor = MaterialTheme.extendedColors.outline,
                errorBorderColor = MaterialTheme.colorScheme.error,

                cursorColor = MaterialTheme.colorScheme.primary
            ),
            placeholder = {
                Text2(text = placeHolderText, style = style)
            },
            isError = isError || errorText != null,
            singleLine = singleLine,
            visualTransformation = visualTransformation,
            shape = shape,
            supportingText = if (errorText != null) {
                { LabelSmall(text = errorText, color = MaterialTheme.colorScheme.error) }
            } else null
        )
    }
}

@Composable
fun TextFieldPassword(
    modifier: Modifier = Modifier,
    value: String = "",
    onValueChange: (String) -> Unit = {},
    style: Text2StyleToken = Text2StyleToken.BodyMedium,
    isError: Boolean = false,
    errorText: String? = null,
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
        value = value,
        onValueChange = onValueChange,
        style = style,
        isError = isError,
        errorText = errorText,
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
        modifier = Modifier.wrapContentWidth()
            .clickWithFeedback(if (passwordVisible.value) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff) {
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
fun DisplayLarge(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Unspecified
) {
    Text2(
        modifier = modifier,
        text = text,
        style = Text2StyleToken.DisplayLarge,
        color = color,
        fontWeight = fontWeight,
        textAlign = textAlign
    )
}

@Composable
fun DisplayMedium(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Unspecified
) {
    Text2(
        style = Text2StyleToken.DisplayMedium,
        modifier = modifier,
        text = text,
        color = color,
        fontWeight = fontWeight,
        textAlign = textAlign
    )
}


@Composable
fun DisplaySmall(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.extendedColors.textDark,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Unspecified
) {
    Text2(
        style = Text2StyleToken.DisplaySmall, modifier = modifier,
        text = text,
        color = color,
        fontWeight = fontWeight,
        textAlign = textAlign
    )
}


//32 sp
@Composable
fun HeadlineLarge(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.extendedColors.textDark,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Unspecified
) {
    Text2(
        modifier = modifier,
        style = Text2StyleToken.HeadlineLarge,
        text = text,
        color = color,
        fontWeight = fontWeight,
        textAlign = textAlign
    )
}

//28 sp
@Composable
fun HeadlineMedium(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.extendedColors.textDark,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Unspecified
) {
    Text2(
        modifier = modifier,
        text = text,
        style = Text2StyleToken.HeadlineMedium,
        color = color,
        fontWeight = fontWeight,
        textAlign = textAlign
    )
}

//24 sp
@Composable
fun HeadlineSmall(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.extendedColors.textDark,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Unspecified
) {
    Text2(
        style = Text2StyleToken.HeadlineSmall,
        modifier = modifier,
        text = text,
        color = color,
        fontWeight = fontWeight,
        textAlign = textAlign
    )
}

//22 sp
@Composable
fun TitleLarge(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.extendedColors.textDark,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Unspecified
) {
    Text2(
        style = Text2StyleToken.TitleLarge,
        modifier = modifier,
        text = text,
        color = color,
        fontWeight = fontWeight,
        textAlign = textAlign
    )
}

@Composable
fun TitleMedium(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.extendedColors.textDark,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Unspecified
) {
    Text2(
        style = Text2StyleToken.TitleMedium,
        modifier = modifier,
        text = text,
        color = color,
        fontWeight = fontWeight,
        textAlign = textAlign
    )
}


@Composable
fun TitleSmall(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.extendedColors.textDark,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Unspecified
) {
    Text2(
        style = Text2StyleToken.TitleSmall,
        modifier = modifier,
        text = text,
        color = color,
        fontWeight = fontWeight,
        textAlign = textAlign
    )
}

@Composable
fun BodyLarge(
    modifier: Modifier,
    text: String,
    color: Color = MaterialTheme.extendedColors.textDark,
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
    color: Color = MaterialTheme.extendedColors.textDark,
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
    color: Color = MaterialTheme.extendedColors.textDark,
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
    color: Color = MaterialTheme.extendedColors.textDark,
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
    color: Color = MaterialTheme.extendedColors.textDark,
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
    color: Color = MaterialTheme.extendedColors.textDark,
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
    color: Color = MaterialTheme.extendedColors.textDark,
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
    color: Color = MaterialTheme.extendedColors.textDark,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Unspecified,
) {
    Text2(
        modifier = modifier,
        text = text,
        style = Text2StyleToken.LabelSmall,
        color = color,
        fontWeight = fontWeight,
        textAlign = textAlign
    )
}

@Composable
fun IconText(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    onClick: () -> Unit = {}
) {
    val hapticFeedback = LocalHapticFeedback.current
    FilterChip(
        selected = false,
        onClick = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        label = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    icon()
                    text()
                }
            }
        },
        modifier = modifier.defaultMinSize(minHeight = 48.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.extendedColors.outline
        ),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.extendedColors.widgetBg,
            labelColor = MaterialTheme.extendedColors.textDark
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransparentToolbar(onBackPressed: () -> Unit) {
    TopAppBar(
        navigationIcon = {
            Icon(
                modifier = Modifier.minimumInteractiveComponentSize().clickWithFeedback(
                    HapticFeedbackType.Confirm
                ) {
                    onBackPressed()
                },
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.extendedColors.textDark
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
                imageVector = vectorResource(Res.drawable.ic_google_logo), contentDescription = null
            )
        }, text = {
            BodyMedium(
                modifier = Modifier.padding(start = Padding.padding8Dp),
                text = stringResource(Res.string.signup_continue_with_google),
                fontWeight = FontWeight.W700,
                color = MaterialTheme.extendedColors.textDark
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
                color = MaterialTheme.extendedColors.textDark
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
    desc: String? = null,
    trailing: @Composable () -> Unit,
    onActionClick: () -> Unit
) {
    val shape2 = RoundedCornerShape(20.dp)
    Box(
        modifier = modifier.fillMaxWidth().clip(shape = shape2).background(
            color = MaterialTheme.extendedColors.widgetBg, shape = shape2
        )
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.extendedColors.outline
                ),
                shape = shape2
            ).clickWithFeedback(HapticFeedbackType.SegmentTick) {
                onActionClick()
            }) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(
                horizontal = Padding.padding16Dp, vertical = Padding.padding12Dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Padding.padding16Dp)
        ) {
            Box(
                modifier = Modifier.padding(vertical = 4.dp).size(48.dp).background(
                    color = iconBgColor, shape = semiCircleCornerShape
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
                LabelLarge(
                    modifier = Modifier,
                    text = text,
                    color = MaterialTheme.extendedColors.textDark,
                    fontWeight = FontWeight.SemiBold
                )
                if (desc?.isNotEmpty() == true) {
                    LabelSmall(
                        modifier = Modifier,
                        text = desc,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            trailing()
        }
    }
}

@Composable
fun FilterChip2(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = semiCircleCornerShape,
    colors: SelectableChipColors = FilterChipDefaults.filterChipColors(),
    elevation: SelectableChipElevation? = null,
    border: BorderStroke? = null,
) {
    val hapticFeedback = LocalHapticFeedback.current
    FilterChip(
        selected = selected,
        onClick = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        label = label,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        border = border,
        colors = colors,
        elevation = elevation,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon
    )
}


@Composable
fun SportChip(text: String, imageVector: ImageVector) {
    val state: MutableState<SportChipState> = remember {
        mutableStateOf(SportChipState.UnSelected)
    }

    val tintColor =
        if (state.value.isSelected) MaterialTheme.extendedColors.selectedChipText else MaterialTheme.extendedColors.chipText
    val ringBgColor =
        if (state.value.isSelected) Color.White.copy(alpha = 0.2f) else MaterialTheme.extendedColors.chipIconBg

    FilterChip2(
        modifier = Modifier.defaultMinSize(minHeight = 38.dp),
        selected = state.value.isSelected,
        onClick = {
            state.value =
                if (state.value.isSelected) SportChipState.UnSelected else SportChipState.Selected

        },
        leadingIcon = {
            /*Box(
                modifier = Modifier.size(38.dp)
                    .background(color = ringBgColor, shape = semiCircleCornerShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = imageVector,
                    contentDescription = null,
                    tint = tintColor
                )
            }*/
            Icon(
                modifier = Modifier.size(38.dp)
                    .background(color = ringBgColor, shape = semiCircleCornerShape),
                imageVector = imageVector,
                contentDescription = null,
                tint = tintColor
            )
        },
        label = {
            TitleMedium(
                text = text,
                color = Color.Unspecified,
                fontWeight = FontWeight.W500,
                textAlign = TextAlign.Center
            )
        },
        trailingIcon = {
            Box(
                modifier = Modifier.size(24.dp)
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = state.value.isSelected,
                    enter = fadeIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ),
                    exit = fadeOut() + scaleOut()
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = tintColor
                    )
                }
            }
        },

        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.extendedColors.chipContainer,
            selectedContainerColor = MaterialTheme.extendedColors.selectedChipContainer,
            labelColor = MaterialTheme.extendedColors.chipText,
            selectedLabelColor = MaterialTheme.extendedColors.selectedChipText,
            selectedTrailingIconColor = MaterialTheme.extendedColors.selectedChipText,
            disabledTrailingIconColor = MaterialTheme.extendedColors.chipIconBg
        )
    )
}

val semiCircleCornerShape = RoundedCornerShape(999.dp)

@Composable
fun Modifier.clickWithFeedback(
    hapticFeedbackType: HapticFeedbackType, onClick: () -> Unit
): Modifier {
    val hapticFeedback = LocalHapticFeedback.current
    return this.clickable {
        hapticFeedback.performHapticFeedback(hapticFeedbackType)
        onClick()
    }
}


@Preview(backgroundColor = 0x666666, showBackground = true)
@Composable
fun LargeButtonPreview() {
    AppTheme {
        Column(modifier = Modifier.padding(16.dp)) {
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
            SportChip("Basketball", Icons.Filled.SportsBasketball)
            ProfileAction(
                icon = Icons.Filled.PhotoCamera,
                iconTint = MaterialTheme.colorScheme.primary,
                iconBgColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                text = stringResource(Res.string.profile_picture_take_photo),
                desc = "",
                trailing = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(16.dp)
                    )
                },
                onActionClick = { }
            )
            LargeButton(modifier = Modifier.fillMaxWidth(), "", isLoading = true)
            DangerButton(modifier = Modifier.fillMaxWidth(), "", isLoading = true)
        }
    }
}

data class SportUi(val name: String, val color: Color, val icon: ImageVector)

@Composable
fun getSportsMap() =
    mapOf(
        "Football" to SportUi("Football", Color(0xFF22C55E), Icons.Filled.SportsSoccer),
        "Basketball" to SportUi("Basketball", Color(0xFFF97316), Icons.Filled.SportsBasketball),
        "Tennis" to SportUi("Tennis", Color(0xFFEAB308), Icons.Filled.SportsTennis),
        "Running" to SportUi("Running", Color(0xFFEC4899), Icons.AutoMirrored.Filled.DirectionsRun),
        "Volleyball" to SportUi("Volleyball", Color(0xFF8B5CF6), Icons.Filled.SportsVolleyball),
        "Swimming" to SportUi("Swimming", Color(0xFF3B82F6), Icons.Filled.Pool),
        "Cycling" to SportUi("Cycling", Color(0xFF14B8A6), Icons.AutoMirrored.Filled.DirectionsBike),
        "Cricket" to SportUi("Cricket", Color(0xFF10B981), Icons.Filled.SportsCricket),
        "Baseball" to SportUi("Baseball", Color(0xFFEF4444), Icons.Filled.SportsBaseball),
        "Badminton" to SportUi("Badminton", Color(0xFF06B6D4), vectorResource(Res.drawable.ic_badminton)),
        "Gym" to SportUi("Gym", Color(0xFF6366F1), Icons.Filled.FitnessCenter),
        "Golf" to SportUi("Golf", Color(0xFF84CC16), Icons.Filled.SportsGolf),
        "Yoga" to SportUi("Yoga", Color(0xFFE50833), Icons.Filled.SelfImprovement) // Added appropriate icon
    )


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