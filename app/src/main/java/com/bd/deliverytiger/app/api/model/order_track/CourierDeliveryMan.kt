package com.bd.deliverytiger.app.api.model.order_track


import com.google.gson.annotations.SerializedName

data class CourierDeliveryMan(
    @SerializedName("courierDeliveryManMobile")
    var courierDeliveryManMobile: String? = "",
    @SerializedName("courierDeliveryManName")
    var courierDeliveryManName: String? = "",
    @SerializedName("courierComment")
    var courierComment: String? = ""
)