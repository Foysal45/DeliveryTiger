package com.bd.deliverytiger.app.api.model.complain


import com.google.gson.annotations.SerializedName

data class ComplainRequest(
    @SerializedName("OrderId")
    var orderId: String?,
    @SerializedName("Comments")
    var comments: String?
)