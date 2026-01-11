package com.harsh.playspot.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.harsh.playspot.ui.core.AppTheme
import com.harsh.playspot.ui.core.BodyLarge
import com.harsh.playspot.ui.core.BodyMedium
import com.harsh.playspot.ui.core.HeadlineLarge
import com.harsh.playspot.ui.core.LabelLarge
import com.harsh.playspot.ui.core.LargeButton
import com.harsh.playspot.ui.core.Padding
import com.harsh.playspot.ui.core.ProfileAction
import com.harsh.playspot.ui.core.Text2StyleToken
import com.harsh.playspot.ui.core.TextLightGray
import com.harsh.playspot.ui.core.TextLighterGray
import com.harsh.playspot.ui.core.TextMediumDark
import com.harsh.playspot.ui.core.TitleMedium
import com.harsh.playspot.ui.core.TransparentToolbar
import com.harsh.playspot.ui.core.clickWithFeedback
import com.harsh.playspot.ui.core.extendedColors
import com.harsh.playspot.ui.core.semiCircleCornerShape
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import playspot.composeapp.generated.resources.Res
import playspot.composeapp.generated.resources.pref_set_up_complete_do_later
import playspot.composeapp.generated.resources.pref_set_up_finish_profile
import playspot.composeapp.generated.resources.pref_set_up_finish_profile_bio
import playspot.composeapp.generated.resources.pref_set_up_finish_profile_bio_optional
import playspot.composeapp.generated.resources.pref_set_up_finish_profile_desc
import playspot.composeapp.generated.resources.pref_set_up_finish_profile_play_time
import playspot.composeapp.generated.resources.pref_set_up_finish_profile_play_time_title
import playspot.composeapp.generated.resources.pref_set_up_finish_profile_save
import playspot.composeapp.generated.resources.pref_set_up_finish_profile_skill_level
import playspot.composeapp.generated.resources.pref_set_up_finish_profile_skill_level_casual
import playspot.composeapp.generated.resources.pref_set_up_finish_profile_skill_level_casual_desc
import playspot.composeapp.generated.resources.pref_set_up_finish_profile_skill_level_competitive
import playspot.composeapp.generated.resources.pref_set_up_finish_profile_skill_level_competitive_desc
import playspot.composeapp.generated.resources.pref_set_up_finish_profile_skill_level_pro
import playspot.composeapp.generated.resources.pref_set_up_finish_profile_skill_level_pro_desc

@Composable
fun PersonalDetailsScreenRoute(
    onSaveClicked: () -> Unit,
    onBackPressed: () -> Unit,
    onSkipClicked: () -> Unit,
    viewModel: PersonalDetailsViewModel = viewModel { PersonalDetailsViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val bioTextFieldState = rememberTextFieldState(initialText = uiState.bio)

    // Sync bio text field with ViewModel
    LaunchedEffect(bioTextFieldState) {
        snapshotFlow { bioTextFieldState.text.toString() }
            .collectLatest { text ->
                viewModel.onBioChange(text)
            }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is PersonalDetailsEvent.SaveSuccess -> onSaveClicked()
                is PersonalDetailsEvent.SaveError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    PersonalDetailsScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        bioTextFieldState = bioTextFieldState,
        onBackPressed = onBackPressed,
        onSkipClicked = onSkipClicked,
        onSkillLevelChange = viewModel::onSkillLevelChange,
        onPlayTimeToggle = viewModel::togglePlayTime,
        onSaveClicked = viewModel::saveProfile
    )
}


@Composable
fun PersonalDetailsScreen(
    uiState: PersonalDetailsUiState = PersonalDetailsUiState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    bioTextFieldState: TextFieldState = rememberTextFieldState(),
    onBackPressed: () -> Unit = {},
    onSkipClicked: () -> Unit = {},
    onSkillLevelChange: (String) -> Unit = {},
    onPlayTimeToggle: (String) -> Unit = {},
    onSaveClicked: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    AppTheme {
        Scaffold(
            topBar = { TransparentToolbar(onBackPressed = onBackPressed) },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues)
                    .padding(horizontal = Padding.padding16Dp)
                    .verticalScroll(state = scrollState),
            ) {
                HeadlineLarge(
                    modifier = Modifier.padding(top = Padding.padding24Dp),
                    text = stringResource(Res.string.pref_set_up_finish_profile),
                    fontWeight = FontWeight.Bold,
                )
                BodyLarge(
                    modifier = Modifier.padding(
                        start = Padding.padding4Dp, top = Padding.padding12Dp
                    ),
                    text = stringResource(Res.string.pref_set_up_finish_profile_desc),
                    color = TextMediumDark
                )
                UserBioTextField(bioTextFieldState)

                UserSkillLevelSelection(
                    selectedSkillLevel = uiState.skillLevel,
                    onSkillLevelChange = onSkillLevelChange
                )

                UserPlayTimeSelection(
                    selectedPlayTimes = uiState.selectedPlayTimes,
                    onPlayTimeToggle = onPlayTimeToggle
                )

                LargeButton(
                    modifier = Modifier.fillMaxWidth().padding(top = 72.dp),
                    label = stringResource(Res.string.pref_set_up_finish_profile_save),
                    isLoading = uiState.isLoading,
                    onClick = onSaveClicked
                )
                LabelLarge(
                    modifier = Modifier.minimumInteractiveComponentSize()
                        .clickWithFeedback(HapticFeedbackType.Confirm) {
                            onSkipClicked()
                        }
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = Padding.padding32Dp),
                    text = stringResource(Res.string.pref_set_up_complete_do_later),
                    fontWeight = FontWeight.SemiBold,
                    color = TextLightGray
                )
            }
        }
    }
}

