package com.example.doctor_appointment.presentation.ui.screens

import android.annotation.SuppressLint
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
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
import com.example.doctor_appointment.data.model.SocialLinks
import com.example.doctor_appointment.data.model.WorkingHour
import com.example.doctor_appointment.data.model.WorkingHours
import com.example.doctor_appointment.presentation.theme.Poppins
import com.example.doctor_appointment.presentation.ui.components.EditableProfileField

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

@Composable
fun ProfileScreen(
    navController: NavController,
    isPatient: Boolean = false
) {
    var isEditing by remember { mutableStateOf(false) }

    // Common fields
    val firstName = remember { mutableStateOf("Jean") }
    val lastName = remember { mutableStateOf("Dupont") }
    val address = remember { mutableStateOf("123 Rue de Paris, France") }
    val phone = remember { mutableStateOf("+33 6 12 34 56 78") }

    // Doctor-specific fields
    val contactEmail = remember { mutableStateOf("jean.dupont@medecin.com") }
    val contactPhone = remember { mutableStateOf("+33 1 23 45 67 89") }

    // Social media fields
    val facebook = remember { mutableStateOf("") }
    val instagram = remember { mutableStateOf("") }
    val twitter = remember { mutableStateOf("") }
    val linkedin = remember { mutableStateOf("") }

    // Working hours
    val morning = remember { mutableStateOf(WorkingHour) }
    val evening = remember { mutableStateOf(WorkingHour) }
    val workingHours = remember { mutableStateOf(WorkingHours()) }

    // Original values for cancel functionality
    val originalFirstName = remember { mutableStateOf(firstName.value) }
    val originalLastName = remember { mutableStateOf(lastName.value) }
    val originalAddress = remember { mutableStateOf(address.value) }
    val originalPhone = remember { mutableStateOf(phone.value) }
    val originalContactEmail = remember { mutableStateOf(contactEmail.value) }
    val originalContactPhone = remember { mutableStateOf(contactPhone.value) }
    val originalFacebook = remember { mutableStateOf(facebook.value) }
    val originalInstagram = remember { mutableStateOf(instagram.value) }
    val originalTwitter = remember { mutableStateOf(twitter.value) }
    val originalLinkedin = remember { mutableStateOf(linkedin.value) }
    val originalWorkingHours = remember { mutableStateOf(workingHours.value) }

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
                    onClick = { /* TODO: Logout */ }
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
                        val morningStart = remember { mutableStateOf(workingHours.value.morningStart) }
                        val morningEnd = remember { mutableStateOf(workingHours.value.morningEnd) }

                        TimeSelectionField(
                            label = "Matin - Début",
                            time = morningStart.value,
                            onTimeChange = { newTime ->
                                morningStart.value = newTime
                                workingHours.value = workingHours.value.copy(morningStart = newTime)
                            },
                            isEditing = isEditing
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        TimeSelectionField(
                            label = "Matin - Fin",
                            time = morningEnd.value,
                            onTimeChange = { newTime ->
                                morningEnd.value = newTime
                                workingHours.value = workingHours.value.copy(morningEnd = newTime)
                            },
                            isEditing = isEditing
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        val afternoonStart = remember { mutableStateOf(workingHours.value.afternoonStart) }
                        val afternoonEnd = remember { mutableStateOf(workingHours.value.afternoonEnd) }

                        TimeSelectionField(
                            label = "Après-midi - Début",
                            time = afternoonStart.value,
                            onTimeChange = { newTime ->
                                afternoonStart.value = newTime
                                workingHours.value = workingHours.value.copy(afternoonStart = newTime)
                            },
                            isEditing = isEditing
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        TimeSelectionField(
                            label = "Après-midi - Fin",
                            time = afternoonEnd.value,
                            onTimeChange = { newTime ->
                                afternoonEnd.value = newTime
                                workingHours.value = workingHours.value.copy(afternoonEnd = newTime)
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
                                workingHours.value = originalWorkingHours.value
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
                            // Save changes
                            if (!isPatient) {
                                // Update working hours from individual fields
                                workingHours.value = WorkingHours(
                                    morningStart = workingHours.value.morningStart,
                                    morningEnd = workingHours.value.morningEnd,
                                    afternoonStart = workingHours.value.afternoonStart,
                                    afternoonEnd = workingHours.value.afternoonEnd
                                )

                                // Create SocialLinks object
                                val socialLinks = SocialLinks(
                                    facebook = facebook.value.ifBlank { null },
                                    instagram = instagram.value.ifBlank { null },
                                    twitter = twitter.value.ifBlank { null },
                                    linkedin = linkedin.value.ifBlank { null }
                                )

                                // TODO: Save doctor profile with all fields including workingHours and socialLinks
                            } else {
                                // TODO: Save patient profile
                            }

                            isEditing = false
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
                // Edit Button when not editing
                Button(
                    onClick = {
                        // Enter edit mode → backup current values
                        originalFirstName.value = firstName.value
                        originalLastName.value = lastName.value
                        originalAddress.value = address.value
                        originalPhone.value = phone.value

                        if (!isPatient) {
                            originalContactEmail.value = contactEmail.value
                            originalContactPhone.value = contactPhone.value
                            originalFacebook.value = facebook.value
                            originalInstagram.value = instagram.value
                            originalTwitter.value = twitter.value
                            originalLinkedin.value = linkedin.value
                            originalWorkingHours.value = workingHours.value
                        }

                        isEditing = true
                    },
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

        Spacer(modifier = Modifier.height(32.dp))
    }
}