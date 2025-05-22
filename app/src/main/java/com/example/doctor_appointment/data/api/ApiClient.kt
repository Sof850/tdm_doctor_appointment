package com.example.doctor_appointment.data.api


import com.example.doctor_appointment.data.model.LoginRequest
import com.example.doctor_appointment.data.model.ProfileResponse
import com.example.doctor_appointment.data.model.SignUpRequestDoctor
import com.example.doctor_appointment.data.model.SignUpRequestPatient
import com.example.doctor_appointment.data.model.TokenResponse
import com.example.doctor_appointment.data.model.UpdateDoctorRequest
import com.example.doctor_appointment.data.model.UpdatePatientRequest
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://2595-154-121-30-112.ngrok-free.app/") // MUST end with `/`
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()


    private val apiService: ApiService = retrofit.create(ApiService::class.java)

    suspend fun patientLogin(email: String, password: String): Response<TokenResponse> {
        val request = LoginRequest(email, password)
        return apiService.patient_login(request)
    }

    suspend fun patientSignup(firstName: String, lastName: String, email: String, password: String, address: String?, phone: String?): Response<Void> {
        val request = SignUpRequestPatient(firstName, lastName, email, password, address, phone)
        return apiService.patient_signup(request)
    }

    suspend fun doctorLogin(email: String, password: String): Response<TokenResponse> {
        val request = LoginRequest(email, password)
        return apiService.doctor_login(request)
    }

    suspend fun doctorSignup(firstName: String, lastName: String, email: String, password: String, address: String, specialty: Int, phone: String?, contact_email: String?, contact_phone: String?): Response<Void> {
        val request = SignUpRequestDoctor(
            firstName,
            lastName,
            email,
            password,
            address,
            specialty,
            phone,
            null,
            null,
            contact_email,
            contact_phone
        )
        return apiService.doctor_signup(request)
    }

    suspend fun updatePatientProfile(
        token: String,
        firstName: String,
        lastName: String,
        address: String,
        phone: String?
    ): Response<JsonObject> {
        val request = UpdatePatientRequest(firstName, lastName, address, phone)
        return apiService.updatePatientProfile(token, request)
    }

    suspend fun updateDoctorProfile(
        token: String,
        firstName: String,
        lastName: String,
        address: String,
        phone: String?,
        contactEmail: String?,
        contactPhone: String?,
        socialLinks: Map<String, String?>,
        workingHours: List<Map<String, Any>>
    ): Response<JsonObject> {
        val request = UpdateDoctorRequest(
            firstName,
            lastName,
            address,
            phone,
            contactEmail,
            contactPhone,
            socialLinks,
            workingHours
        )
        return apiService.updateDoctorProfile(token, request)
    }

    suspend fun getProfile(token : String) : Response<JsonObject> {
        return apiService.getUserProfile(token)
    }


}