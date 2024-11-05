package com.emirsansar.hesapptracker.view.authenticationScreens.components

import android.content.Context
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emirsansar.hesapptracker.R

@Composable
fun GoogleButton(
    isLoginButton: Boolean,
    onClick: () -> Unit,
    context: Context
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.80f),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_google),
                contentDescription = "Google Icon",
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = if (isLoginButton) context.getString(R.string.label_login_by_google)
                       else context.getString(R.string.label_register_by_google),
                fontSize = 15.sp,
                color = Color.Black
            )
        }
    }
}