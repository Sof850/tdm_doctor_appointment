package com.example.doctor_appointment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.doctor_appointment.ui.theme.Poppins


@Composable
fun ProfileScreen(navController: NavController) {
    val topBarHeight = 175.dp
    val profileImageSize = 120.dp

    var isEditing by remember { mutableStateOf(false) }

    val fullName = remember { mutableStateOf("Jean Dupont") }
    val email = remember { mutableStateOf("jean.dupont@example.com") }
    val phone = remember { mutableStateOf("+33 6 12 34 56 78") }
    val address = remember { mutableStateOf("123 Rue de Paris, France") }

    val originalFullName = remember { mutableStateOf(fullName.value) }
    val originalEmail = remember { mutableStateOf(email.value) }
    val originalPhone = remember { mutableStateOf(phone.value) }
    val originalAddress = remember { mutableStateOf(address.value) }



    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFD2EBE7))) {

        // 🔹 Top Section (Colored background)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(topBarHeight)
                .background(Color(0xFFD2EBE7))
                .align(Alignment.TopCenter)
        ) {
            // 🔹 "Profile" title
            Text(
                text = "Profile",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 24.dp, top = 24.dp)
            )

            // 🔔 Bell Icon
            IconButton(
                onClick = { /* TODO: Go to notifications */ },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.Black
                )
            }

            // ✅ Edit (pencil) and Save (check) button
            IconButton(
                onClick = {
                    if (isEditing) {
                        // Save
                        isEditing = false
                    } else {
                        // Enter edit mode → backup current values
                        originalFullName.value = fullName.value
                        originalEmail.value = email.value
                        originalPhone.value = phone.value
                        originalAddress.value = address.value
                        isEditing = true
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                    contentDescription = if (isEditing) "Save Profile" else "Edit Profile",
                    tint = Color.Black
                )
            }

            if (isEditing) {
                IconButton(
                    onClick = {
                        // Cancel → restore previous values
                        fullName.value = originalFullName.value
                        email.value = originalEmail.value
                        phone.value = originalPhone.value
                        address.value = originalAddress.value
                        isEditing = false
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 72.dp, bottom = 16.dp) // move left of checkmark
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel Edit",
                        tint = Color.Black
                    )
                }
            }


        }

        // 🔹 Profile Picture (Overlapping top and bottom)
        Box(
            modifier = Modifier
                .size(96.dp)
                .align(Alignment.TopCenter)
                .offset(y = topBarHeight - 48.dp) // Half the image height to overlap
                .zIndex(2f)
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile Placeholder",
                tint = Color.Gray,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        }
        // 🔹 Bottom Section (White background with fields)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topBarHeight)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(Color.White)
                .zIndex(1f)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = fullName.value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp, top = profileImageSize - 65.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            EditableProfileField("Nom complet", fullName, isEditing)
            EditableProfileField("Email", email, isEditing)
            EditableProfileField("Téléphone", phone, isEditing)
            EditableProfileField("Adresse", address, isEditing)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableProfileField(
    label: String,
    textState: MutableState<String>,
    isEditing: Boolean
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            fontFamily = Poppins
        )
        if (isEditing) {
            TextField(
                value = textState.value,
                onValueChange = { textState.value = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(  // 👈 this sets the font
                    fontFamily = Poppins,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                ),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    cursorColor = Color(0xFF1ABC9C), // teal cursor
                    focusedIndicatorColor = Color(0xFF1ABC9C), // teal line when focused
                    unfocusedIndicatorColor = Color(0xFFB2DFDB), // light teal when not focused
                )
            )
        } else {
            Text(
                text = textState.value,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                fontFamily = Poppins
            )
        }
        Divider(
            color = Color(0xFFE0E0E0),
            thickness = 1.dp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

