package com.bd.deliverytiger.app.api.model.referral


import com.google.gson.annotations.SerializedName

data class RefereeInfo(
    @SerializedName("refereeId")
    var refereeId: Int = 0,
    @SerializedName("refereeType")
    var refereeType: String? = "",
    @SerializedName("refereeOrder")
    var refereeOrder: Int = 0,
    @SerializedName("isActive")
    var isActive: Boolean = false,
    @SerializedName("refereeUseDays")
    var refereeUseDays: Int = 0
)