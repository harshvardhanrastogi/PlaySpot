package com.harsh.playspot

import androidx.compose.runtime.Composable
import com.harsh.playspot.ui.navigtion.NavigationRoutes
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(onBackPressed: () -> Unit = {}) {
    NavigationRoutes(onBackPressed)
}