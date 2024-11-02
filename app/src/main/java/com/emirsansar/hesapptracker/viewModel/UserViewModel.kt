package com.emirsansar.hesapptracker.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.emirsansar.hesapptracker.manager.AuthManager
import com.emirsansar.hesapptracker.manager.FirestoreManager

class UserViewModel: ViewModel() {

    private val db = FirestoreManager.instance.db
    private val userEmail = AuthManager.instance.currentUserEmail

    fun fetchUserFullName(completion: (String?) -> Unit) {

        if (userEmail != null) {
            db.collection("Users").document(userEmail).get()
                .addOnSuccessListener { documentSnapshot ->
                    val name = documentSnapshot.getString("Name") ?: ""
                    val surname = documentSnapshot.getString("Surname") ?: ""
                    val fullName = "$name $surname"
                    completion(fullName)
                }
                .addOnFailureListener { exception ->
                    Log.e("UserViewModel", "User cannot be found in 'Users' collection.", exception)
                    completion(null)
                }
        } else {
            Log.e("UserViewModel", "Current user email is null.")
            completion(null)
        }
    }

}