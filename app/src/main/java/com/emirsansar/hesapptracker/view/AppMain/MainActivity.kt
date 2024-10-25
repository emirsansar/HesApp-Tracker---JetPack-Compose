package com.emirsansar.hesapptracker.view.AppMain

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.emirsansar.hesapptracker.ui.theme.HesAppTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        applySavedTheme(this)

        setContent {
            HesAppTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainActivityScreen()
                }
            }
        }
    }
}

sealed class BottomNavItem(val route: String, val icon: ImageVector, val title: String) {
    object Home : BottomNavItem("home_screen", Icons.Default.Home, "Home")
    object Services : BottomNavItem("services_screen", Icons.Default.List, "Services")
    object UserSubscriptions : BottomNavItem("usersubscriptions_screen", Icons.Default.Person, "Subscriptions")
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivityScreen(modifier: Modifier = Modifier) {
    val items = listOf(BottomNavItem.Home, BottomNavItem.Services, BottomNavItem.UserSubscriptions)
    val selectedBar = remember { mutableStateOf(0)}
    val navController = rememberNavController()

    Scaffold(
        content = { paddingValues ->
            NavHost(navController = navController, startDestination = "home_screen") {
                composable("home_screen") {
                    HomeScreen(modifier = Modifier.padding(paddingValues))
                }
                composable("services_screen") {
                    ServicesScreen(
                        modifier = Modifier.padding(paddingValues),
                        navController = navController
                    )
                }
                composable("usersubscriptions_screen") {
                    UserSubscriptionsScreen(modifier = Modifier.padding(paddingValues))
                }
                composable("service_plans_screen/{serviceName}") { backStackEntry ->
                    val serviceName = backStackEntry.arguments?.getString("serviceName")
                    PlansScreen(
                        modifier = Modifier.padding(paddingValues),
                        serviceName = serviceName!!,
                        navController = navController
                    )
                }
            }
        },
        bottomBar = {
            ApplicationNavigationBar(items, selectedBar, navController)
        }
    )
}

@Composable
private fun ApplicationNavigationBar(
    items: List<BottomNavItem>,
    selectedBar: MutableState<Int>,
    navController: NavController
) {
    NavigationBar(containerColor = Color.LightGray) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedBar.value == index,
                onClick = {
                    if (selectedBar.value != index) {
                        selectedBar.value = index
                        navigateAndClearBackStack(navController, item.route)
                    }
                },
                label = { Text(item.title) },
                icon = { Icon(item.icon, contentDescription = item.title) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFFc3c3c3),
                    selectedTextColor = Color.Black,
                    unselectedTextColor = Color.DarkGray,
                    selectedIconColor = Color.Black,
                    unselectedIconColor = Color.DarkGray,
                )
            )
        }
    }
}


// Navigate to a new destination while clearing all previous screens from the back stack.
private fun navigateAndClearBackStack(
    navController: NavController,
    destination: String
) {
    navController.navigate(destination) {
        popUpTo(navController.graph.startDestinationId) { inclusive = false }
    }
}

// Applies the saved theme mode (dark or light) based on user preference.
private fun applySavedTheme(context: Context) {
    val sharedPref = context.getSharedPreferences("theme_pref", Context.MODE_PRIVATE)
    val isDarkMode = sharedPref.getBoolean("isDarkMode", false)

    val mode = if (isDarkMode) {
        AppCompatDelegate.MODE_NIGHT_YES
    } else {
        AppCompatDelegate.MODE_NIGHT_NO
    }
    AppCompatDelegate.setDefaultNightMode(mode)
}


@Preview(showBackground = true)
@Composable
fun AppMainActivityPreview() {
    HesAppTrackerTheme {
        MainActivityScreen()
    }
}