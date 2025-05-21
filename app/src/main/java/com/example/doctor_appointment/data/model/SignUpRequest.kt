package com.example.doctor_appointment.data.model

data class SignUpRequest(
    val first_name: String,
    val last_name: String,
    val email: String,
    val password: String
)
