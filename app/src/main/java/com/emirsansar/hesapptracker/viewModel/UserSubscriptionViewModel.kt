package com.emirsansar.hesapptracker.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emirsansar.hesapptracker.model.Service
import com.emirsansar.hesapptracker.model.UserSubscription
import com.google.firebase.firestore.FirebaseFirestore

class UserSubscriptionViewModel: ViewModel() {

    enum class FetchingSubscriptionsState {
        IDLE,
        SUCCESS,
        FAILURE
    }

    private val _fetchingSubscriptionsState = MutableLiveData<FetchingSubscriptionsState>()
    var fetchingSubscriptionsState: LiveData<FetchingSubscriptionsState> = _fetchingSubscriptionsState

    var userSubscriptions: MutableList<UserSubscription> = mutableListOf()

    private val _userSubscriptionList = MutableLiveData<List<UserSubscription>>()
    var userSubscriptionList: LiveData<List<UserSubscription>> = _userSubscriptionList

    private val db = FirebaseFirestore.getInstance()


    // Fetches the user's subscriptions from Firestore.
    fun fetchUserSubscriptionsFromFirestore(userEmail: String) {

        val userRef = db.collection("Users").document(userEmail)

        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null && document.exists()) {
                    val subscriptions = document.data?.get("Subscriptions") as? Map<String, Map<String, Any>>

                    if (subscriptions == null) {
                        Log.e("UserSubscriptionsVM", "User's subscription list cannot be found.")
                        _fetchingSubscriptionsState.value = FetchingSubscriptionsState.FAILURE
                        return@addOnCompleteListener
                    }

                    val fetchedSubscriptions: MutableList<UserSubscription> = mutableListOf()

                    for ((serviceName, serviceDetails) in subscriptions) {
                        val planName = serviceDetails["PlanName"] as? String
                        val planPrice = serviceDetails["Price"] as? Double
                        val personCount = (serviceDetails["PersonCount"] as? Number)?.toInt()

                        if (planName != null && planPrice != null && personCount != null) {
                            val userSub = UserSubscription(serviceName, planName, planPrice, personCount)

                            fetchedSubscriptions.add(userSub)
                        }
                    }

                    _userSubscriptionList.value = fetchedSubscriptions
                    _fetchingSubscriptionsState.value  = FetchingSubscriptionsState.SUCCESS

                    Log.i("UserSubscriptionsVM", "User's subscriptions list has been fetched successfully.")
                } else {
                    Log.e("UserSubscriptionsVM", "User's subscription list cannot be found.")
                    _fetchingSubscriptionsState.value  = FetchingSubscriptionsState.FAILURE
                }
            } else {
                Log.e("UserSubscriptionsVM", "An error occurred while fetching user's subscription list: ${task.exception?.localizedMessage}")
                _fetchingSubscriptionsState.value  = FetchingSubscriptionsState.FAILURE
            }
        }
    }

}