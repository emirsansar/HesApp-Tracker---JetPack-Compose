package com.emirsansar.hesapptracker.view.mainScreens.editSubscriptionScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.ui.theme.DarkThemeColors
import com.emirsansar.hesapptracker.ui.theme.LightThemeColors

@Composable
internal fun BottomSheetContentEditSubscriptionScreen(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    isDarkMode: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isDarkMode) DarkThemeColors.DrawerContentColor
                else LightThemeColors.DrawerContentColor
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(id = R.string.text_question_changing_service),
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 20.dp),
            color = if (isDarkMode) Color.White else Color.Black
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(
                text = stringResource(id = R.string.button_cancel),
                color = Color.Red,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable(onClick = onCancel)
                    .padding(16.dp)
            )

            Text(
                text = stringResource(id = R.string.button_confirm),
                color = Color.Green,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable(onClick = onConfirm)
                    .padding(16.dp)
            )
        }
    }
}