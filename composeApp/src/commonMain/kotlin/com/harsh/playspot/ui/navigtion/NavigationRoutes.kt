package com.harsh.playspot.ui.navigtion

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.harsh.playspot.ui.events.CreateEventScreenRoute
import com.harsh.playspot.ui.home.HomeScreenRoute
import com.harsh.playspot.ui.login.LoginScreenRoute
import com.harsh.playspot.ui.profile.AddProfilePictureScreenRoute
import com.harsh.playspot.ui.profile.PersonalDetailsScreenRoute
import com.harsh.playspot.ui.signup.PreferenceSetupCompleteRoute
import com.harsh.playspot.ui.signup.PreferenceSetupRoute
import com.harsh.playspot.ui.signup.SignupScreenRoute
import kotlinx.serialization.Serializable

@Composable
fun NavigationRoutes(hasUserSession: Boolean, onBackPressed: () -> Unit) {
    val navController = rememberNavController()
    val startScreen = if (hasUserSession) "Route.Home" else "Route.Login"
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
                    navController.navigate("Route.SportPreference")
                }
            )
        }

        composable("Route.Login") {
            LoginScreenRoute(
                onBackPressed = onBackPressed,
                onSignUpClicked = { navController.navigate("Route.SignUp") },
                onLoginSuccess = { navController.navigate("Route.Home") }
            )
        }

        composable(route = "Route.SportPreference") {
            PreferenceSetupRoute(
                onBackPressed = { navController.popBackStack() },
                onContinueClicked = { 
                    navController.navigate("Route.SportPreferenceComplete") 
                }
            )
        }

        composable(route = "Route.SportPreferenceComplete") {
            PreferenceSetupCompleteRoute(
                onBackPressed = { navController.popBackStack() },
                onDiscoverClicked = { navController.navigate("Route.Home") },
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

        composable("Route.Home") {
            HomeScreenRoute(
                onLogoutSuccess = {
                    navController.navigate("Route.Login") {
                        popUpTo("Route.Login") { inclusive = true }
                    }
                },
                onAddSportClicked = {
                    navController.navigate("Route.EditSports")
                },
                onCreateEventClicked = {
                    navController.navigate("Route.CreateEvent")
                }
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
                onEventCreated = { navController.popBackStack() }
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
