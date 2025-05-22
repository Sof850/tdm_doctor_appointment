package com.example.doctor_appointment.data.model

import com.google.gson.JsonObject

data class ProfileResponse(
    val isPatient: Boolean,
    val profile: JsonObject
)