package com.harsh.playspot.ui.signup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.harsh.playspot.ui.core.AlternateAccountOptions
import com.harsh.playspot.ui.core.BackgroundImageScreen
import com.harsh.playspot.ui.core.BodyMedium
import com.harsh.playspot.ui.core.BodySmall
import com.harsh.playspot.ui.core.HeadlineLarge
import com.harsh.playspot.ui.core.LabelSmall
import com.harsh.playspot.ui.core.LargeButton
import com.harsh.playspot.ui.core.Padding
import com.harsh.playspot.ui.core.TextField
import com.harsh.playspot.ui.core.TextFieldPassword
import com.harsh.playspot.ui.core.TextMediumDark
import com.harsh.playspot.ui.core.extendedColors
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import playspot.composeapp.generated.resources.Res
import playspot.composeapp.generated.resources.signup_already_member
import playspot.composeapp.generated.resources.signup_confirm_password
import playspot.composeapp.generated.resources.signup_continue_with
import playspot.composeapp.generated.resources.signup_create_password
import playspot.composeapp.generated.resources.signup_cta_signup
import playspot.composeapp.generated.resources.signup_email_address
import playspot.composeapp.generated.resources.signup_enter_email
import playspot.composeapp.generated.resources.signup_find_squad
import playspot.composeapp.generated.resources.signup_get_start
import playspot.composeapp.generated.resources.signup_login_in
import playspot.composeapp.generated.resources.signup_password

@Composable
fun SignupScreenRoute(
    onBackPressed: () -> Unit,
    onLoginClicked: () -> Unit,
    onSignUpSuccess: () -> Unit
) {
    SignUpScreen(onBackPressed, onLoginClicked, onSignUpSuccess)
}

@Composable
private fun SignUpScreen(
    onBackPressed: () -> Unit,
    onLoginClicked: () -> Unit = {},
    onSignUpSuccess: () -> Unit = {}
) {
    BackgroundImageScreen(onBackPressed = onBackPressed) {
        HeadlineLarge(
            modifier = Modifier.padding(top = Padding.padding24Dp),
            text = stringResource(Res.string.signup_get_start),
            fontWeight = FontWeight.Bold,
        )
        BodyMedium(
            modifier = Modifier.padding(
                start = Padding.padding4Dp,
                top = Padding.padding12Dp
            ),
            text = stringResource(Res.string.signup_find_squad),
            color = MaterialTheme.extendedColors.textDark
        )

        TextField(
            modifier = Modifier.fillMaxWidth().padding(top = Padding.padding32Dp),
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
            modifier = Modifier.fillMaxWidth().padding(top = Padding.padding56Dp),
            label = stringResource(Res.string.signup_cta_signup),
            onClick = onSignUpSuccess
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = Padding.padding24Dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            BodyMedium(
                modifier = Modifier.wrapContentWidth()
                    .padding(horizontal = Padding.padding4Dp),
                text = stringResource(Res.string.signup_continue_with),
                color = MaterialTheme.colorScheme.outlineVariant,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        AlternateAccountOptions()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BodyMedium(
                modifier = Modifier
                    .padding(vertical = Padding.padding24Dp),
                text = stringResource(Res.string.signup_already_member),
                color = MaterialTheme.colorScheme.outlineVariant,
                textAlign = TextAlign.Center,
            )
            BodyMedium(
                modifier = Modifier
                    .padding(
                        vertical = Padding.padding24Dp,
                        horizontal = Padding.padding4Dp
                    ).clickable {
                        onLoginClicked()
                    },
                text = stringResource(Res.string.signup_login_in),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }

    }
}

@Preview
@Composable
fun SignUpScreenPreview() {
    SignUpScreen({})
}



