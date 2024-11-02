package com.emirsansar.hesapptracker.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emirsansar.hesapptracker.manager.FirestoreManager
import com.emirsansar.hesapptracker.model.Plan

class PlanViewModel: ViewModel() {

    private var _planList = MutableLiveData<List<Plan>>()
    val planList: LiveData<List<Plan>> = _planList

    private val db = FirestoreManager.instance.db


    // Fetches plans of the selected service from the 'Services' collection in Firestore.
    fun fetchPlansOfServiceFromFirestore(documentID: String) {

        db.collection("Services").document(documentID).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val data = documentSnapshot.data ?: emptyMap<String, Any>()
                    val plans = mutableListOf<Plan>()

                    val plansMap = data["Plans"] as? Map<String, Any>

                    plansMap?.forEach { (key, value) ->
                        val planPrice = value as? Number

                        if (planPrice != null) {
                            val plan = Plan(planName = key, planPrice = planPrice)
                            plans.add(plan)
                        }
                    }

                    plans.sortBy { it.planPrice.toDouble() }
                    _planList.value = plans

                    Log.i("PlanVM", "Plans have been fetched successfully.")
                } else {
                    Log.e("PlanVM", "Plan's document cannot be found or empty")
                }
            }
            .addOnFailureListener { error ->
                Log.e("PlanVM", "Fetching plans error: ${error.localizedMessage}")
            }
    }


}