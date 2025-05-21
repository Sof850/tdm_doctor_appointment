package com.example.doctor_appointment.presentation.ui.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.doctor_appointment.presentation.viewmodel.AuthViewModel
import com.example.doctor_appointment.presentation.theme.Poppins
import com.example.doctor_appointment.R
import com.example.doctor_appointment.presentation.ui.components.CustomTextFieldWithTitle


@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel, onGoogleSignInClicked: () -> Unit) {
    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenue",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = Color(0xFF0B8FAC),
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Se connecter",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Identifiant",
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Poppins,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = user,
                onValueChange = { user = it },
                textStyle = LocalTextStyle.current.copy(  // 👈 this sets the font
                    fontFamily = Poppins,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                ),
                placeholder = {
                    Text(
                        text = "Votre identifiant",
                        fontSize = 16.sp,
                        color = Color(0xFF858585),
                        fontFamily = Poppins
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFD9D9D9),
                    unfocusedContainerColor = Color(0xFFD9D9D9),
                    cursorColor = Color(0xFF0B8FAC),
                    focusedIndicatorColor = Color(0xFFE6E6E6),
                    unfocusedIndicatorColor = Color(0xFFE6E6E6)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Mot de passe",
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Poppins,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                textStyle = LocalTextStyle.current.copy(  // 👈 this sets the font
                    fontFamily = Poppins,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                ),
                placeholder = {
                    Text(
                        text = "Votre mot de passe",
                        fontSize = 16.sp,
                        color = Color(0xFF858585),
                        fontFamily = Poppins
                    )
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFD9D9D9),
                    unfocusedContainerColor = Color(0xFFD9D9D9),
                    cursorColor = Color(0xFF0B8FAC),
                    focusedIndicatorColor = Color(0xFFE6E6E6),
                    unfocusedIndicatorColor = Color(0xFFE6E6E6)
                ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = image,
                            contentDescription = "Toggle Password Visibility",
                            tint = Color(0xFF0B8FAC)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Mot de passe oublié?",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                color = Color(0xFF0B8FAC),
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { /* Handle Forgot Password */ }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (user.isNotEmpty() && password.isNotEmpty()) {
                        Log.d("Login", "Attempting login for $user")
                        viewModel.login(user, password) { success, token, role ->
                            if (success) {
                                Log.d("Login", "Success! Token = $token\nYour role is $role")
                            } else {
                                Log.e("Login", "Failed")
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B8FAC)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Se connecter",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onGoogleSignInClicked,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = "Google Logo",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Se connecter avec Google",
                        color = Color.DarkGray,
                        fontSize = 16.sp,
                        fontFamily = Poppins
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Vous n'avez pas de compte? ",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                fontFamily = Poppins
            )
            Text(
                text = "Inscrivez-vous",
                fontSize = 14.sp,
                color = Color(0xFF0B8FAC),
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                modifier = Modifier.clickable {
                    navController.navigate("role")
                }
            )
        }
    }
}


@Composable
fun SignUpScreen(navController: NavController, viewModel: AuthViewModel) {
    var firstName by remember { mutableStateOf("") }
    var familyName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🔹 Top Section (Bienvenue)
        Text(
            text = "Créer un compte",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0B8FAC),
            fontFamily = Poppins,
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 Spacer to push middle section down
        Spacer(modifier = Modifier.weight(1f))

        // 🔹 Middle Section (Centered Vertically)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // First Name Field using CustomTextFieldWithTitle
            CustomTextFieldWithTitle(
                value = firstName,
                onValueChange = { firstName = it },
                title = "Prénom",
                placeholderText = "Entrez votre prénom"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Family Name Field using CustomTextFieldWithTitle
            CustomTextFieldWithTitle(
                value = familyName,
                onValueChange = { familyName = it },
                title = "Nom de famille",
                placeholderText = "Entrez votre nom de famille"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Email Field using CustomTextFieldWithTitle
            CustomTextFieldWithTitle(
                value = email,
                onValueChange = { email = it },
                title = "Email",
                placeholderText = "Entrez votre email"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Password Field using CustomTextFieldWithTitle
            CustomTextFieldWithTitle(
                value = password,
                onValueChange = { password = it },
                title = "Mot de passe",
                placeholderText = "Entrez votre mot de passe",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Confirm Password Field using CustomTextFieldWithTitle
            CustomTextFieldWithTitle(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                title = "Confirmer le mot de passe",
                placeholderText = "Confirmez votre mot de passe",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Up Button
            Button(
                onClick = {
                    if (firstName.isNotEmpty() && familyName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                        if (password == confirmPassword) {
                            viewModel.signup(firstName, familyName, email, password) { success ->
                                if (success) {
                                    Log.d("Login", "Success!")
                                } else {
                                    Log.e("Login", "Failed")
                                }
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B8FAC)), // Blue background
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp), // Increase height
                shape = RoundedCornerShape(8.dp) // Reduce border radius
            ) {
                Text(
                    text = "S'inscrire",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { /* Handle Google login */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White), // White background
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp), // Increase height
                shape = RoundedCornerShape(8.dp), // Reduce border radius
                border = BorderStroke(1.dp, Color.Gray) // Optional border for contrast
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google), // Make sure you have the logo in res/drawable
                        contentDescription = "Google Logo",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Se connecter avec Google",
                        color = Color.DarkGray,
                        fontFamily = Poppins,
                        fontSize = 16.sp

                    )
                }
            }

        }

        // 🔹 Spacer for bottom section
        Spacer(modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.height(24.dp))

        // 🔹 Bottom Section (Stays at the bottom)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Vous avez déjà un compte? ",
                fontSize = 14 .sp,
                color = Color.Gray,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Se connecter",
                fontSize = 14.sp,
                color = Color(0xFF0B8FAC),
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                modifier = Modifier.clickable {  navController.navigate("login") }
            )
        }
    }
}




