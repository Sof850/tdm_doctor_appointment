package com.example.doctor_appointment.presentation.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.doctor_appointment.data.model.DoctorProfile
import com.example.doctor_appointment.data.model.PatientProfile
import com.example.doctor_appointment.data.model.SocialLinks
import com.example.doctor_appointment.data.model.WorkingHours
import com.example.doctor_appointment.presentation.theme.Poppins
import com.example.doctor_appointment.presentation.ui.components.EditableProfileField
import com.example.doctor_appointment.presentation.viewmodel.AuthViewModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    title: String,
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
    initialHour: Int = 8,
    initialMinute: Int = 0
) {
    val timePickerState = remember {
        TimePickerState(
            initialHour = initialHour,
            initialMinute = initialMinute,
            is24Hour = true
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            TimePicker(
                state = timePickerState
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(timePickerState) }) {
                Text("Confirmer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSelectionField(
    label: String,
    time: String,
    onTimeChange: (String) -> Unit,
    isEditing: Boolean
) {
    var showTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = Poppins,
            color = Color(0xFF0B8FAC),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    color = if (isEditing) Color.White else Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = if (isEditing) Color(0xFF0B8FAC) else Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable(enabled = isEditing) {
                    if (isEditing) {
                        showTimePicker = true
                    }
                }
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = time,
                    fontSize = 16.sp,
                    fontFamily = Poppins,
                    color = if (isEditing) Color.Black else Color.Gray
                )

                if (isEditing) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Select time",
                        tint = Color(0xFF0B8FAC),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    if (showTimePicker) {
        val timeParts = time.split(":")
        val hour = timeParts[0].toIntOrNull() ?: 8
        val minute = timeParts[1].toIntOrNull() ?: 0

        TimePickerDialog(
            title = "Sélectionner $label",
            onConfirm = { timePickerState ->
                val selectedTime = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                onTimeChange(selectedTime)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false },
            initialHour = hour,
            initialMinute = minute
        )
    }
}

// Helper function to create working hours JsonArray for saving
fun createWorkingHoursJson(morningStart: String, morningEnd: String, afternoonStart: String, afternoonEnd: String): List<Map<String, Any>> {
    return listOf(
        mapOf(
            "start_time" to morningStart,
            "end_time" to morningEnd,
            "period" to false // morning shift
        ),
        mapOf(
            "start_time" to afternoonStart,
            "end_time" to afternoonEnd,
            "period" to true // evening/afternoon shift
        )
    )
}

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var profileData by remember { mutableStateOf<Any?>(null) }
    var isPatient by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }

    // Common fields
    val firstName = remember { mutableStateOf("") }
    val lastName = remember { mutableStateOf("") }
    val address = remember { mutableStateOf("") }
    val phone = remember { mutableStateOf("") }

    // Doctor-specific fields
    val contactEmail = remember { mutableStateOf("") }
    val contactPhone = remember { mutableStateOf("") }

    // Social media fields
    val facebook = remember { mutableStateOf("") }
    val instagram = remember { mutableStateOf("") }
    val twitter = remember { mutableStateOf("") }
    val linkedin = remember { mutableStateOf("") }

    // Working hours - individual time fields
    val morningStart = remember { mutableStateOf("") }
    val morningEnd = remember { mutableStateOf("") }
    val afternoonStart = remember { mutableStateOf("") }
    val afternoonEnd = remember { mutableStateOf("") }

    // Original values for cancel functionality
    val originalFirstName = remember { mutableStateOf("") }
    val originalLastName = remember { mutableStateOf("") }
    val originalAddress = remember { mutableStateOf("") }
    val originalPhone = remember { mutableStateOf("") }
    val originalContactEmail = remember { mutableStateOf("") }
    val originalContactPhone = remember { mutableStateOf("") }
    val originalFacebook = remember { mutableStateOf("") }
    val originalInstagram = remember { mutableStateOf("") }
    val originalTwitter = remember { mutableStateOf("") }
    val originalLinkedin = remember { mutableStateOf("") }
    val originalMorningStart = remember { mutableStateOf("") }
    val originalMorningEnd = remember { mutableStateOf("") }
    val originalAfternoonStart = remember { mutableStateOf("") }
    val originalAfternoonEnd = remember { mutableStateOf("") }

    // Load profile data on screen initialization
    LaunchedEffect(Unit) {
        try {
            val profile = authViewModel.getProfile()
            profileData = profile

            when (profile) {
                is PatientProfile -> {
                    isPatient = true
                    firstName.value = profile.first_name
                    lastName.value = profile.last_name
                    address.value = profile.address.toString()
                    phone.value = profile.phone ?: ""
                }
                is DoctorProfile -> {
                    isPatient = false
                    firstName.value = profile.first_name
                    lastName.value = profile.last_name
                    address.value = profile.address
                    phone.value = profile.phone ?: ""
                    contactEmail.value = profile.contact_email ?: ""
                    contactPhone.value = profile.contact_phone ?: ""

                    // Set social links
                    facebook.value = profile.social_links.facebook ?: ""
                    instagram.value = profile.social_links.instagram ?: ""
                    twitter.value = profile.social_links.twitter ?: ""
                    linkedin.value = profile.social_links.linkedin ?: ""

                    // Set working hours from the WorkingHours object
                    morningStart.value = profile.working_hours.morning.start
                    morningEnd.value = profile.working_hours.morning.end
                    afternoonStart.value = profile.working_hours.evening.start
                    afternoonEnd.value = profile.working_hours.evening.end
                }
            }

            // Store original values
            originalFirstName.value = firstName.value
            originalLastName.value = lastName.value
            originalAddress.value = address.value
            originalPhone.value = phone.value
            originalContactEmail.value = contactEmail.value
            originalContactPhone.value = contactPhone.value
            originalFacebook.value = facebook.value
            originalInstagram.value = instagram.value
            originalTwitter.value = twitter.value
            originalLinkedin.value = linkedin.value
            originalMorningStart.value = morningStart.value
            originalMorningEnd.value = morningEnd.value
            originalAfternoonStart.value = afternoonStart.value
            originalAfternoonEnd.value = afternoonEnd.value

        } catch (e: Exception) {
            Log.e("ProfileScreen", "Error loading profile data", e)
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF0B8FAC))
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header Section with Profile Picture and Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Profil",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                color = Color(0xFF0B8FAC)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { /* TODO: Go to notifications */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color(0xFF0B8FAC),
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(
                    onClick = {  onLogout() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Logout",
                        tint = Color(0xFF0B8FAC),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Picture Section
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile Picture",
                tint = Color(0xFF0B8FAC),
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // User Name Display
        Text(
            text = "${firstName.value} ${lastName.value}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = Color(0xFF0B8FAC),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isPatient) "Informations personnelles" else "Informations professionnelles",
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = Poppins,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Profile Form Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Common fields
            EditableProfileField("Prénom", firstName, isEditing)
            Spacer(modifier = Modifier.height(20.dp))

            EditableProfileField("Nom", lastName, isEditing)
            Spacer(modifier = Modifier.height(20.dp))

            EditableProfileField("Adresse", address, isEditing)
            Spacer(modifier = Modifier.height(20.dp))

            EditableProfileField("Téléphone", phone, isEditing)
            Spacer(modifier = Modifier.height(20.dp))

            // Doctor-specific fields
            if (!isPatient) {
                EditableProfileField("Email de contact", contactEmail, isEditing)
                Spacer(modifier = Modifier.height(20.dp))

                EditableProfileField("Téléphone de contact", contactPhone, isEditing)
                Spacer(modifier = Modifier.height(20.dp))

                // Working Hours Section
                Text(
                    text = "Horaires de travail",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins,
                    color = Color(0xFF0B8FAC),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        TimeSelectionField(
                            label = "Matin - Début",
                            time = morningStart.value,
                            onTimeChange = { newTime ->
                                morningStart.value = newTime
                            },
                            isEditing = isEditing
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        TimeSelectionField(
                            label = "Matin - Fin",
                            time = morningEnd.value,
                            onTimeChange = { newTime ->
                                morningEnd.value = newTime
                            },
                            isEditing = isEditing
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        TimeSelectionField(
                            label = "Après-midi - Début",
                            time = afternoonStart.value,
                            onTimeChange = { newTime ->
                                afternoonStart.value = newTime
                            },
                            isEditing = isEditing
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        TimeSelectionField(
                            label = "Après-midi - Fin",
                            time = afternoonEnd.value,
                            onTimeChange = { newTime ->
                                afternoonEnd.value = newTime
                            },
                            isEditing = isEditing
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Social Media Section
                Text(
                    text = "Réseaux sociaux",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins,
                    color = Color(0xFF0B8FAC),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                EditableProfileField("Facebook", facebook, isEditing)
                Spacer(modifier = Modifier.height(12.dp))

                EditableProfileField("Instagram", instagram, isEditing)
                Spacer(modifier = Modifier.height(12.dp))

                EditableProfileField("Twitter", twitter, isEditing)
                Spacer(modifier = Modifier.height(12.dp))

                EditableProfileField("LinkedIn", linkedin, isEditing)
                Spacer(modifier = Modifier.height(20.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            if (isEditing) {
                // Save and Cancel buttons when editing
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Cancel Button
                    Button(
                        onClick = {
                            // Cancel → restore previous values
                            firstName.value = originalFirstName.value
                            lastName.value = originalLastName.value
                            address.value = originalAddress.value
                            phone.value = originalPhone.value

                            if (!isPatient) {
                                contactEmail.value = originalContactEmail.value
                                contactPhone.value = originalContactPhone.value
                                facebook.value = originalFacebook.value
                                instagram.value = originalInstagram.value
                                twitter.value = originalTwitter.value
                                linkedin.value = originalLinkedin.value
                                morningStart.value = originalMorningStart.value
                                morningEnd.value = originalMorningEnd.value
                                afternoonStart.value = originalAfternoonStart.value
                                afternoonEnd.value = originalAfternoonEnd.value
                            }

                            isEditing = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, Color(0xFF0B8FAC))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel",
                                tint = Color(0xFF0B8FAC),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "Annuler",
                                color = Color(0xFF0B8FAC),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Poppins
                            )
                        }
                    }

                    // Save Button
                    Button(
                        onClick = {
                            if (!isSaving) {
                                isSaving = true

                                // Get first and last name from the editable fields
                                val firstNameValue = firstName.value.trim()
                                val lastNameValue = lastName.value.trim()

                                if (!isPatient) {
                                    // Create working hours data structure for API
                                    val workingHoursData = listOf(
                                        mapOf(
                                            "start_time" to "${morningStart.value}:00",
                                            "end_time" to "${morningEnd.value}:00",
                                            "period" to false // morning shift
                                        ),
                                        mapOf(
                                            "start_time" to "${afternoonStart.value}:00",
                                            "end_time" to "${afternoonEnd.value}:00",
                                            "period" to true // afternoon shift
                                        )
                                    )

                                    // Create social links data structure for API
                                    val socialLinksData = mapOf(
                                        "facebook" to if (facebook.value.isNotBlank()) facebook.value else null,
                                        "linkedin" to if (linkedin.value.isNotBlank()) linkedin.value else null,
                                        "twitter" to if (twitter.value.isNotBlank()) twitter.value else null,
                                        "instagram" to if (instagram.value.isNotBlank()) instagram.value else null
                                    ).filterValues { it != null }

                                    // Save doctor profile
                                    authViewModel.updateDoctorProfile(
                                        firstNameValue,
                                        lastNameValue,
                                        address.value,
                                        phone.value.takeIf { it.isNotBlank() },
                                        contactEmail.value.takeIf { it.isNotBlank() },
                                        contactPhone.value.takeIf { it.isNotBlank() },
                                        socialLinksData,
                                        workingHoursData
                                    ) { success ->
                                        isSaving = false
                                        if (success) {
                                            // Update full name and original values
                                            originalFirstName.value = firstName.value
                                            originalAddress.value = address.value
                                            originalPhone.value = phone.value
                                            originalContactEmail.value = contactEmail.value
                                            originalContactPhone.value = contactPhone.value
                                            originalFacebook.value = facebook.value
                                            originalInstagram.value = instagram.value
                                            originalTwitter.value = twitter.value
                                            originalLinkedin.value = linkedin.value
                                            originalMorningStart.value = morningStart.value
                                            originalMorningEnd.value = morningEnd.value
                                            originalAfternoonStart.value = afternoonStart.value
                                            originalAfternoonEnd.value = afternoonEnd.value

                                            isEditing = false
                                            Log.d("ProfileUpdate", "Doctor profile updated successfully")
                                        } else {
                                            Log.e("ProfileUpdate", "Failed to update doctor profile")
                                        }
                                    }
                                } else {
                                    // Save patient profile
                                    authViewModel.updatePatientProfile(
                                        firstNameValue,
                                        lastNameValue,
                                        address.value,
                                        phone.value.takeIf { it.isNotBlank() }
                                    ) { success ->
                                        isSaving = false
                                        if (success) {
                                            // Update full name and original values
                                            originalFirstName.value = firstName.value
                                            originalLastName.value = lastName.value
                                            originalAddress.value = address.value
                                            originalPhone.value = phone.value

                                            isEditing = false
                                            Log.d("ProfileUpdate", "Patient profile updated successfully")
                                        } else {
                                            Log.e("ProfileUpdate", "Failed to update patient profile")
                                        }
                                    }
                                }
                            }
                        },

                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B8FAC)),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Save",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "Sauvegarder",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Poppins
                            )
                        }
                    }
                }
            } else {
                // Edit button when not editing
                Button(
                    onClick = { isEditing = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B8FAC)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Modifier le profil",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Poppins
                        )
                    }
                }
            }
        }
    }
}