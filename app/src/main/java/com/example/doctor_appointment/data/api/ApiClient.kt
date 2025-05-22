package com.example.doctor_appointment.data.api


import com.example.doctor_appointment.data.model.LoginRequest
import com.example.doctor_appointment.data.model.SignUpRequestDoctor
import com.example.doctor_appointment.data.model.SignUpRequestPatient
import com.example.doctor_appointment.data.model.TokenResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private var authInterceptor : Interceptor? = null

    fun buildAuth(token : String) {
        authInterceptor = Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            token.let {
                requestBuilder.addHeader("Authorization", "Bearer $it")
            }

            chain.proceed(requestBuilder.build())
        }
    }

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


}