package com.example.doctor_appointment.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.doctor_appointment.presentation.ui.screens.LoginScreen
import com.example.doctor_appointment.presentation.ui.screens.ProfileScreen
import com.example.doctor_appointment.presentation.ui.screens.SignUpScreen
import com.example.doctor_appointment.presentation.ui.screens.UserTypeSelectionScreen
import com.example.doctor_appointment.presentation.viewmodel.AuthViewModel

@Composable
fun AppNavigation(
    viewModel: AuthViewModel,
    onGoogleSignInClicked: () -> Unit,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val isPatient by viewModel.isPatient.collectAsState()

    // Determine the start destination based on login status
    val startDestination = if (isLoggedIn) "profile" else "login"

    // Handle navigation when login status changes
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            // User just logged in, navigate to profile
            navController.navigate("profile") {
                // Clear the back stack so user can't go back to login
                popUpTo("login") { inclusive = true }
                launchSingleTop = true
            }
        } else {
            // User logged out, navigate to login
            navController.navigate("login") {
                // Clear the back stack
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(
                navController = navController,
                viewModel = viewModel,
                onGoogleSignInClicked = onGoogleSignInClicked
            )
        }

        composable(
            route = "signup/{isPatient}",
            arguments = listOf(navArgument("isPatient") { type = NavType.BoolType })
        ) { backStackEntry ->
            val isPatientParam = backStackEntry.arguments?.getBoolean("isPatient") ?: true
            SignUpScreen(
                navController = navController,
                viewModel = viewModel,
                isPatient = isPatientParam
            )
        }

        composable("role") {
            UserTypeSelectionScreen(navController = navController)
        }

        composable("profile") {
            ProfileScreen(
                navController = navController,
                authViewModel = viewModel,
                onLogout = {
                    // Call the logout function from viewModel
                    viewModel.logout()
                    // Also call the MainActivity logout handler
                    onLogout()
                }
            )
        }
    }
}