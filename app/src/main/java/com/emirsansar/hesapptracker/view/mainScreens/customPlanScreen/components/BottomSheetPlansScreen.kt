package com.emirsansar.hesapptracker.view.mainScreens.customPlanScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.TextButton
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun BottomSheetContentForPlansScreen(
    isSuccess: Boolean,
    coroutineScope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState,
    onFinish: () -> Unit,
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isSuccess) stringResource(id = R.string.label_success) else stringResource(id = R.string.label_error),
            fontSize = 22.sp,
            color = if (isSuccess) Color.Green else Color.Red,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = if (isSuccess) stringResource(id = R.string.text_selected_plan_added) else stringResource(id = R.string.text_error_selected_plan_added),
            fontSize = 16.sp,
            color = if (isDarkMode) Color.White else Color.Black,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        TextButton(onClick = {
            coroutineScope.launch {
                scaffoldState.bottomSheetState.collapse()
                onFinish()
            }
        }) {
            Text(text = stringResource(id = R.string.button_ok), fontSize = 16.sp, fontWeight = FontWeight.Medium,
                color = if (isDarkMode) Color.White else Color.Black)
        }
    }

}