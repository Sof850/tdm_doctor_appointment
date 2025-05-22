package com.example.doctor_appointment.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doctor_appointment.data.model.DoctorProfile
import com.example.doctor_appointment.data.model.PatientProfile
import com.example.doctor_appointment.data.model.SocialLinks
import com.example.doctor_appointment.data.model.WorkingHour
import com.example.doctor_appointment.data.model.WorkingHours
import com.example.doctor_appointment.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.sql.Time

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
        val isLoggedIn = authRepository.isLoggedIn()
        _isLoggedIn.value = isLoggedIn
        if (isLoggedIn) {
            _isPatient.value = authRepository.getRole()
        }
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
                    if (success) {
                        val token = response?.body()?.access_token
                        val isPatient = response?.body()?.isPatient

                        // Update login state
                        _isLoggedIn.value = true
                        if (isPatient != null) {
                            _isPatient.value = isPatient
                        }

                        Log.d("Login", "Success! Token = $token, \nyour role is $isPatient")
                        onResult(true, token, isPatient)
                    } else {
                        if (response != null) {
                            Log.e(
                                "Login",
                                "Failed: ${response.code()} - ${response.errorBody()?.string()}"
                            )
                        }
                        onResult(false, null, null)
                    }
                }
            } catch (e: Exception) {
                Log.e("Login", "Exception during login: ${e.message}", e)
                onResult(false, null, null)
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        Log.d("GoogleAuth", "Firebase sign in successful: ${user?.email}")

                        viewModelScope.launch {
                            try {
                                user?.email?.let { email ->
                                    // First, try to login with existing account
                                    login(email, idToken) { success, token, role ->
                                        if (success) {
                                            Log.d("GoogleAuth", "Login successful with existing account")
                                            _isGoogleSignedIn.value = true
                                            _isLoggedIn.value = true
                                            role?.let { _isPatient.value = it }
                                        } else {
                                            // If login fails, try to sign up as patient with Google info
                                            Log.d("GoogleAuth", "No existing account, attempting signup")
                                            val displayName = user.displayName?.split(" ") ?: listOf("", "")
                                            val firstName = displayName.getOrNull(0) ?: ""
                                            val lastName = displayName.getOrNull(1) ?: ""

                                            signupPatient(
                                                firstName = firstName,
                                                lastName = lastName,
                                                email = email,
                                                password = idToken, // Use idToken as password
                                                address = null,
                                                phone = user.phoneNumber
                                            ) { signupSuccess ->
                                                if (signupSuccess) {
                                                    Log.d("GoogleAuth", "Signup successful, now logging in")
                                                    // After successful signup, login
                                                    login(email, idToken) { loginSuccess, loginToken, loginRole ->
                                                        if (loginSuccess) {
                                                            _isGoogleSignedIn.value = true
                                                            _isLoggedIn.value = true
                                                            loginRole?.let { _isPatient.value = it }
                                                        } else {
                                                            Log.e("GoogleAuth", "Login failed after signup")
                                                            _isGoogleSignedIn.value = false
                                                        }
                                                    }
                                                } else {
                                                    Log.e("GoogleAuth", "Signup failed")
                                                    _isGoogleSignedIn.value = false
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("GoogleAuth", "Exception during Google sign in process: ${e.message}", e)
                                _isGoogleSignedIn.value = false
                            }
                        }
                    } else {
                        Log.e("GoogleAuth", "Firebase sign in failed", task.exception)
                        _isGoogleSignedIn.value = false
                    }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                // Clear local storage
                authRepository.logout()

                // Sign out from Google
                signOutGoogle()

                // Update state
                _isLoggedIn.value = false
                _isGoogleSignedIn.value = false
                _isPatient.value = true // Reset to default
                _loginState.value = LoginState.Idle

                Log.d("Logout", "User logged out successfully")
            } catch (e: Exception) {
                Log.e("Logout", "Exception during logout: ${e.message}", e)
            }
        }
    }

    fun signOutGoogle() {
        firebaseAuth.signOut()
        _isGoogleSignedIn.value = false
    }

    suspend fun getProfile(): Any? {
        val response = authRepository.getProfile()
        Log.d("Success", response?.isSuccessful.toString())

        if (response?.isSuccessful == true) {
            val body = response.body()
            val isPatient = body?.get("isPatient")?.asBoolean
            Log.d("TOKEN_LOG", "isPatient: $isPatient")

            // Safe string extraction - check for null and JsonNull
            val firstName = body?.get("first_name")?.takeIf { !it.isJsonNull }?.asString
            val lastName = body?.get("last_name")?.takeIf { !it.isJsonNull }?.asString
            val address = body?.get("address")?.takeIf { !it.isJsonNull }?.asString
            val phone = body?.get("phone")?.takeIf { !it.isJsonNull }?.asString

            if (firstName == null || lastName == null) return null

            return if (isPatient == true) {
                PatientProfile(firstName, lastName, address ?: "", phone)
            } else {
                try {
                    val specialtyId = body.get("specialty_id")?.takeIf { !it.isJsonNull }?.asInt ?: 0
                    val contactEmail = body.get("contact_email")?.takeIf { !it.isJsonNull }?.asString
                    val contactPhone = body.get("contact_phone")?.takeIf { !it.isJsonNull }?.asString

                    // Handle social links safely
                    val socialLinksJson = body.getAsJsonObject("social_links")
                    val socialLinks = if (socialLinksJson != null && !socialLinksJson.isJsonNull) {
                        SocialLinks(
                            facebook = socialLinksJson.get("facebook")?.takeIf { !it.isJsonNull }?.asString,
                            instagram = socialLinksJson.get("instagram")?.takeIf { !it.isJsonNull }?.asString,
                            twitter = socialLinksJson.get("twitter")?.takeIf { !it.isJsonNull }?.asString,
                            linkedin = socialLinksJson.get("linkedin")?.takeIf { !it.isJsonNull }?.asString
                        )
                    } else {
                        SocialLinks()
                    }

                    val workingHoursArray = body.getAsJsonArray("working_hours")

                    // Default working hours if none provided
                    var morning = WorkingHour(
                        start = "08:00",
                        end = "12:00",
                        morning = true
                    )

                    var evening = WorkingHour(
                        start = "14:00",
                        end = "18:00",
                        morning = false
                    )

                    // Parse working hours if available
                    if (workingHoursArray != null && workingHoursArray.size() > 0) {
                        try {
                            val morningJson = workingHoursArray.find {
                                it.asJsonObject.get("period")?.asBoolean == false
                            }?.asJsonObject

                            val eveningJson = workingHoursArray.find {
                                it.asJsonObject.get("period")?.asBoolean == true
                            }?.asJsonObject

                            if (morningJson != null) {
                                val morningStart = morningJson.get("start_time")?.takeIf { !it.isJsonNull }?.asString ?: "08:00"
                                val morningEnd = morningJson.get("end_time")?.takeIf { !it.isJsonNull }?.asString ?: "12:00"

                                morning = WorkingHour(
                                    start = formatTimeString(morningStart),
                                    end = formatTimeString(morningEnd),
                                    morning = true
                                )
                            }

                            if (eveningJson != null) {
                                val eveningStart = eveningJson.get("start_time")?.takeIf { !it.isJsonNull }?.asString ?: "14:00"
                                val eveningEnd = eveningJson.get("end_time")?.takeIf { !it.isJsonNull }?.asString ?: "18:00"

                                evening = WorkingHour(
                                    start = formatTimeString(eveningStart),
                                    end = formatTimeString(eveningEnd),
                                    morning = false
                                )
                            }
                        } catch (e: Exception) {
                            Log.e("AuthViewModel", "Error parsing working hours", e)
                            // Keep default values
                        }
                    }

                    val workingHours = WorkingHours(morning, evening)

                    return DoctorProfile(
                        first_name = firstName,
                        last_name = lastName,
                        address = address ?: "",
                        phone = phone,
                        contact_email = contactEmail,
                        contact_phone = contactPhone,
                        social_links = socialLinks,
                        specialty_id = specialtyId,
                        working_hours = workingHours
                    )
                } catch (e: Exception) {
                    Log.e("AuthViewModel", "Error parsing doctor profile", e)
                    return null
                }
            }
        }
        return null
    }

    // Helper function to format time strings
    private fun formatTimeString(timeString: String): String {
        return try {
            // If the time string already contains seconds, return as is
            if (timeString.count { it == ':' } == 2) {
                timeString
            } else {
                // If it's in HH:MM format, add :00 seconds
                if (timeString.matches(Regex("\\d{2}:\\d{2}"))) {
                    "$timeString:00"
                } else {
                    timeString
                }
            }
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error formatting time string: $timeString", e)
            timeString
        }
    }

    fun updatePatientProfile(
        firstName: String,
        lastName: String,
        address: String,
        phone: String?,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val success = authRepository.updatePatientProfile(
                    firstName,
                    lastName,
                    address,
                    phone
                )
                if (success) {
                    Log.d("UpdateProfile", "Patient profile updated successfully")
                } else {
                    Log.e("UpdateProfile", "Failed to update patient profile")
                }
                onResult(success)
            } catch (e: Exception) {
                Log.e("UpdateProfile", "Exception updating patient profile: ${e.message}", e)
                onResult(false)
            }
        }
    }

    fun updateDoctorProfile(
        firstName: String,
        lastName: String,
        address: String,
        phone: String?,
        contactEmail: String?,
        contactPhone: String?,
        socialLinks: Map<String, String?>,
        workingHours: List<Map<String, Any>>,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val success = authRepository.updateDoctorProfile(
                    firstName,
                    lastName,
                    address,
                    phone,
                    contactEmail,
                    contactPhone,
                    socialLinks,
                    workingHours
                )
                if (success) {
                    Log.d("UpdateProfile", "Doctor profile updated successfully")
                } else {
                    Log.e("UpdateProfile", "Failed to update doctor profile")
                }
                onResult(success)
            } catch (e: Exception) {
                Log.e("UpdateProfile", "Exception updating doctor profile: ${e.message}", e)
                onResult(false)
            }
        }
    }
}