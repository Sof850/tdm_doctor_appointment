package com.example.doctor_appointment.data.model

data class SignUpRequestDoctor(
    val first_name: String,
    val last_name: String,
    val email: String,
    val password: String,
    val address: String,
    val specialty_id: Int,
    val phone: String? = null,
    val social_links: SocialLinks? = null,
    val photo_url: String? = null,
    val contact_email: String? = null,
    val contact_phone: String? = null
)
