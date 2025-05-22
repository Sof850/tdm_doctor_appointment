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

    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    lateinit var authRepository : AuthRepository
    lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authRepository = AuthRepository(this@MainActivity)
        authViewModel = AuthViewModel(authRepository)

        // Setup Google Sign-In options (use your Web client ID)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Launcher for Google Sign-In intent result
        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    authViewModel.signInWithGoogle(idToken)
                    Log.d("GoogleSignInTest", "ID Token: $idToken")
                }
            } catch (e: ApiException) {
                Log.e("GoogleSignInTest", "Sign-in failed", e)
            }
        }

        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                AppNavigation(viewModel = authViewModel, onGoogleSignInClicked = {
                        launcher.launch(googleSignInClient.signInIntent)
                    }
                )
            }
        }
    }
}


