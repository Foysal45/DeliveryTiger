package com.bd.deliverytiger.app.api.model.deal_management


import com.google.gson.annotations.SerializedName

data class DeleteRequest(
    @SerializedName("DealId")
    var dealId: Int,
    @SerializedName("DeletedByid")
    var deletedById: Int
)