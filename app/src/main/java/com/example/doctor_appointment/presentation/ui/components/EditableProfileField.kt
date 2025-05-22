package com.example.doctor_appointment.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doctor_appointment.presentation.theme.Poppins

@Composable
fun EditableProfileField(
    label: String,
    textState: MutableState<String>,
    isEditing: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Label
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (isEditing) {
            // Editable TextField
            TextField(
                value = textState.value,
                onValueChange = { textState.value = it },
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = Poppins,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                ),
                placeholder = {
                    Text(
                        text = "Entrez $label",
                        fontSize = 16.sp,
                        color = Color(0xFF858585),
                        fontFamily = Poppins
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    cursorColor = Color(0xFF0B8FAC),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        } else {
            // Display-only field with LoginScreen styling
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = textState.value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    fontFamily = Poppins
                )
            }
        }
    }
}