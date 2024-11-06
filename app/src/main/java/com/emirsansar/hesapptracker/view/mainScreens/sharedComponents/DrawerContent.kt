@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.emirsansar.hesapptracker.view.mainScreens.sharedComponents

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.manager.AppManager
import com.emirsansar.hesapptracker.ui.theme.DarkThemeColors
import com.emirsansar.hesapptracker.ui.theme.LightThemeColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun DrawerContent(
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
                fontSize = 22.sp,
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
private fun ThemeSwitch(
    context: Context,
    appManager: AppManager
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.icon_dark_mode),
            contentDescription = "Dark Mode Icon",
            tint = if (appManager.isDarkMode.value) Color.White else Color.Black,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = R.string.label_dark_mode),
            fontSize = 17.sp,
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
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.label_log_out),
                fontSize = 15.sp,
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
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = R.string.label_change_language),
            fontSize = 17.sp,
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

// Functions:

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