package com.emirsansar.hesapptracker.view.Authentication

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.manager.AppManager
import com.emirsansar.hesapptracker.ui.theme.DarkThemeColors
import com.emirsansar.hesapptracker.ui.theme.LightThemeColors
import com.emirsansar.hesapptracker.viewModel.AuthenticationViewModel
import kotlinx.coroutines.delay

@Composable
fun RegisterScreen(
    navController: NavController,
    authVM: AuthenticationViewModel = AuthenticationViewModel()
){
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val appManager = AppManager.getInstance(context)
    val focusManager = LocalFocusManager.current

    var nameState by remember { mutableStateOf("") }
    var surnameState by remember { mutableStateOf("") }
    var emailState by remember { mutableStateOf("") }
    var passwordState by remember { mutableStateOf("") }
    var rePasswordState by remember { mutableStateOf("") }
    var matchingPasswordError by remember { mutableStateOf(false) }

    val registerState by authVM.registerState.observeAsState(AuthenticationViewModel.RegisterState.IDLE)
    val registerError by authVM.registrationError.observeAsState()

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
                .background(
                    if (appManager.isDarkMode.value) DarkThemeColors.BarColor
                    else LightThemeColors.BarColor
                ),
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
                text = stringResource(id = R.string.label_create_account),
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
                        value = nameState,
                        onValueChange = { nameState = it },
                        label = stringResource(id = R.string.label_name),
                        isDarkMode = appManager.isDarkMode.value,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    )

                    // Surname TextField
                    CustomOutlinedTextFieldForAuthScreens(
                        value = surnameState,
                        onValueChange = { surnameState = it },
                        label = stringResource(id = R.string.label_surname),
                        isDarkMode = appManager.isDarkMode.value,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    )
                }

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
                        .padding(bottom = 10.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = matchingPasswordError
                )

                // Re-password TextField
                CustomOutlinedTextFieldForAuthScreens(
                    value = rePasswordState,
                    onValueChange = { rePasswordState = it },
                    label = stringResource(id = R.string.label_confirm_password),
                    isDarkMode = appManager.isDarkMode.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = matchingPasswordError
                )
            }

            Button(
                modifier = Modifier.fillMaxWidth(0.8f),
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    authVM.setRegisterStateIdle()

                    if (passwordState != rePasswordState) {
                        matchingPasswordError = true
                    } else {
                        matchingPasswordError = false
                        authVM.registerUserToFirebaseAuth(emailState, passwordState, nameState, surnameState)
                    }
                },
                enabled = registerState != AuthenticationViewModel.RegisterState.SUCCESS
            ) {
                Text(text = stringResource(id = R.string.button_register), fontSize = 16.sp,
                     color = Color.White)
            }

            Text(
                text = stringResource(id = R.string.label_navigate_to_login),
                style = MaterialTheme.typography.bodyMedium.copy(
                    textDecoration = TextDecoration.Underline
                ),
                color = if (appManager.isDarkMode.value) Color.White
                        else Color.Black,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clickable { navigateToLoginScreen(navController) }
            )

            if (matchingPasswordError) {
                Text(
                    text = stringResource(id = R.string.error_password_does_not_match),
                    color = Color.Red, fontSize = 15.sp, modifier = Modifier.padding(top = 8.dp)
                )

                LaunchedEffect(Unit) {
                    delay(3000)
                    matchingPasswordError = false
                }
            }


            Box (modifier = Modifier.padding(top = 12.dp).fillMaxWidth(0.75f))
            {
                when (registerState) {
                    AuthenticationViewModel.RegisterState.SUCCESS -> {
                        Text(text = stringResource(id = R.string.text_registration_successful),
                            color = Color.Green,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )

                        LaunchedEffect(Unit) {
                            delay(1500)
                            navigateToLoginScreen(navController)
                        }
                    }

                    AuthenticationViewModel.RegisterState.FAILURE -> {
                        Text(text = stringResource(id = R.string.text_registration_failed, registerError!!),
                            color = Color.Red,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )

                        LaunchedEffect(Unit) {
                            delay(2000)
                            authVM.setRegisterStateIdle()
                        }
                    }

                    else -> {}
                }
            }

        }
    }

}


private fun navigateToLoginScreen(navController: NavController) {
    navController.navigate("login_screen") {
        popUpTo("register_screen") { inclusive = true }
    }
}