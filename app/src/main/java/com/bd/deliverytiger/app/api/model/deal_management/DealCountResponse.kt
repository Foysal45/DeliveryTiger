package com.bd.deliverytiger.app.api.model.deal_management

import com.google.gson.annotations.SerializedName

data class DealCountResponse (
    @SerializedName("Count")
    var count: Int = 0
)