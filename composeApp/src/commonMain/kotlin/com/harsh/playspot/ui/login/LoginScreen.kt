package com.harsh.playspot.ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.harsh.playspot.ui.core.AlternateAccountOptions
import com.harsh.playspot.ui.core.BackgroundImageScreen
import com.harsh.playspot.ui.core.BodyMedium
import com.harsh.playspot.ui.core.BodySmall
import com.harsh.playspot.ui.core.HeadlineLarge
import com.harsh.playspot.ui.core.LargeButton
import com.harsh.playspot.ui.core.Padding
import com.harsh.playspot.ui.core.TextField
import com.harsh.playspot.ui.core.TextFieldPassword
import com.harsh.playspot.ui.core.extendedColors
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import playspot.composeapp.generated.resources.Res
import playspot.composeapp.generated.resources.login_continue_with
import playspot.composeapp.generated.resources.login_cta
import playspot.composeapp.generated.resources.login_enter_email
import playspot.composeapp.generated.resources.login_enter_password
import playspot.composeapp.generated.resources.login_forgot_password
import playspot.composeapp.generated.resources.login_join_squad
import playspot.composeapp.generated.resources.login_label_email
import playspot.composeapp.generated.resources.login_label_password
import playspot.composeapp.generated.resources.login_password_reset_success
import playspot.composeapp.generated.resources.login_signup
import playspot.composeapp.generated.resources.login_signup_account
import playspot.composeapp.generated.resources.login_welcome_back

@Composable
fun LoginScreenRoute(
    onBackPressed: () -> Unit,
    onSignUpClicked: () -> Unit,
    onForgotPasswordClicked: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    showPasswordResetSuccess: Boolean = false,
    viewModel: LoginViewModel = viewModel { LoginViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val passwordResetSuccessMessage = stringResource(Res.string.login_password_reset_success)

    // Show password reset success message when navigated from deep link
    LaunchedEffect(showPasswordResetSuccess) {
        if (showPasswordResetSuccess) {
            snackbarHostState.showSnackbar(passwordResetSuccessMessage)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is LoginEvent.LoginSuccess -> onLoginSuccess()
                is LoginEvent.LoginError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    LoginScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackPressed = onBackPressed,
        onSignUpClicked = onSignUpClicked,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClicked = viewModel::login,
        onForgotPasswordClicked = onForgotPasswordClicked
    )
}


@Composable
fun LoginScreen(
    uiState: LoginUiState = LoginUiState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onBackPressed: () -> Unit = {},
    onSignUpClicked: () -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onLoginClicked: () -> Unit = {},
    onForgotPasswordClicked: () -> Unit = {}
) {
    BackgroundImageScreen(
        onBackPressed = onBackPressed,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = {
            HeadlineLarge(
                modifier = Modifier.padding(top = Padding.padding24Dp),
                text = stringResource(Res.string.login_welcome_back),
                fontWeight = FontWeight.Bold,
            )
            BodyMedium(
                modifier = Modifier.padding(
                    start = Padding.padding4Dp,
                    top = Padding.padding12Dp
                ),
                text = stringResource(Res.string.login_join_squad),
                color = MaterialTheme.extendedColors.textDark
            )

            TextField(
                modifier = Modifier.fillMaxWidth().padding(top = Padding.padding32Dp),
                value = uiState.email,
                onValueChange = onEmailChange,
                singleLine = true,
                staticLabelText = stringResource(Res.string.login_label_email),
                placeHolderText = stringResource(Res.string.login_enter_email),
                isError = uiState.emailError != null,
                errorText = uiState.emailError,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Email, contentDescription = null
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Email),
                keyboardActions = KeyboardActions(onNext = {
                    defaultKeyboardAction(ImeAction.Next)
                }))

            TextFieldPassword(
                modifier = Modifier.fillMaxWidth().padding(top = Padding.padding16Dp),
                value = uiState.password,
                onValueChange = onPasswordChange,
                singleLine = true,
                staticLabelText = stringResource(Res.string.login_label_password),
                placeHolderText = stringResource(Res.string.login_enter_password),
                isError = uiState.passwordError != null,
                errorText = uiState.passwordError,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
                keyboardActions = KeyboardActions(onDone = {
                    onLoginClicked()
                })

            )

            BodySmall(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Padding.padding24Dp)
                    .clickable { onForgotPasswordClicked() },
                text = stringResource(Res.string.login_forgot_password),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold
            )

            LargeButton(
                modifier = Modifier.fillMaxWidth().padding(top = Padding.padding16Dp),
                label = stringResource(Res.string.login_cta),
                enabled = !uiState.isLoading,
                onClick = onLoginClicked
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = Padding.padding24Dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                BodyMedium(
                    modifier = Modifier.wrapContentWidth()
                        .padding(horizontal = Padding.padding4Dp),
                    text = stringResource(Res.string.login_continue_with),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            AlternateAccountOptions()

            LoginFooter(onSignUpClicked)
        },
    )
}

@Composable
fun LoginFooter(onSignUpClicked: () -> Unit) {
    val footerString = buildAnnotatedString {
        append(stringResource(Res.string.login_signup_account))
        append(" ")
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        ) {
            append(stringResource(Res.string.login_signup))
        }
    }
    BodyMedium(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Padding.padding24Dp)
            .clickable {
                onSignUpClicked()
            },
        text = footerString,
        color = MaterialTheme.colorScheme.outlineVariant,
        textAlign = TextAlign.Center
    )
}


@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}
