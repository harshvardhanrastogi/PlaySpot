package com.harsh.playspot.ui.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.harsh.playspot.ui.core.AppTheme
import com.harsh.playspot.ui.core.BodyLarge
import com.harsh.playspot.ui.core.BodySmall
import com.harsh.playspot.ui.core.HeadlineLarge
import com.harsh.playspot.ui.core.LabelLarge
import com.harsh.playspot.ui.core.Padding
import com.harsh.playspot.ui.core.ProfileAction
import com.harsh.playspot.ui.core.TextLightGray
import com.harsh.playspot.ui.core.TextMediumDark
import com.harsh.playspot.ui.core.TransparentToolbar
import com.harsh.playspot.ui.core.extendedColors
import com.harsh.playspot.ui.core.semiCircleCornerShape
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import playspot.composeapp.generated.resources.Res
import playspot.composeapp.generated.resources.ic_person_edit
import playspot.composeapp.generated.resources.pref_all_set_title
import playspot.composeapp.generated.resources.pref_set_up_complete_add_bio
import playspot.composeapp.generated.resources.pref_set_up_complete_desc
import playspot.composeapp.generated.resources.pref_set_up_complete_discover
import playspot.composeapp.generated.resources.pref_set_up_complete_do_later
import playspot.composeapp.generated.resources.pref_set_up_complete_find_games
import playspot.composeapp.generated.resources.pref_set_up_complete_note
import playspot.composeapp.generated.resources.pref_set_up_complete_profile_edit

@Composable
fun PreferenceSetupCompleteRoute(
    onDiscoverClicked: () -> Unit,
    onCompleteProfileClicked: () -> Unit
) {
    PreferenceSetupCompleteScreen(onDiscoverClicked, onCompleteProfileClicked)
}

@Composable
fun PreferenceSetupCompleteScreen(
    onDiscoverClicked: () -> Unit,
    onCompleteProfileClicked: () -> Unit
) {
    AppTheme {
        Scaffold(topBar = { TransparentToolbar { } }) { paddingValues ->
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues)
                    .padding(horizontal = Padding.padding16Dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = Padding.padding32Dp)
                        .size(100.dp)
                        .align(Alignment.CenterHorizontally).rotate(5f)
                        .background(
                            shape = RoundedCornerShape(24.dp), brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF135bec), Color(0xFF2563EB)
                                )
                            )
                        )
                ) {
                    Icon(
                        modifier = Modifier.size(56.dp).align(alignment = Alignment.Center)
                            .rotate(-5f),
                        imageVector = Icons.Filled.Celebration,
                        tint = Color.White,
                        contentDescription = null
                    )
                }

                HeadlineLarge(
                    modifier = Modifier.fillMaxWidth().padding(top = Padding.padding32Dp)
                        .padding(horizontal = Padding.padding8Dp),
                    text = stringResource(Res.string.pref_all_set_title),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                BodyLarge(
                    modifier = Modifier.fillMaxWidth().padding(
                        start = Padding.padding4Dp, top = Padding.padding24Dp
                    ),
                    text = stringResource(Res.string.pref_set_up_complete_desc),
                    color = TextMediumDark,
                    textAlign = TextAlign.Center
                )

                ProfileAction(
                    modifier = Modifier.padding(top = Padding.padding24Dp),
                    icon = Icons.Filled.Explore,
                    iconTint = MaterialTheme.colorScheme.primary,
                    iconBgColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    text = stringResource(Res.string.pref_set_up_complete_discover),
                    desc = stringResource(Res.string.pref_set_up_complete_find_games),
                    trailing = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline
                        )
                    },
                    onActionClick = onDiscoverClicked
                )
                ProfileAction(
                    modifier = Modifier.padding(top = Padding.padding16Dp),
                    icon = vectorResource(Res.drawable.ic_person_edit),
                    iconTint = MaterialTheme.extendedColors.purple,
                    iconBgColor = MaterialTheme.extendedColors.purpleContainer,
                    text = stringResource(Res.string.pref_set_up_complete_profile_edit),
                    desc = stringResource(Res.string.pref_set_up_complete_add_bio),
                    trailing = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline
                        )
                    },
                    onActionClick = onCompleteProfileClicked
                )
                FooterNote()
                Spacer(modifier = Modifier.weight(1f))
                LabelLarge(
                    modifier = Modifier.minimumInteractiveComponentSize()
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = Padding.padding32Dp),
                    text = stringResource(Res.string.pref_set_up_complete_do_later),
                    fontWeight = FontWeight.SemiBold,
                    color = TextLightGray
                )
            }
        }
    }
}

@Composable
fun FooterNote() {
    Box(
        modifier = Modifier.fillMaxWidth()
            .padding(top = Padding.padding24Dp)
            .border(width = 1.dp, color = Color(0xFFDBEAFE), shape = semiCircleCornerShape)
            .background(
                shape = semiCircleCornerShape, color = Color(0xFFE3F2FD).copy(alpha = 0.5f)
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = Padding.padding16Dp, vertical = Padding.padding12Dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            BodySmall(
                modifier = Modifier.padding(start = Padding.padding8Dp),
                text = stringResource(Res.string.pref_set_up_complete_note),
                color = Color(0xFF475569)
            )
        }
    }
}

@Preview
@Composable
fun PreferenceSetupCompleteScreenPreview() {
    PreferenceSetupCompleteScreen({}, {})
}