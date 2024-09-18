package com.emirsansar.hesapptracker.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.emirsansar.hesapptracker.view.AppMain.MainActivity
import com.emirsansar.hesapptracker.view.Authentication.AuthenticationActivity
import com.emirsansar.hesapptracker.ui.theme.HesAppTrackerTheme
import com.google.firebase.auth.FirebaseAuth

class StartupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, AuthenticationActivity::class.java)
            startActivity(intent)
        }

        finish()

//        setContent {
//            HesAppTrackerTheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    Greeting()
//                }
//            }
//        }

    }
}

//@Composable
//fun Greeting(modifier: Modifier = Modifier) {
//    Text(text = "Welcome to HesApp Tracker!")
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    HesAppTrackerTheme {
//        Greeting()
//    }
//}
