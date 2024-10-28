package com.emirsansar.hesapptracker.manager

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import java.util.Locale

// This class is used to hold and manage the user's application theme and language option values.
class AppManager private constructor(context: Context) {

    private val sharedThemePref: SharedPreferences = context.getSharedPreferences("theme_pref", Context.MODE_PRIVATE)
    private val sharedLanguagePref: SharedPreferences = context.getSharedPreferences("language_pref", Context.MODE_PRIVATE)

    // Tracks dark mode preference with Compose state management.
    var isDarkMode = mutableStateOf(sharedThemePref.getBoolean("isDarkMode", false))
        private set

    // Toggles the theme between dark and light modes.
    fun toggleTheme() {
        isDarkMode.value = !isDarkMode.value
        sharedThemePref.edit().putBoolean("isDarkMode", isDarkMode.value).apply()
    }

    // Sets the application language and updates the shared preference.
    fun setLanguage(context: Context, language: String) {
        sharedLanguagePref.edit().putString("app_language", language).apply()

        Locale.setDefault(Locale(language))
        val config = context.resources.configuration
        config.setLocale(Locale(language))
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    // Retrieves the stored language, or defaults to system language if not set.
    // If system language is not "en" or "tr", defaults to "tr".
    fun getLanguage(): String {
        val language = sharedLanguagePref.getString("app_language", null)

        if (language == null) {
            return when (val systemLanguage = Locale.getDefault().language) {
                "en", "tr" -> systemLanguage
                else -> "tr"
            }
        }

        return language
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