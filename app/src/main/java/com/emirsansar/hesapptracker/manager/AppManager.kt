package com.emirsansar.hesapptracker.manager

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf

// This class is used to hold and manage the user's application theme and language option values.
class AppManager private constructor(context: Context) {

    private val sharedPref: SharedPreferences = context.getSharedPreferences("theme_pref", Context.MODE_PRIVATE)

    var isDarkMode = mutableStateOf(sharedPref.getBoolean("isDarkMode", false))
        private set

    fun toggleTheme() {
        isDarkMode.value = !isDarkMode.value
        sharedPref.edit().putBoolean("isDarkMode", isDarkMode.value).apply()
    }

    companion object {
        @Volatile
        private var INSTANCE: AppManager? = null

        fun getInstance(context: Context): AppManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppManager(context).also { INSTANCE = it }
            }
        }
    }

}