package com.example.doctor_appointment.presentation.ui.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.doctor_appointment.R
import com.example.doctor_appointment.presentation.theme.Poppins
import com.example.doctor_appointment.presentation.ui.components.CustomTextFieldWithTitle
import com.example.doctor_appointment.presentation.viewmodel.AuthViewModel

data class Specialty(
    val id: Int,
    val label: String
)

@Composable
fun SignUpScreen(navController: NavController, viewModel: AuthViewModel, isPatient: Boolean) {
    var currentStep by remember { mutableStateOf(1) }

    // Step 1 fields
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // Step 2 fields
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Step 3 fields (Patient)
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    // Step 3 fields (Doctor)
    var clinicAddress by remember { mutableStateOf("") }
    var personalPhone by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf("") }

    // Step 4 fields (Doctor only)
    var selectedSpecialty by remember { mutableStateOf<Specialty?>(null) }

    // Sample specialties (replace with actual data from database)
    val specialties = remember {
        listOf(
            Specialty(1, "Cardiologie"),
            Specialty(2, "Dermatologie"),
            Specialty(3, "Neurologie"),
            Specialty(4, "Pédiatrie"),
            Specialty(5, "Orthopédie"),
            Specialty(6, "Gynécologie"),
            Specialty(7, "Psychiatrie"),
            Specialty(8, "Ophtalmologie")
        )
    }

    // Error states
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Créer un compte",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0B8FAC),
            fontFamily = Poppins,
            modifier = Modifier.padding(top = 32.dp)
        )

        Text(
            text = if (isPatient) "Compte Patient" else "Compte Docteur",
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Gray,
            fontFamily = Poppins,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Progress Indicator
        StepProgressIndicator(
            currentStep = currentStep,
            totalSteps = if (isPatient) 3 else 4
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Form Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Error Message
            if (showError) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(
                            Color.Red.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Content based on current step
            when (currentStep) {
                1 -> Step1Content(
                    firstName = firstName,
                    onFirstNameChange = {
                        firstName = it
                        showError = false
                    },
                    lastName = lastName,
                    onLastNameChange = {
                        lastName = it
                        showError = false
                    },
                    email = email,
                    onEmailChange = {
                        email = it
                        showError = false
                    }
                )

                2 -> Step2Content(
                    password = password,
                    onPasswordChange = {
                        password = it
                        showError = false
                    },
                    confirmPassword = confirmPassword,
                    onConfirmPasswordChange = {
                        confirmPassword = it
                        showError = false
                    }
                )

                3 -> {
                    if (isPatient) {
                        Step3PatientContent(
                            phoneNumber = phoneNumber,
                            onPhoneNumberChange = {
                                phoneNumber = it
                                showError = false
                            },
                            address = address,
                            onAddressChange = {
                                address = it
                                showError = false
                            }
                        )
                    } else {
                        Step3DoctorContent(
                            clinicAddress = clinicAddress,
                            onClinicAddressChange = {
                                clinicAddress = it
                                showError = false
                            },
                            personalPhone = personalPhone,
                            onPersonalPhoneChange = {
                                personalPhone = it
                                showError = false
                            },
                            contactPhone = contactPhone,
                            onContactPhoneChange = {
                                contactPhone = it
                                showError = false
                            },
                            contactEmail = contactEmail,
                            onContactEmailChange = {
                                contactEmail = it
                                showError = false
                            }
                        )
                    }
                }

                4 -> {
                    if (!isPatient) {
                        Step4SpecialtyContent(
                            specialties = specialties,
                            selectedSpecialty = selectedSpecialty,
                            onSpecialtySelected = {
                                selectedSpecialty = it
                                showError = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back Button
                if (currentStep > 1) {
                    OutlinedButton(
                        onClick = {
                            currentStep--
                            showError = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, Color(0xFF0B8FAC))
                    ) {
                        Text(
                            text = "Précédent",
                            color = Color(0xFF0B8FAC),
                            fontSize = 16.sp,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                }

                // Next/Submit Button
                Button(
                    onClick = {
                        val validation = validateCurrentStep(
                            currentStep = currentStep,
                            isPatient = isPatient,
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            password = password,
                            confirmPassword = confirmPassword,
                            clinicAddress = clinicAddress,
                            selectedSpecialty = selectedSpecialty
                        )

                        if (validation.first) {
                            showError = false
                            val maxSteps = if (isPatient) 3 else 4

                            if (currentStep < maxSteps) {
                                currentStep++
                            } else {
                                // Final submission
                                handleSignUp(
                                    viewModel = viewModel,
                                    isPatient = isPatient,
                                    firstName = firstName,
                                    lastName = lastName,
                                    email = email,
                                    password = password,
                                    phoneNumber = phoneNumber,
                                    address = address,
                                    clinicAddress = clinicAddress,
                                    personalPhone = personalPhone,
                                    contactPhone = contactPhone,
                                    contactEmail = contactEmail,
                                    selectedSpecialty = selectedSpecialty,
                                    navController = navController
                                )
                            }
                        } else {
                            showError = true
                            errorMessage = validation.second
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B8FAC)),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    val maxSteps = if (isPatient) 3 else 4
                    Text(
                        text = if (currentStep < maxSteps) "Suivant" else "S'inscrire",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Google Sign-in Button (only on first step)
            if (currentStep == 1) {
                // Divider
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(Color.LightGray)
                    )
                    Text(
                        text = "OU",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(Color.LightGray)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { /* Handle Google signup */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, Color(0xFFE0E0E0))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.google),
                            contentDescription = "Google Logo",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                        Text(
                            text = "Continuer avec Google",
                            color = Color.DarkGray,
                            fontSize = 16.sp,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login Link
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Vous avez déjà un compte? ",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray,
                fontFamily = Poppins
            )
            Text(
                text = "Se connecter",
                fontSize = 14.sp,
                color = Color(0xFF0B8FAC),
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                modifier = Modifier.clickable { navController.navigate("login") }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun StepProgressIndicator(currentStep: Int, totalSteps: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (step in 1..totalSteps) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (step <= currentStep) Color(0xFF0B8FAC) else Color(0xFFE0E0E0)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = step.toString(),
                    color = if (step <= currentStep) Color.White else Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = Poppins
                )
            }

            if (step < totalSteps) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(3.dp)
                        .background(
                            if (step < currentStep) Color(0xFF0B8FAC) else Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(1.5.dp)
                        )
                )
            }
        }
    }
}

@Composable
fun Step1Content(
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Informations personnelles",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0B8FAC),
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        CustomTextFieldWithTitle(
            value = firstName,
            onValueChange = onFirstNameChange,
            title = "Prénom",
            placeholderText = "Entrez votre prénom"
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomTextFieldWithTitle(
            value = lastName,
            onValueChange = onLastNameChange,
            title = "Nom de famille",
            placeholderText = "Entrez votre nom de famille"
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomTextFieldWithTitle(
            value = email,
            onValueChange = onEmailChange,
            title = "Email",
            placeholderText = "exemple@email.com"
        )
    }
}

@Composable
fun Step2Content(
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Créer votre mot de passe",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0B8FAC),
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        CustomTextFieldWithTitle(
            value = password,
            onValueChange = onPasswordChange,
            title = "Mot de passe",
            placeholderText = "Minimum 8 caractères",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomTextFieldWithTitle(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            title = "Confirmer le mot de passe",
            placeholderText = "Confirmez votre mot de passe",
            isPassword = true
        )
    }
}

@Composable
fun Step3PatientContent(
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    address: String,
    onAddressChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Informations supplémentaires",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0B8FAC),
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Ces informations sont optionnelles",
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Gray,
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        CustomTextFieldWithTitle(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            title = "Numéro de téléphone (optionnel)",
            placeholderText = "Entrez votre numéro de téléphone"
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomTextFieldWithTitle(
            value = address,
            onValueChange = onAddressChange,
            title = "Adresse (optionnel)",
            placeholderText = "Entrez votre adresse"
        )
    }
}

@Composable
fun Step3DoctorContent(
    clinicAddress: String,
    onClinicAddressChange: (String) -> Unit,
    personalPhone: String,
    onPersonalPhoneChange: (String) -> Unit,
    contactPhone: String,
    onContactPhoneChange: (String) -> Unit,
    contactEmail: String,
    onContactEmailChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Informations professionnelles",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0B8FAC),
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        CustomTextFieldWithTitle(
            value = clinicAddress,
            onValueChange = onClinicAddressChange,
            title = "Adresse de la clinique",
            placeholderText = "Entrez l'adresse de votre clinique"
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomTextFieldWithTitle(
            value = personalPhone,
            onValueChange = onPersonalPhoneChange,
            title = "Téléphone personnel (optionnel)",
            placeholderText = "Entrez votre numéro personnel"
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomTextFieldWithTitle(
            value = contactPhone,
            onValueChange = onContactPhoneChange,
            title = "Téléphone de contact (optionnel)",
            placeholderText = "Numéro pour les patients"
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomTextFieldWithTitle(
            value = contactEmail,
            onValueChange = onContactEmailChange,
            title = "Email de contact (optionnel)",
            placeholderText = "Email pour les patients"
        )
    }
}

@Composable
fun Step4SpecialtyContent(
    specialties: List<Specialty>,
    selectedSpecialty: Specialty?,
    onSpecialtySelected: (Specialty) -> Unit
) {
    Column {
        Text(
            text = "Choisissez votre spécialité",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0B8FAC),
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(
            modifier = Modifier.height(400.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(specialties) { specialty ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSpecialtySelected(specialty) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedSpecialty?.id == specialty.id)
                            Color(0xFF0B8FAC).copy(alpha = 0.1f) else Color(0xFFF5F5F5)
                    ),
                    border = BorderStroke(
                        1.5.dp,
                        if (selectedSpecialty?.id == specialty.id)
                            Color(0xFF0B8FAC) else Color.Transparent
                    )
                ) {
                    Text(
                        text = specialty.label,
                        fontSize = 16.sp,
                        fontFamily = Poppins,
                        fontWeight = if (selectedSpecialty?.id == specialty.id)
                            FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedSpecialty?.id == specialty.id)
                            Color(0xFF0B8FAC) else Color.Black,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
        }
    }
}

fun validateCurrentStep(
    currentStep: Int,
    isPatient: Boolean,
    firstName: String,
    lastName: String,
    email: String,
    password: String,
    confirmPassword: String,
    clinicAddress: String,
    selectedSpecialty: Specialty?
): Pair<Boolean, String> {
    return when (currentStep) {
        1 -> {
            when {
                firstName.isBlank() -> Pair(false, "Le prénom est requis")
                lastName.isBlank() -> Pair(false, "Le nom de famille est requis")
                email.isBlank() -> Pair(false, "L'email est requis")
                !email.contains("@") || !email.contains(".") -> Pair(false, "Format d'email invalide")
                else -> Pair(true, "")
            }
        }
        2 -> {
            when {
                password.isBlank() -> Pair(false, "Le mot de passe est requis")
                password.length < 8 -> Pair(false, "Le mot de passe doit contenir au moins 8 caractères")
                confirmPassword.isBlank() -> Pair(false, "La confirmation du mot de passe est requise")
                password != confirmPassword -> Pair(false, "Les mots de passe ne correspondent pas")
                else -> Pair(true, "")
            }
        }
        3 -> {
            if (!isPatient && clinicAddress.isBlank()) {
                Pair(false, "L'adresse de la clinique est requise")
            } else {
                Pair(true, "")
            }
        }
        4 -> {
            if (!isPatient && selectedSpecialty == null) {
                Pair(false, "Veuillez sélectionner une spécialité")
            } else {
                Pair(true, "")
            }
        }
        else -> Pair(true, "")
    }
}

fun handleSignUp(
    viewModel: AuthViewModel,
    isPatient: Boolean,
    firstName: String,
    lastName: String,
    email: String,
    password: String,
    phoneNumber: String,
    address: String,
    clinicAddress: String,
    personalPhone: String,
    contactPhone: String,
    contactEmail: String,
    selectedSpecialty: Specialty?,
    navController: NavController
) {
    // Here you would call your viewModel signup method with all the collected data
    // The exact implementation depends on your backend API structure

    if (isPatient) {
        // Patient signup
        viewModel.signupPatient(
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password,
            phone = phoneNumber.ifBlank { null },
            address = address.ifBlank { null },
        ) { success ->
            if (success) {
                Log.d("SignUp", "Patient signup successful!")
            } else {
                Log.e("SignUp", "Patient signup failed")
            }
            navController.navigate("login")
        }
    } else {
        // Doctor signup
        selectedSpecialty?.id?.let {
            viewModel.signupDoctor(
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password,
                address = clinicAddress,
                contact_email = contactEmail.ifBlank { null },
                contact_phone = contactPhone.ifBlank { null },
                specialty = it,
                phone = personalPhone.ifBlank { null }
            ) { success ->
                if (success) {
                    Log.d("SignUp", "Doctor signup successful!")
                } else {
                    Log.e("SignUp", "Doctor signup failed")
                }
                navController.navigate("login")
            }
        }
    }
}

