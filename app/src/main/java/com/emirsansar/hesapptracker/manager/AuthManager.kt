package com.emirsansar.hesapptracker.manager

import com.google.firebase.auth.FirebaseAuth

class AuthManager private constructor() {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    var currentUserEmail: String? = null

    init {
        currentUserEmail = auth.currentUser?.email

        // AuthStateListener to update the email whenever user state changes
        auth.addAuthStateListener { firebaseAuth ->
            currentUserEmail = firebaseAuth.currentUser?.email
        }
    }

    companion object {
        val instance: AuthManager by lazy { AuthManager() }
    }

}
