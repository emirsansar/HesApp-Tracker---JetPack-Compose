package com.emirsansar.hesapptracker.view.mainScreens.homeScreen.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.ui.theme.DarkThemeColors
import com.emirsansar.hesapptracker.ui.theme.LightThemeColors
import com.emirsansar.hesapptracker.view.mainScreens.MainActivity
import com.emirsansar.hesapptracker.view.authenticationScreens.AuthenticationActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun LogOutDialog(
    context: Context,
    scope: CoroutineScope,
    isDarkMode: Boolean,
    setShowLogOutDialog: (Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = { setShowLogOutDialog(true) },
        title = { Text(text = stringResource(id = R.string.label_log_out), fontSize = 18.sp, fontWeight = FontWeight.Medium,
            color = if (isDarkMode) Color.White else Color.Black)  },
        text = { Text(
            stringResource(id = R.string.text_log_out_confirmation), fontSize = 16.sp,
            color = if (isDarkMode) Color.White else Color.Black) },
        confirmButton = {
            TextButton(
                onClick = {
                    setShowLogOutDialog(false)
                    logOut(context, scope)
                }
            ) {
                Text(stringResource(id = R.string.label_log_out), fontSize = 15.sp, color = Color.Green)
            }
        },
        dismissButton = {
            TextButton(onClick = { setShowLogOutDialog(false) }) {
                Text(stringResource(id = R.string.button_cancel), fontSize = 15.sp, color = Color.Red)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = if (isDarkMode) DarkThemeColors.DrawerContentColor
        else LightThemeColors.DrawerContentColor
    )
}

@Composable
internal fun ChangeLanguageDialog(
    context: Context,
    isDarkMode: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = if (isDarkMode) DarkThemeColors.DrawerContentColor else LightThemeColors.DrawerContentColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.text_changing_language),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDarkMode) Color.White else Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            CircularProgressIndicator(
                color = if (isDarkMode) Color.Cyan else Color.Blue,
                modifier = Modifier.size(17.dp)
            )
        }
    }

    LaunchedEffect(Unit) {
        delay(1500)
        restartMainActivityForLanguageChange(context)
    }
}


// Functions:

// Logs out the user, finishes the current activity, and restarts the AuthenticationActivity.
private fun logOut(context: Context, scope: CoroutineScope) {
    scope.launch {
        try {
            FirebaseAuth.getInstance().signOut()

            delay(1000L)

            (context as Activity).finish()
            context.startActivity(Intent(context, AuthenticationActivity::class.java))
        } catch (e: Exception) {
            Toast.makeText(context, R.string.text_error_log_out, Toast.LENGTH_SHORT).show()
        }
    }
}

// Restarts the main activity to apply the language change.
private fun restartMainActivityForLanguageChange(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    context.startActivity(intent)
    (context as Activity).finish()
}