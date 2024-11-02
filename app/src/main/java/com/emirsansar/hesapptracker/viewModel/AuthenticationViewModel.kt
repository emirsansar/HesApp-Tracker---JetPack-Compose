package com.emirsansar.hesapptracker.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emirsansar.hesapptracker.manager.AuthManager
import com.emirsansar.hesapptracker.manager.FirestoreManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthenticationViewModel: ViewModel() {

    enum class LoginState {
        IDLE,
        SUCCESS,
        FAILURE
    }

    enum class RegisterState {
        IDLE,
        SUCCESS,
        FAILURE
    }

    private val auth = AuthManager.instance.auth
    private val db = FirestoreManager.instance.db

    private val _loginState = MutableLiveData<LoginState>()
    var loginState: LiveData<LoginState> = _loginState
    private val _loggingError = MutableLiveData<String?>()
    var loggingError: LiveData<String?> = _loggingError

    private val _registerState = MutableLiveData<RegisterState>()
    var registerState: LiveData<RegisterState> = _registerState
    private val _registrationError = MutableLiveData<String?>()
    var registrationError: LiveData<String?> = _registrationError


    // Logs in a user with Firebase Authentication.
    fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("AuthenticationVM", "Login successful.")
                _loginState.value = LoginState.SUCCESS
            } else {
                Log.e("AuthenticationVM", "Login error: ${task.exception?.localizedMessage}")
                _loggingError.value = task.exception?.localizedMessage
                _loginState.value = LoginState.FAILURE
            }
        }
    }

    // Registers a user with Firebase Authentication.
    fun registerUserToFirebaseAuth(email: String, password: String, name: String, surname: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                saveUserDetailsToFirestore(email, name, surname) { success ->
                    if (success) {
                        Log.i("AuthenticationVM", "Registration has been successful.")
                        _registerState.value = RegisterState.SUCCESS
                    } else {
                        _registerState.value = RegisterState.FAILURE
                    }
                }
            } else {
                Log.e("AuthenticationVM", "Register error: ${task.exception?.localizedMessage}")
                _registrationError.value = task.exception?.localizedMessage
                _registerState.value = RegisterState.FAILURE
            }
        }
    }

    // Saves user details to Firestore.
    private fun saveUserDetailsToFirestore(email: String, name: String, surname: String, completion: (Boolean) -> Unit) {
        val userData = hashMapOf(
            "Name" to name,
            "Surname" to surname,
            "Subscriptions" to hashMapOf<String, Any>()
        )

        db.collection("Users").document(email).set(userData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("AuthenticationVM", "User's details have been saved on Firestore successfully.")
                completion(true)
            } else {
                Log.e("AuthenticationVM", "An error occurred while saving user's details: ${task.exception?.localizedMessage}")
                completion(false)
            }
        }
    }

    fun setRegisterStateIdle() {
        _registerState.value = RegisterState.IDLE
    }

    fun setLoginStateIdle() {
        _loginState.value = LoginState.IDLE
    }

}