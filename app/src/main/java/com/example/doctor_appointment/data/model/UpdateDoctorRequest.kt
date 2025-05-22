package com.example.doctor_appointment.data.model

data class UpdateDoctorRequest(
    val first_name: String,
    val last_name: String,
    val address: String,
    val phone: String?,
    val contact_email: String?,
    val contact_phone: String?,
    val social_links: Map<String, String?>,
    val working_hours: List<Map<String, Any>>
)