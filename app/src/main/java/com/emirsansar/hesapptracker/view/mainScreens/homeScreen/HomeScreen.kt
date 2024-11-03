@file:OptIn(ExperimentalMaterial3Api::class)

package com.emirsansar.hesapptracker.view.mainScreens.homeScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.manager.AppManager
import com.emirsansar.hesapptracker.ui.theme.DarkThemeColors
import com.emirsansar.hesapptracker.ui.theme.LightThemeColors
import com.emirsansar.hesapptracker.view.mainScreens.homeScreen.components.ChangeLanguageDialog
import com.emirsansar.hesapptracker.view.mainScreens.homeScreen.components.DrawerContent
import com.emirsansar.hesapptracker.view.mainScreens.homeScreen.components.LogOutDialog
import com.emirsansar.hesapptracker.view.mainScreens.homeScreen.components.SubscriptionSummaryCard
import com.emirsansar.hesapptracker.viewModel.UserSubscriptionViewModel
import com.emirsansar.hesapptracker.viewModel.UserViewModel
import kotlinx.coroutines.CoroutineScope
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
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showChangeLanguageDialog by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val appManager = AppManager.getInstance(context)


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
                drawerState = drawerState,
                scope = scope,
                context = context,
                setShowLogoutDialog = { showLogoutDialog = it },
                setShowSwitchLanguageMessage = { showChangeLanguageDialog = it},
                appManager = appManager
            )
        },
        content = {
            Scaffold(
                topBar = {
                    TopBarHomeScreen(
                        scope = scope,
                        drawerState = drawerState,
                        isDarkMode = appManager.isDarkMode.value
                    )
                },
                backgroundColor = if (appManager.isDarkMode.value) DarkThemeColors.BackgroundColor
                                  else LightThemeColors.BackgroundColor,
                content = { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        WelcomeMessage(
                            userFullName = userFullName,
                            isDarkMode = appManager.isDarkMode.value
                        )

                        SubscriptionSummaryCard(
                            fetchingSummaryState = fetchingSummaryState,
                            subscriptionCount = fetchedSubsCount,
                            monthlySpend = fetchedMonthlySpend,
                            annualSpend = fetchedMonthlySpend * 12,
                            isDarkMode = appManager.isDarkMode.value
                        )
                    }
                }
            )
        }
    )

    if (showLogoutDialog) {
        LogOutDialog(
            context = context,
            scope = scope,
            isDarkMode = appManager.isDarkMode.value,
            setShowLogOutDialog = { showLogoutDialog = it }
        )
    }

    if (showChangeLanguageDialog) {
        ChangeLanguageDialog(context = context, isDarkMode = appManager.isDarkMode.value)
    }

}

// Components:

@Composable
private fun TopBarHomeScreen(
    scope: CoroutineScope,
    drawerState: DrawerState,
    isDarkMode: Boolean
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
                Icon( imageVector = Icons.Default.Settings, contentDescription = "Settings",
                    tint = if (isDarkMode) Color.White else Color.Black )
            }
        },
        backgroundColor = if (isDarkMode) DarkThemeColors.BarColor else LightThemeColors.BarColor,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun WelcomeMessage(
    userFullName: String,
    isDarkMode: Boolean
) {
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
                tint = if (isDarkMode) Color.White else Color.Black,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = stringResource(id = R.string.welcome),
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkMode) Color.White else Color.Black
            )
        }

        Text(
            text = userFullName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDarkMode) Color.White else Color.Black
        )
    }
}