package com.emirsansar.hesapptracker.view.authenticationScreens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.ui.theme.DarkThemeColors
import com.emirsansar.hesapptracker.ui.theme.LightThemeColors
import kotlinx.coroutines.delay

@Composable
internal fun HyperlinkTextForAuthScreens(
    text: String,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = text,
        style = MaterialTheme.typography.body2.copy(
            textDecoration = TextDecoration.Underline
        ),
        color = if (isDarkMode) Color.White else Color.Black,
        modifier = Modifier
            .padding(top = 16.dp)
            .clickable { onClick() }
    )
}

@Composable
internal fun ErrorMessageForAuthScreen(message: String) {
    Text(
        text = message,
        style = MaterialTheme.typography.body2.copy(
            textDecoration = TextDecoration.Underline,
            color = Color.Red
        ),
        textAlign = TextAlign.Center
    )
}

@Composable
internal fun HeaderTextForAuthScreen(message: String, isDarkMode: Boolean){
    Text(
        text = message,
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold,
        color = if (isDarkMode) Color.White else Color.Black,
        modifier = Modifier.padding(bottom = 16.dp, top = 15.dp)
    )
}

@Composable
internal fun AppLogoForAuthScreen(){
    Box (modifier = Modifier
        .fillMaxWidth()
        .height(90.dp)
        .padding(bottom = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.hesapp),
            contentDescription = "Application Logo",
            modifier = Modifier
                .width(220.dp)
        )
    }
}

@Composable
internal fun LoginSuccessDialog (
    isDarkMode: Boolean,
    navigateToApplication: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.75f)
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(10.dp),
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
                text = stringResource(id = R.string.label_success),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Green,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(id = R.string.text_login_successful),
                fontSize = 15.sp,
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
        navigateToApplication()
    }
}

@Composable
internal fun ErrorDialogForAuthScreen(
    message: String,
    isDarkMode: Boolean,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.75f)
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(10.dp),
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
                text = stringResource(id = R.string.label_error),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Red,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = message,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDarkMode) Color.White else Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(15.dp))

            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(
                    text = stringResource(id = R.string.button_close),
                    color = if (isDarkMode) Color.White else Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }

}

@Composable
internal fun RegisterSuccessDialog (
    isDarkMode: Boolean,
    onDismiss: () ->  Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.75f)
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(10.dp),
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
                text = stringResource(id = R.string.label_success),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Green,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(id = R.string.text_registration_successful),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDarkMode) Color.White else Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(15.dp))

            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(
                    text = stringResource(id = R.string.button_close),
                    color = if (isDarkMode) Color.White else Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }

}