@Composable
fun UserSkillLevelSelection(
    selectedSkillLevel: String,
    onSkillLevelChange: (String) -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    val skillLevels = getSkillLevelStates()
    
    BodyLarge(
        modifier = Modifier.padding(
            start = Padding.padding4Dp, top = Padding.padding24Dp
        ),
        text = stringResource(Res.string.pref_set_up_finish_profile_skill_level),
        color = MaterialTheme.extendedColors.textDark,
        fontWeight = FontWeight.Bold
    )

    Column(
        modifier = Modifier.padding(top = Padding.padding24Dp),
        verticalArrangement = Arrangement.spacedBy(Padding.padding12Dp)
    ) {
        skillLevels.forEach { levelState ->
            val isSelected = levelState.skillLevel.name == selectedSkillLevel
            val onClick = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
                onSkillLevelChange(levelState.skillLevel.name)
            }
            ProfileAction(
                icon = levelState.skillLevel.iconRes,
                iconTint = levelState.skillLevel.iconTint(),
                iconBgColor = levelState.skillLevel.iconBgColor(),
                text = stringResource(levelState.title),
                desc = stringResource(levelState.desc),
                trailing = {
                    RadioButton(selected = isSelected, onClick = onClick)
                },
                onActionClick = onClick
            )
        }
    }
}


@Composable
fun UserBioTextField(bioTextFieldState: TextFieldState) {
    val hapticFeedback = LocalHapticFeedback.current
    val charLimit = 200
    val shape = RoundedCornerShape(size = 22.dp)
    
    BodyMedium(
        modifier = Modifier.padding(
            start = Padding.padding4Dp, top = Padding.padding24Dp
        ),
        text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.extendedColors.textDark,
                    fontWeight = FontWeight.ExtraBold
                )
            ) {
                append(stringResource(Res.string.pref_set_up_finish_profile_bio))
            }
            append(" ")
            withStyle(style = SpanStyle(color = TextLighterGray)) {
                append(stringResource(Res.string.pref_set_up_finish_profile_bio_optional))
            }
        },
        color = TextMediumDark
    )
    Box(
        modifier = Modifier
            .padding(top = Padding.padding24Dp)
            .fillMaxWidth()
            .height(180.dp)
            .background(
                color = MaterialTheme.extendedColors.widgetBg,
                shape = shape
            )
    ) {
        BasicTextField(
            state = bioTextFieldState,
            modifier = Modifier
                .fillMaxSize()
                .padding(Padding.padding16Dp)
                .padding(bottom = 20.dp)
                .onFocusChanged { state ->
                    if (state.isFocused) {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                },
            textStyle = Text2StyleToken.BodyMedium.toTextStyle()
                .copy(color = MaterialTheme.extendedColors.textDark),
            decorator = { innerTextField ->
                Box {
                    if (bioTextFieldState.text.isEmpty()) {
                        BodyMedium(
                            text = "I usually play as a striker, looking for casual games on weekends...",
                            color = TextLighterGray
                        )
                    }
                    innerTextField()
                }
            }
        )

        Text(
            text = "${bioTextFieldState.text.length} / $charLimit",
            style = Text2StyleToken.LabelSmall.toTextStyle(),
            color = TextLighterGray.copy(alpha = 0.7f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 12.dp)
        )
    }
}

