package com.harsh.playspot.ui.navigtion

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.harsh.playspot.ui.events.CreateEventScreenRoute
import com.harsh.playspot.ui.events.EventDetailsScreenRoute
import com.harsh.playspot.ui.home.HomeScreenRoute
import com.harsh.playspot.ui.login.ForgotPasswordScreenRoute
import com.harsh.playspot.ui.login.LoginScreenRoute
import com.harsh.playspot.ui.profile.AddProfilePictureScreenRoute
import com.harsh.playspot.ui.profile.PersonalDetailsScreenRoute
import com.harsh.playspot.ui.signup.GenderSelectionScreenRoute
import com.harsh.playspot.ui.signup.LocationPermissionScreenRoute
import com.harsh.playspot.ui.signup.PreferenceSetupCompleteRoute
import com.harsh.playspot.ui.signup.PreferenceSetupRoute
import com.harsh.playspot.ui.signup.SignupScreenRoute
import com.harsh.playspot.util.DeepLinkHandler
import kotlinx.serialization.Serializable

@Composable
fun NavigationRoutes(
    hasUserSession: Boolean,
    deepLinkUri: String? = null,
    onDeepLinkHandled: () -> Unit = {},
    onBackPressed: () -> Unit
) {
    val navController = rememberNavController()
    val startScreen = if (hasUserSession) "Route.Home?defaultTab={defaultTab}" else "Route.Login?passwordReset={passwordReset}"
    
    // Handle deeplink from Android (passed as parameter)
    LaunchedEffect(deepLinkUri) {
        deepLinkUri?.let { uri ->
            handleDeepLinkNavigation(uri, hasUserSession, navController)
            onDeepLinkHandled()
        }
    }
    
    // Handle deeplink from iOS (via shared DeepLinkHandler)
    val pendingDeepLink by DeepLinkHandler.pendingDeepLink.collectAsState()
    LaunchedEffect(pendingDeepLink) {
        pendingDeepLink?.let { uri ->
            handleDeepLinkNavigation(uri, hasUserSession, navController)
            DeepLinkHandler.consumeDeepLink()
        }
    }
    NavHost(navController = navController, startDestination = startScreen, enterTransition = {
        slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = tween(700)
        )
    }, exitTransition = {
        slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = tween(700)
        )
    }, popEnterTransition = {
        slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = tween(700)
        )
    }, popExitTransition = {
        slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = tween(700)
        )
    }) {
        composable("Route.SignUp") {
            SignupScreenRoute(
                onBackPressed = { navController.popBackStack() },
                onLoginClicked = { navController.navigate("Route.Login") },
                onSignUpSuccess = {
                    navController.navigate("Route.GenderSelection")
                }
            )
        }

        composable("Route.GenderSelection") {
            GenderSelectionScreenRoute(
                onCancelOnboarding = {
                    // Navigate to home and skip remaining onboarding
                    navController.navigate("Route.Home") {
                        popUpTo("Route.Login") { inclusive = true }
                    }
                },
                onContinue = {
                    navController.navigate("Route.SportPreference") {
                        popUpTo("Route.GenderSelection") { inclusive = true }
                    }
                },
                onSkip = {
                    navController.navigate("Route.SportPreference") {
                        popUpTo("Route.GenderSelection") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "Route.Login?passwordReset={passwordReset}",
            arguments = listOf(
                navArgument("passwordReset") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val passwordResetSuccess = backStackEntry.arguments?.getBoolean("passwordReset") ?: false
            LoginScreenRoute(
                onBackPressed = onBackPressed,
                onSignUpClicked = { navController.navigate("Route.SignUp") },
                onForgotPasswordClicked = { navController.navigate("Route.ForgotPassword") },
                onLoginSuccess = {
                    navController.navigate("Route.Home") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                showPasswordResetSuccess = passwordResetSuccess
            )
        }

        composable("Route.ForgotPassword") {
            ForgotPasswordScreenRoute(
                onBackPressed = { navController.popBackStack() },
                onEmailSent = { navController.popBackStack() }
            )
        }

        composable(route = "Route.SportPreference") {
            PreferenceSetupRoute(
                onBackPressed = { navController.popBackStack() },
                onContinueClicked = {
                    navController.navigate("Route.LocationPermission")
                }
            )
        }

        composable(route = "Route.LocationPermission") {
            LocationPermissionScreenRoute(
                onContinue = {
                    navController.navigate("Route.SportPreferenceComplete") {
                        popUpTo("Route.LocationPermission") { inclusive = true }
                    }
                },
                onSkip = {
                    navController.navigate("Route.SportPreferenceComplete") {
                        popUpTo("Route.LocationPermission") { inclusive = true }
                    }
                }
            )
        }

        composable(route = "Route.SportPreferenceComplete") {
            PreferenceSetupCompleteRoute(
                onBackPressed = { navController.popBackStack() },
                onDiscoverClicked = { navController.navigate("Route.Home?defaultTab=1") {
                    popUpTo("Route.Login") { inclusive = true }
                } },
                onCompleteProfileClicked = {
                    navController.navigate("Route.FinishProfile")
                }
            )
        }

        composable(route = "Route.FinishProfile") {
            PersonalDetailsScreenRoute(
                onBackPressed = { navController.popBackStack() },
                onSaveClicked = { navController.navigate("Route.AddProfilePicture") },
                onSkipClicked = { navController.navigate("Route.AddProfilePicture") }
            )
        }

        composable("Route.AddProfilePicture") {
            AddProfilePictureScreenRoute(
                onBackPressed = { navController.popBackStack() },
                onSkipClicked = { navController.navigate("Route.Home") },
                onSaveClicked = { navController.navigate("Route.Home") }
            )
        }

        composable("Route.Home?defaultTab={defaultTab}") { backStackEntry ->
            val openOrganizingEvents by backStackEntry.savedStateHandle.getStateFlow(
                "openOrganizing",
                false
            ).collectAsState()
            LaunchedEffect(openOrganizingEvents) {
                if (openOrganizingEvents) {
                    backStackEntry.savedStateHandle["openOrganizing"] = false
                }
            }
            val defaultTab = backStackEntry.arguments?.getString("defaultTab")?.toInt() ?: 0
            HomeScreenRoute(
                defaultBottomTab = defaultTab,
                openOrganizingEvents = openOrganizingEvents,
                onLogoutSuccess = {
                    navController.navigate("Route.Login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onAddSportClicked = {
                    navController.navigate("Route.EditSports")
                },
                onCreateEventClicked = {
                    navController.navigate("Route.CreateEvent")
                },
                onEditPictureClicked = {
                    navController.navigate("Route.EditProfilePicture")
                },
                onEditEventClicked = { eventId ->
                    navController.navigate("Route.EditEvent/$eventId")
                },
                onEventClick = { eventId ->
                    navController.navigate("Route.EventDetails/$eventId")
                }
            )
        }

        composable("Route.EditProfilePicture") {
            AddProfilePictureScreenRoute(
                onBackPressed = { navController.popBackStack() },
                onSkipClicked = { navController.popBackStack() },
                onSaveClicked = { navController.popBackStack() }
            )
        }

        composable("Route.EditSports") {
            PreferenceSetupRoute(
                onBackPressed = { navController.popBackStack() },
                onContinueClicked = { navController.popBackStack() }
            )
        }

        composable("Route.CreateEvent") {
            CreateEventScreenRoute(
                onBackPressed = { navController.popBackStack() },
                onEventCreated = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "openOrganizing",
                        true
                    )
                    navController.popBackStack()
                }
            )
        }

        composable("Route.EditEvent/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            CreateEventScreenRoute(
                onBackPressed = { navController.popBackStack() },
                onEventCreated = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "openOrganizing",
                        true
                    )
                    navController.popBackStack()
                },
                eventId = eventId
            )
        }

        composable("Route.EventDetails/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailsScreenRoute(
                eventId = eventId,
                onBackPressed = { navController.popBackStack() }
            )
        }
    }
}

@Serializable
sealed class Route {
    @Serializable
    data object Splash : Route()

    @Serializable
    data object Login : Route()

    @Serializable
    data object SignUp : Route()

    data object SportPreference : Route()
}

/**
 * Handle deeplink navigation
 * Supports:
 * - Event details: playspot://event/{eventId} or https://playspot.app/event/{eventId}
 * - Password reset complete: playspot://password-reset-complete or https://playspot.app/password-reset-complete
 */
private fun handleDeepLinkNavigation(
    uri: String,
    hasUserSession: Boolean,
    navController: NavController
) {
    // Check for password reset completion link first
    if (DeepLinkHandler.isPasswordResetComplete(uri)) {
        // Navigate to login with password reset success flag
        navController.navigate("Route.Login?passwordReset=true") {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
        return
    }
    
    // Handle event deep links
    val eventId = DeepLinkHandler.parseEventId(uri)
    if (eventId != null && hasUserSession) {
        // Navigate to event details
        navController.navigate("Route.EventDetails/$eventId") {
            launchSingleTop = true
        }
    }
}
