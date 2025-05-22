package com.example.doctor_appointment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.doctor_appointment.data.repository.AuthRepository
import com.example.doctor_appointment.presentation.ui.navigation.AppNavigation
import com.example.doctor_appointment.presentation.ui.screens.LoginScreen
import com.example.doctor_appointment.presentation.ui.screens.ProfileScreen
import com.example.doctor_appointment.presentation.ui.screens.SignUpScreen
import com.example.doctor_appointment.presentation.ui.screens.UserTypeSelectionScreen
import com.example.doctor_appointment.presentation.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException

class MainActivity : ComponentActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private lateinit var authRepository: AuthRepository
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize repository and view model
        authRepository = AuthRepository(this@MainActivity)
        authViewModel = AuthViewModel(authRepository)

        // Setup Google Sign-In options (use your Web client ID)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Launcher for Google Sign-In intent result
        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    Log.d("GoogleSignIn", "ID Token received: $idToken")
                    authViewModel.signInWithGoogle(idToken)
                } else {
                    Log.e("GoogleSignIn", "ID Token is null")
                }
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Sign-in failed with code: ${e.statusCode}", e)
            }
        }

        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                AppNavigation(
                    viewModel = authViewModel,
                    onGoogleSignInClicked = {
                        Log.d("GoogleSignIn", "Google Sign-In button clicked")
                        startGoogleSignIn()
                    },
                    onLogout = {
                        handleLogout()
                    }
                )
            }
        }
    }

    private fun startGoogleSignIn() {
        try {
            // Sign out first to ensure account selection
            googleSignInClient.signOut().addOnCompleteListener {
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            }
        } catch (e: Exception) {
            Log.e("GoogleSignIn", "Error starting Google Sign-In", e)
        }
    }

    private fun handleLogout() {
        // Sign out from Google
        googleSignInClient.signOut().addOnCompleteListener {
            Log.d("Logout", "Google sign out completed")
        }

        // Clear Google Sign-In cache
        googleSignInClient.revokeAccess().addOnCompleteListener {
            Log.d("Logout", "Google access revoked")
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is already signed in with Google
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null && authViewModel.isLoggedIn.value) {
            Log.d("GoogleSignIn", "User already signed in: ${account.email}")
        }
    }
}