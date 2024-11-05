package com.emirsansar.hesapptracker.manager.googleAuth

data class AuthResultOnGoogle(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val userEmail: String,
    val userName: String
)
