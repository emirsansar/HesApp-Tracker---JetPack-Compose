package com.emirsansar.hesapptracker.view.authenticationScreens

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.manager.AppManager
import com.emirsansar.hesapptracker.manager.googleAuth.AuthStateOnGoogle
import com.emirsansar.hesapptracker.ui.theme.DarkThemeColors
import com.emirsansar.hesapptracker.ui.theme.LightThemeColors
import com.emirsansar.hesapptracker.view.mainScreens.MainActivity
import com.emirsansar.hesapptracker.view.mainScreens.sharedComponents.CustomOutlinedTextFieldForAuthScreens
import com.emirsansar.hesapptracker.view.authenticationScreens.components.AppLogoForAuthScreen
import com.emirsansar.hesapptracker.view.authenticationScreens.components.ErrorDialogForAuthScreen
import com.emirsansar.hesapptracker.view.authenticationScreens.components.GoogleButton
import com.emirsansar.hesapptracker.view.authenticationScreens.components.HeaderTextForAuthScreen
import com.emirsansar.hesapptracker.view.authenticationScreens.components.HyperlinkTextForAuthScreens
import com.emirsansar.hesapptracker.view.authenticationScreens.components.LoginSuccessDialog
import com.emirsansar.hesapptracker.viewModel.AuthenticationViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    authVM: AuthenticationViewModel = AuthenticationViewModel(),
    state: AuthStateOnGoogle,
    onSignInClick: () -> Unit
){
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val appManager = AppManager.getInstance(context)

    var emailState by remember { mutableStateOf("") }
    var passwordState by remember { mutableStateOf("") }

    val loginState by authVM.loginState.observeAsState(AuthenticationViewModel.LoginState.IDLE)
    val loggingError by authVM.loggingError.observeAsState()

    var isLoggedByGoogle by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = if (appManager.isDarkMode.value) DarkThemeColors.BarColor
                else LightThemeColors.BarColor
    ) {
        LaunchedEffect(key1 = state.signInError) {
            state.signInError?.let {error ->
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
        }

        LaunchedEffect(key1 = state.isSignInSuccessful) {
            if (state.isSignInSuccessful) {
                isLoggedByGoogle = true
            }
        }

        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppLogoForAuthScreen()

            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
                    .background(LightThemeColors.BackgroundColor),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HeaderTextForAuthScreen(
                    message = stringResource(id = R.string.label_login),
                    isDarkMode = appManager.isDarkMode.value
                )

                Column (
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                ) {
                    // Email TextField
                    CustomOutlinedTextFieldForAuthScreens(
                        value = emailState,
                        onValueChange = { emailState = it },
                        label = stringResource(id = R.string.label_email),
                        isDarkMode = appManager.isDarkMode.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    )

                    // Password TextField
                    CustomOutlinedTextFieldForAuthScreens(
                        value = passwordState,
                        onValueChange = { passwordState = it },
                        label = stringResource(id = R.string.label_password),
                        isDarkMode = appManager.isDarkMode.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        visualTransformation = PasswordVisualTransformation()
                    )
                }

                Button(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    onClick = {
                        keyboardController?.hide()
                        authVM.loginUser(emailState, passwordState, context)
                    },
                    enabled = loginState != AuthenticationViewModel.LoginState.SUCCESS
                )
                {
                    Text(text = stringResource(id = R.string.button_login), fontSize = 16.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.heightIn(8.dp))
                Text(text = stringResource(id = R.string.label_or), fontSize = 15.sp, fontWeight = FontWeight.Normal)
                Spacer(modifier = Modifier.heightIn(10.dp))

                GoogleButton(
                    isLoginButton = true,
                    onClick = {
                        onSignInClick()
                    },
                    context = context
                )

                if (loginState != AuthenticationViewModel.LoginState.SUCCESS) {
                    HyperlinkTextForAuthScreens(
                        text = stringResource(id = R.string.label_navigate_to_register),
                        isDarkMode = appManager.isDarkMode.value,
                        onClick = { navigateToRegisterScreen(navController) }
                    )
                }
            }


        }
    }

    when (loginState) {
        AuthenticationViewModel.LoginState.SUCCESS -> {
            LoginSuccessDialog(
                isDarkMode = appManager.isDarkMode.value,
                navigateToApplication = {
                    navigateToApplicationFromLoginScreen(context)
                }
            )
        }

        AuthenticationViewModel.LoginState.FAILURE -> {
            ErrorDialogForAuthScreen(
                message = stringResource(id = R.string.text_login_failed, loggingError!!),
                isDarkMode = appManager.isDarkMode.value,
                onDismiss = {
                    authVM.setLoginStateIdle()
                }
            )
        }
        else -> {}
    }

    if (isLoggedByGoogle) {
        LoginSuccessDialog(
            isDarkMode = appManager.isDarkMode.value,
            navigateToApplication = {
                navigateToApplicationFromLoginScreen(context)
            }
        )
    }

}


// Functions:

// Navigates to the registration screen.
private fun navigateToRegisterScreen(navController: NavController) {
    navController.navigate("register_screen")
}

// Navigates to the main application screen after a delay with a fade transition.
fun navigateToApplicationFromLoginScreen(context: Context) {
    Handler(Looper.getMainLooper()).postDelayed({
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        context.startActivity(intent)
        (context as android.app.Activity).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }, 1500)
}