package com.bd.deliverytiger.app.api.model.deal_management

import com.google.gson.annotations.SerializedName

data class UpdateSoldOutRequest(
    @SerializedName("DealCategory")
    var dealCategory: String = "IsSoldOut",
    @SerializedName("DealIdList")
    var dealIdList: String = "",
    @SerializedName("UserId")
    var userId: Int = 0,
    @SerializedName("IsSoldOut")
    var isSoldOut: String = ""
)