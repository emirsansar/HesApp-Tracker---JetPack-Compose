package com.emirsansar.hesapptracker.view.AppMain

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.viewModel.UserSubscriptionViewModel
import com.emirsansar.hesapptracker.viewModel.UserViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    modifier: Modifier,
    userSubVM: UserSubscriptionViewModel = UserSubscriptionViewModel(),
    userVM: UserViewModel = UserViewModel()
){
    val fetchingSummaryState by userSubVM.fetchingSummaryState.observeAsState(UserSubscriptionViewModel.FetchingSummaryState.IDLE)
    val fetchedSubsCount by userSubVM.totalSubscriptionCount.observeAsState(0)
    val fetchedMonthlySpend by userSubVM.totalMonthlySpending.observeAsState(0.0)
    var userFullName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        userVM.fetchUserFullName { fullName ->
            userFullName = fullName ?: "Unknown"
        }

        userSubVM.fetchSubscriptionsSummary()
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFFe3e5e6)
    ) {
        Scaffold(
            topBar = { TopBarHomeScreen() },
            backgroundColor = Color.Transparent,
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    WelcomeMessage(userFullName)

                    SubscriptionSummaryCard(
                        fetchingSummaryState = fetchingSummaryState,
                        subscriptionCount = fetchedSubsCount,
                        monthlySpend = fetchedMonthlySpend,
                        annualSpend = fetchedMonthlySpend * 12
                    )
                }
            }
        )
    }

}

@Composable
private fun SubscriptionSummaryCard(
    fetchingSummaryState: UserSubscriptionViewModel.FetchingSummaryState,
    subscriptionCount: Int,
    monthlySpend: Double,
    annualSpend: Double
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .padding(top = 32.dp)
            .background(
                color = Color.LightGray,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SummaryRow(
            label = "Subscriptions Count:",
            value = subscriptionCount.toString(),
            icon = R.drawable.icon_numbers,
            showProgress = fetchingSummaryState == UserSubscriptionViewModel.FetchingSummaryState.IDLE
        )

        SummaryRow(
            label = "Monthly Spending:",
            value = String.format("%.2f ₺", monthlySpend),
            icon = R.drawable.icon_calendar,
            showProgress = fetchingSummaryState == UserSubscriptionViewModel.FetchingSummaryState.IDLE
        )

        SummaryRow(
            label = "Annual Spending:",
            value = String.format("%.2f ₺", annualSpend),
            icon = R.drawable.icon_calendar,
            showProgress = fetchingSummaryState == UserSubscriptionViewModel.FetchingSummaryState.IDLE
        )
    }
}

@Composable
private fun SummaryRow(label: String, value: String, icon: Int, showProgress: Boolean) {
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
                modifier = Modifier.size(24.dp).padding(end = 8.dp)
            )
            Text(text = label, fontSize = 19.sp, fontWeight = FontWeight.Medium)
        }

        if (showProgress) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp))
        } else {
            Text(text = value, fontSize = 19.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun TopBarHomeScreen() {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hesapp),
                    contentDescription = "application logo",
                    modifier = Modifier
                        .width(130.dp)
                )
            }
        },
        actions = {
            IconButton(onClick = {
                // Side Menu will open.
            }) {
                Icon( imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = Color.DarkGray )
            }
        },
        backgroundColor = Color.LightGray,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun WelcomeMessage(userFullName: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(top = 30.dp,start = 16.dp, end = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.icon_waving_hand),
                contentDescription = "waving hand icon",
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = " Welcome,",
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Text(
            text = userFullName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
