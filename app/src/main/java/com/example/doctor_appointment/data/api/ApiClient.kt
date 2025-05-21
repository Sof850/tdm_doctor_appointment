package com.example.doctor_appointment.data.api


import com.example.doctor_appointment.data.model.LoginRequest
import com.example.doctor_appointment.data.model.SignUpRequest
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
        .baseUrl("https://6612-41-111-189-175.ngrok-free.app/") // MUST end with `/`
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()


    private val apiService: ApiService = retrofit.create(ApiService::class.java)

    suspend fun patientLogin(email: String, password: String): Response<TokenResponse> {
        val request = LoginRequest(email, password)
        return apiService.patient_login(request)
    }

    suspend fun patientSignup(firstName: String, lastName: String, email: String, password: String): Response<Void> {
        val request = SignUpRequest(firstName, lastName, email, password)
        return apiService.patient_signup(request)
    }

}