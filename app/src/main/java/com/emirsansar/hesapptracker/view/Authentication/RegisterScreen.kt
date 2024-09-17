package com.emirsansar.hesapptracker.view.Authentication

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.ui.theme.AppBackgroundColor
import com.emirsansar.hesapptracker.viewModel.AuthenticationViewModel

@Composable
fun RegisterScreen(navController: NavController, authVM: AuthenticationViewModel = AuthenticationViewModel() ){

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val nameState = remember { mutableStateOf("") }
    val surnameState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val rePasswordState = remember { mutableStateOf("") }

    val registerState by authVM.registerState.observeAsState(AuthenticationViewModel.RegisterState.IDLE)
    val registerError by authVM.registrationError.observeAsState()

    LaunchedEffect(registerState) {
        when (registerState) {
            AuthenticationViewModel.RegisterState.SUCCESS -> {
                Toast.makeText(context, "Registration successful.", Toast.LENGTH_SHORT).show()
                navController.navigate("login_screen")
            }
            AuthenticationViewModel.RegisterState.FAILURE -> {
                Toast.makeText(context, "Registration failed: ${registerError.toString()}", Toast.LENGTH_LONG).show()
                authVM.setRegisterStateIdle()
            }
            else -> {}
        }
    }

    BackHandler {
        navController.navigate("login_screen") {
            navigateToLoginScreen(navController)
        }
    }

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

        Text(
            text = "Register",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column (
            modifier = Modifier
                .fillMaxWidth(0.8f)
        ) {
            Row (
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            )
            {
                OutlinedTextField( // Name TextField
                    value = nameState.value,
                    onValueChange = { nameState.value = it },
                    label = { androidx.compose.material.Text("Name") },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )

                OutlinedTextField( // Surname TextField
                    value = surnameState.value,
                    onValueChange = { surnameState.value = it },
                    label = { androidx.compose.material.Text("Surname") },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )
            }

            // Email TextField
            OutlinedTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                label = { androidx.compose.material.Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            )

            // Password TextField
            OutlinedTextField(
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                label = { androidx.compose.material.Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            )

            // Re-password TextField
            OutlinedTextField(
                value = rePasswordState.value,
                onValueChange = { rePasswordState.value = it },
                label = { androidx.compose.material.Text("Re-Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                keyboardController?.hide()
                authVM.registerUserToFirebaseAuth(emailState.value, passwordState.value, nameState.value, surnameState.value) })
        {
            Text(text = "Register")
        }

        Text(
            text = "Do you have an account? Sign In",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(top = 16.dp)
                .clickable { navigateToLoginScreen(navController) },
            color = MaterialTheme.colorScheme.primary
        )

    }
}

fun navigateToLoginScreen(navController: NavController) {
    navController.navigate("login_screen") {
        popUpTo("register_screen") { inclusive = true }
    }
}