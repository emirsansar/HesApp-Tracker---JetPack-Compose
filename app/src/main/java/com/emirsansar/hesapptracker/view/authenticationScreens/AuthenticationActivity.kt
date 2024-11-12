package com.emirsansar.hesapptracker.view.authenticationScreens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.manager.AppManager
import com.emirsansar.hesapptracker.manager.googleAuth.GoogleAuthUiClient
import com.emirsansar.hesapptracker.ui.theme.HesAppTrackerTheme
import com.emirsansar.hesapptracker.viewModel.AuthenticationViewModel
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

class AuthenticationActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HesAppTrackerTheme {
                // A surface container using the 'background' color from the theme
                AuthenticationScreen(
                    context = applicationContext,
                    lifecycleScope = lifecycleScope,
                    googleAuthUiClient = googleAuthUiClient
                )
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AuthenticationScreen(
    context: Context,
    lifecycleScope: LifecycleCoroutineScope,
    googleAuthUiClient: GoogleAuthUiClient
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val navController = rememberNavController()

        val appManager = AppManager.getInstance(context)
        val isDarkMode = appManager.isDarkMode.value

        val viewModel = viewModel<AuthenticationViewModel>()
        val state by viewModel.stateForGoogle.collectAsStateWithLifecycle()

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult(),
            onResult = { result ->
                if(result.resultCode == Activity.RESULT_OK) {
                    lifecycleScope.launch {
                        val signInResult = googleAuthUiClient.signInWithIntent(
                            intent = result.data ?: return@launch
                        )
                        viewModel.onSignInResult(signInResult)
                    }
                }
            }
        )

        NavHost(navController = navController, startDestination = "login_screen") {
            composable("login_screen") {
                LoginScreen(
                    navController = navController,
                    state = state,
                    onSignInClick = {
                        performSignIn(googleAuthUiClient, lifecycleScope, launcher, context)
                    },
                    isDarkMode = isDarkMode
                )
            }

            composable("register_screen") {
                RegisterScreen(
                    navController = navController,
                    state = state,
                    onSignUpClick = {
                        performSignIn(googleAuthUiClient, lifecycleScope, launcher, context)
                    },
                    isDarkMode = isDarkMode
                )
            }

        }
    }
}

// Functions:

// Initiates the sign-in process using the GoogleAuthUiClient.
// If the sign-in intent sender is not null, it launches the intent.
private fun performSignIn(
    googleAuthUiClient: GoogleAuthUiClient,
    lifecycleScope: LifecycleCoroutineScope,
    launcher: ActivityResultLauncher<IntentSenderRequest>,
    context: Context
) {
    lifecycleScope.launch {
        val signInIntentSender = googleAuthUiClient.signIn()
        if (signInIntentSender != null) {
            launcher.launch(IntentSenderRequest.Builder(signInIntentSender).build())
        } else {
            showErrorToast(context, context.getString(R.string.text_login_failed))
        }
    }
}

// Displays a toast message to inform the user of login failures.
private fun showErrorToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}


@Preview(showBackground = true)
@Composable
fun AuthenticationScreenPreview() {
    HesAppTrackerTheme {

    }
}