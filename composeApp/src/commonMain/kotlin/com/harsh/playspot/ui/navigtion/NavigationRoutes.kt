package com.harsh.playspot.ui.navigtion

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.harsh.playspot.ui.SplashScreen
import com.harsh.playspot.ui.login.LoginScreenRoute
import com.harsh.playspot.ui.signup.SignupScreenRoute
import kotlinx.serialization.Serializable

@Composable
fun NavigationRoutes(onBackPressed: () -> Unit) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "Route.Splash", enterTransition = {
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
        composable("Route.Splash") {
            SplashScreen {
                navController.navigate("Route.Login")
            }
        }

        composable("Route.SignUp") {
            SignupScreenRoute(
                onBackPressed = { navController.popBackStack() },
                onLoginClicked = { navController.navigate("Route.Login") })
        }

        composable("Route.Login") {
            LoginScreenRoute(onBackPressed = onBackPressed, onSignUpClicked = {
                navController.navigate("Route.SignUp")
            })
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
}