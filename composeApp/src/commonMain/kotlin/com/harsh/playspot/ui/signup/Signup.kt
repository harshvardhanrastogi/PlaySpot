package com.harsh.playspot.ui.signup

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.harsh.playspot.ui.core.AlternateAccountOptions
import com.harsh.playspot.ui.core.BackgroundImageScreen
import com.harsh.playspot.ui.core.BodyMedium
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
import playspot.composeapp.generated.resources.signup_already_member
import playspot.composeapp.generated.resources.signup_confirm_password
import playspot.composeapp.generated.resources.signup_continue_with
import playspot.composeapp.generated.resources.signup_create_password
import playspot.composeapp.generated.resources.signup_cta_signup
import playspot.composeapp.generated.resources.signup_email_address
import playspot.composeapp.generated.resources.signup_enter_email
import playspot.composeapp.generated.resources.signup_enter_full_name
import playspot.composeapp.generated.resources.signup_find_squad
import playspot.composeapp.generated.resources.signup_full_name
import playspot.composeapp.generated.resources.signup_get_start
import playspot.composeapp.generated.resources.signup_login_in
import playspot.composeapp.generated.resources.signup_password

@Composable
fun SignupScreenRoute(
    onBackPressed: () -> Unit,
    onLoginClicked: () -> Unit,
    onSignUpSuccess: () -> Unit,
    viewModel: SignUpViewModel = viewModel { SignUpViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is SignUpEvent.SignUpSuccess -> onSignUpSuccess()
                is SignUpEvent.SignUpError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    SignUpScreen(
        uiState = uiState,
        scrollState = scrollState,
        snackbarHostState = snackbarHostState,
        onBackPressed = onBackPressed,
        onLoginClicked = onLoginClicked,
        onNameChange = viewModel::onFullNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onSignUpClicked = viewModel::signUp
    )
}

@Composable
private fun SignUpScreen(
    uiState: SignUpUiState = SignUpUiState(),
    scrollState: ScrollState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onBackPressed: () -> Unit = {},
    onLoginClicked: () -> Unit = {},
    onNameChange: (String) -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    onSignUpClicked: () -> Unit = {}
) {
    BackgroundImageScreen(
        scrollState = scrollState,
        onBackPressed = onBackPressed,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
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
            value = uiState.fullName,
            onValueChange = onNameChange,
            singleLine = true,
            staticLabelText = stringResource(Res.string.signup_full_name),
            placeHolderText = stringResource(Res.string.signup_enter_full_name),
            isError = uiState.fullNameError != null,
            errorText = uiState.fullNameError,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.Person, contentDescription = null
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, capitalization = KeyboardCapitalization.Words),
            keyboardActions = KeyboardActions(onNext = {
                defaultKeyboardAction(ImeAction.Next)
            }))

        TextField(
            modifier = Modifier.fillMaxWidth().padding(top = Padding.padding16Dp),
            value = uiState.email,
            onValueChange = onEmailChange,
            singleLine = true,
            staticLabelText = stringResource(Res.string.signup_email_address),
            placeHolderText = stringResource(Res.string.signup_enter_email),
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
            staticLabelText = stringResource(Res.string.signup_password),
            placeHolderText = stringResource(Res.string.signup_create_password),
            isError = uiState.passwordError != null,
            errorText = uiState.passwordError,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Password),
            keyboardActions = KeyboardActions(onNext = {
                defaultKeyboardAction(ImeAction.Next)
            })
        )

        TextFieldPassword(
            modifier = Modifier.fillMaxWidth().padding(top = Padding.padding16Dp),
            value = uiState.confirmPassword,
            onValueChange = onConfirmPasswordChange,
            singleLine = true,
            staticLabelText = stringResource(Res.string.signup_confirm_password),
            placeHolderText = stringResource(Res.string.signup_confirm_password),
            isError = uiState.confirmPasswordError != null,
            errorText = uiState.confirmPasswordError,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
            keyboardActions = KeyboardActions(onDone = {
                defaultKeyboardAction(ImeAction.Done)
                onSignUpClicked()
            })
        )

        LargeButton(
            modifier = Modifier.fillMaxWidth().padding(top = Padding.padding56Dp),
            label = stringResource(Res.string.signup_cta_signup),
            enabled = !uiState.isLoading,
            onClick = onSignUpClicked
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
    SignUpScreen(scrollState = rememberScrollState())
}