@Composable
fun UserPlayTimeSelection(
    selectedPlayTimes: Set<String>,
    onPlayTimeToggle: (String) -> Unit
) {
    val playTimeOptions = stringArrayResource(Res.array.pref_set_up_finish_profile_play_time)
    val hapticFeedback = LocalHapticFeedback.current
    
    BodyLarge(
        modifier = Modifier.padding(
            start = Padding.padding4Dp, top = Padding.padding24Dp
        ),
        text = stringResource(Res.string.pref_set_up_finish_profile_play_time_title),
        color = MaterialTheme.extendedColors.textDark,
        fontWeight = FontWeight.Bold
    )

    FlowRow(
        modifier = Modifier.padding(top = Padding.padding24Dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Padding.padding16Dp),
        verticalArrangement = Arrangement.spacedBy(Padding.padding12Dp)
    ) {
        playTimeOptions.forEach { playTime ->
            val isSelected = selectedPlayTimes.contains(playTime)
            FilterChip(
                selected = isSelected,
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onPlayTimeToggle(playTime)
                },
                label = {
                    TitleMedium(
                        modifier = Modifier.padding(
                            horizontal = Padding.padding8Dp,
                            vertical = Padding.padding16Dp
                        ),
                        text = playTime,
                        color = Color.Unspecified,
                        fontWeight = FontWeight.W500,
                    )
                },
                shape = semiCircleCornerShape,
                border = null,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.extendedColors.chipContainer,
                    selectedContainerColor = MaterialTheme.extendedColors.selectedChipContainer,
                    labelColor = MaterialTheme.extendedColors.chipText,
                    selectedLabelColor = MaterialTheme.extendedColors.selectedChipText
                )
            )
        }
    }
}

@Preview
@Composable
fun PersonalDetailsScreenPreview() {
    PersonalDetailsScreen()
}


sealed class SkillLevel(val name: String) {
    abstract val iconRes: ImageVector

    @Composable
    abstract fun iconTint(): Color

    @Composable
    abstract fun iconBgColor(): Color

    object Casual : SkillLevel("Casual") {
        override val iconRes: ImageVector
            get() = Icons.Filled.EmojiEvents

        @Composable
        override fun iconTint(): Color {
            return MaterialTheme.extendedColors.green
        }

        @Composable
        override fun iconBgColor(): Color {
            return MaterialTheme.extendedColors.greenContainer
        }
    }

    object Competitive : SkillLevel("Competitive") {
        override val iconRes: ImageVector
            get() = Icons.Filled.SportsScore

        @Composable
        override fun iconTint(): Color {
            return MaterialTheme.extendedColors.orange
        }

        @Composable
        override fun iconBgColor(): Color {
            return MaterialTheme.extendedColors.orangeContainer
        }
    }

    object Pro : SkillLevel("Pro") {
        override val iconRes: ImageVector
            get() = Icons.Filled.WorkspacePremium

        @Composable
        override fun iconTint(): Color {
            return MaterialTheme.extendedColors.purple
        }

        @Composable
        override fun iconBgColor(): Color {
            return MaterialTheme.extendedColors.purpleContainer
        }
    }
}

data class SkillLevelState(
    val skillLevel: SkillLevel,
    val title: StringResource,
    val desc: StringResource
)

fun getSkillLevelStates(): List<SkillLevelState> {
    return listOf(
        SkillLevelState(
            skillLevel = SkillLevel.Casual,
            title = Res.string.pref_set_up_finish_profile_skill_level_casual,
            desc = Res.string.pref_set_up_finish_profile_skill_level_casual_desc
        ),
        SkillLevelState(
            skillLevel = SkillLevel.Competitive,
            title = Res.string.pref_set_up_finish_profile_skill_level_competitive,
            desc = Res.string.pref_set_up_finish_profile_skill_level_competitive_desc
        ),
        SkillLevelState(
            skillLevel = SkillLevel.Pro,
            title = Res.string.pref_set_up_finish_profile_skill_level_pro,
            desc = Res.string.pref_set_up_finish_profile_skill_level_pro_desc
        ),
    )
}
