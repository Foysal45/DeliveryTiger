package com.bd.deliverytiger.app.api.model.lead_management.phonebook


import com.google.gson.annotations.SerializedName

data class PhonebookData(
    @SerializedName("courierUserId")
    var courierUserId: Int = 0,
    @SerializedName("mobile")
    var mobile: String? = "",
    @SerializedName("customerName")
    var customerName: String? = "",
    @SerializedName("ownPhoneBookId")
    var ownPhoneBookId: Int = 0,
    @SerializedName("phoneBookGroupId")
    var phoneBookGroupId: Int = 0
)