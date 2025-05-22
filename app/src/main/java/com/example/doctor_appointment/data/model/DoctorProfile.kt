package com.example.doctor_appointment.data.model

data class DoctorProfile(
    val first_name: String,
    val last_name: String,
    val address: String,
    val phone: String? = null,
    val contact_email: String? = null,
    val contact_phone: String? = null,
    val social_links: SocialLinks? = null,
    val specialty_id: Int,
    val working_hours: WorkingHours
)
