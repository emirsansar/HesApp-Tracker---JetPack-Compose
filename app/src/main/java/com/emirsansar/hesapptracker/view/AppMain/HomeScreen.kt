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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.DrawerValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.manager.AppManager
import com.emirsansar.hesapptracker.ui.theme.DarkThemeColors
import com.emirsansar.hesapptracker.ui.theme.LightThemeColors
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
                drawerState,
                scope,
                context,
                { showLogoutDialog = it },
                { showChangeLanguageDialog = it},
                appManager
            )
        },
        content = {
            Scaffold(
                topBar = {
                    TopBarHomeScreen(
                        scope,
                        drawerState,
                        appManager.isDarkMode.value
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
                        WelcomeMessage(userFullName, appManager.isDarkMode.value)

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

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(text = stringResource(id = R.string.label_log_out), fontSize = 18.sp, fontWeight = FontWeight.Medium,
                color = if (appManager.isDarkMode.value) Color.White else Color.Black)  },
            text = { Text(stringResource(id = R.string.text_log_out_confirmation), fontSize = 16.sp,
                color = if (appManager.isDarkMode.value) Color.White else Color.Black) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        logOut(context, scope)
                    }
                ) {
                    Text(stringResource(id = R.string.label_log_out), fontSize = 15.sp, color = Color.Green)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(id = R.string.button_cancel), fontSize = 15.sp, color = Color.Red)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = if (appManager.isDarkMode.value) DarkThemeColors.DrawerContentColor
                             else LightThemeColors.DrawerContentColor
        )
    }

    if (showChangeLanguageDialog) {
        ChangeLanguageDialog(context, appManager)
    }

}

// Composable:

@Composable
private fun SubscriptionSummaryCard(
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
private fun WelcomeMessage(userFullName: String, isDarkMode: Boolean) {
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

@Composable
private fun DrawerContent(
    drawerState: DrawerState,
    scope: CoroutineScope,
    context: Context,
    setShowLogoutDialog: (Boolean) -> Unit,
    setShowSwitchLanguageMessage: (Boolean) -> Unit,
    appManager: AppManager
) {
    ModalDrawerSheet (
        modifier = Modifier
    ) {
        Column(
            modifier = Modifier
                .width(310.dp)
                .fillMaxHeight()
                .background(
                    if (appManager.isDarkMode.value) DarkThemeColors.DrawerContentColor
                    else LightThemeColors.DrawerContentColor
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.label_settings),
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (appManager.isDarkMode.value) Color.White else Color.Black
            )

            Divider(thickness = 1.dp, color = if (appManager.isDarkMode.value) Color.White else Color.Black)

            // Light/Dark Theme Switch
            ThemeSwitch(context, appManager)

            // Language Selection
            ChangeLanguageButton(context, appManager, scope, drawerState) { setShowSwitchLanguageMessage(true) }

            Divider(thickness = 0.5.dp, color = if (appManager.isDarkMode.value) Color.White else Color.Black)

            // Logout Button
            LogOutButton(scope, drawerState) { setShowLogoutDialog(true) }
        }
    }

}

@Composable
private fun ThemeSwitch(context: Context, appManager: AppManager) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.icon_dark_mode),
            contentDescription = "Dark Mode Icon",
            tint = if (appManager.isDarkMode.value) Color.White else Color.Black,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = R.string.label_dark_mode),
            fontSize = 18.sp,
            color = if (appManager.isDarkMode.value) Color.White else Color.Black
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = appManager.isDarkMode.value,
            onCheckedChange = { isChecked ->
                appManager.toggleTheme()
                updateTheme(context, isChecked)
            }
        )
    }
}


@Composable
private fun LogOutButton(
    scope: CoroutineScope,
    drawerState: DrawerState,
    onLogOutClick: () -> Unit
) {
    TextButton(
        onClick = {
            scope.launch {
                drawerState.close()
            }
            onLogOutClick()
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.icon_logout),
                contentDescription = "Logout Icon",
                tint = Color.Red,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.label_log_out),
                fontSize = 16.sp,
                color = Color.Red
            )
        }
    }
}

@Composable
private fun ChangeLanguageButton(
    context: Context,
    appManager: AppManager,
    scope: CoroutineScope,
    drawerState: DrawerState,
    onSwitchLanguageClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val languages = listOf("TR", "EN")
    val currentLanguage = appManager.getLanguage()
    var selectedLanguage by remember { mutableStateOf(currentLanguage) }

    Row(
        modifier = Modifier
            .width(300.dp)
            .clickable(
                onClick = { expanded = true },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.icon_language),
            contentDescription = "Language Icon",
            tint = if (appManager.isDarkMode.value) Color.White else Color.Black,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = R.string.label_change_language),
            fontSize = 18.sp,
            color = if (appManager.isDarkMode.value) Color.White else Color.Black
        )
        Spacer(modifier = Modifier.weight(1f))

        Column {
            OutlinedTextField(
                modifier = Modifier
                    .width(90.dp)
                    .height(50.dp),
                value = selectedLanguage.uppercase(),
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Dropdown Icon",
                        modifier = Modifier
                            .clickable(
                                onClick = { expanded = !expanded },
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ),
                        tint = if (appManager.isDarkMode.value) Color.White else Color.Black
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = if (appManager.isDarkMode.value) Color.White else Color.Black,
                    focusedBorderColor = if (appManager.isDarkMode.value) Color.LightGray else Color.DarkGray ,
                    unfocusedLabelColor = Color.Gray
                )
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(90.dp)
                    .background(
                        color = if (appManager.isDarkMode.value) Color.DarkGray else Color.LightGray
                    )
            ) {
                languages.forEach { language ->
                    DropdownMenuItem( onClick = {
                        selectedLanguage = language.lowercase()
                        expanded = false
                        scope.launch { drawerState.close() }
                        onSwitchLanguageClick()
                        changeLanguage(selectedLanguage, context, appManager)
                    }) {
                        Text(
                            text = language,
                            fontSize = 16.sp,
                            color = if (appManager.isDarkMode.value) Color.White else Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChangeLanguageDialog(
    context: Context,
    appManager: AppManager
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = if (appManager.isDarkMode.value) DarkThemeColors.DrawerContentColor else LightThemeColors.DrawerContentColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.text_changing_language),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = if (appManager.isDarkMode.value) Color.White else Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            CircularProgressIndicator(
                color = if (appManager.isDarkMode.value) Color.Cyan else Color.Blue,
                modifier = Modifier.size(18.dp)
            )
        }
    }

    LaunchedEffect(Unit) {
        delay(1500)
        restartMainActivityForLanguageChange(context)
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
            Toast.makeText(context, R.string.text_error_log_out, Toast.LENGTH_SHORT).show()
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

// Sets the new language by checking the current language, then restarts the app.
private fun changeLanguage(selectedLanguage: String, context: Context, appManager: AppManager) {
    if (appManager.getLanguage() != selectedLanguage) {
        appManager.setLanguage(context, selectedLanguage)
    }
}

// Restarts the main activity to apply the language change.
private fun restartMainActivityForLanguageChange(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    context.startActivity(intent)
    (context as Activity).finish()
}