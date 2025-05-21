package com.example.doctor_appointment.data.model

data class PatientProfile(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val email: String,
    val address: String?
)

