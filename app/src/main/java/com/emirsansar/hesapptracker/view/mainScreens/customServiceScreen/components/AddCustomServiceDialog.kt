package com.emirsansar.hesapptracker.view.mainScreens.customServiceScreen.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.ui.theme.DarkThemeColors
import com.emirsansar.hesapptracker.ui.theme.LightThemeColors

@Composable
internal fun AddCustomServiceDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    alertMessage: String,
    isDarkMode: Boolean
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
            }) {
                Text(
                    text = stringResource(id = R.string.button_confirm),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Green
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(
                    text = stringResource(id = R.string.button_cancel),
                    fontWeight = FontWeight.Medium,
                    color = Color.Red
                )
            }
        },
        title = {
            Text(
                text = stringResource(id = R.string.label_adding_service),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDarkMode) Color.White else Color.Black
            )
        },
        text = { Text(text = alertMessage,
            color = if(isDarkMode) Color.White else Color.Black)
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = if (isDarkMode) DarkThemeColors.DrawerContentColor
        else LightThemeColors.DrawerContentColor
    )

}