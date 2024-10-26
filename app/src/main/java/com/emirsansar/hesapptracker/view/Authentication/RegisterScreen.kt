package com.emirsansar.hesapptracker.view.Authentication

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.manager.AppManager
import com.emirsansar.hesapptracker.ui.theme.DarkThemeColors
import com.emirsansar.hesapptracker.ui.theme.LightThemeColors
import com.emirsansar.hesapptracker.viewModel.AuthenticationViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    authVM: AuthenticationViewModel = AuthenticationViewModel()
){
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val appManager = AppManager.getInstance(context)

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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = if (appManager.isDarkMode.value) DarkThemeColors.BackgroundColor
                else LightThemeColors.BackgroundColor
    ) {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            Box (modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .padding(bottom = 30.dp)
                .background( if (appManager.isDarkMode.value) DarkThemeColors.BarColor
                else LightThemeColors.BarColor ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hesapp),
                    contentDescription = "Application Logo",
                    modifier = Modifier
                        .width(220.dp)
                )
            }

            Text(
                text = "Register",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (appManager.isDarkMode.value) Color.White else Color.Black,
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
                    // Name TextField
                    CustomOutlinedTextFieldForAuthScreens(
                        value = nameState.value,
                        onValueChange = { nameState.value = it },
                        label = "Name",
                        isDarkMode = appManager.isDarkMode.value,
                        modifier = Modifier.weight(1f).fillMaxWidth().padding(bottom = 10.dp)
                    )

                    // Surname TextField
                    CustomOutlinedTextFieldForAuthScreens(
                        value = surnameState.value,
                        onValueChange = { surnameState.value = it },
                        label = "Surname",
                        isDarkMode = appManager.isDarkMode.value,
                        modifier = Modifier.weight(1f).fillMaxWidth().padding(bottom = 10.dp)
                    )
                }

                // Email TextField
                CustomOutlinedTextFieldForAuthScreens(
                    value = emailState.value,
                    onValueChange = { emailState.value = it },
                    label = "Email",
                    isDarkMode = appManager.isDarkMode.value,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                )

                // Password TextField
                CustomOutlinedTextFieldForAuthScreens(
                    value = passwordState.value,
                    onValueChange = { passwordState.value = it },
                    label = "Password",
                    isDarkMode = appManager.isDarkMode.value,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    visualTransformation = PasswordVisualTransformation()
                )

                // Re-password TextField
                CustomOutlinedTextFieldForAuthScreens(
                    value = rePasswordState.value,
                    onValueChange = { rePasswordState.value = it },
                    label = "Re-Password",
                    isDarkMode = appManager.isDarkMode.value,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    visualTransformation = PasswordVisualTransformation()
                )
            }

            Button(
                modifier = Modifier.fillMaxWidth(0.8f),
                onClick = {
                    keyboardController?.hide()
                    authVM.registerUserToFirebaseAuth(emailState.value, passwordState.value, nameState.value, surnameState.value) })
            {
                Text(text = "Register", fontSize = 16.sp,
                     color = Color.White)
            }

            Text(
                text = "Do you have an account? Sign In",
                style = MaterialTheme.typography.bodyMedium,
                color = if (appManager.isDarkMode.value) Color.White
                        else MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clickable { navigateToLoginScreen(navController) }
            )
        }
    }

}


fun navigateToLoginScreen(navController: NavController) {
    navController.navigate("login_screen") {
        popUpTo("register_screen") { inclusive = true }
    }
}