package com.emirsansar.hesapptracker.view.AppMain

import android.annotation.SuppressLint
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.emirsansar.hesapptracker.ui.theme.HesAppTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivityScreen(modifier: Modifier = Modifier) {
    val items = listOf("Home", "Services", "UserSubscriptions")
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
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedBar.value == index,
                        onClick = {
                            selectedBar.value = index;
                            navigateAndClearBackStack(
                                navController = navController,
                                destination = "${item.lowercase()}_screen"
                            ) },
                        label = {
                            if (item == "UserSubscriptions" ) Text(text = "Subscriptions")
                            else Text(item) },
                        icon = {
                            when (item) {
                                "Home" -> Icon(Icons.Default.Home, contentDescription = "Home Icon")
                                "Services" -> Icon(Icons.Default.List, contentDescription = "Services Icon")
                                "UserSubscriptions" -> Icon(Icons.Default.Person, contentDescription = "User Subscriptions Icon")
                            }
                        }
                    )
                }
            }
        }
    )
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

@Preview(showBackground = true)
@Composable
fun AppMainActivityPreview() {
    HesAppTrackerTheme {
        MainActivityScreen()
    }
}