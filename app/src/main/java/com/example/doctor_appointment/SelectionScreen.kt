package com.example.doctor_appointment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.doctor_appointment.ui.theme.Poppins

@Composable
fun UserTypeSelectionScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome",
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp,
            color = Color.Black
        )

        Text(
            text = "Please choose your role",
            fontFamily = Poppins,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = Color.Gray
        )

        // Doctor Card - Teal background, white content
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable { navController.navigate("signup") },
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0B8FAC),
                contentColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.doctor),
                    contentDescription = "Doctor Icon",
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Doctor",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        }

        // Patient Card - White background, teal content
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable { navController.navigate("signup") },
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
                contentColor = Color(0xFF0B8FAC)
            ),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.patient),
                    contentDescription = "Patient Icon",
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Patient",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = Color(0xFF0B8FAC)
                )
            }
        }
        Button(
            onClick = { navController.navigate("profile") }, // ← navigate to "profile"
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B8FAC)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Go to Profile",
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}

