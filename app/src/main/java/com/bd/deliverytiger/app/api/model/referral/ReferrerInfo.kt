package com.bd.deliverytiger.app.api.model.referral


import com.google.gson.annotations.SerializedName

data class ReferrerInfo(
    @SerializedName("referrerId")
    var referrerId: Int = 0,
    @SerializedName("referrerType")
    var referrerType: String? = "",
    @SerializedName("referrerOrder")
    var referrerOrder: Int = 0,
    @SerializedName("isActive")
    var isActive: Boolean = false,
    @SerializedName("referrerUseDays")
    var referrerUseDays: Int = 0
)