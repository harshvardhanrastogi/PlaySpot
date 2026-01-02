package com.harsh.playspot.ui.core

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import playspot.composeapp.generated.resources.Res
import playspot.composeapp.generated.resources.login_bg

@Composable
fun BackgroundImageScreen(
    onBackPressed: () -> Unit,
    content: @Composable ColumnScope.(PaddingValues) -> Unit
) {
    AppTheme {
        Scaffold(topBar = { TransparentToolbar(onBackPressed) }) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                Box {
                    Image(
                        modifier = Modifier.fillMaxWidth(),
                        painter = painterResource(Res.drawable.login_bg),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier.matchParentSize().background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.4f), // Start subtle
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), // Get darker
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        )
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = Padding.padding16Dp)
            ) {
                content(paddingValues)
            }
        }
    }
}


@Preview
@Composable
fun BackgroundImageScreenPreview() {
    BackgroundImageScreen({}) {
        HeadlineLarge(text = "Get Started")
    }
}