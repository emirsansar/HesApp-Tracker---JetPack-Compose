package com.emirsansar.hesapptracker.view.mainScreens.userSubscriptionScreen.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.model.UserSubscription
import com.emirsansar.hesapptracker.ui.theme.DarkThemeColors
import com.emirsansar.hesapptracker.ui.theme.LightThemeColors

@Composable
internal fun ConfirmationRemoveSubDialog(
    selectedSubscription: UserSubscription,
    setShowRemoveDialog: (Boolean) -> Unit,
    onRemove: () -> Unit,
    isDarkMode: Boolean
) {
    AlertDialog(
        onDismissRequest = { setShowRemoveDialog(true) },
        title = { Text(text = "Remove Subscription", fontSize = 17.sp, fontWeight = FontWeight.Medium,
            color = if (isDarkMode) Color.White else Color.Black)  },
        text = { Text(
            "Are you sure to remove the ${selectedSubscription.serviceName}?", fontSize = 15.sp,
            color = if (isDarkMode) Color.White else Color.Black) },
        confirmButton = {
            TextButton(
                onClick = {
                    setShowRemoveDialog(false)
                    onRemove()
                }
            ) {
                Text(stringResource(id = R.string.button_remove), fontSize = 14.sp, color = Color.Green)
            }
        },
        dismissButton = {
            TextButton(onClick = { setShowRemoveDialog(false) }) {
                Text(stringResource(id = R.string.button_cancel), fontSize = 14.sp, color = Color.Red)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = if (isDarkMode) DarkThemeColors.DrawerContentColor
        else LightThemeColors.DrawerContentColor
    )
}