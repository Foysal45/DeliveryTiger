package com.bd.deliverytiger.app.api.model.offer


import com.google.gson.annotations.SerializedName

data class OfferUpdateRequest(
    @SerializedName("offerBkashDiscount")
    var offerBkashDiscount: Int = 0,
    @SerializedName("offerCodDiscount")
    var offerCodDiscount: Int = 0,
    @SerializedName("classifiedId")
    var classifiedId: Int = 0
)