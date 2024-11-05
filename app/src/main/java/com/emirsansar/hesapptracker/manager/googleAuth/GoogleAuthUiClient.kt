package com.emirsansar.hesapptracker.manager.googleAuth

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.viewModel.AuthenticationViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

open class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient
) {
    private val auth = FirebaseAuth.getInstance()
    private val authVM: AuthenticationViewModel = AuthenticationViewModel()

    // Initiates the sign-in process and returns an IntentSender for handling the result.
    suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        } catch(e: Exception) {
            Log.e("GoogleAuthUiClient", "An error occurred: $e")
            if(e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    // Handles the result of the sign-in intent and retrieves the user's credentials.
    suspend fun signInWithIntent(intent: Intent): AuthResultOnGoogle {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user

            user?.let {
                authVM.saveUserDetailsToFirestore(
                    email = it.email ?: "",
                    name = it.displayName?.split(" ")?.getOrNull(0) ?: "",
                    surname = it.displayName?.split(" ")?.getOrNull(1) ?: ""
                ) {

                }
            }

            AuthResultOnGoogle(
                data = user?.run {
                    UserData(uid, email.toString(), displayName.toString())
                },
                errorMessage = null
            )
        } catch(e: Exception) {
            Log.e("GoogleAuthUiClient", "An error occurred: $e")
            if(e is CancellationException) throw e
            AuthResultOnGoogle(
                data = null,
                errorMessage = e.message
            )
        }
    }

    // Signs the user out of both the One Tap client and Firebase Authentication.
    suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        } catch(e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
        }
    }

    // Retrieves the currently signed-in user's data, if available.
    fun getSignedInUser(): UserData? = auth.currentUser?.run {
        UserData(uid, email.toString(), displayName.toString())
    }

    // Builds the sign-in request with the necessary options for Google ID token.
    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}
