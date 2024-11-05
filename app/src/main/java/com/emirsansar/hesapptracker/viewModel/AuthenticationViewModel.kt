package com.emirsansar.hesapptracker.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emirsansar.hesapptracker.R
import com.emirsansar.hesapptracker.manager.googleAuth.AuthResultOnGoogle
import com.emirsansar.hesapptracker.manager.AuthManager
import com.emirsansar.hesapptracker.manager.FirestoreManager
import com.emirsansar.hesapptracker.manager.googleAuth.AuthStateOnGoogle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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

    private val _stateForGoogle = MutableStateFlow(AuthStateOnGoogle())
    val stateForGoogle = _stateForGoogle.asStateFlow()


    // Logs in a user with Firebase Authentication.
    fun loginUser(email: String, password: String, context: Context) {
        if (email == "" || password == "") {
            _loggingError.value = context.getString(R.string.error_fill_all_fields)
            _loginState.value = LoginState.FAILURE
        } else {
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
    }

    // Registers a user with Firebase Authentication.
    fun registerUserToFirebaseAuth(email: String, password: String, name: String, surname: String, context: Context) {
        if (email == "" || password == "" || name == "" || surname == "") {
            _registrationError.value = context.getString(R.string.error_fill_all_fields)
            _registerState.value = RegisterState.FAILURE
        } else {
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
    }

    // Saves user details to Firestore.
    fun saveUserDetailsToFirestore(email: String, name: String, surname: String, completion: (Boolean) -> Unit) {
        db.collection("Users").document(email).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null && document.exists()) {
                    Log.e("AuthenticationVM", "A document with this email already exists.")
                    completion(false)
                } else {
                    val userData = hashMapOf(
                        "Name" to name,
                        "Surname" to surname,
                        "Subscriptions" to hashMapOf<String, Any>()
                    )

                    db.collection("Users").document(email).set(userData).addOnCompleteListener { saveTask ->
                        if (saveTask.isSuccessful) {
                            Log.i("AuthenticationVM", "User's details have been saved on Firestore successfully.")
                            completion(true)
                        } else {
                            Log.e("AuthenticationVM", "An error occurred while saving user's details: ${saveTask.exception?.localizedMessage}")
                            completion(false)
                        }
                    }
                }
            } else {
                Log.e("AuthenticationVM", "An error occurred while checking for existing document: ${task.exception?.localizedMessage}")
                completion(false)
            }
        }
    }

    fun onSignInResult(result: AuthResultOnGoogle) {
        _stateForGoogle.update { it.copy(
            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage
            )
        }
    }

    fun resetStateForGoogle() {
        _stateForGoogle.update { AuthStateOnGoogle() }
    }

    fun setRegisterStateIdle() {
        _registerState.value = RegisterState.IDLE
    }

    fun setLoginStateIdle() {
        _loginState.value = LoginState.IDLE
    }

}