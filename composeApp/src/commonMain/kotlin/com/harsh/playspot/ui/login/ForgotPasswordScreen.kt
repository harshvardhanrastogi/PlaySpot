package com.harsh.playspot.ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.harsh.playspot.ui.core.AppTheme
import com.harsh.playspot.ui.core.BodyMedium
import com.harsh.playspot.ui.core.HeadlineLarge
import com.harsh.playspot.ui.core.LargeButton
import com.harsh.playspot.ui.core.Padding
import com.harsh.playspot.ui.core.TextField
import com.harsh.playspot.ui.core.TitleToolbar
import com.harsh.playspot.ui.core.extendedColors
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import playspot.composeapp.generated.resources.Res
import playspot.composeapp.generated.resources.forgot_password_back_to_login
import playspot.composeapp.generated.resources.forgot_password_description
import playspot.composeapp.generated.resources.forgot_password_email_label
import playspot.composeapp.generated.resources.forgot_password_email_placeholder
import playspot.composeapp.generated.resources.forgot_password_send_link
import playspot.composeapp.generated.resources.forgot_password_title
import playspot.composeapp.generated.resources.forgot_password_toolbar_title

@Composable
fun ForgotPasswordScreenRoute(
    onBackPressed: () -> Unit,
    onEmailSent: () -> Unit = {},
    viewModel: ForgotPasswordViewModel = viewModel { ForgotPasswordViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ForgotPasswordEvent.EmailSent -> {
                    snackbarHostState.showSnackbar("Password reset email sent!")
                    onEmailSent()
                }
                is ForgotPasswordEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    ForgotPasswordScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackPressed = onBackPressed,
        onEmailChange = viewModel::onEmailChange,
        onSendLinkClicked = viewModel::sendResetLink
    )
}

@Composable
fun ForgotPasswordScreen(
    uiState: ForgotPasswordUiState = ForgotPasswordUiState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onBackPressed: () -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onSendLinkClicked: () -> Unit = {}
) {
    AppTheme {
        Scaffold(
            topBar = {
                TitleToolbar(
                    title = stringResource(Res.string.forgot_password_toolbar_title),
                    onBackPressed = onBackPressed
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = Padding.padding24Dp)
            ) {
                // Header Section
                HeadlineLarge(
                    modifier = Modifier.padding(top = Padding.padding32Dp),
                    text = stringResource(Res.string.forgot_password_title),
                    fontWeight = FontWeight.Bold
                )

                BodyMedium(
                    modifier = Modifier.padding(top = Padding.padding12Dp),
                    text = stringResource(Res.string.forgot_password_description),
                    color = MaterialTheme.extendedColors.textDark.copy(alpha = 0.7f)
                )

                // Email Field
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Padding.padding32Dp),
                    value = uiState.email,
                    onValueChange = onEmailChange,
                    singleLine = true,
                    staticLabelText = stringResource(Res.string.forgot_password_email_label),
                    placeHolderText = stringResource(Res.string.forgot_password_email_placeholder),
                    isError = uiState.emailError != null,
                    errorText = uiState.emailError,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Email,
                            contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Email
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onSendLinkClicked() }
                    )
                )

                // Send Recovery Link Button
                LargeButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Padding.padding24Dp),
                    label = stringResource(Res.string.forgot_password_send_link),
                    enabled = !uiState.isLoading,
                    isLoading = uiState.isLoading,
                    onClick = onSendLinkClicked
                )

                Spacer(modifier = Modifier.weight(1f))

                // Back to Login Link
                BodyMedium(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Padding.padding48dp)
                        .clickable { onBackPressed() }
                        .align(Alignment.CenterHorizontally),
                    text = stringResource(Res.string.forgot_password_back_to_login),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
fun ForgotPasswordScreenPreview() {
    ForgotPasswordScreen()
}
