package com.example.doctor_appointment.presentation.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doctor_appointment.data.model.DoctorProfile
import com.example.doctor_appointment.data.model.PatientProfile
import com.example.doctor_appointment.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
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

    fun signupPatient(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        address: String?,
        phone: String?,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (authRepository.patientSignUp(
                        firstName,
                        lastName,
                        email,
                        password,
                        address,
                        phone
                    )
                ) {
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

    fun signupDoctor(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        address: String,
        specialty: Int,
        phone: String?,
        contact_email: String?,
        contact_phone: String?,
        onResult: (Boolean) -> Unit,

        ) {
        viewModelScope.launch {
            try {
                if (authRepository.doctorSignup(
                        firstName,
                        lastName,
                        email,
                        password,
                        address,
                        specialty,
                        phone,
                        contact_email,
                        contact_phone
                    )
                ) {
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
                var success = false
                var response = password?.let { authRepository.patientLogin(email, it) }
                if (response != null) {
                    if (response.isSuccessful) {
                        success = true
                    } else {
                        response = password?.let { authRepository.doctorLogin(email, it) }
                        if (response != null) {
                            if (response.isSuccessful) {
                                success = true
                            }
                        }
                    }
                    if (!success) {
                        if (response != null) {
                            Log.e(
                                "Login",
                                "Failed: ${response.code()} - ${response.errorBody()?.string()}"
                            )
                        }
                        onResult(false, null, true)
                    }
                    val token = response?.body()?.access_token
                    val isPatient = response?.body()?.isPatient
                    Log.d("Login", "Success! Token = $token, \nyour role is $isPatient")
                    onResult(true, token, isPatient)
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
                    } catch (e: Exception) {
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

    suspend fun getId(): Int? {
        val response = authRepository.getProfile()
        if (response != null) {
            if (response.isSuccessful) {
                val isPatient = response.body()?.isPatient
                val profile = response.body()?.profile
                if (profile != null) {
                    return profile.get("id").asInt
                }
            }
        }
        return null
    }

    suspend fun getProfile(): Any? {
        val response = authRepository.getProfile()
        if (response?.isSuccessful == true) {
            val body = response.body()
            val profile = body?.profile
            val isPatient = body?.isPatient

            if (profile == null) return null

            val gson = Gson()

            return if (isPatient == true) {
                val patientProfile = gson.fromJson(profile, PatientProfile::class.java)
                patientProfile
            } else {
                val doctorProfile = gson.fromJson(profile, DoctorProfile::class.java)
                doctorProfile
            }
        }
        return null
    }

}



