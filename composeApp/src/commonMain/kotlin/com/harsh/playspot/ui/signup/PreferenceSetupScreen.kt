package com.harsh.playspot.ui.signup

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.SportsBaseball
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.SportsGolf
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.SportsVolleyball
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harsh.playspot.ui.core.AppTheme
import com.harsh.playspot.ui.core.BodyLarge
import com.harsh.playspot.ui.core.HeadlineLarge
import com.harsh.playspot.ui.core.LabelLarge
import com.harsh.playspot.ui.core.LargeButton
import com.harsh.playspot.ui.core.Padding
import com.harsh.playspot.ui.core.TextLightGray
import com.harsh.playspot.ui.core.TitleMedium
import com.harsh.playspot.ui.core.TransparentToolbar
import com.harsh.playspot.ui.core.extendedColors
import com.harsh.playspot.ui.core.semiCircleCornerShape
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import playspot.composeapp.generated.resources.Res
import playspot.composeapp.generated.resources.ic_badminton
import playspot.composeapp.generated.resources.pref_choose_sport
import playspot.composeapp.generated.resources.pref_choose_sport_clear
import playspot.composeapp.generated.resources.pref_choose_sport_continue
import playspot.composeapp.generated.resources.pref_choose_sport_desc
import playspot.composeapp.generated.resources.pref_choose_sport_select_all
import playspot.composeapp.generated.resources.pref_sports_list

@Composable
fun PreferenceSetupRoute(onContinueClicked: () -> Unit) {
    PreferenceSetupScreen(onContinueClicked)
}


@Composable
fun PreferenceSetupScreen(onContinueClicked: () -> Unit = {}) {
    AppTheme {
        Scaffold(topBar = { TransparentToolbar { } }) { paddingValues ->
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues)
                    .padding(horizontal = Padding.padding16Dp)
            ) {
                HeadlineLarge(
                    modifier = Modifier.padding(top = Padding.padding24Dp),
                    text = stringResource(Res.string.pref_choose_sport),
                    fontWeight = FontWeight.Bold,
                )
                BodyLarge(
                    modifier = Modifier.padding(
                        start = Padding.padding4Dp, top = Padding.padding12Dp
                    ),
                    text = stringResource(Res.string.pref_choose_sport_desc),
                    color = MaterialTheme.colorScheme.extendedColors.textDark
                )
                SportPreferenceSelector()
                Spacer(modifier = Modifier.weight(1f))
                LargeButton(
                    modifier = Modifier.fillMaxWidth().padding(bottom = Padding.padding16Dp),
                    label = stringResource(Res.string.pref_choose_sport_continue),
                    onClick = onContinueClicked
                )
            }
        }
    }
}


@Composable
private fun SportPreferenceSelector() {
    val sports = stringArrayResource(Res.array.pref_sports_list)
    Column(modifier = Modifier.padding(top = Padding.padding32Dp)) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            sports.forEach { sport ->
                SportPreferenceChip(text = sport, getSportIcon(sport))
            }
        }
        Row(
            modifier = Modifier
                .padding(bottom = Padding.padding12Dp)
                .padding(horizontal = Padding.padding16Dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LabelLarge(
                modifier = Modifier.minimumInteractiveComponentSize(),
                text = stringResource(Res.string.pref_choose_sport_select_all),
                fontWeight = FontWeight.SemiBold,
                color = TextLightGray
            )

            LabelLarge(
                modifier = Modifier.minimumInteractiveComponentSize(),
                text = stringResource(Res.string.pref_choose_sport_clear),
                fontWeight = FontWeight.SemiBold,
                color = TextLightGray
            )
        }
    }
}

@Composable
private fun getSportIcon(sport: String): ImageVector {
    return when (sport) {
        "Football" -> Icons.Filled.SportsSoccer
        "Basketball" -> Icons.Filled.SportsBasketball
        "Tennis" -> Icons.Filled.SportsTennis
        "Running" -> Icons.AutoMirrored.Filled.DirectionsRun
        "Volleyball" -> Icons.Filled.SportsVolleyball
        "Swimming" -> Icons.Filled.Pool
        "Cycling" -> Icons.AutoMirrored.Filled.DirectionsBike
        "Cricket" -> Icons.Filled.SportsCricket
        "Baseball" -> Icons.Filled.SportsBaseball
        "Badminton" -> vectorResource(Res.drawable.ic_badminton)
        "Gym" -> Icons.Filled.FitnessCenter
        "Golf" -> Icons.Filled.SportsGolf
        else -> throw IllegalStateException()
    }
}


@Composable
private fun SportPreferenceChip(text: String = "Football", vectorImage: ImageVector) {
    val state: MutableState<SportChipState> = remember {
        mutableStateOf(SportChipState.UnSelected)
    }
    val bgColor =
        if (state.value.isSelected) MaterialTheme.colorScheme.extendedColors.selectedChipContainer else MaterialTheme.colorScheme.extendedColors.chipContainer
    val textColor =
        if (state.value.isSelected) MaterialTheme.colorScheme.extendedColors.selectedChipText else MaterialTheme.colorScheme.extendedColors.chipText
    val tintColor = textColor
    val ringBgColor =
        if (state.value.isSelected) Color.White.copy(alpha = 0.2f) else MaterialTheme.colorScheme.extendedColors.chipIconBg
    val hapticFeedback = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .clip(shape = semiCircleCornerShape)
            .background(color = bgColor, shape = semiCircleCornerShape)
            .clickable {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                state.value =
                    if (state.value.isSelected) SportChipState.UnSelected else SportChipState.Selected
            }) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.padding(vertical = 4.dp).padding(start = 4.dp).size(38.dp)
                    .background(color = ringBgColor, shape = semiCircleCornerShape)
            ) {
                Icon(
                    modifier = Modifier.padding(8.dp).size(24.dp),
                    imageVector = vectorImage,
                    contentDescription = null,
                    tint = tintColor
                )
            }
            TitleMedium(
                modifier = Modifier.padding(horizontal = Padding.padding16Dp),
                text = text,
                color = textColor,
                fontWeight = FontWeight.W500,
            )

            Box(
                modifier = Modifier.padding(end = 8.dp).size(24.dp)
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
        }
    }
}

sealed class SportChipState(val isSelected: Boolean) {
    data object Selected : SportChipState(true)
    data object UnSelected : SportChipState(false)
}

@Preview
@Composable
fun PreferenceSetupPreview() {
    PreferenceSetupScreen()
}