package com.example.doctor_appointment.presentation.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doctor_appointment.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository : AuthRepository) : ViewModel() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _isGoogleSignedIn = MutableStateFlow(false)
    val isGoogleSignedIn: StateFlow<Boolean> = _isGoogleSignedIn

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _isPatient = MutableStateFlow(true)
    val isPatient: StateFlow<Boolean> = _isPatient

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        object Success : LoginState()
        data class Error(val message: String) : LoginState()
    }

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        _isLoggedIn.value = authRepository.isLoggedIn()
        _isPatient.value = authRepository.getRole()
    }

    fun signup(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (authRepository.patientSignUp(firstName, lastName, email, password)) {
                    Log.d("Signup", "Successful")
                    onResult(true)
                } else {
                    Log.e("Signup", "Failed")
                    onResult(false)
                }
            } catch (e: Exception) {
                Log.e("Signup", "Exception: ${e.message}", e)
                onResult(false)
            }
        }
    }

    fun login(
        email: String,
        password: String?,
        onResult: (Boolean, String?, Boolean?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = password?.let { authRepository.patientLogin(email, it) }
                if (response != null) {
                    if (response.isSuccessful) {
                        val token = response.body()?.access_token
                        val isPatient = response.body()?.isPatient
                        Log.d("Login", "Success! Token = $token, \nyour role is $isPatient")
                        onResult(true, token, isPatient)
                    } else {
                        Log.e("Login", "Failed: ${response.code()} - ${response.errorBody()?.string()}")
                        onResult(false, null, true)
                    }
                }
            } catch (e: Exception) {
                Log.e("Login", "Exception during login: ${e.message}", e)
                onResult(false, null, true)
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    Log.d("GoogleAuth", "Firebase sign in successful: ${user?.email}")
                    try {
                        user?.email?.let {
                            login(it, null) { success, token, role ->
                                if (success) {
                                    Log.d("Login", "Success! Token = $token\nYour role is $role")
                                } else {
                                    Log.e("Login", "Failed cuz not existing account")
                                }
                            }
                        }
                    }
                    catch (e: Exception) {
                        Log.e("GoogleAuth", "Exception during login: ${e.message}", e)
                    }

                    _isGoogleSignedIn.value = true
                } else {
                    Log.e("GoogleAuth", "Firebase sign in failed", task.exception)
                    _isGoogleSignedIn.value = false
                }
            }
    }

    fun signOutGoogle() {
        firebaseAuth.signOut()
        _isGoogleSignedIn.value = false
    }

}

