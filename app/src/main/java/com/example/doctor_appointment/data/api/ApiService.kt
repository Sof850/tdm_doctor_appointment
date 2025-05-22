package com.example.doctor_appointment.data.api

import com.example.doctor_appointment.data.model.LoginRequest
import com.example.doctor_appointment.data.model.PatientProfile
import com.example.doctor_appointment.data.model.ProfileResponse
import com.example.doctor_appointment.data.model.SignUpRequestDoctor
import com.example.doctor_appointment.data.model.SignUpRequestPatient
import com.example.doctor_appointment.data.model.TokenResponse
import com.example.doctor_appointment.data.model.WorkingHour
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("auth/patient/signup")
    suspend fun patient_signup(@Body request: SignUpRequestPatient): Response<Void>

    @POST("auth/doctor/signup")
    suspend fun doctor_signup(@Body request: SignUpRequestDoctor): Response<Void>

    @POST("auth/patient/login")
    suspend fun patient_login(@Body request: LoginRequest): Response<TokenResponse>

    @POST("auth/doctor/login")
    suspend fun doctor_login(@Body request: LoginRequest): Response<TokenResponse>

    @GET("/me")
    suspend fun getUserProfile(@Body token: String): Response<ProfileResponse>

    @GET("/doctor/{doctor_id}/working-hours")
    suspend fun getWorkingHours(@Path("doctor_id") doctorId: Int): Response<List<WorkingHour>>

    @POST("/doctor/{doctor_id}/working-hours")
    suspend fun updateWorkingHours(@Path("doctor_id") doctorId: Int): Response<Void>
}

