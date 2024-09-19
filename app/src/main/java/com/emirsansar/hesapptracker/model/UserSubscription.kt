package com.emirsansar.hesapptracker.model

data class UserSubscription(
    var serviceName: String,
    var planName: String,
    var planPrice: Double,
    var personCount: Int
)