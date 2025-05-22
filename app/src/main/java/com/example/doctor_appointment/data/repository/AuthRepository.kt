package com.example.doctor_appointment.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.doctor_appointment.data.api.ApiClient
import com.example.doctor_appointment.data.model.TokenResponse
import retrofit2.Response

class AuthRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val TOKEN_KEY = "jwt_token"
        private const val PATIENT_KEY = "is_patient"
    }

    private fun saveToken(token: String, isPatient : Boolean) {
        prefs.edit().putString(TOKEN_KEY, token)
            .putBoolean(PATIENT_KEY, isPatient)
            .apply()
    }

    fun getToken(): String? = prefs.getString(TOKEN_KEY, null)

    fun isLoggedIn(): Boolean = getToken() != null

    suspend fun patientLogin(email: String, password: String): Response<TokenResponse> {
        val response = ApiClient.patientLogin(email, password)
        if (response.isSuccessful) {
            response.body()?.let { saveToken(it.access_token, it.isPatient) }
        }
        return response
    }

    suspend fun doctorLogin(email: String, password: String): Response<TokenResponse> {
        val response = ApiClient.doctorLogin(email, password)
        if (response.isSuccessful) {
            response.body()?.let { saveToken(it.access_token, it.isPatient) }
        }
        return response
    }

    suspend fun patientSignUp(firstName: String, lastName: String, email: String, password: String, address: String?, phone: String?): Boolean {
        return ApiClient.patientSignup(firstName, lastName, email, password, address, phone).isSuccessful
    }

    suspend fun doctorSignup(firstName: String, lastName: String, email: String, password: String, address: String, specialty: Int, phone: String?, contact_email: String?, contact_phone: String?): Boolean {
        return ApiClient.doctorSignup(firstName, lastName, email, password, address, specialty, phone, contact_email, contact_phone).isSuccessful
    }

    fun logout() {
        prefs.edit().remove(TOKEN_KEY).apply()
    }

    fun getRole(): Boolean = prefs.getBoolean(PATIENT_KEY, true)
}
