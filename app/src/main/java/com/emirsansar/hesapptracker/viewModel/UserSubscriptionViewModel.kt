package com.emirsansar.hesapptracker.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emirsansar.hesapptracker.model.Plan
import com.emirsansar.hesapptracker.model.Service
import com.emirsansar.hesapptracker.model.UserSubscription
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class UserSubscriptionViewModel: ViewModel() {

    enum class FetchingSubscriptionsState {
        IDLE, SUCCESS, FAILURE
    }

    enum class FetchingSummaryState {
        IDLE, SUCCESS, FAILURE
    }

    private val _fetchingSubscriptionsState = MutableLiveData<FetchingSubscriptionsState>()
    var fetchingSubscriptionsState: LiveData<FetchingSubscriptionsState> = _fetchingSubscriptionsState

    private val _userSubscriptionList = MutableLiveData<List<UserSubscription>>()
    var userSubscriptionList: LiveData<List<UserSubscription>> = _userSubscriptionList

    private val _fetchingSummaryState = MutableLiveData<FetchingSummaryState>()
    var fetchingSummaryState: LiveData<FetchingSummaryState> = _fetchingSummaryState

    private val _totalSubscriptionCount = MutableLiveData<Int>()
    var totalSubscriptionCount: LiveData<Int> = _totalSubscriptionCount

    private val _totalMonthlySpending = MutableLiveData<Double>()
    var totalMonthlySpending: LiveData<Double> = _totalMonthlySpending


    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()


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

    // Adds a subscription plan to the user's collection in Firestore.
    fun addPlanToUserOnFirestore(serviceName: String, plan: Plan, personCount: Int, completion: (Boolean) -> Unit) {
        val userEmail = auth.currentUser!!.email

        val userRef = db.collection("Users").document(userEmail!!)

        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val updateData: MutableMap<String, Any> = mutableMapOf()

                if (documentSnapshot.exists()) {
                    val existingSubscriptions = documentSnapshot.data?.get("Subscriptions") as? Map<String, Any> ?: emptyMap()
                    val updatedSubscriptions = existingSubscriptions.toMutableMap()

                    updatedSubscriptions[serviceName] = mapOf(
                        "PlanName" to plan.planName,
                        "Price" to plan.planPrice,
                        "PersonCount" to personCount
                    )

                    updateData["Subscriptions"] = updatedSubscriptions
                }
                else {
                    updateData["Subscriptions"] = mapOf(
                        serviceName to mapOf(
                            "PlanName" to plan.planName,
                            "Price" to plan.planPrice,
                            "PersonCount" to personCount
                        )
                    )
                }

                userRef.set(updateData, SetOptions.merge())
                    .addOnSuccessListener {
                        println("Successfully added plan ${plan.planName} to user $userEmail.")
                        completion(true)
                    }
                    .addOnFailureListener { error ->
                        println("Error: ${error.localizedMessage}")
                        completion(false)
                    }
            }
            .addOnFailureListener { error ->
                println("There was an error: ${error.localizedMessage}")
                completion(false)
            }
    }

    // Fetches a summary of the user's total subscription count and monthly spending from Firestore.
    fun fetchSubscriptionsSummary() {
        val userEmail = auth.currentUser!!.email

        val userRef = db.collection("Users").document(userEmail!!)

        userRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        val subscriptions = document.data?.get("Subscriptions") as? Map<String, Map<String, Any>>

                        if (subscriptions != null) {
                            var monthlySpend = 0.0
                            val serviceCount = subscriptions.size

                            for ((_, serviceDetails) in subscriptions) {
                                val price = serviceDetails["Price"] as? Double ?: 0.0
                                val personCount = serviceDetails["PersonCount"] as? Long ?: 1
                                monthlySpend += price / personCount.toDouble()
                            }

                            _totalSubscriptionCount.value = serviceCount
                            _totalMonthlySpending.value = monthlySpend

                            _fetchingSummaryState.value = FetchingSummaryState.SUCCESS
                            Log.i("UserViewModel", "User's subscriptions summary have been fetched successfully.")
                        } else {
                            Log.e("UserViewModel", "No subscriptions found.")
                            _fetchingSummaryState.value = FetchingSummaryState.FAILURE
                        }
                    }
                }
                else {
                    val error = task.exception
                    Log.e("UserViewModel", "User's summary error: ${error?.localizedMessage}")
                    _fetchingSummaryState.value = FetchingSummaryState.FAILURE
                }
            }
    }

}