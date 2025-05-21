package com.example.doctor_appointment.data.model

data class TokenResponse(
    val access_token: String,
    val token_type: String,
    val isPatient: Boolean
)
