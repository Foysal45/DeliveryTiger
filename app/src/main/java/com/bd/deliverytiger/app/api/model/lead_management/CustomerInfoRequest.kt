package com.bd.deliverytiger.app.api.model.lead_management


import com.google.gson.annotations.SerializedName

data class CustomerInfoRequest(
    @SerializedName("merchantId")
    var merchantId: Int = 0,
    @SerializedName("index")
    var index: Int = 0,
    @SerializedName("count")
    var count: Int = 0
)