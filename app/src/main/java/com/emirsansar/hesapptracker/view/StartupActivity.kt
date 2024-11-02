package com.emirsansar.hesapptracker.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.emirsansar.hesapptracker.manager.AppManager
import com.emirsansar.hesapptracker.manager.AuthManager
import com.emirsansar.hesapptracker.view.AppMain.MainActivity
import com.emirsansar.hesapptracker.view.Authentication.AuthenticationActivity
import com.google.firebase.auth.FirebaseAuth

class StartupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appManager = AppManager.getInstance(this)

        // Set the app language based on the saved preference.
        appManager.setLanguage(this, appManager.getLanguage())

        val auth = AuthManager.instance.auth
        val currentUser = auth.currentUser

        // Redirect to MainActivity if the user is authenticated, else go to AuthenticationActivity.
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
