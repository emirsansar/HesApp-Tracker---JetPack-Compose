@file:OptIn(ExperimentalMaterial3Api::class)

package com.emirsansar.hesapptracker.view.AppMain

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.view.Authentication.AuthenticationActivity
import com.emirsansar.hesapptracker.viewModel.UserSubscriptionViewModel
import com.emirsansar.hesapptracker.viewModel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showLogoutDialog by remember { mutableStateOf(false) }

    val sharedPref = context.getSharedPreferences("theme_pref", Context.MODE_PRIVATE)
    var isDarkMode by remember { mutableStateOf(sharedPref.getBoolean("isDarkMode", false)) }

    val currentTheme = if (isDarkMode) {
        "Dark Mode is ON"
    } else {
        "Dark Mode is OFF"
    }

    LaunchedEffect(Unit) {
        userVM.fetchUserFullName { fullName ->
            userFullName = fullName ?: "Unknown"
        }

        userSubVM.fetchSubscriptionsSummary()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                drawerState,
                scope,
                context,
                { showLogoutDialog = it },
                isDarkMode,
                { isDark -> isDarkMode = isDark }
            )
        },
        content = {
            Scaffold(
                topBar = { TopBarHomeScreen(scope, drawerState) },
                backgroundColor = Color(0xFFe3e5e6),
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
    )

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(text = "Log Out", fontSize = 18.sp, fontWeight = FontWeight.Medium) },
            text = { Text("Are you sure you want to log out?", fontSize = 16.sp) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        logOut(context, scope)
                    }
                ) {
                    Text("Log Out", color = Color.Green)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = Color.Red)
                }
            },
            modifier = Modifier.background(Color.LightGray),
            shape = RoundedCornerShape(16.dp)
        )
    }

}

// Composable:

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
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp)
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
private fun TopBarHomeScreen(
    scope: CoroutineScope,
    drawerState: DrawerState
) {
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
                scope.launch { drawerState.open() }
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

@Composable
private fun DrawerContent(
    drawerState: DrawerState,
    scope: CoroutineScope,
    context: Context,
    setShowLogoutDialog: (Boolean) -> Unit,
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit
) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .width(250.dp)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )

            Divider(color = Color.Black, thickness = 1.dp)

            // Light/Dark Theme Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Dark Mode", fontSize = 18.sp)
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { isChecked ->
                        onDarkModeToggle(isChecked)
                        updateTheme(context, isChecked)
                    }
                )
            }

            // Language Selection
            Text(
                text = "Language Selection",
                fontSize = 18.sp,
                modifier = Modifier.clickable { /* TODO: Language selection action */ }
            )

            // Logout Button
            TextButton(
                onClick = {
                    scope.launch {
                        drawerState.close()
                    }
                    setShowLogoutDialog(true)
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(text = "Log Out", fontSize = 16.sp, color = Color.Red)
            }
        }
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
            Toast.makeText(context, "Logout failed, please try again.", Toast.LENGTH_SHORT).show()
        }
    }
}

// Updates and applies the theme mode (dark or light) based on user selection.
private fun updateTheme(context: Context, isDarkMode: Boolean) {
    val sharedPref = context.getSharedPreferences("theme_pref", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putBoolean("isDarkMode", isDarkMode)
    editor.apply()

    val mode = if (isDarkMode) {
        AppCompatDelegate.MODE_NIGHT_YES
    } else {
        AppCompatDelegate.MODE_NIGHT_NO
    }
    AppCompatDelegate.setDefaultNightMode(mode)
}