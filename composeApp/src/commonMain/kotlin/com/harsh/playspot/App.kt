package com.harsh.playspot

import androidx.compose.runtime.Composable
import com.harsh.playspot.ui.navigtion.NavigationRoutes
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    hasUserSession: Boolean = false,
    deepLinkUri: String? = null,
    onDeepLinkHandled: () -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    NavigationRoutes(
        hasUserSession = hasUserSession,
        deepLinkUri = deepLinkUri,
        onDeepLinkHandled = onDeepLinkHandled,
        onBackPressed = onBackPressed
    )
}