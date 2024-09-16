package com.emirsansar.hesapptracker.view.Authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.emirsansar.hesapptracker.R

@Composable
fun LoginScreen(navController: NavController){

    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Image(
            painter = painterResource(id = R.drawable.hesapp),
            contentDescription = "Uygulama Logosu",
            modifier = Modifier
                .padding(bottom = 40.dp, top = 30.dp)
                .width(230.dp)
        )

        androidx.compose.material3.Text(
            text = "Log In",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column (
            modifier = Modifier
                .fillMaxWidth(0.8f)
        ) {
            // Email TextField
            OutlinedTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            )

            // Password TextField
            OutlinedTextField(
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        Button(onClick = {  }) {
            androidx.compose.material3.Text(text = "Sign In")
        }

        // Register link
        Text(
            text = "Don't have an account? Register",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(top = 16.dp)
                .clickable { navigateToRegisterScreen(navController) },
            color = MaterialTheme.colorScheme.primary
        )
    }
}

fun navigateToRegisterScreen(navController: NavController) {
    navController.navigate("register_screen")
}
