package com.emirsansar.hesapptracker.view.Authentication

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults.outlinedTextFieldColors
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.manager.AppManager
import com.emirsansar.hesapptracker.ui.theme.DarkThemeColors
import com.emirsansar.hesapptracker.ui.theme.LightThemeColors
import com.emirsansar.hesapptracker.view.AppMain.MainActivity
import com.emirsansar.hesapptracker.viewModel.AuthenticationViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    authVM: AuthenticationViewModel = AuthenticationViewModel()
){
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val appManager = AppManager.getInstance(context)

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
                text = "Log In",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (appManager.isDarkMode.value) Color.White else Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column (
                modifier = Modifier
                    .fillMaxWidth(0.8f)
            ) {
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
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    visualTransformation = PasswordVisualTransformation()
                )
            }

            Button(
                modifier = Modifier.fillMaxWidth(0.8f),
                onClick = {
                keyboardController?.hide()
                authVM.loginUser(emailState.value, passwordState.value) },
                enabled = loginState != AuthenticationViewModel.LoginState.SUCCESS)
            {
                Text(text = "Sign In", fontSize = 16.sp, color = Color.White)
            }

            if (loginState != AuthenticationViewModel.LoginState.SUCCESS) {
                Text(
                    text = "Don't have an account? Register",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textDecoration = TextDecoration.Underline
                    ),
                    color = if (appManager.isDarkMode.value) Color.White
                            else MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .clickable { navigateToRegisterScreen(navController) },
                )
            }
        }
    }

}


// Composable:

@Composable
fun CustomOutlinedTextFieldForAuthScreens(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    visualTransformation: PasswordVisualTransformation? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        visualTransformation = visualTransformation ?: VisualTransformation.None,
        modifier = modifier,
        colors = outlinedTextFieldColors(
            textColor = if (isDarkMode) Color.White else Color.Black,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = if (isDarkMode) Color.Gray else Color.DarkGray,
            focusedLabelColor = if (isDarkMode) Color.LightGray else Color.DarkGray,
            unfocusedLabelColor = if (isDarkMode) Color.LightGray else Color.DarkGray,
            cursorColor = if (isDarkMode) Color.LightGray else Color.DarkGray
        ),
        textStyle = TextStyle(fontSize = 16.sp)
    )
}


// Functions:

// Navigates to the registration screen.
fun navigateToRegisterScreen(navController: NavController) {
    navController.navigate("register_screen")
}

// Navigates to the main application screen after a delay with a fade transition.
fun navigateToAppMainScreen(context: Context) {
    Handler(Looper.getMainLooper()).postDelayed({
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        context.startActivity(intent)
        (context as android.app.Activity).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }, 1500)
}