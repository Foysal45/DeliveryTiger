package com.bd.deliverytiger.app.api.model.collector_info


import com.google.gson.annotations.SerializedName

data class CollectorInfoRequest(
    @SerializedName("merchantId")
    var merchantId: Int = 0
)