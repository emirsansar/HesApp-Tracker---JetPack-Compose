package com.emirsansar.hesapptracker.view.Authentication

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.view.AppMain.MainActivity
import com.emirsansar.hesapptracker.viewModel.AuthenticationViewModel

@Composable
fun LoginScreen(navController: NavController, authVM: AuthenticationViewModel = AuthenticationViewModel() ){

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }

    val loginState by authVM.loginState.observeAsState(AuthenticationViewModel.LoginState.IDLE)
    val loggingError by authVM.loggingError.observeAsState()

    LaunchedEffect(loginState) {
        when (loginState) {
            AuthenticationViewModel.LoginState.SUCCESS -> {
                Toast.makeText(context, "Login successful!\nYou are being redirected to app", Toast.LENGTH_SHORT).show()
                navigateToAppMainScreen(context)
            }
            AuthenticationViewModel.LoginState.FAILURE -> {
                Toast.makeText(context, "Login failed: ${loggingError.toString()}", Toast.LENGTH_LONG).show()
                authVM.setRegisterStateIdle()
            }
            else -> {}
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFe3e5e6)
    ) {
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

            Button(onClick = {
                keyboardController?.hide()
                authVM.loginUser(emailState.value, passwordState.value) },
                enabled = loginState != AuthenticationViewModel.LoginState.SUCCESS)
            {
                androidx.compose.material3.Text(text = "Sign In")
            }

            if (loginState != AuthenticationViewModel.LoginState.SUCCESS) {
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
    }

}

fun navigateToRegisterScreen(navController: NavController) {
    navController.navigate("register_screen")
}

fun navigateToAppMainScreen(context: Context) {
    Handler(Looper.getMainLooper()).postDelayed({
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        context.startActivity(intent)
        (context as android.app.Activity).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }, 1500)
}