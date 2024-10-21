package com.emirsansar.hesapptracker.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserViewModel: ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    fun fetchUserFullName(completion: (String?) -> Unit) {
        val email = auth.currentUser!!.email

        if (email != null) {
            db.collection("Users").document(email).get()
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