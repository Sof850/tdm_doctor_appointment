package com.example.doctor_appointment.data.api

import com.example.doctor_appointment.data.model.LoginRequest
import com.example.doctor_appointment.data.model.PatientProfile
import com.example.doctor_appointment.data.model.ProfileResponse
import com.example.doctor_appointment.data.model.SignUpRequestDoctor
import com.example.doctor_appointment.data.model.SignUpRequestPatient
import com.example.doctor_appointment.data.model.TokenResponse
import com.example.doctor_appointment.data.model.UpdateDoctorRequest
import com.example.doctor_appointment.data.model.UpdatePatientRequest
import com.example.doctor_appointment.data.model.WorkingHour
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @GET("auth/me")
    suspend fun getUserProfile(@Header("Authorization") token: String): Response<JsonObject>

    @GET("/doctor/{doctor_id}/working-hours")
    suspend fun getWorkingHours(@Path("doctor_id") doctorId: Int): Response<List<WorkingHour>>

    @POST("/doctor/{doctor_id}/working-hours")
    suspend fun updateWorkingHours(@Path("doctor_id") doctorId: Int): Response<Void>

    @PUT("auth/patient/profile")
    suspend fun updatePatientProfile(
        @Header("Authorization") token: String,
        @Body request: UpdatePatientRequest
    ): Response<JsonObject>

    @PUT("auth/doctor/profile")
    suspend fun updateDoctorProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateDoctorRequest
    ): Response<JsonObject>
}

