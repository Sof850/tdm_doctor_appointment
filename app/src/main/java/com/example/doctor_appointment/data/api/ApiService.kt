package com.example.doctor_appointment.data.api

import com.example.doctor_appointment.data.model.LoginRequest
import com.example.doctor_appointment.data.model.PatientProfile
import com.example.doctor_appointment.data.model.SignUpRequest
import com.example.doctor_appointment.data.model.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("auth/patient/signup")
    suspend fun patient_signup(@Body request: SignUpRequest): Response<Void>

    @POST("auth/patient/login")
    suspend fun patient_login(@Body request: LoginRequest): Response<TokenResponse>

    @GET("patients/me")
    suspend fun getProfile(@Header("Authorization") token: String): Response<PatientProfile>
}

