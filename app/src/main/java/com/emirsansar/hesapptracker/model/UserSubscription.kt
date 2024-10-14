package com.emirsansar.hesapptracker.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserSubscription(
    var serviceName: String,
    var planName: String,
    var planPrice: Double,
    var personCount: Int
) : Parcelable