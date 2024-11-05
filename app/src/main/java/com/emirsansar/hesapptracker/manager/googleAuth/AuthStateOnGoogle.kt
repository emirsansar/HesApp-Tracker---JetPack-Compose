package com.emirsansar.hesapptracker.manager.googleAuth

data class AuthStateOnGoogle(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)