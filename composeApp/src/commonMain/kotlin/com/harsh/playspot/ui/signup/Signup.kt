package com.harsh.playspot.ui.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.harsh.playspot.ui.core.AppTheme
import com.harsh.playspot.ui.core.BodyMedium
import com.harsh.playspot.ui.core.BodySmall
import com.harsh.playspot.ui.core.HeadlineLarge
import com.harsh.playspot.ui.core.IconText
import com.harsh.playspot.ui.core.InputTextColor
import com.harsh.playspot.ui.core.LargeButton
import com.harsh.playspot.ui.core.Padding
import com.harsh.playspot.ui.core.Text2
import com.harsh.playspot.ui.core.Text2StyleToken
import com.harsh.playspot.ui.core.TextField
import com.harsh.playspot.ui.core.TextFieldPassword
import com.harsh.playspot.ui.core.TextMediumDark
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import playspot.composeapp.generated.resources.Res
import playspot.composeapp.generated.resources.blob_scene_haikei
import playspot.composeapp.generated.resources.ic_facebook_logo
import playspot.composeapp.generated.resources.ic_google_logo
import playspot.composeapp.generated.resources.signup_already_member
import playspot.composeapp.generated.resources.signup_confirm_password
import playspot.composeapp.generated.resources.signup_continue_with
import playspot.composeapp.generated.resources.signup_continue_with_facebook
import playspot.composeapp.generated.resources.signup_continue_with_google
import playspot.composeapp.generated.resources.signup_create_password
import playspot.composeapp.generated.resources.signup_cta_signup
import playspot.composeapp.generated.resources.signup_email_address
import playspot.composeapp.generated.resources.signup_enter_email
import playspot.composeapp.generated.resources.signup_find_squad
import playspot.composeapp.generated.resources.signup_get_start
import playspot.composeapp.generated.resources.signup_login_in
import playspot.composeapp.generated.resources.signup_password

@Composable
fun SignupScreenRoute() {
    SignUpScreen()
}

@Composable
private fun SignUpScreen() {
    AppTheme {
        Scaffold() { paddingValues ->
            Box {
                Image(
                    modifier = Modifier.fillMaxWidth(),
                    painter = painterResource(Res.drawable.blob_scene_haikei),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alpha = 0.5f
                )
                Column(
                    modifier = Modifier.padding(
                        start = Padding.padding16Dp,
                        end = Padding.padding16Dp,
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding()
                    ).fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {

                    HeadlineLarge(
                        text = stringResource(Res.string.signup_get_start),
                        fontWeight = FontWeight.Bold
                    )
                    BodyMedium(
                        modifier = Modifier.padding(
                            start = Padding.padding4Dp,
                            top = Padding.padding12Dp
                        ),
                        text = stringResource(Res.string.signup_find_squad),
                        color = TextMediumDark
                    )

                    TextField(
                        modifier = Modifier.fillMaxWidth().padding(top = Padding.padding24Dp),
                        singleLine = true,
                        staticLabelText = stringResource(Res.string.signup_email_address),
                        placeHolderText = stringResource(Res.string.signup_enter_email),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Email, contentDescription = null
                            )
                        })

                    TextFieldPassword(
                        modifier = Modifier.fillMaxWidth().padding(top = Padding.padding16Dp),
                        singleLine = true,
                        staticLabelText = stringResource(Res.string.signup_password),
                        placeHolderText = stringResource(Res.string.signup_create_password)
                    )

                    TextFieldPassword(
                        modifier = Modifier.fillMaxWidth().padding(top = Padding.padding16Dp),
                        singleLine = true,
                        staticLabelText = stringResource(Res.string.signup_confirm_password),
                        placeHolderText = stringResource(Res.string.signup_confirm_password),
                    )

                    LargeButton(
                        modifier = Modifier.fillMaxWidth().padding(top = Padding.padding16Dp),
                        label = stringResource(Res.string.signup_cta_signup)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = Padding.padding16Dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f))
                        Text2(
                            modifier = Modifier.wrapContentWidth()
                                .padding(horizontal = Padding.padding4Dp),
                            text = stringResource(Res.string.signup_continue_with),
                            style = Text2StyleToken.LabelSmall,
                            color = MaterialTheme.colorScheme.outlineVariant,
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = Padding.padding20Dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconText(modifier = Modifier.width(160.dp), icon = {
                            Image(
                                imageVector = vectorResource(Res.drawable.ic_google_logo),
                                contentDescription = null
                            )
                        }, text = {
                            BodyMedium(
                                modifier = Modifier.padding(start = Padding.padding4Dp),
                                text = stringResource(Res.string.signup_continue_with_google),
                                fontWeight = FontWeight.W700
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
                                modifier = Modifier.padding(start = Padding.padding4Dp),
                                text = stringResource(Res.string.signup_continue_with_facebook),
                                fontWeight = FontWeight.W700
                            )
                        })
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BodySmall(
                            modifier = Modifier
                                .padding(vertical = Padding.padding24Dp),
                            text = stringResource(Res.string.signup_already_member),
                            color = TextMediumDark,
                            textAlign = TextAlign.Center,
                        )
                        BodySmall(
                            modifier = Modifier
                                .padding(
                                    vertical = Padding.padding24Dp,
                                    horizontal = Padding.padding4Dp
                                ),
                            text = stringResource(Res.string.signup_login_in),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupTopBar() {
    TopAppBar(navigationIcon = {
        Icon(
            modifier = Modifier.minimumInteractiveComponentSize(),
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null,
            tint = InputTextColor
        )
    }, title = {

    })
}

@Preview
@Composable
fun SignUpScreenPreview() {
    SignUpScreen()
}




