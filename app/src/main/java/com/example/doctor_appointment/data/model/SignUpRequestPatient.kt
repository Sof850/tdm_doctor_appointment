package com.example.doctor_appointment.data.model

data class SignUpRequestPatient(
    val first_name: String,
    val last_name: String,
    val email: String,
    val password: String,
    val address: String? = null,
    val phone: String? = null,
    val photo_url: String? = null
)
