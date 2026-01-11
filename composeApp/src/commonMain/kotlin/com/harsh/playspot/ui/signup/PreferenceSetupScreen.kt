package com.harsh.playspot.ui.signup

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.harsh.playspot.ui.core.AppTheme
import com.harsh.playspot.ui.core.BodyLarge
import com.harsh.playspot.ui.core.HeadlineLarge
import com.harsh.playspot.ui.core.LabelLarge
import com.harsh.playspot.ui.core.LargeButton
import com.harsh.playspot.ui.core.Padding
import com.harsh.playspot.ui.core.SportUi
import com.harsh.playspot.ui.core.TextLightGray
import com.harsh.playspot.ui.core.TitleMedium
import com.harsh.playspot.ui.core.TransparentToolbar
import com.harsh.playspot.ui.core.extendedColors
import com.harsh.playspot.ui.core.getSports
import com.harsh.playspot.ui.core.semiCircleCornerShape
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import playspot.composeapp.generated.resources.Res
import playspot.composeapp.generated.resources.pref_choose_sport
import playspot.composeapp.generated.resources.pref_choose_sport_clear
import playspot.composeapp.generated.resources.pref_choose_sport_continue
import playspot.composeapp.generated.resources.pref_choose_sport_desc
import playspot.composeapp.generated.resources.pref_choose_sport_select_all

@Composable
fun PreferenceSetupRoute(
    onBackPressed: () -> Unit = {},
    onContinueClicked: () -> Unit,
    viewModel: PreferenceSetupViewModel = viewModel { PreferenceSetupViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val sports = getSports()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is PreferenceSetupEvent.SaveSuccess -> onContinueClicked()
                is PreferenceSetupEvent.SaveError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    PreferenceSetupScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        sports = sports,
        onBackPressed = onBackPressed,
        onSportToggle = viewModel::toggleSport,
        onSelectAll = { viewModel.selectAll(sports) },
        onClearAll = viewModel::clearAll,
        onContinueClicked = viewModel::savePreferences
    )
}


@Composable
fun PreferenceSetupScreen(
    uiState: PreferenceSetupUiState = PreferenceSetupUiState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    sports: List<SportUi> = emptyList(),
    onBackPressed: () -> Unit = {},
    onSportToggle: (String) -> Unit = {},
    onSelectAll: () -> Unit = {},
    onClearAll: () -> Unit = {},
    onContinueClicked: () -> Unit = {}
) {
    AppTheme {
        Scaffold(
            topBar = { TransparentToolbar(onBackPressed = onBackPressed) },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
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
                    color = MaterialTheme.extendedColors.textDark
                )
                
                if (uiState.isLoadingExisting) {
                    // Show loading while fetching existing sports
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    SportPreferenceSelector(
                        sports = sports,
                        selectedSports = uiState.selectedSports,
                        onSportToggle = onSportToggle,
                        onSelectAll = onSelectAll,
                        onClearAll = onClearAll
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                LargeButton(
                    modifier = Modifier.fillMaxWidth().padding(bottom = Padding.padding16Dp),
                    label = stringResource(Res.string.pref_choose_sport_continue),
                    isLoading = uiState.isLoading,
                    enabled = uiState.selectedSports.isNotEmpty(),
                    onClick = onContinueClicked
                )
            }
        }
    }
}


@Composable
private fun SportPreferenceSelector(
    sports: List<SportUi>,
    selectedSports: Set<String>,
    onSportToggle: (String) -> Unit,
    onSelectAll: () -> Unit,
    onClearAll: () -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    
    Column(modifier = Modifier.padding(top = Padding.padding32Dp)) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            sports.forEach { sport ->
                SportPreferenceChip(
                    text = sport.name,
                    vectorImage = sport.icon,
                    isSelected = selectedSports.contains(sport.name),
                    onClick = { onSportToggle(sport.name) }
                )
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
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clickable {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        onSelectAll()
                    },
                text = stringResource(Res.string.pref_choose_sport_select_all),
                fontWeight = FontWeight.SemiBold,
                color = TextLightGray
            )

            LabelLarge(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clickable {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        onClearAll()
                    },
                text = stringResource(Res.string.pref_choose_sport_clear),
                fontWeight = FontWeight.SemiBold,
                color = TextLightGray
            )
        }
    }
}


@Composable
private fun SportPreferenceChip(
    text: String,
    vectorImage: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor =
        if (isSelected) MaterialTheme.extendedColors.selectedChipContainer else MaterialTheme.extendedColors.chipContainer
    val textColor =
        if (isSelected) MaterialTheme.extendedColors.selectedChipText else MaterialTheme.extendedColors.chipText
    val tintColor = textColor
    val ringBgColor =
        if (isSelected) Color.White.copy(alpha = 0.2f) else MaterialTheme.extendedColors.chipIconBg
    val hapticFeedback = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .clip(shape = semiCircleCornerShape)
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.extendedColors.outline
                ),
                shape = semiCircleCornerShape
            )
            .background(color = bgColor, shape = semiCircleCornerShape)

            .clickable {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
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
                    visible = isSelected,
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
