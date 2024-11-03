package com.emirsansar.hesapptracker.view.mainScreens.plansScreen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.model.Plan
import com.emirsansar.hesapptracker.ui.theme.DarkThemeColors
import com.emirsansar.hesapptracker.ui.theme.LightThemeColors

@Composable
internal fun AddPlanDialog(
    plan: Plan,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit,
    isDarkMode: Boolean
) {
    var personCount by remember { mutableStateOf("1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.label_adding_plan),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) Color.White else Color.Black
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(id = R.string.text_plan_details, plan.planName, plan.planPrice),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = if (isDarkMode) Color.White else Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(id = R.string.label_how_many_users),
                    fontSize = 16.sp,
                    color = if (isDarkMode) Color.White else Color.Black
                )

                TextField(
                    value = personCount,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) {
                            personCount = it
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = if (isDarkMode) Color.White else Color.Black,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = if (isDarkMode) Color.Gray else Color.DarkGray,
                        focusedLabelColor = if (isDarkMode) Color.LightGray else Color.DarkGray,
                        unfocusedLabelColor = if (isDarkMode) Color.LightGray else Color.DarkGray,
                        cursorColor = if (isDarkMode) Color.White else Color.Black,
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    personCount.toIntOrNull()?.let { count ->
                        onConfirm(count)
                    }
                    onDismiss()
                }
            ) {
                Text(stringResource(id = R.string.button_confirm), fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color.Green)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(id = R.string.button_cancel), fontSize = 17.sp, fontWeight = FontWeight.Medium, color = Color.Red)
            }
        },
        containerColor = if (isDarkMode) DarkThemeColors.DrawerContentColor
        else LightThemeColors.DrawerContentColor
    )

}
