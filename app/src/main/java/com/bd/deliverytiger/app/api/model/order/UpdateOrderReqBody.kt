package com.bd.deliverytiger.app.api.model.order


import com.google.gson.annotations.SerializedName

data class UpdateOrderReqBody(
    @SerializedName("mobile")
    var mobile: String? = "",
    @SerializedName("otherMobile")
    var otherMobile: String? = "",
    @SerializedName("address")
    var address: String? = ""
)