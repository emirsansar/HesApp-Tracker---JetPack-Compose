package com.emirsansar.hesapptracker.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emirsansar.hesapptracker.manager.AuthManager
import com.emirsansar.hesapptracker.manager.FirestoreManager
import com.emirsansar.hesapptracker.model.Plan
import com.emirsansar.hesapptracker.model.UserSubscription
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

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


    private val db = FirestoreManager.instance.db
    private val userEmail = AuthManager.instance.currentUserEmail

    // Fetches the user's subscriptions from Firestore.
    fun fetchUserSubscriptionsFromFirestore() {
        val userRef = db.collection("Users").document(userEmail!!)

        userRef.addSnapshotListener { documentSnapshot, exception ->
            if (exception != null) {
                Log.e("UserSubscriptionsVM", "An error occurred while fetching user's subscription list: ${exception.localizedMessage}")
                _fetchingSubscriptionsState.value = FetchingSubscriptionsState.FAILURE
                return@addSnapshotListener
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                val subscriptions = documentSnapshot.data?.get("Subscriptions") as? Map<String, Map<String, Any>>

                if (subscriptions == null) {
                    Log.e("UserSubscriptionsVM", "User's subscription list cannot be found.")
                    _fetchingSubscriptionsState.value = FetchingSubscriptionsState.FAILURE
                    return@addSnapshotListener
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

                val sortedSubscriptions = fetchedSubscriptions.sortedBy { it.planPrice / it.personCount.toDouble() }

                _userSubscriptionList.value = sortedSubscriptions
                _fetchingSubscriptionsState.value = FetchingSubscriptionsState.SUCCESS

                Log.i("UserSubscriptionsVM", "User's subscriptions list has been fetched successfully.")
            } else {
                Log.e("UserSubscriptionsVM", "User's subscription list cannot be found.")
                _fetchingSubscriptionsState.value = FetchingSubscriptionsState.FAILURE
            }
        }
    }

    // Adds a subscription plan to the user's collection in Firestore.
    fun addPlanToUserOnFirestore(serviceName: String, plan: Plan, personCount: Int, completion: (Boolean) -> Unit) {

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

    // Removes a selected subscription from the user's collection in Firestore.
    fun removeSubscriptionFromUser(selectedSub: UserSubscription, onComplete: (Boolean) -> Unit) {

        val userRef = db.collection("Users").document(userEmail!!)

        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val fieldToRemove = selectedSub.serviceName

                userRef.update(mapOf("Subscriptions.$fieldToRemove" to FieldValue.delete()))
                    .addOnSuccessListener {
                        MainScope().launch {
                            val currentList = _userSubscriptionList.value?.toMutableList() ?: mutableListOf()
                            val index = currentList.indexOfFirst { it.serviceName == selectedSub.serviceName }
                            if (index != -1) {
                                currentList.removeAt(index)
                                _userSubscriptionList.value = currentList
                            }
                        }
                        Log.d("RemoveSubscription", "Subscription successfully removed.")
                        onComplete(true)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("RemoveSubscription", "Error while trying to remove subscription: ", exception)
                        onComplete(false)
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("RemoveSubscription", "Error while trying to remove subscription: ", exception)
                onComplete(false)
            }
    }

    // Updates the selected subscription on Firebase.
    fun updateSubscription(updatedSubscription: UserSubscription, completion: (Boolean) -> Unit) {

        val userRef = db.collection("Users").document(userEmail!!)

        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val documentSnapshot = task.result
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val subscriptions = documentSnapshot.get("Subscriptions") as? Map<String, Map<String, Any>> ?: run {
                        Log.e("UpdateSubscription", "No subscriptions found or document does not exist.")
                        completion(false)
                        return@addOnCompleteListener
                    }

                    val serviceDetails = subscriptions[updatedSubscription.serviceName]?.toMutableMap()

                    if (serviceDetails != null) {
                        serviceDetails["PlanName"] = updatedSubscription.planName
                        serviceDetails["Price"] = updatedSubscription.planPrice
                        serviceDetails["PersonCount"] = updatedSubscription.personCount

                        // Update the subscriptions map
                        val updatedSubscriptions = subscriptions.toMutableMap()
                        updatedSubscriptions[updatedSubscription.serviceName] = serviceDetails

                        userRef.update("Subscriptions", updatedSubscriptions).addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Log.e("UpdateSubscription", "Service has been updated successfully.")
                                completion(true)
                            } else {
                                Log.e("UpdateSubscription", "Unknown error occurred.")
                                completion(false)
                            }
                        }
                    } else {
                        Log.e("UpdateSubscription", "Service not found in user's subscriptions.")
                        completion(false)
                    }
                } else {
                    Log.e("UpdateSubscription", "No subscriptions found or document does not exist.")
                    completion(false)
                }
            } else {
                Log.e("UpdateSubscription", "Error when trying to update sub: ${task.exception?.localizedMessage}")
                completion(false)
            }
        }
    }


}