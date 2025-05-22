package com.example.doctor_appointment.presentation.ui.navigation

import androidx.compose.runtime.Composable
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
fun AppNavigation(viewModel: AuthViewModel, onGoogleSignInClicked: () -> Unit) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "login") {
        composable("login") { LoginScreen(navController, viewModel, onGoogleSignInClicked) }
        composable(
            route = "signup/{isPatient}",
            arguments = listOf(navArgument("isPatient") { type = NavType.BoolType })
        ) { backStackEntry ->
            val isPatient = backStackEntry.arguments?.getBoolean("isPatient") ?: true
            SignUpScreen(navController, viewModel, isPatient)
        }
        composable("role") { UserTypeSelectionScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
    }
}