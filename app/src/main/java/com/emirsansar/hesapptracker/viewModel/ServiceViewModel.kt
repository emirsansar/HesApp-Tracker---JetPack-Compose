package com.emirsansar.hesapptracker.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emirsansar.hesapptracker.model.Service
import com.google.firebase.firestore.FirebaseFirestore

class ServiceViewModel: ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

//    var services: List<Service> = emptyList()
//        private set

    private val _serviceList = MutableLiveData<List<Service>>()
    var services: LiveData<List<Service>> = _serviceList


    // Fetches all services from the 'Services' collection in Firestore.
    fun fetchServicesFromFirestore(completion: (List<Service>?, Exception?) -> Unit) {
        firestore.collection("Services").get()
            .addOnSuccessListener { querySnapshot ->
                val services = mutableListOf<Service>()

                for (document in querySnapshot.documents) {
                    val serviceName = document.id
                    val serviceType = document.getString("Type") ?: ""

                    val service = Service(serviceName, serviceType)
                    services.add(service)
                }

                Log.i("ServiceViewModel", "Successfully fetched ${services.size} services from Firestore.")
                completion(services, null)
            }
            .addOnFailureListener { exception ->
                Log.e("ServiceViewModel", "Error fetching services from Firestore: ${exception.localizedMessage}")
                completion(null, exception)
            }
    }

}