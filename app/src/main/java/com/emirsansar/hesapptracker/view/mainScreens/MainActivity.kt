@file:OptIn(ExperimentalMaterial3Api::class)

package com.emirsansar.hesapptracker.view.mainScreens

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.emirsansar.hesapptracker.manager.googleAuth.GoogleAuthUiClient
import com.emirsansar.hesapptracker.manager.AppManager
import com.emirsansar.hesapptracker.ui.theme.DarkThemeColors
import com.emirsansar.hesapptracker.ui.theme.HesAppTrackerTheme
import com.emirsansar.hesapptracker.ui.theme.LightThemeColors
import com.emirsansar.hesapptracker.view.mainScreens.homeScreen.HomeScreen
import com.emirsansar.hesapptracker.view.mainScreens.servicesScreen.ServicesScreen
import com.emirsansar.hesapptracker.view.mainScreens.userSubscriptionScreen.UserSubscriptionsScreen
import com.emirsansar.hesapptracker.view.mainScreens.plansScreen.PlansScreen
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HesAppTrackerTheme {
                // A surface container using the 'background' color from the theme
                MainActivityScreen(
                    context = applicationContext,
                    lifecycleScope = lifecycleScope,
                    googleAuthUiClient = googleAuthUiClient
                )
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
fun MainActivityScreen(
    context: Context,
    lifecycleScope: CoroutineScope,
    googleAuthUiClient: GoogleAuthUiClient
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val items = listOf(BottomNavItem.Home, BottomNavItem.Services, BottomNavItem.UserSubscriptions)
        val selectedBar = remember { mutableStateOf(0)}
        val navController = rememberNavController()
        val appManager = AppManager.getInstance(context)

        Scaffold(
            content = { paddingValues ->
                NavHost(navController = navController, startDestination = "home_screen") {

                    composable("home_screen") {
                        HomeScreen(
                            modifier = Modifier.padding(paddingValues),
                            onSignOut = {
                                lifecycleScope.launch {
                                    googleAuthUiClient.signOut()
                                }
                            }
                        )
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
                ApplicationNavigationBar(items, selectedBar, navController, appManager)
            }
        )
    }
}

// Components:

@Composable
private fun ApplicationNavigationBar(
    items: List<BottomNavItem>,
    selectedBar: MutableState<Int>,
    navController: NavController,
    appManager: AppManager
) {
    NavigationBar(
        containerColor = if (appManager.isDarkMode.value) DarkThemeColors.BarColor else LightThemeColors.BarColor
    ) {
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
                    indicatorColor = if (appManager.isDarkMode.value) Color.DarkGray else Color(0xFF84a4c4),
                    selectedTextColor = if (appManager.isDarkMode.value) Color.White else Color.Black,
                    unselectedTextColor = if (appManager.isDarkMode.value) Color.Gray else Color.DarkGray,
                    selectedIconColor = if (appManager.isDarkMode.value) Color.White else Color.Black,
                    unselectedIconColor = if (appManager.isDarkMode.value) Color.Gray else Color.DarkGray,
                )
            )
        }
    }
}

// Functions:

// Navigate to a new destination while clearing all previous screens from the back stack.
private fun navigateAndClearBackStack(
    navController: NavController,
    destination: String
) {
    navController.navigate(destination) {
        popUpTo(navController.graph.startDestinationId) { inclusive = false }
    }
}


@Preview(showBackground = true)
@Composable
fun AppMainActivityPreview() {

}