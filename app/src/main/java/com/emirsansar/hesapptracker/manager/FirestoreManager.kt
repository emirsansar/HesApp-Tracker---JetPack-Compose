package com.emirsansar.hesapptracker.manager

import com.google.firebase.firestore.FirebaseFirestore

class FirestoreManager private constructor() {

    val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    companion object {
        val instance: FirestoreManager by lazy { FirestoreManager() }
    }

}