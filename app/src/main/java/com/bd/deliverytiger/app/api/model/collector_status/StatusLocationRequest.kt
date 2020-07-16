package com.bd.deliverytiger.app.api.model.collector_status


import com.google.gson.annotations.SerializedName

data class StatusLocationRequest(
    @SerializedName("MerchantId")
    var merchantId: Int = 0,
    @SerializedName("Confirmation")
    var confirmation: String = "no",
    @SerializedName("DeliveryUserId")
    var deliveryUserId: Int = 0,
    @SerializedName("Latitude")
    var latitude: String = "",
    @SerializedName("Longitude")
    var longitude: String = ""

)