package com.example.doctor_appointment.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.doctor_appointment.data.api.ApiClient
import com.example.doctor_appointment.data.model.ProfileResponse
import com.example.doctor_appointment.data.model.TokenResponse
import com.google.gson.JsonObject
import retrofit2.Response

class AuthRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val TOKEN_KEY = "jwt_token"
        private const val PATIENT_KEY = "is_patient"
        private const val USER_EMAIL_KEY = "user_email"
        private const val FIRST_NAME_KEY = "first_name"
        private const val LAST_NAME_KEY = "last_name"
        private const val IS_GOOGLE_USER_KEY = "is_google_user"
    }

    private fun saveToken(token: String, isPatient: Boolean) {
        prefs.edit().putString(TOKEN_KEY, token)
            .putBoolean(PATIENT_KEY, isPatient)
            .apply()
    }

    private fun saveUserInfo(email: String, firstName: String? = null, lastName: String? = null, isGoogleUser: Boolean = false) {
        val editor = prefs.edit()
            .putString(USER_EMAIL_KEY, email)
            .putBoolean(IS_GOOGLE_USER_KEY, isGoogleUser)

        firstName?.let { editor.putString(FIRST_NAME_KEY, it) }
        lastName?.let { editor.putString(LAST_NAME_KEY, it) }

        editor.apply()
    }

    private fun getToken(): String? {
        Log.d("TOKEN_LOG", prefs.getString(TOKEN_KEY, "No token found") ?: "Token is null")
        return prefs.getString(TOKEN_KEY, null)
    }

    fun isLoggedIn(): Boolean = getToken() != null

    fun getUserEmail(): String? = prefs.getString(USER_EMAIL_KEY, null)

    fun getFirstName(): String? = prefs.getString(FIRST_NAME_KEY, null)

    fun getLastName(): String? = prefs.getString(LAST_NAME_KEY, null)

    fun isGoogleUser(): Boolean = prefs.getBoolean(IS_GOOGLE_USER_KEY, false)

    suspend fun patientLogin(email: String, password: String): Response<TokenResponse> {
        val response = ApiClient.patientLogin(email, password)
        if (response.isSuccessful) {
            response.body()?.let {
                saveToken(it.access_token, it.isPatient)
                saveUserInfo(email)
            }
        }
        return response
    }

    suspend fun doctorLogin(email: String, password: String): Response<TokenResponse> {
        val response = ApiClient.doctorLogin(email, password)
        if (response.isSuccessful) {
            response.body()?.let {
                saveToken(it.access_token, it.isPatient)
                saveUserInfo(email)
            }
        }
        return response
    }

    suspend fun patientSignUp(firstName: String, lastName: String, email: String, password: String, address: String?, phone: String?): Boolean {
        val response = ApiClient.patientSignup(firstName, lastName, email, password, address, phone)
        if (response.isSuccessful) {
            saveUserInfo(email, firstName, lastName)
        }
        return response.isSuccessful
    }

    suspend fun doctorSignup(firstName: String, lastName: String, email: String, password: String, address: String, specialty: Int, phone: String?, contact_email: String?, contact_phone: String?): Boolean {
        val response = ApiClient.doctorSignup(firstName, lastName, email, password, address, specialty, phone, contact_email, contact_phone)
        if (response.isSuccessful) {
            saveUserInfo(email, firstName, lastName)
        }
        return response.isSuccessful
    }

    fun logout() {
        prefs.edit()
            .remove(TOKEN_KEY)
            .remove(PATIENT_KEY)
            .remove(USER_EMAIL_KEY)
            .remove(FIRST_NAME_KEY)
            .remove(LAST_NAME_KEY)
            .remove(IS_GOOGLE_USER_KEY)
            .apply()
        Log.d("AuthRepository", "All user data cleared from SharedPreferences")
    }

    fun getRole(): Boolean = prefs.getBoolean(PATIENT_KEY, true)

    suspend fun getProfile(): Response<JsonObject>? {
        val token = prefs.getString(TOKEN_KEY, null)
        if (token != null) {
            val bearerToken = "Bearer $token"
            return ApiClient.getProfile(bearerToken)
        }
        return null
    }

    suspend fun updatePatientProfile(
        firstName: String,
        lastName: String,
        address: String,
        phone: String?
    ): Boolean {
        val token = getToken()
        return if (token != null) {
            val bearerToken = "Bearer $token"
            try {
                val response = ApiClient.updatePatientProfile(
                    bearerToken,
                    firstName,
                    lastName,
                    address,
                    phone
                )
                if (response.isSuccessful) {
                    // Update stored user info
                    saveUserInfo(getUserEmail() ?: "", firstName, lastName, isGoogleUser())
                }
                response.isSuccessful
            } catch (e: Exception) {
                Log.e("AuthRepository", "Error updating patient profile", e)
                false
            }
        } else {
            false
        }
    }

    suspend fun updateDoctorProfile(
        firstName: String,
        lastName: String,
        address: String,
        phone: String?,
        contactEmail: String?,
        contactPhone: String?,
        socialLinks: Map<String, String?>,
        workingHours: List<Map<String, Any>>
    ): Boolean {
        val token = getToken()
        return if (token != null) {
            val bearerToken = "Bearer $token"
            try {
                val response = ApiClient.updateDoctorProfile(
                    bearerToken,
                    firstName,
                    lastName,
                    address,
                    phone,
                    contactEmail,
                    contactPhone,
                    socialLinks,
                    workingHours
                )
                if (response.isSuccessful) {
                    // Update stored user info
                    saveUserInfo(getUserEmail() ?: "", firstName, lastName, isGoogleUser())
                }
                response.isSuccessful
            } catch (e: Exception) {
                Log.e("AuthRepository", "Error updating doctor profile", e)
                false
            }
        } else {
            false
        }
    }
}