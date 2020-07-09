package com.bd.deliverytiger.app.api.model.service_bill_pay


import com.google.gson.annotations.SerializedName

data class MonthlyReceivableRequest(
    @SerializedName("CourierOrMerchant")
    var courierOrMerchant: String? = "",
    @SerializedName("FromDate")
    var fromDate: String? = "",
    @SerializedName("ToDate")
    var toDate: String? = ""
)