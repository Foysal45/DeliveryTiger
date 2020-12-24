package com.bd.deliverytiger.app.api.model.complain


import com.google.gson.annotations.SerializedName

data class ComplainListRequest(
    @SerializedName("MerchantId")
    var merchantId: Int = 0,
    @SerializedName("Index")
    var index: Int = 0,
    @SerializedName("Count")
    var count: Int = 20
)