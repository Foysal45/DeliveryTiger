package com.bd.deliverytiger.app.api.model.loan_survey


import com.google.gson.annotations.SerializedName

data class CourierModel(
    @SerializedName("contactAddress")
    var contactAddress: String = "",
    @SerializedName("contactNo")
    var contactNo: String = "",
    @SerializedName("courierId")
    var courierId: Int = 0,
    @SerializedName("courierName")
    var courierName: String = ""
)