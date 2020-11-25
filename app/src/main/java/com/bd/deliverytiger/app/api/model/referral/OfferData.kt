package com.bd.deliverytiger.app.api.model.referral


import com.google.gson.annotations.SerializedName

data class OfferData(
    @SerializedName("isDeliveryCharge")
    var isDeliveryCharge: Boolean,
    @SerializedName("relationType")
    var relationType: String = ""
)