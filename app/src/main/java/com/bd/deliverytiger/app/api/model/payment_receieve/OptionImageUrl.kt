package com.bd.deliverytiger.app.api.model.payment_receieve


import com.google.gson.annotations.SerializedName

data class OptionImageUrl(
    @SerializedName("ImageUrl")
    val imageUrl: String = "",
    @SerializedName("OptionName")
    val optionName: String = "",
    @SerializedName("PaymentMethod")
    val paymentMethod: Int = 0
)