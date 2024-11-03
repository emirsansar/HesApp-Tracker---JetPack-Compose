package com.emirsansar.hesapptracker.view.mainScreens.homeScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.ui.theme.DarkThemeColors
import com.emirsansar.hesapptracker.ui.theme.LightThemeColors
import com.emirsansar.hesapptracker.viewModel.UserSubscriptionViewModel

@Composable
internal fun SubscriptionSummaryCard(
    fetchingSummaryState: UserSubscriptionViewModel.FetchingSummaryState,
    subscriptionCount: Int,
    monthlySpend: Double,
    annualSpend: Double,
    isDarkMode: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .padding(top = 32.dp)
            .background(
                color = if (isDarkMode) DarkThemeColors.CardColorHomeScreen else LightThemeColors.CardColorHomeScreen,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SummaryRow(
            label = stringResource(id = R.string.label_total_sub_count),
            value = subscriptionCount.toString(),
            icon = R.drawable.icon_numbers,
            showProgress = fetchingSummaryState == UserSubscriptionViewModel.FetchingSummaryState.IDLE,
            isDarkMode
        )

        SummaryRow(
            label = stringResource(id = R.string.label_monthly_spend),
            value = String.format("%.2f ₺", monthlySpend),
            icon = R.drawable.icon_calendar,
            showProgress = fetchingSummaryState == UserSubscriptionViewModel.FetchingSummaryState.IDLE,
            isDarkMode
        )

        SummaryRow(
            label = stringResource(id = R.string.label_annual_spend),
            value = String.format("%.2f ₺", annualSpend),
            icon = R.drawable.icon_calendar,
            showProgress = fetchingSummaryState == UserSubscriptionViewModel.FetchingSummaryState.IDLE,
            isDarkMode
        )
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    icon: Int,
    showProgress: Boolean,
    isDarkMode: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = if (isDarkMode) Color.White else Color.Black,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp)
            )

            Text(text = label, fontSize = 18.sp, fontWeight = FontWeight.Medium,
                color = if (isDarkMode) Color.White else Color.Black)
        }

        if (showProgress) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp))
        } else {
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Medium,
                color = if (isDarkMode) Color.White else Color.Black)
        }
    }
}