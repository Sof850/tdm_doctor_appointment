package com.example.doctor_appointment.data.model

data class PatientProfile(
    val first_name: String,
    val last_name: String,
    val email: String,
    val address: String?,
    val phone: String?,
    val photo_url: String?
)

