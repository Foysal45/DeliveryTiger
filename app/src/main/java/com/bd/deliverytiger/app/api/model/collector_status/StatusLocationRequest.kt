package com.bd.deliverytiger.app.api.model.collector_status


import com.google.gson.annotations.SerializedName

data class StatusLocationRequest(
    @SerializedName("merchantId")
    var merchantId: Int = 0,
    @SerializedName("confirmation")
    var confirmation: String = "no",
    @SerializedName("deliveryUserId")
    var deliveryUserId: Int = 0,
    @SerializedName("latitude")
    var latitude: String = "",
    @SerializedName("longitude")
    var longitude: String = ""

)