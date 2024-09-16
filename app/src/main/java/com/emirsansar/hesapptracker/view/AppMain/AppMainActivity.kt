package com.emirsansar.hesapptracker.view.AppMain

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
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

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "HesApp")})
        },
        content = {
            when (selectedBar.value) {
                0 -> HomeScreen()
                1 -> ServicesScreen()
                2 -> UserSubscriptionsScreen()
            }
        },
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedBar.value == index,
                        onClick = { selectedBar.value = index },
                        label = { Text(item) },
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

@Preview(showBackground = true)
@Composable
fun AppMainActivityPreview() {
    HesAppTrackerTheme {
        MainActivityScreen()
    }
}